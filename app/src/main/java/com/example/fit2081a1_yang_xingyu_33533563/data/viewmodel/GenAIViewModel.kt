package com.example.fit2081a1_yang_xingyu_33533563.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fit2081a1_yang_xingyu_33533563.view.UiState
import com.example.fit2081a1_yang_xingyu_33533563.BuildConfig
import com.example.fit2081a1_yang_xingyu_33533563.data.legacy.ScoreTypes
import com.example.fit2081a1_yang_xingyu_33533563.data.model.repository.ChatRepository
import com.google.ai.client.generativeai.GenerativeModel
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
 * It communicates with the repository to fetch and update data, and integrates user stats.
 */
class GenAIViewModel(
    private val chatRepository: ChatRepository,
    private val userStatsViewModel: UserStatsViewModel
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Initial)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    
    private var vmCurrentUserId: Long? = null
    private var vmCurrentSessionId: String = UUID.randomUUID().toString()

    private val _currentUserIdSF = MutableStateFlow<Long?>(null)
    private val _currentSessionIdSF = MutableStateFlow(this.vmCurrentSessionId)

    companion object {
        private const val SUGGESTED_FOLLOW_UPS_DELIMITER = "SUGGESTED_FOLLOW_UPS:"
        private const val NUTRITIONAL_GUIDELINES_JSON = """
[
  {
    "food_group_nutrient": "Discretionary",
    "energy_per_serve": "600kJ",
    "max_score": 10,
    "criteria_max_score": "Males: < 3 serves\nFemales: < 2.5 serves",
    "criteria_zero_score": "Males: ≥ 6 serves\nFemales: ≥ 5.5 serves",
    "terminology_notes": "Discretionary foods: Foods high in saturated fat, added sugar, salt\nand/or alcohol that are not necessary for a healthy diet (e.g., chips,\nsweets, soft drinks).\nkJ (kilojoule): Unit of energy; 1 calorie = 4.2 kilojoules."
  },
  {
    "food_group_nutrient": "Vegetables",
    "energy_per_serve": "75g",
    "max_score": 5,
    "criteria_max_score": "Males: ≥ 6 serves\nFemales: ≥ 5 serves",
    "criteria_zero_score": "No vegetables",
    "terminology_notes": "Vegetable variety: Consuming different types of vegetables (leafy,\ncruciferous, root, etc.)\nServe size: 75g is approximately 1/2 cup cooked vegetables or 1 cup\nof leafy salad vegetables.",
    "sub_criteria": [
      {
        "name": "Variety of vegetables consumed",
        "max_score": 5,
        "criteria_max_score": "Variety of vegetables consumed",
        "criteria_zero_score": "Variety score: 0"
      }
    ]
  },
  {
    "food_group_nutrient": "Fruits",
    "energy_per_serve": "350kJ",
    "max_score": 5,
    "criteria_max_score": "≥ 2 serves",
    "criteria_zero_score": "No fruit",
    "terminology_notes": "Fruit serve: Approximately 150g (1 medium piece or 2 small pieces)\nor 350kJ.\nFruit variety: Consuming different types of fruits across categories\n(e.g., berries, citrus, stone fruits).",
    "sub_criteria": [
      {
        "name": "Variety of fruit consumed",
        "max_score": 5,
        "criteria_max_score": "> 2 varieties of fruit consumed",
        "criteria_zero_score": "Variety score: 0"
      }
    ]
  },
  {
    "food_group_nutrient": "Grains and cereals",
    "energy_per_serve": "500kJ",
    "max_score": 5,
    "criteria_max_score": "≥ 6 serves",
    "criteria_zero_score": "No grains and/or cereals",
    "terminology_notes": "Wholegrains: Grains that retain all parts of the grain (bran, germ,\nendosperm).\nRefined grains: Grains that have had the bran and germ removed.",
    "sub_criteria": [
      {
        "name": "Wholegrains consumption",
        "max_score": 5,
        "criteria_max_score": "≥ 50% wholegrains or ≥ 3 serves",
        "criteria_zero_score": "No wholegrains"
      }
    ]
  },
  {
    "food_group_nutrient": "Meat and alternatives",
    "energy_per_serve": "550kJ",
    "max_score": 10,
    "criteria_max_score": "Males: ≥ 3 serves\nFemales: ≥ 2.5 serves",
    "criteria_zero_score": "Males: ≤ 0.5 serves\nFemales: 0 serves",
    "terminology_notes": "Meat alternatives: Include eggs, nuts, seeds, legumes, tofu.\nServe size: ~65-100g cooked meat, 2 eggs, 170g tofu, 30g\nnuts/seeds."
  },
  {
    "food_group_nutrient": "Dairy and alternatives",
    "energy_per_serve": "550kJ",
    "max_score": 10,
    "criteria_max_score": "≥ 2.5 serves",
    "criteria_zero_score": "No dairy and/or alternatives",
    "terminology_notes": "Dairy alternatives: Plant-based milk and products fortified with\ncalcium.\nServe size: 250ml milk, 200g yogurt, 40g cheese."
  },
  {
    "food_group_nutrient": "Water",
    "energy_per_serve": null,
    "max_score": 5,
    "criteria_max_score": "> 50% water consumed relative to\ntotal beverages",
    "criteria_zero_score": "Did not meet 1.5L of non-alcoholic\nbeverages",
    "terminology_notes": "Total beverages: Includes all fluids consumed.\nRecommended intake: 8-10 cups (2-2.5L) of fluid daily, primarily\nfrom water."
  },
  {
    "food_group_nutrient": "Fats",
    "energy_per_serve": "10 g",
    "max_score": null,
    "criteria_max_score": null,
    "criteria_zero_score": null,
    "terminology_notes": "Saturated fat: Type of fat found in animal products and some plant\noils.\nMUFA: Monounsaturated Fatty Acids (olive oil, avocados).\nPUFA: Polyunsaturated Fatty Acids (fish, nuts, seeds).\nServe size: 10g or approximately 2 teaspoons.",
    "sub_criteria": [
      {
        "name": "Saturated fat",
        "max_score": 5,
        "criteria_max_score": "Saturated fat ≤ 10% of total energy\nintake",
        "criteria_zero_score": "Saturated fat ≥ 12% total energy\nintake"
      },
      {
        "name": "MUFA & PUFA",
        "max_score": 5,
        "criteria_max_score": "MUFA & PUFA Males: 4 serves\nMUFA & PUFA Females: 2 serves",
        "criteria_zero_score": "MUFA & PUFA Males: < 1 serve\nMUFA & PUFA Females: < 0.5\nserves"
      }
    ]
  },
  {
    "food_group_nutrient": "Sodium",
    "energy_per_serve": null,
    "max_score": 10,
    "criteria_max_score": "≤ 70 mmol (920 mg)",
    "criteria_zero_score": "> 100 mmol (3200 mg)",
    "terminology_notes": "Sodium: Main component of salt (NaCl).\nmmol: Millimole, a unit of measurement (1 mmol sodium = 23mg).\nRecommended intake: Less than 2000mg per day."
  },
  {
    "food_group_nutrient": "Added sugars",
    "energy_per_serve": null,
    "max_score": 10,
    "criteria_max_score": "< 15% of total energy intake",
    "criteria_zero_score": "> 20% of total energy intake",
    "terminology_notes": "Added sugars: Sugars added during food processing or preparation,\nnot naturally occurring in foods.\nWHO recommendation: Less than 10% of total energy from added\nsugars."
  },
  {
    "food_group_nutrient": "Alcohol",
    "energy_per_serve": "10 g = 1 std",
    "max_score": 5,
    "criteria_max_score": "≤ 1.4 standard drinks per day",
    "criteria_zero_score": "> 1.4 standard drinks per day",
    "terminology_notes": "Standard drink: Contains 10g of pure alcohol.\nExamples: 100ml wine (13% alcohol), 285ml beer (4.9% alcohol).\nGuidelines: For health reasons, consuming no alcohol is safest."
  }
]
"""

        /**
         * Converts a UserStats object to a JSON string for use in AI prompts.
         * This method handles all the fields in the UserStats class including scores and food preferences.
         */
        fun userStatsToJson(stats: UserStats?): String {
            stats ?: return "{}"
            val parts = mutableListOf<String>()
            
            // Basic user preferences
            stats.persona?.let { parts.add("\"persona\": \"$it\"") }
            stats.biggestMealTime?.let { parts.add("\"biggestMealTime\": \"$it\"") }
            stats.sleepTime?.let { parts.add("\"sleepTime\": \"$it\"") }
            stats.wakeUpTime?.let { parts.add("\"wakeUpTime\": \"$it\"") }
            
            // All nutrition scores from the map
            stats.allScores?.let { scores ->
                if (scores.isNotEmpty()) {
                    val scoreParts = scores.map { (key, value) ->
                        // Attempt to find a display name for the score key, otherwise use the key itself.
                        // This requires ScoreTypes to be accessible here, or passing a mapping.
                        // For simplicity, using the key directly. Consider enhancing if display names are crucial.
                        val scoreName = ScoreTypes.entries.find { it.scoreId == key }?.displayName?.replace(" ", "") ?: key
                        "\"$scoreName\": $value"
                    }
                    parts.add("\"nutritionScores\": {${scoreParts.joinToString(", ")}}")
                }
            }
            
            // Food preferences as a separate JSON object
            stats.foodPreferences?.let { prefs ->
                if (prefs.isNotEmpty()) {
                    val prefParts = prefs.map { (key, value) -> "\"$key\": $value" }
                    parts.add("\"foodPreferences\": {${prefParts.joinToString(", ")}}")
                }
            }

            return if (parts.isEmpty()) "{}" else "{${parts.joinToString(", ")}}"
        }
    }

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

    private fun buildThemedPrompt(userQuery: String, userStatsJson: String?, nutritionalGuidelinesJson: String): String {
        val statsInfo = userStatsJson?.takeIf { it.length > 2 && it != "{}" }?.let { " User's current stats (in JSON): $it." } ?: ""
        val guidelinesInfo = " Refer to these comprehensive nutritional guidelines (in JSON format) for your answer and when providing recommendations: $nutritionalGuidelinesJson."
        // Updated prompt to request follow-up questions
        return "You are NutriCoach, a helpful and friendly AI nutrition assistant.$statsInfo$guidelinesInfo " +
               "Please provide a concise and informative answer to the user's query: \"$userQuery\". " +
               "Your answer should be grounded in the provided nutritional guidelines. " +
               "After your main answer, on a new separate line, write '$SUGGESTED_FOLLOW_UPS_DELIMITER' " +
               "followed by a comma-separated list of 2-3 brief follow-up questions the user might find helpful. " +
               "Ensure the follow-up questions are relevant to the query and the user's potential nutritional goals. " +
               "Example of follow-up format: \n$SUGGESTED_FOLLOW_UPS_DELIMITER" +
               "What are benefits of X?,How can I track Y better?,Tell me more about Z."
    }

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
            var userStatsForJson: UserStats? = null
            if (requestUserId != null) {
                val userIdStr = requestUserId.toString()

                // Use the new comprehensive method to get all user stats at once
                userStatsForJson = userStatsViewModel.getUserStats(userIdStr)
            }
            val userStatsJson = userStatsToJson(userStatsForJson)

            try {
                val themedPrompt = buildThemedPrompt(prompt, userStatsJson, NUTRITIONAL_GUIDELINES_JSON)
                println("GenAIViewModel: Sending themed prompt: '$themedPrompt' for UserID: $requestUserId, SessionID: $requestSessionId")

                chatRepository.saveUserMessage(
                    message = prompt, 
                    userId = requestUserId,
                    sessionId = requestSessionId
                )
                
                val response = genAIModel.generateContent(themedPrompt)
                
                response.text?.let { rawAiResponse ->
                    println("GenAIViewModel: Raw AI Response received: '$rawAiResponse'")
                    var mainAnswer = rawAiResponse
                    var followUpQuestions = emptyList<String>()

                    val followUpDelimiterIndex = rawAiResponse.indexOf(SUGGESTED_FOLLOW_UPS_DELIMITER)
                    if (followUpDelimiterIndex != -1) {
                        mainAnswer = rawAiResponse.substring(0, followUpDelimiterIndex).trim()
                        val followUpsString = rawAiResponse.substring(followUpDelimiterIndex + SUGGESTED_FOLLOW_UPS_DELIMITER.length).trim()
                        if (followUpsString.isNotBlank()) {
                            followUpQuestions = followUpsString.split(',').map { it.trim() }.filter { it.isNotEmpty() }
                        }
                    }
                    
                    println("GenAIViewModel: Parsed Main Answer: '$mainAnswer', Follow-ups: $followUpQuestions")

                    chatRepository.saveAiResponse(
                        response = mainAnswer, 
                        userId = requestUserId,
                        sessionId = requestSessionId,
                    )
                    _uiState.value = UiState.Success(mainAnswer, followUpQuestions)
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