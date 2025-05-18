package com.bumper_car.vroomie_fe.ui.screen.drivescore

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.bumper_car.vroomie_fe.R

@Composable
fun DriveScoreScreen(
    navController: NavHostController,
    viewModel: DriveScoreViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val scrollState = rememberScrollState()
    val monthListState = rememberLazyListState()

    val density = LocalDensity.current

    val selectedDriveStats = uiState.driveStatsList.getOrElse(uiState.selectedIndex) {
        DriveStats("-", "-", "-", "-", "-", "-", "-")
    }

    val statItems = listOf(
        "운전거리" to selectedDriveStats.totalDistance,
        "운전시간" to selectedDriveStats.totalTime,
        "과속" to selectedDriveStats.overspeed,
        "급감속" to selectedDriveStats.harshBraking,
        "급가속" to selectedDriveStats.harshAccel,
        "안전거리 미확보" to selectedDriveStats.closeFollowing,
        "차선 치우침" to selectedDriveStats.laneDeviation,
    )

    LaunchedEffect(Unit) {
        monthListState.layoutInfo.takeIf { it.totalItemsCount > 0 && it.viewportEndOffset > 0 }?.let {
            val itemWidthPx = with(density) { 80.dp.toPx() }
            val centerOffset = ((it.viewportEndOffset - itemWidthPx) / 2).toInt()
            monthListState.scrollToItem(uiState.selectedIndex, centerOffset)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawWithCache {
                onDrawBehind {
                    drawRect(
                        brush = Brush.linearGradient(
                            colorStops = arrayOf(
                                0.0f to Color(0xFFFFFFFF),
                                0.2f to Color(0xFFFFFFFF),
                                0.3f to Color(0xFFEEF0F3)
                            ),
                            start = Offset(0f, 0f),
                            end = Offset(0f, size.height)
                        )
                    )
                }
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.padding(vertical = 24.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.icon_back),
                    contentDescription = "뒤로가기"
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                val stroke = with(LocalDensity.current) {
                    Stroke(width = 24.dp.toPx(), cap = StrokeCap.Round)
                }

                Canvas(modifier = Modifier.size(200.dp)) {
                    val size = size.minDimension
                    val radius = size / 2
                    val sweepAngle = uiState.driveScore / 100f * 270f
                    val center = Offset(size / 2, size / 2)
                    val innerRadius = radius - stroke.width / 2

                    val arcSize = androidx.compose.ui.geometry.Size(innerRadius * 2, innerRadius * 2)
                    val topLeft = Offset(center.x - innerRadius, center.y - innerRadius)

                    // 회색 배경 원
                    drawArc(
                        color = Color.LightGray,
                        startAngle = 135f,
                        sweepAngle = 270f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = stroke
                    )

                    // 그라데이션 Progress 원
                    drawArc(
                        brush = Brush.sweepGradient(
                            colorStops = arrayOf(
                                0.0f to Color(0xFF67FFE7),
                                0.5f to Color(0xFF51A2FF),
                                1.0f to Color(0xFF9B4AFF)
                            ),
                            center = center
                        ),
                        startAngle = 135f,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = stroke
                    )
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .wrapContentSize(Alignment.Center)
                ) {
                    Text(
                        "내 운전점수",
                        fontSize = 16.sp,
                        color = Color.LightGray
                    )
                    Text(
                        text = "${uiState.driveScore}점",
                        fontWeight = FontWeight.Bold,
                        fontSize = 40.sp
                    )
                }

            }

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .background(Color.LightGray)
            )

            Column(
                modifier = Modifier
                    .padding(vertical = 24.dp)
            ) {
                // 상위 %
                Text(
                    "전체 중 상위 ${uiState.rankPercent}%",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                // TODO: 그래프
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(Color.Gray)
                )
            }

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .background(Color.LightGray)
            )

            // 점수 추이
            Column(
                modifier = Modifier
                    .padding(vertical = 24.dp)
            ) {
                Text(
                    text = "이번 달은 지난 달보다\n" +
                            "운전점수가 ${
                                uiState.scores.getOrNull(uiState.months.lastIndex)
                            ?.minus(uiState.scores.getOrElse(uiState.months.lastIndex - 1) { 0 })
                    }점 올랐어요",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                // TODO: 그래프
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(Color.Gray)
                )
                LazyRow(
                    state = monthListState,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    itemsIndexed(uiState.months) { index, month ->
                        val isSelected = index == uiState.selectedIndex
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Text(
                                text = "${uiState.scores[index]}점",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = if (isSelected) Color.Blue else Color.Gray
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .height(48.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (isSelected) Color(0xFFE0F0FF) else Color.LightGray.copy(
                                            alpha = 0.3f
                                        )
                                    )
                                    .clickable {
                                        viewModel.updateSelectedIndex(index)
                                    }
                                    .padding(horizontal = 16.dp, vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = month,
                                    color = if (isSelected) Color.Blue else Color.DarkGray,
                                    fontSize = 12.sp,                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth()
                        .border(width = 1.dp, color = Color.Gray, shape = RoundedCornerShape(8.dp))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column()
                    {
                        statItems.forEach { (label, value) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    label,
                                    fontSize = 16.sp,
                                    color = Color.Gray
                                )
                                Text(
                                    value,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}