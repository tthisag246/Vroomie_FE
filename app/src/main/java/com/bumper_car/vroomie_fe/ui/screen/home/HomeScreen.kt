package com.bumper_car.vroomie_fe.ui.screen.home

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.bumper_car.vroomie_fe.R
import com.bumper_car.vroomie_fe.ui.screen.drive.CameraGuideActivity
import com.bumper_car.vroomie_fe.ui.theme.nanumFamily
import com.google.common.io.Files.append

private fun convertWgsToTm(lat: Double, lon: Double): Pair<Int, Int> {
    val x = (lon * 20037508.34 / 180.0).toInt()
    val y = Math.log(Math.tan((90.0 + lat) * Math.PI / 360.0)) / (Math.PI / 180.0)
    val yMeters = (y * 20037508.34 / 180.0).toInt()
    return Pair(x, yMeters)
}

@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.refreshData()
    }

    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current

    val driveScoreLevel = when {
        uiState.driveScore < 20 -> 1
        uiState.driveScore < 40 -> 2
        uiState.driveScore < 60 -> 3
        uiState.driveScore < 80 -> 4
        else -> 5
    }

    val driveScorePrevLevel = (driveScoreLevel - 1)
    val driveScoreNextLevel = (driveScoreLevel + 1)

    fun getDriveLevelImage(level: Int): Int? = when (level) {
        1 -> R.drawable.drive_score_level_1
        2 -> R.drawable.drive_score_level_2
        3 -> R.drawable.drive_score_level_3
        4 -> R.drawable.drive_score_level_4
        5 -> R.drawable.drive_score_level_5
        else -> null
    }

    LaunchedEffect(uiState.isSearchMode) {
        if (uiState.isSearchMode) {
            focusRequester.requestFocus()
            keyboardController?.show()
        }
    }

    // 화면
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
        // 검색 모드
        if (uiState.isSearchMode) {
            Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
                Spacer(modifier = Modifier.height(40.dp))

                TextField(
                    value = uiState.query,
                    onValueChange = { viewModel.onQueryChange(it) },
                    leadingIcon = {
                        IconButton(onClick = {
                            viewModel.onQueryChange("")
                            viewModel.toggleSearchMode(false)
                            focusManager.clearFocus()
                            keyboardController?.hide()
                        }) {
                            Icon(painter = painterResource(R.drawable.icon_back), contentDescription = "뒤로가기")
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(40.dp),
                    modifier = Modifier.fillMaxWidth().height(56.dp).padding(horizontal = 8.dp)
                        .border(1.5.dp, Color(0xFF0064FF), RoundedCornerShape(40.dp))
                        .background(Color.White, RoundedCornerShape(40.dp)).clip(RoundedCornerShape(40.dp))
                        .focusRequester(focusRequester),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        cursorColor = Color.Black,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            focusManager.clearFocus()
                            keyboardController?.hide()
                            Log.d("HomeScreen", "🔍 검색 실행: ${uiState.query}")

                            viewModel.geocode(uiState.query) { result ->
                                result?.let { doc ->
                                    viewModel.addSearchHistory(uiState.query) // ✅ 검색 성공 시에만 저장

                                    val lat = doc.y.toDoubleOrNull()
                                    val lon = doc.x.toDoubleOrNull()
                                    val name = doc.address_name
                                    if (lat != null && lon != null) {
                                        val intent = Intent(context, CameraGuideActivity::class.java).apply {
                                            putExtra("lat", lat)
                                            putExtra("lon", lon)
                                            putExtra("name", name)
                                        }
                                        context.startActivity(intent)
                                    }
                                }
                            }
                        }
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                LazyColumn {
                    items(uiState.searchHistory) { history ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp, horizontal = 16.dp)
                                .clickable {
                                    Log.d("HomeScreen", "🕘 히스토리 클릭됨: $history")
                                    viewModel.geocode(history) { document ->
                                        document?.let {
                                            val lat = it.y.toDoubleOrNull()
                                            val lon = it.x.toDoubleOrNull()
                                            val name = it.address_name
                                            if (lat != null && lon != null) {
                                                val intent = Intent(context, CameraGuideActivity::class.java).apply {
                                                    putExtra("lat", lat)
                                                    putExtra("lon", lon)
                                                    putExtra("name", name)
                                                }
                                                context.startActivity(intent)
                                            } else {
                                                Toast.makeText(context, "위치 정보 파싱 실패", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }
                                },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(history, fontSize = 18.sp, color = Color.DarkGray)
                            IconButton(onClick = { viewModel.deleteSearchHistoryItem(history) }) {
                                Icon(painter = painterResource(R.drawable.icon_delete), contentDescription = "삭제")
                            }
                        }
                    }
                }
            }
        } else {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(24.dp))

                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    TextField(
                        value = uiState.query,
                        onValueChange = { viewModel.onQueryChange(it) },
                        placeholder = { Text("어디로 갈까요?", color = Color(0xFFD9D9D9), fontSize = 20.sp) },
                        leadingIcon = { Image(painter = painterResource(R.drawable.icon_gps), contentDescription = "", modifier = Modifier.size(28.dp)) },
                        singleLine = true,
                        shape = RoundedCornerShape(40.dp),
                        modifier = Modifier.weight(1f).height(56.dp)
                            .border(1.5.dp, Color(0xFF0064FF), RoundedCornerShape(40.dp))
                            .background(Color.White, RoundedCornerShape(40.dp))
                            .clip(RoundedCornerShape(40.dp))
                            .onFocusChanged { if (it.isFocused) viewModel.toggleSearchMode(true) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            cursorColor = Color.Black,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )
                }


                Spacer(modifier = Modifier.height(36.dp))

                // 운전 점수
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Box(
                            modifier = Modifier.size(100.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            getDriveLevelImage(driveScorePrevLevel)?.let {
                                Image(
                                    painter = painterResource(it),
                                    contentDescription = "이전 레벨",
                                    contentScale = ContentScale.FillWidth,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                        Box(
                            modifier = Modifier.size(100.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            getDriveLevelImage(driveScoreNextLevel)?.let {
                                Image(
                                    painter = painterResource(it),
                                    contentDescription = "이후 레벨",
                                    contentScale = ContentScale.FillWidth,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }

                    // 운전 점수 원형 버튼
                    IconButton(
                        onClick = { navController.navigate("drive_score") },
                        modifier = Modifier
                            .size(240.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.9f))
                            .align(Alignment.Center),
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Top,
                            modifier = Modifier.matchParentSize()
                        ) {
                            Spacer(Modifier.height(40.dp))
                            Text(
                                text = "운전점수",
                                fontFamily = nanumFamily,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = uiState.driveScore.toString(),
                                fontFamily = nanumFamily,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF007BFF)
                            )
                        }
                        getDriveLevelImage(driveScoreLevel)?.let {
                            Image(
                                painter = painterResource(it),
                                contentDescription = "현재 레벨",
                                modifier = Modifier
                                    .size(156.dp)
                                    .padding(4.dp)
                                    .offset(y = 44.dp),
                                contentScale = ContentScale.FillWidth
                            )
                        }
                    }

                    val targetSweep = (uiState.driveScore / 100f) * 270f
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
                    Canvas(
                        modifier = Modifier
                            .size(288.dp)
                            .align(Alignment.Center)
                    ) {
                        val size = size.minDimension
                        val radius = size / 2
                        val center = Offset(size / 2, size / 2)
                        val innerRadius = radius - stroke.width / 2

                        val arcSize =
                            androidx.compose.ui.geometry.Size(innerRadius * 2, innerRadius * 2)
                        val topLeft = Offset(center.x - innerRadius, center.y - innerRadius)

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
                }

                Spacer(modifier = Modifier.height(28.dp))

                Box(
                    modifier = Modifier
                        .height(220.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .clickable { navController.navigate("drive_tip") }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "운전 상식",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        uiState.driveTips.forEach { tip ->
                            Text(
                                text = buildAnnotatedString {
                                    pushStyle(SpanStyle(textDecoration = TextDecoration.Underline))
                                    append(tip.title)
                                    pop()
                                },
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.DarkGray,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .padding(top = 10.dp)
                                    .clickable { navController.navigate("drive_tip/${tip.tipId}") }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // 운전 기록 버튼
                    Button(
                        onClick = { navController.navigate("drive_history") },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        contentPadding = PaddingValues(16.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(120.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.icon_drive_history),
                                contentDescription = "운전 기록",
                                tint = Color.Unspecified,
                                modifier = Modifier.size(60.dp)
                            )
                            Text("운전 기록", color = Color.Black, fontSize = 20.sp, fontFamily = nanumFamily, fontWeight = FontWeight.Bold)
                        }
                    }

                    // 마이페이지 버튼
                    Button(
                        onClick = { navController.navigate("my_page") },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        contentPadding = PaddingValues(16.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(120.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.icon_profile),
                                contentDescription = "마이페이지",
                                tint = Color.Unspecified,
                                modifier = Modifier.size(60.dp)
                            )
                            Text("마이페이지", color = Color.Black, fontSize = 20.sp, fontFamily = nanumFamily, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

    }
}