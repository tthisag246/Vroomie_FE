package com.bumper_car.vroomie_fe.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.location.Location
import android.media.MediaRecorder
import android.util.Base64
import android.util.Log
import android.view.Surface
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
import java.io.File
import java.text.SimpleDateFormat
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicLong
import java.util.*
import androidx.camera.video.*

class CameraStreamer(
    private val context: Context,
    private val previewView: PreviewView,
    private val wsUrl: String
) {
    private lateinit var webSocket: WebSocket
    private val executor = Executors.newSingleThreadExecutor()
    private val lastSentTimeMillis = AtomicLong(0)
    private val targetFrameIntervalMillis = 500L // 2 FPS
    private var messageListener: ((String) -> Unit)? = null

    private var recordingFile: File? = null
    private val eventList = mutableListOf<Pair<String, Long>>()

    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null
    private var recordingStartTimeMillis: Long = 0


    fun getRecordedFile(): File? = recordingFile
    fun getEventList(): List<Pair<String, Long>> = eventList

    @Volatile
    private var currentSpeedKph: Float = 0f

    fun setOnMessageListener(listener: (String) -> Unit) {
        messageListener = listener
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

            override fun onMessage(ws: WebSocket, text: String) {
                Log.d("CameraStreamer", "서버에서 메시지 수신됨: $text")
                try {
                    val json = JSONObject(text)
                    val event = json.getString("event")
                    val timestamp = System.currentTimeMillis()
                    val relativeTime = timestamp - recordingStartTimeMillis
                    eventList.add(event to relativeTime)
                } catch (e: Exception) {
                    Log.e("CameraStreamer", "이벤트 파싱 실패: ${e.message}")
                }
                messageListener?.invoke(text)
            }
        })
    }

    //temp
    fun stopWebSocket() {
        try {
            webSocket.close(1000, "Activity destroyed")  // 정상 종료 코드 1000 사용
            Log.d("CameraStreamer", "WebSocket 정상 종료")
        } catch (e: Exception) {
            Log.e("CameraStreamer", "WebSocket 종료 중 오류: ${e.message}")
        }
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
        bindCameraWithStreamAndRecording(lifecycleOwner) {
            startRecording(lifecycleOwner)
        }
    }

    fun updateSpeedFromKakaoSdk(speedFromSdk: Int, trust: Boolean) {
        if (trust) {
            currentSpeedKph = speedFromSdk.toFloat()
        }
    }

    fun bindCameraWithStreamAndRecording(
        lifecycleOwner: LifecycleOwner,
        onInitialized: (() -> Unit)? = null
    ) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            val recorder = Recorder.Builder()
                .setQualitySelector(QualitySelector.from(Quality.HD))
                .build()

            videoCapture = VideoCapture.withOutput(recorder)

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
            cameraProvider.bindToLifecycle(lifecycleOwner, selector, preview, videoCapture!!, imageAnalyzer)

            // ✅ 이 시점에 videoCapture 준비 완료
            onInitialized?.invoke()

        }, ContextCompat.getMainExecutor(context))
    }

    fun startRecording(lifecycleOwner: LifecycleOwner) {
        recordingStartTimeMillis = System.currentTimeMillis()
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val outputDir = File(context.filesDir, "videos").apply { mkdirs() }
        val outputFile = File(outputDir, "drive_$timestamp.mp4")
        recordingFile = outputFile

        val outputOptions = FileOutputOptions.Builder(outputFile).build()

        val hasAudioPermission = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.RECORD_AUDIO
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED

        val pendingRecording = videoCapture?.output
            ?.prepareRecording(context, outputOptions)

        recording = pendingRecording // 👈 오디오 비활성화
            ?.start(ContextCompat.getMainExecutor(context)) { recordEvent ->
                when (recordEvent) {
                    is VideoRecordEvent.Start -> Log.d("CameraStreamer", "🎥 녹화 시작됨")
                    is VideoRecordEvent.Finalize -> Log.d("CameraStreamer", "✅ 녹화 완료됨: ${outputFile.absolutePath}")
                }
            }
        /*
        recording = if (hasAudioPermission) {
            pendingRecording?.withAudioEnabled()
        } else {
            Log.w("CameraStreamer", "🎤 오디오 권한이 없어 음성 없이 녹화합니다")
            pendingRecording
        }?.start(ContextCompat.getMainExecutor(context)) { recordEvent ->
            when (recordEvent) {
                is VideoRecordEvent.Start -> Log.d("CameraStreamer", "🎥 녹화 시작됨")
                is VideoRecordEvent.Finalize -> Log.d("CameraStreamer", "✅ 녹화 완료됨: ${outputFile.absolutePath}")
            }
        }*/
    }

    fun stopRecording() {
        try {
            recording?.stop()
            recording = null
            Log.d("CameraStreamer", "📴 녹화 중지 호출됨")
        } catch (e: Exception) {
            Log.e("CameraStreamer", "녹화 중지 실패: ${e.message}")
        }
    }


}


