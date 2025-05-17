package com.bumper_car.vroomie_fe.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.bumper_car.vroomie_fe.ui.screen.home.HomeScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Login : Screen("login")
}

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(navController, startDestination = Screen.Login.route) {
        composable(Screen.Home.route) { HomeScreen(navController) }
    }
}