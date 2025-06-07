package com.bumper_car.vroomie_fe.ui.screen.drive

import android.app.Activity
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavHostController
import com.bumper_car.vroomie_fe.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun DriveResultScreen(
    viewModel: DriveResultViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    fun String.toDateFormat(pattern: String = "yyyy.MM.dd"): String {
        return try {
            if (this.isBlank()) return "-"
            val parsed = LocalDateTime.parse(this)
            parsed.format(DateTimeFormatter.ofPattern(pattern))
        } catch (e: Exception) {
            "-"
        }
    }

    fun Int.toHourMinuteFormat(): String {
        val hours = this / 60
        val minutes = this % 60

        return when {
            hours > 0 && minutes > 0 -> "${hours}시간 ${minutes}분"
            hours > 0 -> "${hours}시간"
            minutes > 0 -> "${minutes}분"
            else -> "0분"
        }
    }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(vertical = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = uiState.startLocation,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .weight(1f)
                        )

                        Text(
                            text = "→",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )

                        Text(
                            text = uiState.endLocation,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .weight(1f)
                        )
                    }
                    IconButton(
                        onClick = { (context as? Activity)?.finish() },
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.icon_delete),
                            contentDescription = "닫기"
                        )
                    }
                }
            }
        },
        containerColor = Color(0xFFDCDCDC),
        content = { innerPadding ->
            LazyColumn(
                contentPadding = innerPadding,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, RoundedCornerShape(20.dp))
                            .padding(20.dp)
                    ) {
                        Text(
                            "Vroomie 운전연수 응시표",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(IntrinsicSize.Min)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .weight(2f)
                                        .fillMaxHeight()
                                ) {
                                    Row {
                                        TableCell("응시일자", true, Modifier.weight(3f))
                                        TableCell(
                                            uiState.startAt.toDateFormat(),
                                            false,
                                            Modifier.weight(4f)
                                        )
                                    }
                                    Row {
                                        TableCell("주행거리", true, Modifier.weight(3f))
                                        TableCell(
                                            uiState.distance.toString() + "km",
                                            false,
                                            Modifier.weight(4f)
                                        )
                                    }
                                    Row {
                                        TableCell("주행시간", true, Modifier.weight(3f))
                                        TableCell(
                                            uiState.duration.toHourMinuteFormat(),
                                            false,
                                            Modifier.weight(4f)
                                        )
                                    }
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .border(1.dp, Color.Gray),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(R.drawable.drive_history_stamp),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .fillMaxSize()
                                    )
                                    val scoreString = uiState.score.toString()
                                    Text(
                                        text = scoreString,
                                        fontFamily = FontFamily(Font(R.font.bm_euljiro_10_years_later)),
                                        fontSize = when (scoreString.length) {
                                            1 -> 80.sp
                                            2 -> 72.sp
                                            else -> 56.sp
                                        },
                                        fontWeight = FontWeight.Black,
                                        color = Color.Red
                                    )
                                }
                            }
                            TableCell("평가항목", true, Modifier.fillMaxWidth())
                            Row {
                                Column(
                                    modifier = Modifier
                                        .weight(4f)
                                ) {
                                    Row {
                                        TableCell("차선 치우침(좌)", true, Modifier.weight(3f))
                                        TableCell(
                                            uiState.laneDeviationLeftCount.toString() + "회",
                                            false,
                                            Modifier.weight(1f)
                                        )
                                    }
                                    Row {
                                        TableCell("차선 치우침(우)", true, Modifier.weight(3f))
                                        TableCell(
                                            uiState.laneDeviationRightCount.toString() + "회",
                                            false,
                                            Modifier.weight(1f)
                                        )
                                    }
                                    Row {
                                        TableCell("안전거리 미확보", true, Modifier.weight(3f))
                                        TableCell(
                                            uiState.safeDistanceViolationCount.toString() + "회",
                                            false,
                                            Modifier.weight(1f)
                                        )
                                    }
                                }
                                Column(
                                    modifier = Modifier
                                        .weight(3f)
                                ) {
                                    Row {
                                        TableCell("급과속", true, Modifier.weight(2f))
                                        TableCell(
                                            uiState.suddenAccelerationCount.toString() + "회",
                                            false,
                                            Modifier.weight(1f)
                                        )
                                    }
                                    Row {
                                        TableCell("급감속", true, Modifier.weight(2f))
                                        TableCell(
                                            uiState.suddenDecelerationCount.toString() + "회",
                                            false,
                                            Modifier.weight(1f)
                                        )
                                    }
                                    Row {
                                        TableCell("과속", true, Modifier.weight(2f))
                                        TableCell(
                                            uiState.speedingCount.toString() + "회",
                                            false,
                                            Modifier.weight(1f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, RoundedCornerShape(20.dp))
                            .padding(20.dp)
                            .padding(vertical = 8.dp)
                    ) {
                        Text(
                            "Feedback",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                                .padding(bottom = 4.dp)
                        )

                        uiState.feedback.forEachIndexed { index, feedback ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp)
                            ) {
                                Text(
                                    "${index + 1}. ${feedback.title}",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    feedback.content.replace(" ", "\u00A0"),
                                    fontSize = 18.sp,
                                    color = Color.DarkGray
                                )
                                Spacer(Modifier.height(16.dp))

                                val context = LocalContext.current
                                val exoPlayer = remember(feedback.videoUrl) {
                                    ExoPlayer.Builder(context).build().apply {
                                        setMediaItem(MediaItem.fromUri(feedback.videoUrl))
                                        prepare()
                                    }
                                }
                                AndroidView(
                                    factory = {
                                        PlayerView(it).apply {
                                            player = exoPlayer
                                            useController = true
                                            layoutParams = ViewGroup.LayoutParams(
                                                ViewGroup.LayoutParams.MATCH_PARENT,
                                                ViewGroup.LayoutParams.WRAP_CONTENT
                                            )
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun TableCell(text: String, isHeader: Boolean = false, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .border(1.dp, Color.Gray)
            .padding(8.dp)
    ) {
        Text(
            text = text,
            fontWeight = if (isHeader) FontWeight.Bold else FontWeight.Normal
        )
    }
}