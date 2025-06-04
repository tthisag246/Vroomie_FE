package com.bumper_car.vroomie_fe.ui.screen.drive

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumper_car.util.UploadS3
import com.bumper_car.vroomie_fe.BuildConfig
import com.bumper_car.vroomie_fe.R
import com.bumper_car.vroomie_fe.Vroomie_FEApplication
import com.bumper_car.vroomie_fe.data.remote.kakao.KakaoNaviApi
import com.bumper_car.vroomie_fe.data.remote.kakao.KakaoRetrofitClient
import com.bumper_car.vroomie_fe.util.CameraStreamer
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.kakaomobility.knsdk.KNRoutePriority
import com.kakaomobility.knsdk.KNSDK
import com.kakaomobility.knsdk.common.objects.KNError
import com.kakaomobility.knsdk.common.objects.KNPOI
import com.kakaomobility.knsdk.guidance.knguidance.*
import com.kakaomobility.knsdk.guidance.knguidance.citsguide.KNGuide_Cits
import com.kakaomobility.knsdk.guidance.knguidance.common.KNLocation
import com.kakaomobility.knsdk.guidance.knguidance.locationguide.KNGuide_Location
import com.kakaomobility.knsdk.guidance.knguidance.routeguide.KNGuide_Route
import com.kakaomobility.knsdk.guidance.knguidance.routeguide.objects.KNMultiRouteInfo
import com.kakaomobility.knsdk.guidance.knguidance.safetyguide.KNGuide_Safety
import com.kakaomobility.knsdk.guidance.knguidance.safetyguide.objects.KNSafety
import com.kakaomobility.knsdk.guidance.knguidance.voiceguide.KNGuide_Voice
import com.kakaomobility.knsdk.trip.kntrip.KNTrip
import com.kakaomobility.knsdk.trip.kntrip.knroute.KNRoute
import com.kakaomobility.knsdk.ui.view.KNNaviView
import com.google.android.gms.location.*
import com.kakaomobility.knsdk.KNRGCode
import com.kakaomobility.knsdk.guidance.knguidance.routeguide.objects.KNDirection
import com.kakaomobility.knsdk.guidance.knguidance.voiceguide.KNVoiceCode
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

