package com.bumper_car.vroomie_fe.ui.screen.drive

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import com.bumper_car.vroomie_fe.domain.model.DriveHistory
import com.bumper_car.vroomie_fe.ui.theme.Vroomie_FETheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DriveResultActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Vroomie_FETheme {
                val driveResult = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra("drive_result", DriveHistory::class.java)
                } else {
                    intent.getParcelableExtra<DriveHistory>("drive_result")
                }
                val viewModel: DriveResultViewModel = hiltViewModel()

                LaunchedEffect(driveResult) {
                    driveResult?.let {
                        viewModel.setDriveResult(it)
                    }
                }

                DriveResultScreen(viewModel = viewModel)
            }
        }
    }
}

