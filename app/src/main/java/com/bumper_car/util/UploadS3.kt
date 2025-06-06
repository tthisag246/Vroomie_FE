package com.bumper_car.util

import android.content.Context
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMuxer
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import com.bumper_car.vroomie_fe.BuildConfig
import okhttp3.RequestBody.Companion.toRequestBody

class UploadS3(private val context: Context) {

    fun uploadClipBatch(
        clipList: List<Triple<String, Long, File>>,  // result, timestamp, file
        userId: Int,
        historyId: Int
    ) {
        if (clipList.isEmpty()) {
            Log.d("UploadS3", "클립 리스트가 비어 있어 업로드 생략")
            return
        }
        if (userId == -1 || historyId == -1) {
            Log.e("UploadS3", "❌ 유효하지 않은 userId/historyId: $userId, $historyId")
            return
        }
        CoroutineScope(Dispatchers.IO).launch {
            val client = OkHttpClient()
            val backendList = JSONArray()

            clipList.forEachIndexed { index, (result, timestamp, clipFile) ->
                try {
                    val requestBody = clipFile.asRequestBody("video/mp4".toMediaTypeOrNull())
                    val fileName = "uploads/${clipFile.name}"

                    val request = Request.Builder()
                        .url("https://bumpercar-vroomie.s3.ap-northeast-2.amazonaws.com/$fileName")
                        .put(requestBody)
                        .build()

                    val response = client.newCall(request).execute()
                    if (!response.isSuccessful) throw Exception("S3 업로드 실패: ${response.code}")

                    val s3Url = "https://bumpercar-vroomie.s3.ap-northeast-2.amazonaws.com/$fileName"
                    Log.d("UploadS3", "업로드 성공: $s3Url")

                    val jsonObject = JSONObject().apply {
                        put("user_id", userId)
                        put("history_id", historyId)
                        put("s3_url", s3Url)
                        put("result", result)
                    }
                    backendList.put(jsonObject)
                } catch (e: Exception) {
                    Log.e("UploadS3", "업로드 실패: ${e.message}")
                }
            }


            val finalRequestBody = okhttp3.RequestBody.create(
                "application/json".toMediaTypeOrNull(), backendList.toString()
            )

            val backendRequest = Request.Builder()
                .url("http://${BuildConfig.SERVER_IP_ADDRESS}:8080/drive/video/save")
                .post(finalRequestBody)
                .build()

            try {
                val backendResponse = client.newCall(backendRequest).execute()
                Log.d("UploadS3", "DB 저장 응답: ${backendResponse.code}")
            } catch (e: Exception) {
                Log.e("UploadS3", "백엔드 저장 실패: ${e.message}")
            }
        }
    }

    fun cutVideoClip(source: File, output: File, startSec: Long, durationSec: Long): Boolean {
        return try {
            val extractor = MediaExtractor()
            extractor.setDataSource(source.absolutePath)
            Log.d("UploadS3", "✅ setDataSource 성공: ${source.absolutePath}")
            Log.d("UploadS3", "📁 자르기 대상 파일 경로: ${source.absolutePath}, 존재 여부: ${source.exists()}, 크기: ${source.length()} bytes")
            val trackCount = extractor.trackCount
            Log.d("UploadS3", "🎞 trackCount = $trackCount")

            for (i in 0 until trackCount) {
                val format = extractor.getTrackFormat(i)
                val mime = format.getString(MediaFormat.KEY_MIME)
                Log.d("UploadS3", "🎞 트랙 $i MIME 타입: $mime")
            }

            extractor.selectTrack(0)
            val format = extractor.getTrackFormat(0)
            val muxer = MediaMuxer(output.absolutePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
            val trackIndex = muxer.addTrack(format)
            muxer.start()

            val maxUs = (startSec + durationSec) * 1_000_000
            extractor.seekTo(startSec * 1_000_000, MediaExtractor.SEEK_TO_CLOSEST_SYNC)

            Log.d("UploadS3", "요청한 자르기 시작 시간: ${startSec}s")
            val actualStartTimeSec = extractor.sampleTime / 1_000_000
            Log.d("UploadS3", "🎥 실제 시작 sampleTime: ${actualStartTimeSec}s")


            val bufferSize = 1 * 1024 * 1024
            val byteBuffer = java.nio.ByteBuffer.allocate(bufferSize)

            while (true) {
                val sampleTime = extractor.sampleTime
                if (sampleTime == -1L || sampleTime > maxUs) break

                val sampleSize = extractor.readSampleData(byteBuffer, 0)
                if (sampleSize < 0) break

                val info = android.media.MediaCodec.BufferInfo().apply {
                    offset = 0
                    size = sampleSize
                    presentationTimeUs = sampleTime
                    flags = extractor.sampleFlags
                }
                muxer.writeSampleData(trackIndex, byteBuffer, info)
                extractor.advance()
            }

            muxer.stop()
            muxer.release()
            extractor.release()
            Log.d("UploadS3", "영상 자르기 완료: ${output.name}")
            true
        } catch (e: Exception) {
            Log.e("UploadS3", "영상 자르기 실패: ${e.message}")
            false
        }
    }


    /*
    fun testFakeEventAndUpload(
        context: Context,
        fullVideoFile: File,
        userId: Int,
        historyId: Int
    ) {
        val outputDir = File(context.filesDir, "clips").apply { mkdirs() }

        // 🟡 테스트용 이벤트 3개 (3초, 6초, 9초에 이벤트 발생했다고 가정)
        val fakeEvents = listOf(
            Triple("Left_Deviation", 20_000L, File(outputDir, "clip_0_Left_Deviation.mp4")),
            Triple("Right_Deviation", 20_000L, File(outputDir, "clip_1_Right_Deviation.mp4")),
            Triple("Speeding", 20_000L, File(outputDir, "clip_2_Speeding.mp4"))
        )

        val clipList = mutableListOf<Triple<String, Long, File>>()

        fakeEvents.forEach { (result, timestamp, outputClip) ->
            val startSec = (timestamp - 5000).coerceAtLeast(0) / 1000  // 앞 5초
            val durationSec = 12L  // 총 12초 (앞 5초 + 뒤 7초)
            val success = cutVideoClip(fullVideoFile, outputClip, startSec, durationSec)
            if (success) {
                clipList.add(Triple(result, timestamp, outputClip))
            }
        }

        uploadClipBatch(clipList, userId, historyId)
    }


    fun copyVideoFromAssets(context: Context) {
        val assetManager = context.assets
        val inputStream = assetManager.open("sample_test_video.mp4")
        val outputFile = File(context.filesDir, "videos/drive_test.mp4")
        outputFile.parentFile?.mkdirs()
        inputStream.use { input ->
            outputFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        Log.d("UploadS3", "assets -> files/videos/drive_test.mp4 복사 완료")
    }*/
}
