package com.bumper_car.vroomie_fe.ui.screen.drive

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumper_car.vroomie_fe.R
import com.google.common.util.concurrent.ListenableFuture

class CameraGuideActivity : ComponentActivity() {

    private lateinit var previewView: PreviewView
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                startCamera()
                findViewById<Button>(R.id.btn_focus_done).isEnabled = true
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

        // ✅ 버튼에만 인셋 적용
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

        // 권한 확인
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
            button.isEnabled = true
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        button.setOnClickListener {
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

    private fun startCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().apply {
                setSurfaceProvider(previewView.surfaceProvider)
            }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(this, cameraSelector, preview)
        }, ContextCompat.getMainExecutor(this))
    }
}
