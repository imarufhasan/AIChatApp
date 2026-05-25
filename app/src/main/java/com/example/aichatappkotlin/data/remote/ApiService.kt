package com.example.aichatappkotlin.data.remote


import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("chat")
    suspend fun sendMessage(
        @Body request: ChatRequest
    ): ChatResponse
}