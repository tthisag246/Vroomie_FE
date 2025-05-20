package com.bumper_car.vroomie_fe.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.bumper_car.vroomie_fe.ui.screen.drive.DriveScreen
import com.bumper_car.vroomie_fe.ui.screen.drivehistory.DriveHistoryDetailScreen
import com.bumper_car.vroomie_fe.ui.screen.drivehistory.DriveHistoryScreen
import com.bumper_car.vroomie_fe.ui.screen.drivescore.DriveScoreScreen
import com.bumper_car.vroomie_fe.ui.screen.drivetip.DriveTipDetailScreen
import com.bumper_car.vroomie_fe.ui.screen.drivetip.DriveTipScreen
import com.bumper_car.vroomie_fe.ui.screen.home.HomeScreen
import com.bumper_car.vroomie_fe.ui.screen.login.LoginScreen
import com.bumper_car.vroomie_fe.ui.screen.mypage.MyPageScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object Drive : Screen("drive")
    object DriveScore : Screen("drive_score")
    object DriveTip : Screen("drive_tip")
    object DriveTipDetail : Screen("drive_tip/{id}")
    object DriveHistory : Screen("drive_history")
    object DriveHistoryDetail : Screen("drive_history/{id}")
    object MyPage : Screen("my_page")
}

@Composable
fun AppNavHost(navController: NavHostController, viewModel: AppViewModel = hiltViewModel()) {
    val isLoggedIn = viewModel.isLoggedIn

    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) Screen.Home.route else Screen.Login.route) {
        composable(Screen.Login.route) { LoginScreen(navController) }
        composable(Screen.Home.route) { HomeScreen(navController) }
        composable(Screen.Drive.route) { DriveScreen(navController) }
        composable(Screen.DriveScore.route) { DriveScoreScreen(navController) }
        composable(Screen.DriveTip.route) { DriveTipScreen(navController) }
        composable(
            route = Screen.DriveTipDetail.route,
            arguments = listOf((navArgument("id") { type = NavType.IntType }))
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: return@composable
            DriveTipDetailScreen(id = id, navController = navController)
        }
        composable(Screen.DriveHistory.route) { DriveHistoryScreen(navController) }
        composable(
            route = Screen.DriveHistoryDetail.route,
            arguments = listOf((navArgument("id") { type = NavType.IntType }))
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: return@composable
            DriveHistoryDetailScreen(id = id, navController = navController)
        }
        composable(Screen.MyPage.route) { MyPageScreen(navController) }
    }
}