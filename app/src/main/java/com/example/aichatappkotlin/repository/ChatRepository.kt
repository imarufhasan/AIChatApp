package com.example.aichatappkotlin.repository


import com.example.aichatappkotlin.data.remote.ChatRequest
import com.example.aichatappkotlin.network.RetrofitClient

class ChatRepository {
    suspend fun sendMessage(message: String): Result<String> {
        return try {
            val response = RetrofitClient.api.sendMessage(ChatRequest(message))
            val reply = response.reply ?: response.error ?: "No response"
            Result.success(reply)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}