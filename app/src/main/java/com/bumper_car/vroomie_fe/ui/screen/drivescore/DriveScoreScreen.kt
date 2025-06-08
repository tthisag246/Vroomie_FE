package com.bumper_car.vroomie_fe.ui.screen.drivescore

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.bumper_car.vroomie_fe.R
import kotlin.math.absoluteValue

@Composable
fun DriveScoreScreen(
    navController: NavHostController,
    viewModel: DriveScoreViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchDriveScore()
    }

    val scrollState = rememberScrollState()
    val monthListState = rememberLazyListState()

    val density = LocalDensity.current

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

    val sortedMonths = uiState.monthlyScores.sortedWith(compareBy({ it.year }, { it.month }))

    val selectedMonth = sortedMonths.getOrNull(uiState.selectedIndex)?.month

    val selectedDriveStats = selectedMonth
        ?.let { monthKey -> uiState.monthlyDetailStatsUiState.get(monthKey) }
        ?: DetailStatsUiState(
            averageScore = 0,
            totalDistance = 0f,
            totalDuration = 0,
            totalSpeedingCount = 0,
            totalSuddenAccelerationCount = 0,
            totalSuddenDecelerationCount = 0,
            totalSafeDistanceViolationCount = 0,
            totalLaneDeviationRightCount = 0,
            totalLaneDeviationLeftCount = 0
        )

    val statItems = listOf(
        "운전점수" to "${selectedDriveStats.averageScore}점",
        "운전거리" to "${selectedDriveStats.totalDistance}km",
        "운전시간" to selectedDriveStats.totalDuration.toHourMinuteFormat(),
        "과속" to "${selectedDriveStats.totalSpeedingCount}회",
        "급가속" to "${selectedDriveStats.totalSuddenAccelerationCount}회",
        "급감속" to "${selectedDriveStats.totalSuddenDecelerationCount}회",
        "안전거리 미확보" to "${selectedDriveStats.totalSafeDistanceViolationCount}회",
        "차선 치우침(우)" to "${selectedDriveStats.totalLaneDeviationRightCount}회",
        "차선 치우침(좌)" to "${selectedDriveStats.totalLaneDeviationLeftCount}회"
    )

    // 초기 스크롤 여부
    val isInitialScrollDone = remember { mutableStateOf(false) }
    LaunchedEffect(sortedMonths) {
        snapshotFlow { monthListState.layoutInfo.totalItemsCount }
            .collect { totalCount ->
                if (totalCount > 0 && uiState.selectedIndex < totalCount && !isInitialScrollDone.value) {
                    monthListState.scrollToItem(uiState.selectedIndex)
                    isInitialScrollDone.value = true
                }
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
                        "운전점수",
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
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFFAFAFA))
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        val targetSweep = (uiState.score / 100f) * 270f
                        val sweepAnim = remember { androidx.compose.animation.core.Animatable(0f) }

                        LaunchedEffect(targetSweep) {
                            sweepAnim.animateTo(
                                targetSweep.coerceIn(0f, 270f),
                                animationSpec = androidx.compose.animation.core.tween(
                                    durationMillis = 1000,
                                    easing = androidx.compose.animation.core.FastOutSlowInEasing
                                )
                            )
                        }

                        val stroke = with(LocalDensity.current) {
                            Stroke(width = 24.dp.toPx(), cap = StrokeCap.Round)
                        }

                        Canvas(modifier = Modifier.size(200.dp)) {
                            val size = size.minDimension
                            val radius = size / 2
                            val center = Offset(size / 2, size / 2)
                            val innerRadius = radius - stroke.width / 2

                            val arcSize =
                                androidx.compose.ui.geometry.Size(innerRadius * 2, innerRadius * 2)
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
                            withTransform({
                                rotate(degrees = 135f, pivot = center)
                            }) {
                                drawArc(
                                    brush = Brush.sweepGradient(
                                        colorStops = arrayOf(
                                            0.0f to Color(0xFF67FFE7),
                                            0.375f to Color(0xFF51A2FF),
                                            0.75f to Color(0xFF9B4AFF),
                                            1.0f to Color(0xFF67FFE7)
                                        ),
                                        center = center
                                    ),
                                    startAngle = 0f,
                                    sweepAngle = sweepAnim.value,
                                    useCenter = false,
                                    topLeft = topLeft,
                                    size = arcSize,
                                    style = stroke
                                )
                            }
                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .wrapContentSize(Alignment.Center)
                        ) {
                            Text(
                                "내 운전점수",
                                fontSize = 20.sp,
                                color = Color.Gray
                            )
                            Text(
                                text = "${uiState.score}점",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 44.sp
                            )
                        }
                    }
                }

                item {
                    Column(
                        modifier = Modifier
                            .background(Color(0xFFFAFAFA))
                            .padding(20.dp)
                            .padding(vertical = 8.dp)
                    ) {
                        // 상위 %
                        Text(
                            text = buildAnnotatedString {
                                append("전체 중 ")
                                withStyle(style = SpanStyle(color = Color(0xFF0064FF), fontWeight = FontWeight.ExtraBold)) {
                                    append("상위 ${100 - uiState.percentile}%")
                                }
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.height(40.dp))
                        // 전체 점수 분포 그래프
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                        ) {
                            Canvas(modifier = Modifier.matchParentSize()) {
                                val canvasWidth = size.width
                                val canvasHeight = size.height

                                // 1) 10점 단위로 버킷(구간) 생성
                                val buckets = (0..100 step 10).map { start ->
                                    val end = (start + 9).coerceAtMost(100)
                                    Pair(start, end)
                                }

                                // 2) 각 버킷별 합계 계산
                                val bucketCounts = buckets.map { (s, e) ->
                                    (s..e).sumOf { score ->
                                        uiState.percentileDistribution[score] ?: 0
                                    } / 10f
                                }

                                val bucketCount = buckets.size
                                val bucketSpacing = canvasWidth / (bucketCount - 1)

                                val maxAvg = bucketCounts.maxOrNull()
                                    ?.takeIf { it > 0f }
                                    ?: 1f

                                // 3) 버킷별 포인트 리스트 만들기 (x는 간격, y는 정규화)
                                val points = bucketCounts.mapIndexed { idx, avg ->
                                    val x = idx * bucketSpacing
                                    val yRatio = avg / maxAvg
                                    val y = canvasHeight * (1f - yRatio)
                                    Offset(x, y)
                                }

                                // 4) 스플라인 생성
                                val smoothPath = Path().apply {
                                    if (points.size < 2) return@apply

                                    moveTo(points.first().x, points.first().y)

                                    val tension = 0.5f

                                    // 가상의 시작·끝 점(p0, p{n+1})을 복제해서 극단에서도 자연스럽게
                                    val pts =
                                        listOf(points.first()) + points + listOf(points.last())

                                    for (i in 1 until pts.size - 2) {
                                        val p0 = pts[i - 1]
                                        val p1 = pts[i]
                                        val p2 = pts[i + 1]
                                        val p3 = pts[i + 2]

                                        // Catmull-Rom 제어점 공식
                                        val cp1 = Offset(
                                            x = p1.x + (p2.x - p0.x) * tension / 3f,
                                            y = p1.y + (p2.y - p0.y) * tension / 3f
                                        )
                                        val cp2 = Offset(
                                            x = p2.x - (p3.x - p1.x) * tension / 3f,
                                            y = p2.y - (p3.y - p1.y) * tension / 3f
                                        )

                                        cubicTo(
                                            cp1.x, cp1.y,
                                            cp2.x, cp2.y,
                                            p2.x, p2.y
                                        )
                                    }
                                }

                                // 5) 면 채우기
                                drawPath(
                                    path = smoothPath,
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            Color(0xFF51A2FF), // semi-transparent
                                            Color(0x3451A2FF)
                                        )
                                    )
                                )

                                // 6) 선 그리기
                                drawPath(
                                    path = smoothPath,
                                    color = Color(0xFF51A2FF),
                                    style = Stroke(
                                        width = 2.dp.toPx(),
                                        cap = StrokeCap.Round
                                    )
                                )

                                val myScore = uiState.score.coerceIn(0, 100)
                                val myBucketIndex = myScore / 10
                                val withinFraction = (myScore % 10) / 10f

                                val scoreP0 = points.getOrNull(myBucketIndex - 1) ?: points[myBucketIndex]
                                val scoreP1 = points[myBucketIndex]
                                val scoreP2 = points.getOrNull(myBucketIndex + 1) ?: points.last()
                                val scoreP3 = points.getOrNull(myBucketIndex + 2) ?: points.last()

                                val t2 = withinFraction * withinFraction
                                val t3 = t2 * withinFraction
                                val tension = 0.5f
                                val f0 = -tension*t3 +     t2 - 0.5f*withinFraction
                                val f1 =  1.5f*t3 - 2.5f*t2 + 1f
                                val f2 = -1.5f*t3 + 2f*t2 + 0.5f*withinFraction
                                val f3 =  0.5f*t3 - 0.5f*t2
                                val myScorePos = Offset(
                                    x = scoreP0.x*f0 + scoreP1.x*f1 + scoreP2.x*f2 + scoreP3.x*f3,
                                    y = scoreP0.y*f0 + scoreP1.y*f1 + scoreP2.y*f2 + scoreP3.y*f3
                                )

                                // 7) 점 찍기
                                drawCircle(
                                    color = Color(0xFF0064FF),
                                    radius = 4.dp.toPx(),
                                    center = myScorePos
                                )

                                // 텍스트
                                val balloonText = "상위 ${100 - uiState.percentile}%"
                                val textPaint = android.graphics.Paint().apply {
                                    color = android.graphics.Color.rgb(0, 100, 255)
                                    textSize = 32f
                                    isAntiAlias = true
                                    textAlign = android.graphics.Paint.Align.CENTER
                                }

                                val textX = myScorePos.x
                                val textY = myScorePos.y - 20.dp.toPx()

                                drawIntoCanvas { canvas ->
                                    canvas.nativeCanvas.drawText(
                                        balloonText,
                                        textX,
                                        textY,
                                        textPaint
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    // 점수 추이
                    Column(
                        modifier = Modifier
                            .background(Color(0xFFFAFAFA))
                            .padding(20.dp)
                            .padding(vertical = 8.dp)
                    ) {
                        val scoreDiff =
                            (sortedMonths.lastOrNull()?.score ?: 0) - (sortedMonths.getOrNull(
                                sortedMonths.size - 2
                            )?.score ?: 0)

                        Text(
                            text = buildAnnotatedString {
                                append("이번 달은 지난 달보다\n운전점수가 ")
                                withStyle(style = SpanStyle(color = (if (scoreDiff >= 0) Color(0xFF0064FF) else Color.Red), fontWeight = FontWeight.ExtraBold)) {
                                    append("${scoreDiff.absoluteValue}점")
                                }
                                append(" ${if (scoreDiff >= 0) "올랐어요" else "떨어졌어요"}")
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.height(28.dp))

                        // 그래프용 spacing 계산
                        val itemSpacing = 8.dp
                        val itemWidth = 60.dp

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        ) {
                            // 그래프 Canvas
                            Canvas(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp)
                            ) {
                                val canvasWidth = size.width
                                val canvasHeight = size.height

                                val spacePx = itemSpacing.toPx()
                                val itemWidthPx = itemWidth.toPx()
                                val totalItemWidth = itemWidthPx + spacePx

                                val maxScore = 100f
                                val minScore = 0f

                                val scrollOffsetPx =
                                    monthListState.firstVisibleItemIndex * totalItemWidth +
                                            monthListState.firstVisibleItemScrollOffset.toFloat()

                                val points =
                                    sortedMonths.map { it.score }.mapIndexed { index, score ->
                                        val rawX = (index * totalItemWidth) + totalItemWidth / 2f
                                        val x = rawX - scrollOffsetPx
                                        val yRatio = (score - minScore) / (maxScore - minScore)
                                        val y = canvasHeight * (1f - yRatio)
                                        Offset(x, y)
                                    }

                                // 선
                                for (i in 0 until points.size - 1) {
                                    if (points[i].x in 0f..canvasWidth || points[i + 1].x in 0f..canvasWidth) {
                                        drawLine(
                                            color = Color(0xFF51A2FF),
                                            start = points[i],
                                            end = points[i + 1],
                                            strokeWidth = 5f
                                        )
                                    }
                                }

                                // 점
                                points.forEachIndexed { index, point ->
                                    if (point.x in 0f..canvasWidth) { // 화면 내에서만 그림 (성능 최적화)
                                        drawCircle(
                                            color = if (index == uiState.selectedIndex) Color(0xFF0064FF) else Color(0xFF51A2FF),
                                            radius = if (index == uiState.selectedIndex) 10f else 6f,
                                            center = point
                                        )
                                    }
                                }
                            }

                            LazyRow(
                                state = monthListState,
                                horizontalArrangement = Arrangement.spacedBy(itemSpacing),
                                contentPadding = PaddingValues(
                                    start = itemSpacing / 2,
                                    end = itemSpacing / 2
                                ),
                                modifier = Modifier.matchParentSize()
                            ) {
                                itemsIndexed(sortedMonths) { index, item ->
                                    val isSelected = index == uiState.selectedIndex
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.width(itemWidth)
                                    ) {
                                        Spacer(modifier = Modifier.height(128.dp))
                                        Text(
                                            text = "${item.score}점",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = if (isSelected) Color(0xFF0064FF) else Color.Gray
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Box(
                                            modifier = Modifier
                                                .height(48.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(
                                                    if (isSelected) Color.Transparent else Color.LightGray.copy(alpha = 0.3f)
                                                )
                                                .border(
                                                    BorderStroke(1.5.dp, if(isSelected) Color(0xFF0064FF) else Color.Transparent),
                                                    RoundedCornerShape(10.dp)
                                                )
                                                .clickable {
                                                    viewModel.updateSelectedIndex(index)
                                                }
                                                .padding(horizontal = 16.dp, vertical = 6.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "${item.month}월",
                                                color = if (isSelected) Color(0xFF0064FF) else Color.DarkGray,
                                                fontSize = 16.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Box(
                            modifier = Modifier
                                .wrapContentHeight()
                                .fillMaxWidth()
                                .border(
                                    width = 1.dp,
                                    color = Color.Gray,
                                    shape = RoundedCornerShape(8.dp)
                                )
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
                                            fontSize = 18.sp,
                                            color = Color.DarkGray
                                        )
                                        Text(
                                            value,
                                            fontSize = 18.sp,
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
    )
}