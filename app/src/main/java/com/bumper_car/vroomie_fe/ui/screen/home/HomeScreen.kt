package com.bumper_car.vroomie_fe.ui.screen.home

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.bumper_car.vroomie_fe.R

@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    val driveScoreLevel = when {
        uiState.driveScore < 20 -> 1
        uiState.driveScore < 40 -> 2
        uiState.driveScore < 60 -> 3
        uiState.driveScore < 80 -> 4
        else -> 5
    }

    val driveScorePrevLevel = (driveScoreLevel - 1).coerceAtLeast(1)
    val driveScoreNexyLevel = (driveScoreLevel + 1).coerceAtMost(5)

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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
            ) {
                Spacer(modifier = Modifier.height(40.dp))

                // 검색 바
                TextField(
                    value = uiState.query,
                    onValueChange = { viewModel.onQueryChange(it) },
                    placeholder = {
                        Text(
                            text = "어디로 갈까요?",
                            color = Color(0xFFD9D9D9),
                            fontSize = 18.sp
                        )
                    },
                    leadingIcon = {
                        IconButton(onClick = {
                            viewModel.onQueryChange("")
                            viewModel.toggleSearchMode(false)
                            focusManager.clearFocus()
                            keyboardController?.hide()
                        }) {
                            Icon(
                                painter = painterResource(R.drawable.icon_back),
                                contentDescription = "뒤로가기"
                            )
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(40.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = 8.dp)
                        .border(1.5.dp, Color(0xFF0064FF), RoundedCornerShape(40.dp))
                        .background(Color.White, RoundedCornerShape(40.dp))
                        .clip(RoundedCornerShape(40.dp))
                        .focusRequester(focusRequester),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        cursorColor = Color.Black,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            viewModel.handleSearch(uiState.query)
                            focusManager.clearFocus()
                            keyboardController?.hide()
                        }
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 최근 검색어
                LazyColumn {
                    items(uiState.searchHistory) { history ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp, horizontal = 16.dp)
                                .clickable { navController.navigate("drive") },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(history, fontSize = 18.sp, color = Color.DarkGray)
                            IconButton(onClick = { viewModel.deleteSearchHistoryItem(history) }) {
                                Icon(
                                    painter = painterResource(R.drawable.icon_delete),
                                    contentDescription = "삭제"
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // 검색 모드 X
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // 검색
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 검색 바
                    TextField(
                        value = uiState.query,
                        onValueChange = { viewModel.onQueryChange(it) },
                        placeholder = {
                            Text(
                                text = "어디로 갈까요?",
                                color = Color(0xFFD9D9D9),
                                fontSize = 18.sp
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(40.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .border(1.5.dp, Color(0xFF0064FF), RoundedCornerShape(40.dp))
                            .background(Color.White, RoundedCornerShape(40.dp))
                            .clip(RoundedCornerShape(40.dp))
                            .onFocusChanged {
                                if (it.isFocused) viewModel.toggleSearchMode(true)
                            },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            cursorColor = Color.Black,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    //자유 주행
                    IconButton(
                        onClick = {
                            navController.navigate("drive")
                        },
                        modifier = Modifier
                            .size(56.dp)
                            .shadow(4.dp, shape = CircleShape, clip = false)
                            .clip(CircleShape)
                            .background(Color.White)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.icon_gps),
                            contentDescription = "자유 주행",
                            tint = Color.Unspecified,
                            modifier = Modifier
                                .size(44.dp)
                                .padding(end = 4.dp, top = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(60.dp))

                // 운전 점수
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        getDriveLevelImage(driveScorePrevLevel)?.let {
                            Image(
                                painter = painterResource(it),
                                contentDescription = "이전 레벨",
                                contentScale = ContentScale.FillWidth,
                                modifier = Modifier.size(100.dp)
                            )
                        }
                        getDriveLevelImage(driveScoreNexyLevel)?.let {
                            Image(
                                painter = painterResource(it),
                                contentDescription = "이후 레벨",
                                contentScale = ContentScale.FillWidth,
                                modifier = Modifier.size(100.dp)
                            )
                        }
                    }

                    // 운전 점수 원형 버튼
                    IconButton(
                        onClick = { navController.navigate("drive_score") },
                        modifier = Modifier
                            .size(240.dp)
                            .shadow(64.dp, shape = CircleShape, clip = false)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.8f))
                            .align(Alignment.BottomCenter)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(vertical = 32.dp)
                            ) {
                            Text(
                                text = "운전점수",
                                fontSize = 24.sp
                                )
                            Text(
                                text = uiState.driveScore.toString(),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF007BFF)
                            )
                            getDriveLevelImage(driveScoreLevel)?.let {
                                Image(
                                    painter = painterResource(it),
                                    contentDescription = "현재 레벨",
                                    modifier = Modifier.size(160.dp),
                                    contentScale = ContentScale.FillWidth
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(60.dp))

                Box(
                    modifier = Modifier
                        .height(240.dp)
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
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        uiState.driveInformations.forEachIndexed { index, info ->
                            Text(
                                text = info,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                style = TextStyle(textDecoration = TextDecoration.Underline),
                                color = Color.DarkGray,
                                modifier = Modifier
                                    .padding(top = 8.dp)
                                    .clickable { navController.navigate("drive_tip/${index}") }
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
                                painter = painterResource(R.drawable.icon_drive_record),
                                contentDescription = "운전 기록",
                                tint = Color.Unspecified,
                                modifier = Modifier.size(60.dp)
                            )
                            Text("운전 기록", color = Color.Black)
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
                            Text("마이페이지", color = Color.Black)
                        }
                    }
                }
            }
        }

    }
}