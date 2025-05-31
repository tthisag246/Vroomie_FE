package com.bumper_car.vroomie_fe.util

import android.content.Context
import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
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
    private val targetFrameIntervalMillis = 500L  // 2 FPS → 500ms 마다 1프레임



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
                            // 업데이트
                            lastSentTimeMillis.set(currentTimeMillis)
                            val bitmap = imageProxy.toBitmap()
                            val baos = ByteArrayOutputStream()
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos)
                            val base64 = Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP)
                            Log.d("CameraStreamer", "프레임 전송 중...")

                            webSocket.send(base64)
                        }
                        imageProxy.close()
                    }
                }

            val selector = CameraSelector.DEFAULT_BACK_CAMERA
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(lifecycleOwner, selector, preview, imageAnalyzer)
        }, ContextCompat.getMainExecutor(context))
    }

    private fun ImageProxy.toBitmap(): Bitmap {
        val yPlane = planes[0].buffer
        val uPlane = planes[1].buffer
        val vPlane = planes[2].buffer
        val yRowStride = planes[0].rowStride
        val uvRowStride = planes[1].rowStride
        val uvPixelStride = planes[1].pixelStride
        val width = width
        val height = height
        val argb = IntArray(width * height)

        var index = 0
        for (j in 0 until height) {
            val pY = yRowStride * j
            val uvRow = uvRowStride * (j shr 1)

            for (i in 0 until width) {
                val uvOffset = uvRow + (i shr 1) * uvPixelStride
                val y = 0xff and yPlane.get(pY + i).toInt()
                val u = 0xff and uPlane.get(uvOffset).toInt()
                val v = 0xff and vPlane.get(uvOffset).toInt()
                val r = (y + 1.370705f * (v - 128)).toInt().coerceIn(0, 255)
                val g = (y - 0.337633f * (u - 128) - 0.698001f * (v - 128)).toInt().coerceIn(0, 255)
                val b = (y + 1.732446f * (u - 128)).toInt().coerceIn(0, 255)
                argb[index++] = -0x1000000 or (r shl 16) or (g shl 8) or b
            }
        }
        return Bitmap.createBitmap(argb, width, height, Bitmap.Config.ARGB_8888)
    }
}
