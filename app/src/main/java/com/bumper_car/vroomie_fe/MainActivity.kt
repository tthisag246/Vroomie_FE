package com.bumper_car.vroomie_fe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.bumper_car.vroomie_fe.navigation.AppNavHost
import dagger.hilt.android.AndroidEntryPoint
import com.bumper_car.vroomie_fe.ui.theme.Vroomie_FETheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Vroomie_FETheme {
                val navController = rememberNavController()
                AppNavHost(navController)
            }
        }
    }
}