import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

        plugins {
            alias(libs.plugins.android.application)
            alias(libs.plugins.kotlin.android)
            alias(libs.plugins.kotlin.compose)
            alias(libs.plugins.kotlin.android.ksp)
            alias(libs.plugins.hilt.android)
            id("kotlin-kapt")
            id("dagger.hilt.android.plugin")
        }

val localProperties = gradleLocalProperties(rootDir, providers)
val kakaoAppKey = localProperties.getProperty("KAKAO_APP_KEY") ?: ""
val kakaoRestApiKey = localProperties.getProperty("KAKAO_REST_API_KEY") ?: ""
val serverIPAddress = localProperties.getProperty("SERVER_IP_ADDRESS") ?: ""

android {
    namespace = "com.bumper_car.vroomie_fe"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.bumper_car.vroomie_fe"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "KAKAO_APP_KEY", "\"$kakaoAppKey\"")
        buildConfigField("String", "KAKAO_REST_API_KEY", "\"$kakaoRestApiKey\"")
        buildConfigField("String", "SERVER_IP_ADDRESS", "\"$serverIPAddress\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
        viewBinding = true
        buildConfig = true
    }
}

configurations.all {
    resolutionStrategy {
        force("androidx.test.ext:junit:1.1.5")
    }
}

dependencies {
    // 카카오 내비
    implementation("com.kakaomobility.knsdk:knsdk_ui:1.9.4")

    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("androidx.compose.material3:material3:1.2.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.6.4")

    implementation("androidx.camera:camera-core:1.3.1")
    implementation("androidx.camera:camera-camera2:1.3.1")
    implementation("androidx.camera:camera-lifecycle:1.3.1")
    implementation("androidx.camera:camera-view:1.3.1")
    implementation("androidx.camera:camera-extensions:1.3.1")

    implementation("androidx.datastore:datastore-preferences:1.0.0")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose.android)
    implementation(libs.datastore.preferences)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter)
    implementation(libs.hilt.navigation)
    implementation(libs.hilt.android)
    implementation(libs.androidx.foundation.layout.android)
    implementation(libs.core.splashscreen)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    implementation(libs.compose.markdown)
    implementation(libs.androidx.media3.ui)
    implementation(libs.androidx.media3.exoplayer)

    ksp(libs.hilt.compiler)

    testImplementation(libs.junit)

    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

hilt {
    enableAggregatingTask = true
}