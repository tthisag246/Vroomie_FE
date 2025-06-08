package com.bumper_car.vroomie_fe.ui.screen.mypage

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.bumper_car.vroomie_fe.R

@Composable
fun MyPageScreen(
    navController: NavHostController,
    viewModel: MyPageViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchMyInfo()
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
                        "마이페이지",
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
                    .background(Color(0xFFF2F2F2))
            ) {
                item {
                    // 프로필 섹션
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .background(Color(0xFFFAFAFA))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 24.dp)
                        ) {
                            Image(
                                painter = painterResource(
                                    when {
                                        uiState.userScore < 20 -> R.drawable.drive_score_level_1
                                        uiState.userScore < 40 -> R.drawable.drive_score_level_2
                                        uiState.userScore < 60 -> R.drawable.drive_score_level_3
                                        uiState.userScore < 80 -> R.drawable.drive_score_level_4
                                        else -> R.drawable.drive_score_level_5
                                    }
                                ),
                                contentDescription = "운전 레벨",
                                contentScale = ContentScale.FillWidth,
                                modifier = Modifier
                                    .size(120.dp)
                                    .clip(CircleShape)
                                    .border(1.dp, Color.LightGray, CircleShape)
                                    .padding(12.dp)
                            )

                            Column {
                                Text(
                                    uiState.userName,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("차 모델 | ${uiState.carModel ?: "-"}", fontSize = 14.sp)
                                Text(
                                    "하이패스 | ${if (uiState.carHipass == null) "-" else if (uiState.carHipass == true) "보유 중" else "미보유"}",
                                    fontSize = 14.sp
                                )
                                Text("${uiState.carType ?: "-"} | ${uiState.carFuel ?: "-"}", fontSize = 14.sp)
                            }
                        }
                    }
                }

                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .background(Color(0xFFFAFAFA))
                    ) {
                        // 프로필 설정
                        Text("프로필 설정", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp))
                        Text("로그아웃", modifier = Modifier.clickable { /* TODO */ }.padding(horizontal = 24.dp, vertical = 12.dp))
                        HorizontalDivider(color = Color.LightGray, modifier = Modifier.padding(horizontal = 12.dp))
                        Text("회원 탈퇴", modifier = Modifier.clickable { /* TODO */ }.padding(horizontal = 24.dp, vertical = 12.dp))
                    }
                }

                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .background(Color(0xFFFAFAFA))
                    ) {
                        // 약관
                        Text("약관 및 개인정보 처리 동의", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp))
                        Text("이용자 약관", modifier = Modifier.clickable { /* TODO */ }.padding(horizontal = 24.dp, vertical = 12.dp))
                        HorizontalDivider(color = Color.LightGray, modifier = Modifier.padding(horizontal = 12.dp))
                        Text("개인정보 처리방침", modifier = Modifier.clickable { /* TODO */ }.padding(horizontal = 24.dp, vertical = 12.dp))
                        HorizontalDivider(color = Color.LightGray, modifier = Modifier.padding(horizontal = 12.dp))
                        Text("개인정보 방침 동의 및 철회", modifier = Modifier.clickable { /* TODO */ }.padding(horizontal = 24.dp, vertical = 12.dp))
                    }
                }

                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .background(Color(0xFFFAFAFA))
                    ) {
                        // 고객지원
                        Text("고객 지원", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp))
                        Text("자주 묻는 질문", modifier = Modifier.clickable { /* TODO */ }.padding(horizontal = 24.dp, vertical = 12.dp))
                        HorizontalDivider(color = Color.LightGray, modifier = Modifier.padding(horizontal = 12.dp))
                        Text("제안/의견 보내기", modifier = Modifier.clickable { /* TODO */ }.padding(horizontal = 24.dp, vertical = 12.dp))
                        HorizontalDivider(color = Color.LightGray, modifier = Modifier.padding(horizontal = 12.dp))
                        Text("버전 정보", modifier = Modifier.clickable { /* TODO */ }.padding(horizontal = 24.dp, vertical = 12.dp))
                    }
                }
            }
        }
    )
}
