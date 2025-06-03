package com.bumper_car.vroomie_fe.ui.screen.drive

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navi)

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
        return naviView.shouldPlayVoiceGuide(aGuidance, aVoiceGuide, aNewData)
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
    }
}
