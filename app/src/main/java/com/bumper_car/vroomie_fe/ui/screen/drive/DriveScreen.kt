package com.bumper_car.vroomie_fe.ui.screen.drive

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.bumper_car.vroomie_fe.ui.screen.home.HomeViewModel

@Composable
fun DriveScreen(
    navController: NavHostController,
    viewModel: HomeViewModel = hiltViewModel()
) {}