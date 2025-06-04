package com.bumper_car.vroomie_fe.data.remote.kakao.model

import com.google.gson.annotations.SerializedName

data class KakaoCoord2AddressResponse(
    val documents: List<KakaoAddressDocument>
)

data class KakaoAddressDocument(
    val address: KakaoAddress?,
    @SerializedName("road_address")
    val roadAddress: KakaoRoadAddress?
)

data class KakaoAddress(
    @SerializedName("address_name")
    val addressName: String
)

data class KakaoRoadAddress(
    @SerializedName("address_name")
    val addressName: String
)