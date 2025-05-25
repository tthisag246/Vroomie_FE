package com.bumper_car.vroomie_fe

import android.app.Application
import android.util.Log
import com.kakao.sdk.common.util.Utility
import com.kakaomobility.knsdk.KNLanguageType
import com.kakaomobility.knsdk.KNSDK
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class Vroomie_FEApplication : Application() {
    companion object {
        lateinit var knsdk: KNSDK
    }

    override fun onCreate() {
        super.onCreate()

        knsdk = KNSDK.apply {
            install(
                this@Vroomie_FEApplication,
                "$filesDir/KNSample",
            )

            initializeWithAppKey(
                BuildConfig.KAKAO_APP_KEY,
                "1.0.0",
                "test-user", // 실제 유저 ID
                KNLanguageType.KNLanguageType_KOREAN
            ) { error ->
                if (error != null) {
                    Log.e("KNSDK", "초기화 실패: ${error.msg}")
                } else {
                    Log.d("KNSDK", "초기화 성공")
                }
            }
        }
    }
}
