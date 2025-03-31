package eu.tutorials.chatbotapp

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

interface ChatService {
    @POST("/chat")
    suspend fun sendMessage(@Body request: ChatRequest): ChatResponse
}

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:8000" // Change to your FastAPI backend URL

    val instance: ChatService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ChatService::class.java)
    }
}

//http://127.0.0.1:8000/docs#/default/chat_chat_post