@AndroidEntryPoint
class NaviActivity : AppCompatActivity(),
    KNGuidance_GuideStateDelegate,
    KNGuidance_LocationGuideDelegate,
    KNGuidance_RouteGuideDelegate,
    KNGuidance_SafetyGuideDelegate,
    KNGuidance_VoiceGuideDelegate,
    KNGuidance_CitsGuideDelegate {

    private lateinit var naviView: KNNaviView
    private lateinit var previewView: PreviewView
    private lateinit var cameraStreamer: CameraStreamer
    private lateinit var fusedClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var gpsSpeedMonitor: GpsSpeedMonitor

    // TTS
    private lateinit var tts: TextToSpeech
    private val lastEventTimestamps = mutableMapOf<String, Long>()
    private val cooldownMillis = 6000L

    // 거리/시간 계산을 위한 변수 추가
    private var lastLocation: android.location.Location? = null
    private var totalDistance: Float = 0f
    private var startTimeMillis: Long = 0L

    private val naviViewModel: NaviViewModel by viewModels()
    private lateinit var kakaoNaviApi: KakaoNaviApi

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

        if (fineLocationGranted && coarseLocationGranted) {
            // 위치 권한 허용됨: 모든 위치 기반 기능 초기화 및 시작
            Toast.makeText(this, "위치 권한이 허용되었습니다.", Toast.LENGTH_SHORT).show()
            initLocationServices()
        } else {
            // 위치 권한 거부됨: 앱 종료 또는 이전 화면으로 돌아감
            Toast.makeText(this, "위치 권한이 거부되어 내비게이션을 시작할 수 없습니다.", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navi)

        kakaoNaviApi = KakaoRetrofitClient.kakaoNaviApi

        tts = TextToSpeech(this) {
            if (it == TextToSpeech.SUCCESS) {
                tts.language = Locale.KOREAN
            }
        }

        gpsSpeedMonitor = GpsSpeedMonitor(
            context = this,
            onSuddenAccel = {
                val key = "Sudden_Accel"
                if (isCooldownPassed(key) && !tts.isSpeaking) {
                    tts.speak("급가속 했어요. 브레이크를 미리미리 준비하며 부드럽게 가속해보세요.", TextToSpeech.QUEUE_FLUSH, null, key)
                    naviViewModel.incrementSuddenAccelerationCount()
                }
                Log.d("DrivingEvent", "🚀 급가속 감지됨")
            },
            onSuddenDecel = {
                val key = "Sudden_Decel"
                if (isCooldownPassed(key) && !tts.isSpeaking) {
                    tts.speak("급감속 했어요. 미리 주변 상황을 보고 브레이크를 여유있게 밟아보세요.", TextToSpeech.QUEUE_FLUSH, null, key)
                    naviViewModel.incrementSuddenDecelerationCount()
                }
                Log.d("DrivingEvent", "🛑 급감속 감지됨")
            }
        )

        naviView = findViewById(R.id.navi_view)
        previewView = findViewById(R.id.preview_view)

        ViewCompat.setOnApplyWindowInsetsListener(naviView) { view, insets ->
            val navBarHeight = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            view.setPadding(0, 0, 0, navBarHeight)
            insets
        }

        window?.apply {
            statusBarColor = Color.TRANSPARENT
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }

        cameraStreamer = CameraStreamer(
            context = this,
            previewView = previewView,
            wsUrl = "ws://${BuildConfig.SERVER_IP_ADDRESS}:8080/drive/ws/video"
        )
        cameraStreamer.setOnMessageListener { message ->
            runOnUiThread {
                handleDriveEvent(message)
            }
        }
        cameraStreamer.startWebSocket()
        cameraStreamer.startStreaming(this)

        val startDateTime = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(Date())
        naviViewModel.setStartAt(startDateTime)

        val hasFineLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (hasFineLocationPermission && hasCoarseLocationPermission) {
            // 모든 위치 권한이 이미 허용된 경우
            initLocationServices()
        } else {
            // 위치 권한이 없는 경우, 권한 요청
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun extractCityAndProvince(fullAddress: String): String {
        val parts = fullAddress.split(" ")
        if (parts.size >= 2) {
            return "${parts[0]} ${parts[1]}"
        }
        return fullAddress
    }

    // 위치 기반 기능들을 초기화하고 시작하는 함수
    private fun initLocationServices() {
        // 인텐트로부터 목적지 정보 가져오기
        val goalLat = intent.getDoubleExtra("lat", -1.0)
        val goalLon = intent.getDoubleExtra("lon", -1.0)
        val goalPlaceName = intent.getStringExtra("name") ?: "목적지"

        // 목적지 주소 설정 (좌표를 주소로 변환)
        if (goalLat != -1.0 && goalLon != -1.0) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val restApiKey = BuildConfig.KAKAO_REST_API_KEY
                    val response = kakaoNaviApi.getAddressFromCoordinates(
                        authorization = "KakaoAK $restApiKey",
                        longitude = goalLon,
                        latitude = goalLat
                    )

                    withContext(Dispatchers.Main) {
                        if (response.documents.isNotEmpty()) {
                            val fullAddress = response.documents[0].roadAddress?.addressName
                                ?: response.documents[0].address?.addressName
                            if (fullAddress != null) {
                                val simplifiedAddress = extractCityAndProvince(fullAddress)
                                naviViewModel.setEndLocation(simplifiedAddress)
                                Log.d("NaviActivity", "목적지 주소: $fullAddress")
                            } else {
                                naviViewModel.setEndLocation(goalPlaceName)
                                Log.w("NaviActivity", "목적지 주소 변환 불가, 장소명 사용: $goalPlaceName")
                            }
                        } else {
                            naviViewModel.setEndLocation(goalPlaceName)
                            Log.w("NaviActivity", "목적지 주소 검색 결과 없음, 장소명 사용: $goalPlaceName")
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        naviViewModel.setEndLocation(goalPlaceName)
                        Log.e("NaviActivity", "목적지 주소 검색 실패: ${e.message}")
                    }
                }
            }
        } else {
            naviViewModel.setEndLocation(goalPlaceName)
            Toast.makeText(this, "목적지 좌표 정보가 없습니다.", Toast.LENGTH_SHORT).show()
        }

        fusedClient = LocationServices.getFusedLocationProviderClient(this)

        // fusedClient.getCurrentLocation() 호출 전 권한 확인
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { location ->
                    if (location != null) {
                        startTimeMillis = System.currentTimeMillis()
                        lastLocation = location

                        val katecPoint = KNSDK.convertWGS84ToKATEC(location.longitude, location.latitude)
                        val startPoi = KNPOI("현재 위치", katecPoint.x.toInt(), katecPoint.y.toInt(), "출발지")

                        // 현재 위치의 주소 설정 (좌표를 주소로 변환)
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                val restApiKey = BuildConfig.KAKAO_REST_API_KEY
                                val response = kakaoNaviApi.getAddressFromCoordinates(
                                    authorization = "KakaoAK $restApiKey",
                                    longitude = location.longitude,
                                    latitude = location.latitude
                                )

                                withContext(Dispatchers.Main) {
                                    if (response.documents.isNotEmpty()) {
                                        val fullAddress = response.documents[0].roadAddress?.addressName
                                            ?: response.documents[0].address?.addressName
                                        if (fullAddress != null) {
                                            val simplifiedAddress = extractCityAndProvince(fullAddress)
                                            naviViewModel.setStartLocation(simplifiedAddress)
                                            Log.d("NaviActivity", "출발지 주소: $fullAddress")
                                        } else {
                                            naviViewModel.setStartLocation("알 수 없는 주소")
                                            Log.w("NaviActivity", "출발지 주소 변환 불가")
                                        }
                                    } else {
                                        naviViewModel.setStartLocation("알 수 없는 주소")
                                        Log.w("NaviActivity", "출발지 주소 검색 결과 없음")
                                    }
                                }
                            } catch (e: Exception) {
                                withContext(Dispatchers.Main) {
                                    naviViewModel.setStartLocation("주소 검색 오류")
                                    Log.e("NaviActivity", "출발지 주소 검색 실패: ${e.message}")
                                }
                            }
                        }

                        val guidance = Vroomie_FEApplication.knsdk.sharedGuidance()

                        if (guidance == null) {
                            Toast.makeText(this, "SDK 초기화 안됨", Toast.LENGTH_SHORT).show()
                            Log.e("NaviActivity", "KNSDK not initialized")
                            return@addOnSuccessListener
                        }

                        guidance.stop()

                        Vroomie_FEApplication.knsdk.makeTripWithStart(
                            aStart = startPoi,
                            aGoal = KNPOI(goalPlaceName, KNSDK.convertWGS84ToKATEC(goalLon, goalLat).x.toInt(), KNSDK.convertWGS84ToKATEC(goalLon, goalLat).y.toInt(), goalPlaceName),
                            aVias = null
                        ) { error, trip ->
                            runOnUiThread {
                                if (error == null) {
                                    startGuide(trip)
                                } else {
                                    Toast.makeText(this, "경로 탐색 실패: ${error.msg} (code: ${error.code})", Toast.LENGTH_LONG).show()
                                    Log.e("NaviActivity", "Route error: ${error.msg}, code: ${error.code}")
                                }
                            }
                        }
                    } else {
                        Toast.makeText(this, "현재 위치를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(this, "위치 권한이 없어 현재 위치를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
        }


        // GPS 위치 업데이트 설정
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000L) // 1초마다
            .setMinUpdateDistanceMeters(1.0f) // 1m 이상 이동해야 반응
            .build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val currentLocation = result.lastLocation ?: return

                // 속도 갱신
                cameraStreamer.updateSpeedFromLocation(currentLocation)
                Log.d("GPS", "속도 갱신됨: ${currentLocation.speed * 3.6f} km/h")

                // 누적 주행 거리 계산
                lastLocation?.let { prevLocation ->
                    val distanceBetweenMeters = prevLocation.distanceTo(currentLocation)
                    totalDistance += distanceBetweenMeters
                    val totalDistanceKm = totalDistance / 1000.0f
                    Log.d("GPS", "누적 거리: ${String.format("%.1f", totalDistanceKm)} km")
                }
                lastLocation = currentLocation // 현재 위치를 다음 업데이트를 위한 이전 위치로 저장

                // 누적 주행 시간 계산 (분 단위)
                val elapsedTimeMillis = System.currentTimeMillis() - startTimeMillis
                val elapsedTimeSeconds = (elapsedTimeMillis / 1000).toInt()
                val elapsedTimeMinutes = (elapsedTimeSeconds / 60.0).roundToInt() // 초를 분으로 변환, 반올림
                Log.d("GPS", "경과 시간: ${elapsedTimeMinutes} 분") // 분

                // ViewModel 업데이트
                naviViewModel.updateDistanceAndDuration(totalDistance, elapsedTimeSeconds)
            }
        }

        // fusedClient.requestLocationUpdates() 호출 전 권한 확인
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedClient.requestLocationUpdates(locationRequest, locationCallback, mainLooper)
            gpsSpeedMonitor.start()
        } else {
            Log.w("NaviActivity", "requestLocationUpdates: 위치 권한 없음. 호출 건너뜀.")
        }
    }


    private fun startGuide(trip: KNTrip?) {
        val guidance = Vroomie_FEApplication.knsdk.sharedGuidance()
        guidance?.apply {
            guideStateDelegate = this@NaviActivity
            locationGuideDelegate = this@NaviActivity
            routeGuideDelegate = this@NaviActivity
            safetyGuideDelegate = this@NaviActivity
            voiceGuideDelegate = this@NaviActivity
            citsGuideDelegate = this@NaviActivity
        }

        if (guidance != null) {
            naviView.initWithGuidance(
                guidance,
                trip,
                KNRoutePriority.KNRoutePriority_Recommand,
                0
            )
        }
    }

    private fun isCooldownPassed(eventKey: String): Boolean {
        val now = System.currentTimeMillis()
        val lastTime = lastEventTimestamps[eventKey] ?: 0L
        return if (now - lastTime > cooldownMillis) {
            lastEventTimestamps[eventKey] = now
            true
        } else {
            false
        }
    }

    private fun handleDriveEvent(jsonString: String) {
        try {
            val json = JSONObject(jsonString)
            val event = json.getString("event")

            if (tts.isSpeaking || !isCooldownPassed(event)) return

            when (event) {
                "Left_Deviation" -> {
                    naviViewModel.incrementLaneDeviationLeftCount()
                    tts.speak(
                        "차로의 왼쪽으로 치우쳤어요! 오른발이 도로의 중앙에 떠있는 듯한 지점에 맞추고 시야를 멀리 두세요.",
                        TextToSpeech.QUEUE_FLUSH, null, null
                    )
                }

                "Right_Deviation" -> {
                    naviViewModel.incrementLaneDeviationRightCount()
                    tts.speak(
                        "차로의 오른쪽으로 치우쳤어요! 오른발이 도로의 중앙에 떠있는 듯한 지점에 맞추고 시야를 멀리 두세요.",
                        TextToSpeech.QUEUE_FLUSH, null, null
                    )
                }

                "Cut_In" -> {
                    tts.speak(
                        "우측 또는 좌측 차량이 차로를 변경하려고 해요! 속도를 줄여서 끼어들 공간을 만들어주세요.",
                        TextToSpeech.QUEUE_FLUSH, null, null
                    )
                }

                "Safe_Distance_Violation" -> {
                    naviViewModel.incrementSafeDistanceViolationCount()
                    val recommended = json.optDouble("recommended_distance", -1.0)
                    val actual = json.optDouble("actual_distance", -1.0)

                    val message = if (recommended > 0 && actual > 0) {
                        "앞차와 너무 가까워요! 현재 앞차와의 거리 ${"%.1f".format(actual)}미터이며, 현재 속도에서의 권장 안전거리는 ${"%.1f".format(recommended)}미터입니다. 속도를 줄여서 안전거리를 확보하세요."
                    } else {
                        "앞차와 너무 가까워요! 속도를 줄여서 안전거리를 확보하세요."
                    }
                    tts.speak(message, TextToSpeech.QUEUE_FLUSH, null, null)
                }

                "Stopped_Distance_Check" -> {
                    naviViewModel.incrementSafeDistanceViolationCount()
                    val actual = json.optDouble("actual_distance", -1.0)
                    val message = if (actual > 0) {
                        "정지 시 앞 차와 ${"%.1f".format(actual)}미터 거리를 확보하세요.".trimIndent()
                    } else {
                        "정지 시 앞차와의 거리를 충분히 확보하세요."
                    }
                    tts.speak(message, TextToSpeech.QUEUE_FLUSH, null, null)
                }

                else -> {
                    Log.w("DriveEvent", "알 수 없는 이벤트: $event")
                }
            }
        } catch (e: Exception) {
            Log.e("DriveEvent", "JSON 파싱 오류: ${e.message}")
        }
    }

    override fun guidanceCheckingRouteChange(aGuidance: KNGuidance) {
        naviView.guidanceCheckingRouteChange(aGuidance)
    }

    override fun guidanceDidUpdateRoutes(
        aGuidance: KNGuidance,
        aRoutes: List<KNRoute>,
        aMultiRouteInfo: KNMultiRouteInfo?
    ) {
        naviView.guidanceDidUpdateRoutes(aGuidance, aRoutes, aMultiRouteInfo)
    }

    override fun guidanceGuideEnded(aGuidance: KNGuidance) {
        naviView.guidanceGuideEnded(aGuidance)

        // 주행 종료 시간 설정
        val endDateTime = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(Date())
        naviViewModel.setEndAt(endDateTime)

        // 주행이 성공적으로 종료되었을 때만 saveDriveResult 호출
        naviViewModel.saveDriveResult( // saveDriveResult에 필요한 인자를 넘겨줘야 함
            onSuccess = { driveResultResponse ->
                val intent = Intent(this, DriveResultActivity::class.java).apply {
                    // driveResultResponse가 Parcelable 또는 Serializable이어야 putExtra로 전달 가능합니다.
                    putExtra("drive_result", driveResultResponse)
                }
                startActivity(intent)
                finish() // NaviActivity 종료
            },
            onError = {
                Toast.makeText(this, "주행 결과 저장 실패", Toast.LENGTH_SHORT).show()
            }
        )
    }

    override fun guidanceGuideStarted(aGuidance: KNGuidance) {
        naviView.guidanceGuideStarted(aGuidance)
    }

    override fun guidanceOutOfRoute(aGuidance: KNGuidance) {
        naviView.guidanceOutOfRoute(aGuidance)
    }

    override fun guidanceRouteChanged(
        aGuidance: KNGuidance,
        aFromRoute: KNRoute,
        aFromLocation: KNLocation,
        aToRoute: KNRoute,
        aToLocation: KNLocation,
        aChangeReason: KNGuideRouteChangeReason
    ) {
        naviView.guidanceRouteChanged(aGuidance)
    }

    override fun guidanceRouteUnchanged(aGuidance: KNGuidance) {
        naviView.guidanceRouteUnchanged(aGuidance)

        fusedClient.removeLocationUpdates(locationCallback)

        // 녹화 종료 처리
        cameraStreamer.stopRecording()
        cameraStreamer.stopWebSocket() // ← temp


        // 이벤트 기반 영상 클립 자르기 및 업로드
        val uploadS3 = UploadS3(this)
        val recordedFile = cameraStreamer.getRecordedFile() ?: return
        val eventList = cameraStreamer.getEventList()

        val outputDir = File(filesDir, "clips").apply { mkdirs() }
        val clipList = mutableListOf<Triple<String, Long, File>>()

        eventList.forEachIndexed { index, (result, timestamp) ->
            val outputClip = File(outputDir, "clip_${index}_$result.mp4")
            val startSec = (timestamp - 5000).coerceAtLeast(0) / 1000  // 앞 5초 (초 단위)
            val durationSec = 12L  // 총 12초

            val success = uploadS3.cutVideoClip(recordedFile, outputClip, startSec, durationSec)
            if (success) {
                clipList.add(Triple(result, timestamp, outputClip))
            }
        }

        // 백엔드에 user_id, history_id 포함해 업로드 호출
        val userId = intent.getIntExtra("user_id", -1)
        val historyId = intent.getIntExtra("history_id", -1)
        uploadS3.uploadClipBatch(clipList, userId, historyId)
    }

    override fun guidanceRouteUnchangedWithError(aGuidance: KNGuidance, aError: KNError) {
        naviView.guidanceRouteUnchangedWithError(aGuidance, aError)
    }

    override fun guidanceDidUpdateLocation(aGuidance: KNGuidance, aLocationGuide: KNGuide_Location) {
        naviView.guidanceDidUpdateLocation(aGuidance, aLocationGuide)
    }

    override fun guidanceDidUpdateRouteGuide(aGuidance: KNGuidance, aRouteGuide: KNGuide_Route) {
        naviView.guidanceDidUpdateRouteGuide(aGuidance, aRouteGuide)
    }

    override fun guidanceDidUpdateAroundSafeties(aGuidance: KNGuidance, aSafeties: List<KNSafety>?) {
        naviView.guidanceDidUpdateAroundSafeties(aGuidance, aSafeties)
    }

    override fun guidanceDidUpdateSafetyGuide(aGuidance: KNGuidance, aSafetyGuide: KNGuide_Safety?) {
        naviView.guidanceDidUpdateSafetyGuide(aGuidance, aSafetyGuide)
    }

    override fun didFinishPlayVoiceGuide(aGuidance: KNGuidance, aVoiceGuide: KNGuide_Voice) {
        naviView.didFinishPlayVoiceGuide(aGuidance, aVoiceGuide)
    }

    override fun shouldPlayVoiceGuide(
        aGuidance: KNGuidance,
        aVoiceGuide: KNGuide_Voice,
        aNewData: MutableList<ByteArray>
    ): Boolean {
        if (aVoiceGuide.voiceCode == KNVoiceCode.KNVoiceCode_Turn) {
            val direction = aVoiceGuide.guideObj as? KNDirection ?: return true

            when (direction.rgCode) {
                KNRGCode.KNRGCode_LeftTurn -> {
                    tts.speak("좌측 깜빡이를 켜세요. 교차로 내에서는 자기 차선대로 좌회전하세요. 유도선이 있다면 유도선을 따라 회전하세요.", TextToSpeech.QUEUE_FLUSH, null, null)
                    return false
                }
                KNRGCode.KNRGCode_RightTurn -> {
                    tts.speak("우측 깜빡이를 켜세요. 우회전하기 전, 좌측이나 정면에서 오는 차가 있는지 확인하세요. 보행자가 있는지 확인하세요.", TextToSpeech.QUEUE_FLUSH, null, null)
                    return false
                }
                KNRGCode.KNRGCode_UTurn -> {
                    tts.speak("좌측 깜빡이를 켜세요. 정면 신호가 좌회전/보행자/직진 신호일 때 유턴하여 3차선으로 들어가세요. 유턴 구간에서는 앞차의 뒤를 따라 순서대로 돌아야 합니다.", TextToSpeech.QUEUE_FLUSH, null, null)
                    return false
                }
                else -> {
                    Log.d("VoiceGuide", "기타 rgCode: ${direction.rgCode}")
                }
            }
        }

        return true
    }

    override fun willPlayVoiceGuide(aGuidance: KNGuidance, aVoiceGuide: KNGuide_Voice) {
        naviView.willPlayVoiceGuide(aGuidance, aVoiceGuide)
    }

    override fun didUpdateCitsGuide(aGuidance: KNGuidance, aCitsGuide: KNGuide_Cits) {
        naviView.didUpdateCitsGuide(aGuidance, aCitsGuide)
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedClient.removeLocationUpdates(locationCallback)
        gpsSpeedMonitor.stop()

        val endDateTime = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(Date())
        naviViewModel.setEndAt(endDateTime)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = naviViewModel.saveDriveResultDirect()
                Log.d("NaviActivity", "✅ 주행 결과 저장 성공: $result")
            } catch (e: Exception) {
                Log.e("NaviActivity", "❌ 주행 결과 저장 실패: ${e.message}", e)
            }
        }

    }
}
