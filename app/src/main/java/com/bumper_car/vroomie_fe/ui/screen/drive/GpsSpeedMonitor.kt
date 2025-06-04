package com.bumper_car.vroomie_fe.ui.screen.drive

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.*

class GpsSpeedMonitor(
    context: Context,
    private val onSuddenAccel: () -> Unit,
    private val onSuddenDecel: () -> Unit
) {

    private val locationClient = LocationServices.getFusedLocationProviderClient(context)
    private var lastSpeed: Float? = null
    private var lastTime: Long? = null

    private val SPEED_THRESHOLD = 8.33f    // 30 km/h in m/s
    private val TIME_WINDOW_MS = 3000L     // 3초 이내 변화 감지

    private val locationRequest = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY,
        1000L  // 1초 간격 측정
    ).build()

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            val location = result.lastLocation ?: return
            val currentSpeed = location.speed  // m/s
            val currentTime = System.currentTimeMillis()

            if (lastSpeed != null && lastTime != null) {
                val deltaV = currentSpeed - lastSpeed!!
                val deltaT = currentTime - lastTime!!

                if (deltaT <= TIME_WINDOW_MS) {
                    if (deltaV >= SPEED_THRESHOLD) {
                        Log.d("GpsSpeedMonitor", "🚀 급가속 감지됨: Δv=${deltaV}m/s, Δt=${deltaT}ms")
                        onSuddenAccel()
                    } else if (deltaV <= -SPEED_THRESHOLD) {
                        Log.d("GpsSpeedMonitor", "🛑 급감속 감지됨: Δv=${deltaV}m/s, Δt=${deltaT}ms")
                        onSuddenDecel()
                    }
                }
            }

            lastSpeed = currentSpeed
            lastTime = currentTime
        }
    }

    @SuppressLint("MissingPermission")
    fun start() {
        locationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        Log.d("GpsSpeedMonitor", "✅ GPS 속도 감지 시작됨")
    }

    fun stop() {
        locationClient.removeLocationUpdates(locationCallback)
        Log.d("GpsSpeedMonitor", "🛑 GPS 속도 감지 중단됨")
    }
}