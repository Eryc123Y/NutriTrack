package com.example.fit2081a1_yang_xingyu_33533563.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fit2081a1_yang_xingyu_33533563.view.UiState
import com.example.fit2081a1_yang_xingyu_33533563.BuildConfig
import com.example.fit2081a1_yang_xingyu_33533563.data.model.repository.ChatRepository
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * GenAIViewModel is a ViewModel class that handles the logic for the GenAI feature.
 * It communicates with the repository to fetch and update data.
 */
class GenAIViewModel(
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Initial)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    
    // Keep track of the current session ID
    private var currentSessionId = UUID.randomUUID().toString()
    
    // For conversation history
    val conversationHistory = chatRepository.getConversationFlow()

    // Optional userId for tracking user-specific conversations
    private var currentUserId: Long? = null

    private val genAIModel = GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey = BuildConfig.API_KEY
    )

    fun sendRequest(prompt: String, userId: Long? = null) {
        _uiState.value = UiState.Loading
        
        // Remember the user ID if provided
        if (userId != null) {
            currentUserId = userId
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Save the user message first
                chatRepository.saveUserMessage(
                    message = prompt,
                    userId = currentUserId,
                    sessionId = currentSessionId
                )
                
                // Generate response from AI
                val response = genAIModel.generateContent(
                    content {
                        text(prompt)
                    }
                )
                
                if (response.text != null) {
                    val aiResponse = response.text!!
                    
                    // Save the AI response
                    chatRepository.saveAiResponse(
                        response = aiResponse,
                        userId = currentUserId,
                        sessionId = currentSessionId
                    )
                    
                    // Update UI state
                    _uiState.value = UiState.Success(aiResponse)
                } else {
                    _uiState.value = UiState.Error.UnidentifiedError("No response generated")
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error.NetworkError(e.message ?: "Unknown error occurred")
            }
        }
    }
    
    /**
     * Start a new conversation session
     */
    fun startNewSession() {
        currentSessionId = UUID.randomUUID().toString()
    }
    
    /**
     * Set the current user ID
     */
    fun setUserId(userId: Long?) {
        currentUserId = userId
        
        // If userId is provided, we can update to get user-specific conversations
        if (userId != null) {
            val userConversationHistory = chatRepository.getUserConversationFlow(userId)
        }
    }
    
    /**
     * Get user-specific conversation history
     */
    fun getUserConversationHistory(userId: Long) = chatRepository.getUserConversationFlow(userId)
    
    /**
     * Clear all messages from the current session
     */
    fun clearCurrentSession() {
        viewModelScope.launch(Dispatchers.IO) {
            chatRepository.clearSessionMessages(currentSessionId)
        }
    }
    
    /**
     * Clear all messages for the current user
     */
    fun clearUserHistory() {
        currentUserId?.let { userId ->
            viewModelScope.launch(Dispatchers.IO) {
                chatRepository.clearUserMessages(userId)
            }
        }
    }
    
    /**
     * Clear all chat history
     */
    fun clearAllHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            chatRepository.clearAllMessages()
        }
    }
}