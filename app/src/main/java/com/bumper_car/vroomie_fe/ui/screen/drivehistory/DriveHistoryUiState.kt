package com.bumper_car.vroomie_fe.ui.screen.drivehistory

import com.bumper_car.vroomie_fe.domain.model.DriveHistory

data class DriveHistoryUiState(
    val driveHistorys: List<DriveHistory> = listOf(
        DriveHistory(1, "서울역", "강남역", "2025.05.01", 80),
        DriveHistory(2, "신촌", "홍대입구", "2025.05.02", 76),
        DriveHistory(3, "잠실", "성수", "2025.05.03", 88),
        DriveHistory(4, "서울역", "강남역", "2025.05.01", 80),
        DriveHistory(5, "신촌", "홍대입구", "2025.05.02", 76),
        DriveHistory(6, "잠실", "성수", "2025.05.03", 88),
        DriveHistory(7, "서울역", "강남역", "2025.05.01", 80),
        DriveHistory(8, "신촌", "홍대입구", "2025.05.02", 76),
        DriveHistory(9, "잠실", "성수", "2025.05.03", 88)
    )
)