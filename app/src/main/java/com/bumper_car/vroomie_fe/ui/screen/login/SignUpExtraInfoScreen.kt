package com.bumper_car.vroomie_fe.ui.screen.login

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController

@Composable
fun SignUpExtraInfoScreen(
    navController: NavHostController,
    viewModel: SignUpExtraInfoViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFAFAFA))
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "회원가입",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        containerColor = Color(0xFFFAFAFA),
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 유저 이름
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "이름(필수)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        modifier = Modifier.width(100.dp)
                    )
                    OutlinedTextField(
                        value = uiState.userName,
                        onValueChange = { viewModel.updateUserName(it) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("ex. 홍길동") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF0064FF),
                            unfocusedLabelColor = Color(0xFFADADAD)
                        )
                    )
                }

                // 차량 모델
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "차량 모델명",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.width(100.dp)
                    )
                    OutlinedTextField(
                        value = uiState.carModel ?: "",
                        onValueChange = { viewModel.updateCarModel(it) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("ex. 아반떼") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF0064FF),
                            unfocusedLabelColor = Color(0xFFADADAD)
                        )
                    )
                }

                // 하이패스 여부
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "하이패스",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.width(100.dp)
                    )
                    Switch(
                        checked = uiState.carHipass ?: false,
                        onCheckedChange = { viewModel.updateCarHipass(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFF0064FF),
                            checkedTrackColor = Color.Transparent,
                            checkedBorderColor = Color(0xFF0064FF),
                            uncheckedThumbColor = Color(0xFFADADAD),
                            uncheckedTrackColor = Color.Transparent
                            )
                    )
                }

                // 차량 종류
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "차량 종류",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.width(100.dp)
                    )
                    DropdownSelector(
                        options = CarTypeEnum.values().map { it.displayName },
                        selectedOption = uiState.carType?.displayName ?: "",
                        onOptionSelected = { selected ->
                            viewModel.updateCarType(
                                CarTypeEnum.values().first { it.displayName == selected }
                            )
                        }
                    )
                }

                // 연료 종류
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "연료 종류",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.width(100.dp)
                    )
                    DropdownSelector(
                        options = FuelTypeEnum.values().map { it.displayName },
                        selectedOption = uiState.carFuel?.displayName ?: "",
                        onOptionSelected = { selected ->
                            viewModel.updateCarFuel(
                                FuelTypeEnum.values().first { it.displayName == selected }
                            )
                        }
                    )
                }
                Spacer(Modifier.height(16.dp))
                // 등록하기 버튼
                Button(
                    onClick = {
                        viewModel.registerExtraInfo(context) {
                            navController.navigate("home") {
                                popUpTo("signUpExtraInfo") { inclusive = true }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonColors(
                        containerColor = Color(0xFF0064FF),
                        contentColor = Color.White,
                        disabledContainerColor = Color(0xFFADADAD),
                        disabledContentColor = Color.White
                    ),
                    enabled = uiState.userName.isNotBlank()
                ) {
                    Text("등록하기")
                }
            }
        }
    )
}

@Composable
fun DropdownSelector(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier
        .fillMaxWidth()
        .border(1.dp, if (expanded) Color(0xFF0064FF) else Color(0xFFADADAD), RoundedCornerShape(4.dp))
        .clickable { expanded = true }
        .padding(12.dp)
    ) {
        Text(
            text = if (selectedOption.isNotEmpty()) selectedOption else "선택하세요",
            color = if (selectedOption.isNotEmpty()) Color.Black else Color(0xFFADADAD)
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color.White)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    },
                )
            }
        }
    }
}