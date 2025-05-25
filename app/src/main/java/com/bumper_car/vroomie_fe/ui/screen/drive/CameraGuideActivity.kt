package com.bumper_car.vroomie_fe.ui.screen.drive

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumper_car.vroomie_fe.BuildConfig
import com.bumper_car.vroomie_fe.R
import com.bumper_car.vroomie_fe.util.CameraStreamer

class CameraGuideActivity : ComponentActivity() {

    private lateinit var previewView: PreviewView
    private lateinit var cameraStreamer: CameraStreamer

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                initCamera()
            } else {
                Toast.makeText(this, "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_guide)

        previewView = findViewById(R.id.preview_view)
        val button = findViewById<Button>(R.id.btn_focus_done)
        button.isEnabled = false

        // 내비게이션 바 인셋 적용
        ViewCompat.setOnApplyWindowInsetsListener(button) { view, insets ->
            val navBarHeight = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            view.setPadding(
                view.paddingLeft,
                view.paddingTop,
                view.paddingRight,
                navBarHeight + view.paddingBottom
            )
            insets
        }

        // 권한 체크
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            initCamera()
            button.isEnabled = true
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        // 안내 시작 버튼 눌렀을 때 → 웹소켓 + 스트리밍 시작
        button.setOnClickListener {
            cameraStreamer.startStreaming(this)

            val lat = intent.getDoubleExtra("lat", -1.0)
            val lon = intent.getDoubleExtra("lon", -1.0)
            val name = intent.getStringExtra("name")

            val intent = Intent(this, NaviActivity::class.java).apply {
                putExtra("lat", lat)
                putExtra("lon", lon)
                putExtra("name", name)
            }
            startActivity(intent)
            finish()
        }
    }

    private fun initCamera() {
        cameraStreamer = CameraStreamer(
            context = this,
            previewView = previewView,
            wsUrl = "ws://${BuildConfig.SERVER_IP_ADDRESS}:8080" // 🛠 실제 서버 주소로 변경
        )
        cameraStreamer.startPreviewOnly(this) // 🔹 미리보기만 먼저 실행
    }
}
