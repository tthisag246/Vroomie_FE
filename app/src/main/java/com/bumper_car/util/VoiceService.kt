package com.bumper_car.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.core.app.NotificationCompat
import com.bumper_car.vroomie_fe.R
import com.bumper_car.vroomie_fe.data.remote.RetrofitInstance
import com.bumper_car.vroomie_fe.data.remote.gpt.GptApi
import com.bumper_car.vroomie_fe.data.remote.gpt.GptRequest
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

@AndroidEntryPoint
class VoiceService : Service() {

    private lateinit var recognizer: SpeechRecognizer
    private lateinit var tts: TextToSpeech
    private var isTtsReady = false
    private var isListening = false

    override fun onCreate() {
        super.onCreate()
        setupForegroundNotification()
        initTTS()
        CoroutineScope(Dispatchers.Main).launch {
            startWakeWordListening()
        }
    }

    private fun setupForegroundNotification() {
        val channelId = "voice_service_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Voice Service", NotificationManager.IMPORTANCE_LOW)
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Vroomie Listening...")
            .setContentText("사용자의 호출을 기다리고 있어요.")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()

        startForeground(1, notification)
    }

    private fun initTTS() {
        CoroutineScope(Dispatchers.IO).launch {
            tts = TextToSpeech(this@VoiceService) {
                if (it == TextToSpeech.SUCCESS) {
                    tts.language = Locale.KOREAN
                    isTtsReady = true
                }
            }
        }
    }

    private fun startWakeWordListening() {
        recognizer = SpeechRecognizer.createSpeechRecognizer(this)
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")
        }

        recognizer.setRecognitionListener(object : RecognitionListener {
            override fun onResults(results: Bundle?) {
                val sentence = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull().orEmpty()
                Log.d("VoiceService", "🎙️ 인식: $sentence")

                if (!isListening && "부르미" in sentence) {
                    isListening = true
                    Handler(Looper.getMainLooper()).post {
                        speak("네, 말씀하세요")
                        startQuestionListening()
                    }
                } else {
                    startWakeWordListening()
                }
            }

            override fun onError(error: Int) {
                Log.w("VoiceService", "👂 인식 실패 또는 오류: $error")
                Handler(Looper.getMainLooper()).postDelayed({ startWakeWordListening() }, 1500)
            }

            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        recognizer.startListening(intent)
    }

    private fun startQuestionListening() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")
        }

        recognizer.setRecognitionListener(object : RecognitionListener {
            override fun onResults(results: Bundle?) {
                val question = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull().orEmpty()
                Log.d("VoiceService", "❓ 질문 인식: $question")
                askGptAndSpeak(question)
                isListening = false
                startWakeWordListening()
            }

            override fun onError(error: Int) {
                Log.w("VoiceService", "❌ 질문 인식 실패: $error")
                isListening = false
                startWakeWordListening()
            }

            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        recognizer.startListening(intent)
    }

    private fun speak(text: String) {
        if (isTtsReady) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        } else {
            Log.w("VoiceService", "TTS 준비 안됨")
        }
    }

    private fun askGptAndSpeak(message: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val api = RetrofitInstance.create<GptApi>()
                val response = api.ask(GptRequest(message))
                val answer = response.body()?.reply ?: "죄송해요, 답변을 받을 수 없어요."
                Log.d("VoiceService", "🗣️ GPT 응답: $answer")

                withContext(Dispatchers.Main) {
                    speak(answer)
                }
            } catch (e: Exception) {
                Log.e("VoiceService", "GPT 통신 실패: ${e.message}")
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}