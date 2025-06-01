package com.bumper_car.vroomie_fe.data.remote.gpt

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

data class GptRequest(val message: String)
data class GptResponse(val reply: String)

interface GptApi {
    @POST("/gpt/ask")
    suspend fun ask(@Body request: GptRequest): Response<GptResponse>
}