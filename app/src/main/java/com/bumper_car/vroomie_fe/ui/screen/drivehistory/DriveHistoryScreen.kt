package com.bumper_car.vroomie_fe.ui.screen.drivehistory

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.bumper_car.vroomie_fe.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun DriveHistoryScreen(
    navController: NavHostController,
    viewModel: DriveHistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchDriveHistories()
    }

    fun String.toDateFormat(pattern: String = "yyyy년 MM월 dd일"): String {
        return try {
            if (this.isBlank()) return "-"
            val parsed = LocalDateTime.parse(this)
            parsed.format(DateTimeFormatter.ofPattern(pattern))
        } catch (e: Exception) {
            "-"
        }
    }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFAFAFA))
                    .padding(vertical = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    IconButton(
                        onClick = { navController.popBackStack() },
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.icon_back),
                            contentDescription = "뒤로가기"
                        )
                    }
                    Text(
                        "운전 기록",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        containerColor = Color(0xFFF2F2F2),
        content = { innerPadding ->
            LazyColumn(
                contentPadding = innerPadding,
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF2F2F2)),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.histories) { history ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { navController.navigate("drive_history/${history.historyId}") }
                            .background(Color(0xFFFAFAFA), RoundedCornerShape(12.dp))
                            .padding(24.dp)
                    ) {
                        Image(
                            painter = painterResource(
                                when {
                                    history.score < 20 -> R.drawable.drive_history_emoji_1
                                    history.score < 60 -> R.drawable.drive_history_emoji_2
                                    else -> R.drawable.drive_history_emoji_3
                                }
                            ),
                            contentDescription = "운전 점수 이모지",
                            modifier = Modifier.size(72.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    painter = painterResource(R.drawable.drive_history_location),
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "${history.startLocation} → ${history.endLocation}"
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    painter = painterResource(R.drawable.drive_history_date),
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(history.startAt.toDateFormat())
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    painter = painterResource(R.drawable.drive_history_score),
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("${history.score}점")
                            }
                        }
                    }
                }
            }
        }
    )
}