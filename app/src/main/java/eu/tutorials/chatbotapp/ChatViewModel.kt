package eu.tutorials.chatbotapp

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

data class ChatRequest(val query: String)
data class ChatResponse(val response: String)
data class MessageModel(val message: String, val role: String)  // "user" or "model"

class ChatViewModel : ViewModel() {
    private val _messageList = MutableStateFlow<List<MessageModel>>(emptyList())
    val messageList: StateFlow<List<MessageModel>> = _messageList

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun sendMessage(userMessage: String) {
        val updatedMessages = _messageList.value.toMutableList().apply {
            add(MessageModel(userMessage, "user"))
        }
        _messageList.value = updatedMessages
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.sendMessage(ChatRequest(userMessage))
                Log.d("ChatResponse", "Response: ${response.response}")

                val newMessages = _messageList.value.toMutableList().apply {
                    add(MessageModel(response.response, "model"))
                }
                _messageList.value = newMessages
            } catch (e: IOException) {
                _errorMessage.value = "Network error: Please check your connection"
                Log.e("ChatError", "Network error", e)
            } catch (e: HttpException) {
                _errorMessage.value = "Server error: ${e.message()}"
                Log.e("ChatError", "HTTP error", e)
            } catch (e: Exception) {
                _errorMessage.value = "Unexpected error: ${e.localizedMessage}"
                Log.e("ChatError", "Unexpected error", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}