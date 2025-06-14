package com.bumper_car.vroomie_fe.ui.screen.login

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.bumper_car.vroomie_fe.BuildConfig
import com.bumper_car.vroomie_fe.R

@Composable
fun LoginScreen(
    navController: NavHostController,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val intent = activity?.intent
    val handled = remember { mutableStateOf(false) }

    fun onClickKakaoLoginButton() {
        val clientId = BuildConfig.KAKAO_REST_API_KEY
        val redirectUri = "http://${BuildConfig.SERVER_IP_ADDRESS}:8080/auth/kakao/callback"
        val url = "https://kauth.kakao.com/oauth/authorize" +
                "?client_id=$clientId" +
                "&redirect_uri=$redirectUri" +
                "&response_type=code"

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    }

    LaunchedEffect(intent?.data) {
        if (!handled.value) {
            val uri = intent?.data
            if (uri?.scheme == "vroomie" && uri.host == "login-success") {
                val token = uri.getQueryParameter("token") ?: return@LaunchedEffect

                viewModel.saveToken(token) // 토큰 저장

                // ViewModel에서 사용자 추가 정보 등록 여부 확인 요청
                val isExtraInfoRegistered = viewModel.checkIfExtraInfoExists() // 가상의 ViewModel 함수

                if (isExtraInfoRegistered) {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                        launchSingleTop = true
                    }
                } else {
                    navController.navigate("extra_info") {
                        popUpTo("login") { inclusive = true }
                        launchSingleTop = true
                    }
                }
                handled.value = true
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        // 앱 로고
        Image(
            painter = painterResource(id = R.mipmap.ic_launcher_foreground),
            contentDescription = "Vroomie",
            modifier = Modifier.size(320.dp),
            alignment = Alignment.Center
        )

        // 카카오 로그인 버튼
        Image(
            painter = painterResource(id = R.drawable.icon_kakao_login),
            contentDescription = "카카오 로그인",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 60.dp)
                .padding(bottom = 100.dp)
                .align(Alignment.BottomCenter)
                .clickable { onClickKakaoLoginButton() }
        )
    }
}