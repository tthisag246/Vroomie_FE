package com.bumper_car.vroomie_fe.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.location.Location
import android.util.Base64
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicLong

class CameraStreamer(
    private val context: Context,
    private val previewView: PreviewView,
    private val wsUrl: String
) {
    private lateinit var webSocket: WebSocket
    private val executor = Executors.newSingleThreadExecutor()
    private val lastSentTimeMillis = AtomicLong(0)
    private val targetFrameIntervalMillis = 500L // 2 FPS

    @Volatile
    private var currentSpeedKph: Float = 0f

    fun updateSpeedFromLocation(location: Location) {
        val speedMps = location.speed
        currentSpeedKph = speedMps * 3.6f
    }

    fun startWebSocket() {
        val client = OkHttpClient()
        val request = Request.Builder().url(wsUrl).build()
        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(ws: WebSocket, response: Response) {
                Log.d("CameraStreamer", "WebSocket 데이터 전송 준비 완료")
            }

            override fun onFailure(ws: WebSocket, t: Throwable, response: Response?) {
                Log.e("CameraStreamer", "WebSocket 에러: ${t.message}")
                t.printStackTrace()
            }
        })
    }

    fun startPreviewOnly(lifecycleOwner: LifecycleOwner) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }
            val selector = CameraSelector.DEFAULT_BACK_CAMERA
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(lifecycleOwner, selector, preview)
        }, ContextCompat.getMainExecutor(context))
    }

    fun startStreaming(lifecycleOwner: LifecycleOwner) {
        startWebSocket()
        bindCameraWithStream(lifecycleOwner)
    }

    @SuppressLint("UnsafeOptInUsageError")
    fun bindCameraWithStream(lifecycleOwner: LifecycleOwner) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(executor) { imageProxy ->
                        val currentTimeMillis = System.currentTimeMillis()
                        val lastTime = lastSentTimeMillis.get()

                        if (currentTimeMillis - lastTime >= targetFrameIntervalMillis) {
                            lastSentTimeMillis.set(currentTimeMillis)
                            val bitmap = imageProxy.toBitmap()
                            val baos = ByteArrayOutputStream()
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos)
                            val base64 = Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP)

                            val payloadJson = JSONObject().apply {
                                put("frame", base64)
                                put("speed", currentSpeedKph)
                            }.toString()

                            webSocket.send(payloadJson)
                            Log.d("CameraStreamer", "프레임+속도 전송 중... ($currentSpeedKph km/h)")
                        }
                        imageProxy.close()
                    }
                }

            val selector = CameraSelector.DEFAULT_BACK_CAMERA
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(lifecycleOwner, selector, preview, imageAnalyzer)
        }, ContextCompat.getMainExecutor(context))
    }
}
