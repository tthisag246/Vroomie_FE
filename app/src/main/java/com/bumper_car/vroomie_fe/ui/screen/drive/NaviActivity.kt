package com.bumper_car.vroomie_fe.ui.screen.drive

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumper_car.util.UploadS3
import com.bumper_car.vroomie_fe.BuildConfig
import com.bumper_car.vroomie_fe.R
import com.bumper_car.vroomie_fe.Vroomie_FEApplication
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
import org.json.JSONObject
import java.io.File
import java.util.Locale

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navi)

        // TTS 초기화
        tts = TextToSpeech(this) {
            if (it == TextToSpeech.SUCCESS) {
                tts.language = Locale.KOREAN
            }
        }

        // 가속도 감지
        gpsSpeedMonitor = GpsSpeedMonitor(
            context = this,
            onSuddenAccel = {
                val key = "Sudden_Accel"
                if (isCooldownPassed(key) && !tts.isSpeaking) {
                    tts.speak("급가속 했어요. 브레이크를 미리미리 준비하며 부드럽게 가속해보세요.", TextToSpeech.QUEUE_FLUSH, null, key)
                }
                Log.d("DrivingEvent", "🚀 급가속 감지됨")
            },
            onSuddenDecel = {
                val key = "Sudden_Decel"
                if (isCooldownPassed(key) && !tts.isSpeaking) {
                    tts.speak("급감속 했어요. 미리 주변 상황을 보고 브레이크를 여유있게 밟아보세요.", TextToSpeech.QUEUE_FLUSH, null, key)
                }
                Log.d("DrivingEvent", "🛑 급감속 감지됨")
            }
        )
        gpsSpeedMonitor.start()

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

        val lat = intent.getDoubleExtra("lat", -1.0)
        val lon = intent.getDoubleExtra("lon", -1.0)
        val placeName = intent.getStringExtra("name") ?: "목적지"

        if (lat == -1.0 || lon == -1.0) {
            Toast.makeText(this, "좌표 정보가 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val katecPoint = KNSDK.convertWGS84ToKATEC(lon, lat)
        val goalPoi = KNPOI(placeName, katecPoint.x.toInt(), katecPoint.y.toInt(), placeName)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(this, "위치 권한이 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        fusedClient = LocationServices.getFusedLocationProviderClient(this)
        fusedClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location ->
                if (location != null) {
                    val katecPoint = KNSDK.convertWGS84ToKATEC(location.longitude, location.latitude)
                    val startPoi = KNPOI("현재 위치", katecPoint.x.toInt(), katecPoint.y.toInt(), "출발지")

                    val guidance = Vroomie_FEApplication.knsdk.sharedGuidance()

                    if (guidance == null) {
                        Toast.makeText(this, "SDK 초기화 안됨", Toast.LENGTH_SHORT).show()
                        Log.e("NaviActivity", "KNSDK not initialized")
                        return@addOnSuccessListener
                    }

                    guidance.stop()

                    Vroomie_FEApplication.knsdk.makeTripWithStart(
                        aStart = startPoi,
                        aGoal = goalPoi,
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

        // ✅ GPS 위치 업데이트 설정
        fusedClient = LocationServices.getFusedLocationProviderClient(this)

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000L) // 1초마다
            .setMinUpdateDistanceMeters(1.0f) // 1m 이상 이동해야 반응
            .build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val location = result.lastLocation ?: return
                cameraStreamer.updateSpeedFromLocation(location)
                Log.d("GPS", "속도 갱신됨: ${location.speed * 3.6f} km/h")
            }
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedClient.requestLocationUpdates(locationRequest, locationCallback, mainLooper)
        } else {
            Toast.makeText(this, "위치 권한 필요", Toast.LENGTH_SHORT).show()
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
                    tts.speak(
                        "차로의 왼쪽으로 치우쳤어요! 오른발이 도로의 중앙에 떠있는 듯한 지점에 맞추고 시야를 멀리 두세요.",
                        TextToSpeech.QUEUE_FLUSH, null, null
                    )
                }

                "Right_Deviation" -> {
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

        gpsSpeedMonitor.stop()

    }
}
