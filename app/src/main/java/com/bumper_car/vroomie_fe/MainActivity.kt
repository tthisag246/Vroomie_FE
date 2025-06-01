package com.bumper_car.vroomie_fe

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.bumper_car.vroomie_fe.navigation.AppNavHost
import com.bumper_car.vroomie_fe.ui.theme.Vroomie_FETheme
import com.bumper_car.util.VoiceService
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var splashScreen: SplashScreen

    // ✅ 최신 방식의 권한 요청 Launcher
    private val requestAudioPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                startVoiceService()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        splashScreen = installSplashScreen()

        // ✅ 마이크 권한 확인 후 VoiceService 실행
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            == PackageManager.PERMISSION_GRANTED
        ) {
            startVoiceService()
        } else {
            requestAudioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }

        setContent {
            Vroomie_FETheme {
                val navController = rememberNavController()
                AppNavHost(navController)
            }
        }
    }

    // ✅ VoiceService 시작 함수
    private fun startVoiceService() {
        val intent = Intent(this, VoiceService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }
}