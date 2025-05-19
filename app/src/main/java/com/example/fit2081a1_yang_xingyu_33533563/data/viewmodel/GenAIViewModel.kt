package com.example.fit2081a1_yang_xingyu_33533563.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fit2081a1_yang_xingyu_33533563.view.UiState
import com.example.fit2081a1_yang_xingyu_33533563.BuildConfig
import com.example.fit2081a1_yang_xingyu_33533563.data.model.repository.ChatRepository
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch
import java.util.UUID
import com.example.fit2081a1_yang_xingyu_33533563.data.model.entity.ChatMessageEntity as ChatEntity

/**
 * GenAIViewModel is a ViewModel class that handles the logic for the GenAI feature.
 * It communicates with the repository to fetch and update data.
 */
class GenAIViewModel(
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Initial)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    
    private var vmCurrentUserId: Long? = null
    private var vmCurrentSessionId: String = UUID.randomUUID().toString()

    private val _currentUserIdSF = MutableStateFlow<Long?>(null)
    private val _currentSessionIdSF = MutableStateFlow(this.vmCurrentSessionId)

    // Reactive conversation history for the current user and session
    @OptIn(ExperimentalCoroutinesApi::class)
    val conversationHistory: StateFlow<List<ChatEntity>> =
        _currentUserIdSF.flatMapLatest { userId ->
            if (userId == null) {
                flowOf(emptyList())
            } else {
                _currentSessionIdSF.flatMapLatest { sessionId ->
                    chatRepository.getUserConversationFlow(userId).map { messages ->
                        messages.filter { it.sessionId == sessionId }
                    }
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // State for chat search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Try simpler model first to verify API key works
    private val genAIModel = GenerativeModel(
        // Keeping user's preference for the preview model
        modelName = "gemini-2.5-flash-preview-04-17",
        // Correct API key for the model
        apiKey = BuildConfig.MAPS_API_KEY
    )

    fun sendRequest(prompt: String, userIdFromRequest: Long? = null) {
        _uiState.value = UiState.Loading
        
        if (userIdFromRequest != null && this.vmCurrentUserId != userIdFromRequest) {
            this.vmCurrentUserId = userIdFromRequest
            _currentUserIdSF.value = userIdFromRequest
        } else if (this.vmCurrentUserId == null && userIdFromRequest != null) {
            this.vmCurrentUserId = userIdFromRequest
            _currentUserIdSF.value = userIdFromRequest
        }

        val requestUserId = this.vmCurrentUserId
        val requestSessionId = this.vmCurrentSessionId

        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Use the ViewModel's currentUserId and currentSessionId for consistency
                println("GenAIViewModel: Sending prompt: '$prompt' for UserID: $requestUserId, SessionID: $requestSessionId, Model: ${genAIModel.modelName}")

                chatRepository.saveUserMessage(
                    message = prompt,
                    userId = requestUserId,
                    sessionId = requestSessionId
                )
                
                val themedPrompt = "You are NutriCoach, a helpful assistant for nutrition. Answer the user\\'s query: $prompt"
                val response = genAIModel.generateContent(content { text(themedPrompt) })
                
                response.text?.let { aiResponse ->
                    println("GenAIViewModel: Response received for UserID: $requestUserId, SessionID: $requestSessionId.")
                    chatRepository.saveAiResponse(
                        response = aiResponse,
                        userId = requestUserId,
                        sessionId = requestSessionId
                    )
                    _uiState.value = UiState.Success(aiResponse)
                } ?: run {
                    println("GenAIViewModel: No response text for UserID: $requestUserId, SessionID: $requestSessionId. Finish Reason: ${response.candidates.firstOrNull()?.finishReason}, Safety: ${response.candidates.firstOrNull()?.safetyRatings}")
                    _uiState.value = UiState.Error.UnidentifiedError("No response generated. Check logs for details.")
                }
            } catch (e: Exception) {
                println("GenAIViewModel: Error during API call for UserID: $requestUserId, SessionID: $requestSessionId: ${e.javaClass.simpleName} - ${e.message}")
                val errorMessage = e.message ?: "Unknown error occurred"
                when {
                    errorMessage.contains("403") -> _uiState.value = UiState.Error.ApiError("API permission denied (403). Verify API key and model access.")
                    errorMessage.contains("404") -> _uiState.value = UiState.Error.ApiError("Model not found (404). Check model name.")
                    errorMessage.contains("MissingFieldException") -> _uiState.value = UiState.Error.ApiError("API response format issue.")
                    else -> _uiState.value = UiState.Error.NetworkError("Error: $errorMessage")
                }
                // e.printStackTrace() // Optionally uncomment for full stack trace during development
            }
        }
    }
    
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
    
    /**
     * Start a new conversation session
     */
    fun startNewSession() {
        val newSessionId = UUID.randomUUID().toString()
        this.vmCurrentSessionId = newSessionId
        _currentSessionIdSF.value = newSessionId
        _uiState.value = UiState.Initial
        // Search query should ideally be cleared if the context (session) changes completely.
        // updateSearchQuery("") // Consider if this is desired here.
        println("GenAIViewModel: Started new session: ${this.vmCurrentSessionId} for UserID: ${this.vmCurrentUserId}")
    }
    
    /**
     * Set the current user ID
     */
    fun setUserId(userId: Long?) {
        val oldUserId = this.vmCurrentUserId
        this.vmCurrentUserId = userId
        _currentUserIdSF.value = userId
        // The CoachScreen calls startNewSession() after this,
        // which will update currentSessionId and _currentSessionIdSF.
        println("GenAIViewModel: User ID changed from $oldUserId to: ${this.vmCurrentUserId}. A new session should follow if user changed.")
    }
    
    /**
     * Get user-specific conversation history
     */
    fun getUserConversationHistory(userId: Long) = chatRepository.getUserConversationFlow(userId)
    
    /**
     * Clear all messages from the current session
     */
    fun clearCurrentChatSession() {
        val sessionToClear = this.vmCurrentSessionId
        val userContext = this.vmCurrentUserId
        viewModelScope.launch(Dispatchers.IO) {
            chatRepository.clearSessionMessages(sessionToClear)
            // The conversationHistory flow will automatically update to an empty list
            // for the current session due to its reactive nature.
            // Clearing search query makes sense as the context is cleared.
            updateSearchQuery("")
            println("GenAIViewModel: Cleared messages for SessionID: $sessionToClear, UserID: $userContext")
        }
    }
    
    /**
     * Clear all messages for the current user
     */
    fun clearUserHistory() {
        this.vmCurrentUserId?.let { userIdToClear ->
            viewModelScope.launch(Dispatchers.IO) {
                chatRepository.clearUserMessages(userIdToClear)
                // If current user's history is cleared, the current session is part of that.
                // So, might need to also effectively "reset" the current session view.
                // The conversationHistory will update.
                // Consider also calling startNewSession() or updateSearchQuery("").
                updateSearchQuery("")
                println("GenAIViewModel: Cleared all messages for UserID: $userIdToClear")
            }
        }
    }
    
    /**
     * Clear all chat history
     */
    fun clearAllHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            chatRepository.clearAllMessages()
            // This clears everything. The current session view will become empty.
            updateSearchQuery("")
            println("GenAIViewModel: Cleared all chat history from repository.")
        }
    }
}