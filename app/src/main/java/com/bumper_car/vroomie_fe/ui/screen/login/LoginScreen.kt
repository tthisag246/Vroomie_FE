package com.bumper_car.vroomie_fe.ui.screen.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bumper_car.vroomie_fe.R

@Composable
fun LoginScreen(
    navController: NavHostController
) {
    fun onClickKakaoLoginButton() {
        /* TODO: 카카오로그인 */
        navController.navigate("home") {
            popUpTo("login") { inclusive = true }
            launchSingleTop = true
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
            modifier = Modifier
                .size(320.dp),
            alignment = Alignment.Center
        )
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