package com.bumper_car.vroomie_fe.ui.screen.login

data class SignUpExtraInfoUiState(
    val userName: String = "",
    val carModel: String? = null,
    val carHipass: Boolean? = null,
    val carType: CarTypeEnum? = null,
    val carFuel: FuelTypeEnum? = null
)

enum class FuelTypeEnum(val displayName: String) {
    GASOLINE("휘발유"),
    DIESEL("경유"),
    ELECTRIC("전기"),
    LPG("LPG"),
    PREMIUM_GASOLINE("고급휘발유");

    override fun toString(): String = displayName
}

enum class CarTypeEnum(val displayName: String) {
    COMPACT("경차"),
    PASSENGER("승용차"),
    SMALL_TRUCK("소형화물차"),
    MEDIUM("중형차"),
    LARGE("대형차"),
    HEAVY_TRUCK("대형화물차"),
    SPECIAL_TRUCK("특수화물차");

    override fun toString(): String = displayName
}
