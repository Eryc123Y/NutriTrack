package com.example.fit2081a1_yang_xingyu_33533563.data.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fit2081a1_yang_xingyu_33533563.view.UiState
import com.example.fit2081a1_yang_xingyu_33533563.BuildConfig
import com.example.fit2081a1_yang_xingyu_33533563.data.legacy.ScoreTypes
import com.example.fit2081a1_yang_xingyu_33533563.data.model.repository.ChatRepository
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.GenerationConfig
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch
import java.util.UUID
import com.example.fit2081a1_yang_xingyu_33533563.data.model.entity.ChatMessageEntity as ChatEntity
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.delay

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
    
    private var vmCurrentUserId: String? = null
    // vmCurrentSessionId is initialized once and will be used for saving messages.
    // It no longer changes with each call to startNewSession.
    private var vmCurrentSessionId: String = UUID.randomUUID().toString()

    private val _currentUserId = MutableStateFlow<String?>(null)
    
    // Remove the commented-out clinician user ID
    // private val clinicianUserId = "clinician_system_user"

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

    // Reactive conversation history for the current user
    @OptIn(ExperimentalCoroutinesApi::class)
    val conversationHistory: StateFlow<List<ChatEntity>> =
        _currentUserId.flatMapLatest { userId ->
            if (userId == null) {
                flowOf(emptyList())
            } else {
                chatRepository.getUserConversationFlow(userId)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // State for chat search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val genAIModel = GenerativeModel(
        modelName = "gemini-2.5-flash-preview-04-17",
        apiKey = BuildConfig.MAPS_API_KEY
    )
    
    private val TYPING_DELAY_MS = 5L // Delay for typing animation

    /**
     * Create a GenerativeModel with a system instruction
     */
    private fun createModelWithSystemInstruction(systemInstructionText: String): GenerativeModel {
        return GenerativeModel(
            modelName = "gemini-2.5-flash-preview-04-17",
            apiKey = BuildConfig.MAPS_API_KEY,
            systemInstruction = content {
                text(systemInstructionText)
            }
        )
    }

    private fun buildThemedPrompt(userQuery: String, userStatsJson: String?,
                                  nutritionalGuidelinesJson: String): String {
        val statsInfo = userStatsJson?.takeIf { it.length > 2 && it != "{}" }?.let {
            " User's current stats (in JSON): $it." } ?: ""
        val guidelinesInfo = " Refer to these comprehensive nutritional guidelines (in JSON format)" +
                " for your answer and when providing recommendations: $nutritionalGuidelinesJson."
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
    
    /**
     * Send a clinician analysis request with custom system instruction
     * Uses the current logged-in user's ID for session tracking
     */
    fun sendAnalysisRequest(userStatsJson: String, systemPrompt: String) {
        _uiState.value = UiState.Loading
        
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Check if the dataset is empty or contains no users
                if (userStatsJson.contains("\"users\":[]") || 
                    !userStatsJson.contains("\"users\":[{") || 
                    (userStatsJson.contains("\"maleAverageScore\":0") && 
                    userStatsJson.contains("\"femaleAverageScore\":0"))) {
                    
                    Log.w("GenAIViewModel", "Empty dataset detected in analysis request")
                    
                    // Return a default response for empty dataset
                    val emptyDatasetResponse = """
                        PATTERNS:
                        - The dataset is empty, yielding zero average scores for both genders.
                        - Overall dietary trends cannot be assessed without user-specific nutrition data.
                        - Clinical insights require population of the `users` array with relevant HEIFA data.
                        
                        TRENDS:
                        - The current data provides no basis for guiding clinical interventions.
                        - Analysis is blocked by the absence of any user entries in the provided data structure.
                        - No user-specific food frequency patterns can be identified in the empty dataset.
                        - Demographics analysis shows no records for male or female participants.
                        - HEIFA scores cannot be assessed with the current empty dataset.
                    """.trimIndent()
                    
                    // Save the fallback response to repository
                    chatRepository.saveAiResponse(
                        response = emptyDatasetResponse,
                        userId = null,
                        sessionId = "clinician-analysis"
                    )
                    
                    // Update UI with the fallback response
                    _uiState.value = UiState.Success(emptyDatasetResponse, emptyList())
                    return@launch
                }
                
                // Create a model with the specific system instruction
                val clinicianModel = createModelWithSystemInstruction(systemPrompt)
                
                // Use a fixed session ID for analysis to bypass foreign key constraints
                val analysisSessionId = "clinician-analysis"
                
                // Save request to repository using null userId
                chatRepository.saveUserMessage(
                    message = "Generate analysis of user nutrition data", 
                    userId = null,
                    sessionId = analysisSessionId
                )
                
                // Create a prompt focusing just on the data
                val analysisPrompt = """
                    Analyze this nutrition data and provide insights: $userStatsJson
                    
                    FORMAT YOUR RESPONSE EXACTLY AS FOLLOWS:
                    
                    PATTERNS:
                    - [First pattern]
                    - [Second pattern]
                    - [Third pattern]
                    
                    TRENDS:
                    - [First trend]
                    - [Second trend]
                    - [Third trend]
                    - [Fourth trend]
                    - [Fifth trend]
                    
                    Each pattern and trend should be a single line starting with a dash.
                    This format is critical - the dashboard depends on it for parsing.
                """.trimIndent()
                
                val responseFlow = clinicianModel.generateContentStream(analysisPrompt)
                var rawAccumulatedResponse = ""
                var uiDisplayResponse = "" // For character-by-character streaming

                responseFlow.collect { responseChunk ->
                    responseChunk.text?.let { textPart ->
                        rawAccumulatedResponse += textPart // Accumulate full response for internal logic
                        for (char in textPart) {
                            uiDisplayResponse += char
                            _uiState.value = UiState.Streaming(uiDisplayResponse)
                            delay(TYPING_DELAY_MS)
                        }
                    }
                }
                
                // Ensure response is in correct format before finalizing
                val formattedResponse = ensureCorrectAnalysisFormat(rawAccumulatedResponse)
                
                // Save the formatted response to repository using null userId
                chatRepository.saveAiResponse(
                    response = formattedResponse,
                    userId = null,
                    sessionId = analysisSessionId
                )
                
                // Final UI update
                _uiState.value = UiState.Success(formattedResponse, emptyList())
            } catch (e: Exception) {
                handleApiError(e)
            }
        }
    }
    
    /**
     * Send a clinician question about patient statistics with custom system instruction
     * Uses the provided clinician ID for session tracking instead of the current user ID
     */
    fun sendClinicianQuestion(query: String, userStatsJson: String, systemPrompt: String) {
        _uiState.value = UiState.Loading
        
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Create a model with the specific system instruction
                val clinicianModel = createModelWithSystemInstruction(systemPrompt)
                
                // Use a fixed session ID prefix for clinician Q&A
                val qaSessionId = "clinician-qa"
                
                // Save request to repository using null userId to bypass foreign key constraints
                chatRepository.saveUserMessage(
                    message = query, 
                    userId = null,
                    sessionId = qaSessionId
                )
                
                // Create a prompt with the query and data
                val qaPrompt = "Query: $query\nAvailable data: $userStatsJson"
                
                val responseFlow = clinicianModel.generateContentStream(qaPrompt)
                var internalAccumulatedResponse = ""
                var uiDisplayResponse = "" // For character-by-character streaming

                responseFlow.collect { responseChunk ->
                    responseChunk.text?.let { textPart ->
                        internalAccumulatedResponse += textPart // Accumulate full response for internal logic
                        for (char in textPart) {
                            uiDisplayResponse += char
                            _uiState.value = UiState.Streaming(uiDisplayResponse)
                            delay(TYPING_DELAY_MS)
                        }
                    }
                }
                
                // Save the response to repository using null userId
                chatRepository.saveAiResponse(
                    response = internalAccumulatedResponse, // Save the complete message
                    userId = null,
                    sessionId = qaSessionId
                )
                
                // Final UI update
                _uiState.value = UiState.Success(internalAccumulatedResponse, emptyList())
            } catch (e: Exception) {
                handleApiError(e)
            }
        }
    }

    fun sendRequest(prompt: String, userIdFromRequest: String? = null) {
        _uiState.value = UiState.Loading
        
        if (userIdFromRequest != null && this.vmCurrentUserId != userIdFromRequest) {
            this.vmCurrentUserId = userIdFromRequest
            _currentUserId.value = userIdFromRequest
        } else if (this.vmCurrentUserId == null && userIdFromRequest != null) {
            this.vmCurrentUserId = userIdFromRequest
            _currentUserId.value = userIdFromRequest
        }

        val requestUserId = this.vmCurrentUserId
        val requestSessionId = this.vmCurrentSessionId

        viewModelScope.launch(Dispatchers.IO) {
            var userStatsForJson: UserStats? = null
            if (requestUserId != null) {
                userStatsForJson = userStatsViewModel.getUserStats(requestUserId)
            }
            val userStatsJson = userStatsToJson(userStatsForJson)

            try {
                val themedPrompt = buildThemedPrompt(
                    prompt,
                    userStatsJson,
                    NUTRITIONAL_GUIDELINES_JSON
                )

                chatRepository.saveUserMessage(
                    message = prompt, 
                    userId = requestUserId,
                    sessionId = requestSessionId
                )
                
                val responseFlow = genAIModel.generateContentStream(themedPrompt)
                var internalAccumulatedResponse = ""
                var uiDisplayResponse = "" // For character-by-character streaming
                var finalFollowUpQuestions = emptyList<String>()

                responseFlow.collect { responseChunk ->
                    responseChunk.text?.let { textPart ->
                        internalAccumulatedResponse += textPart // Accumulate full response for internal logic
                        // For UI streaming with delay
                        for (char in textPart) {
                            uiDisplayResponse += char
                            _uiState.value = UiState.Streaming(uiDisplayResponse)
                            delay(TYPING_DELAY_MS)
                        }
                    }
                }
                // Once streaming is complete, parse the full response for follow-ups
                var mainAnswer = internalAccumulatedResponse
                val followUpDelimiterIndex = internalAccumulatedResponse.indexOf(SUGGESTED_FOLLOW_UPS_DELIMITER)

                if (followUpDelimiterIndex != -1) {
                    mainAnswer = internalAccumulatedResponse.substring(0, followUpDelimiterIndex).trim()
                    val followUpsString = internalAccumulatedResponse.substring(followUpDelimiterIndex + SUGGESTED_FOLLOW_UPS_DELIMITER.length).trim()
                    if (followUpsString.isNotBlank()) {
                        finalFollowUpQuestions = followUpsString.split(',').map { it.trim() }.filter { it.isNotEmpty() }
                    }
                }
                
                println("GenAIViewModel: Parsed Main Answer: '$mainAnswer', Follow-ups: $finalFollowUpQuestions")

                // Save the final main answer to repository
                chatRepository.saveAiResponse(
                    response = mainAnswer, // Save the parsed main answer
                    userId = requestUserId,
                    sessionId = requestSessionId,
                )
                // Final UI update with main answer and follow-ups using the Success state
                _uiState.value = UiState.Success(mainAnswer, finalFollowUpQuestions)

            } catch (e: Exception) {
                handleApiError(e)
            }
        }
    }
    
    /**
     * Handle API and network errors
     */
    private fun handleApiError(e: Exception) {
        val errorMessage = e.message ?: "Unknown error occurred"
        when {
            errorMessage.contains("403") -> _uiState.value = UiState.Error.ApiError("API permission denied (403). Verify API key and model access.")
            errorMessage.contains("404") -> _uiState.value = UiState.Error.ApiError("Model not found (404). Check model name.")
            errorMessage.contains("MissingFieldException") -> _uiState.value = UiState.Error.ApiError("API response format issue.")
            // Check for specific streaming-related exceptions if any arise
            e is CancellationException -> {
                 _uiState.value = UiState.Error.NetworkError("Stream cancelled: ${e.message}")
            }
            e is java.net.UnknownHostException -> {
                _uiState.value = UiState.Error.NetworkError("Network error: Cannot resolve host. Check internet connection.")
            }
            e is java.net.SocketTimeoutException -> {
                _uiState.value = UiState.Error.NetworkError("Network error: Connection timed out.")
            }
            else -> _uiState.value = UiState.Error.NetworkError("Error: $errorMessage")
        }
        e.printStackTrace()
    }
    
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
    
    /**
     * Start a new conversation session
     */
    fun startNewSession() {
        _uiState.value = UiState.Initial
        // Log the existing session ID that will be used.
        println("GenAIViewModel: Started new session (or re-focused). Using SessionID: ${this.vmCurrentSessionId} for UserID: ${this.vmCurrentUserId}")
    }
    
    /**
     * Set the current user ID
     */
    fun setUserId(userId: String?) {
        val oldUserId = this.vmCurrentUserId
        this.vmCurrentUserId = userId
        _currentUserId.value = userId
    }

    /**
     * Clear all messages from the current session
     */
    fun clearCurrentChatSession() {
        this.vmCurrentSessionId // Keep for logging if needed
        val userContext = this.vmCurrentUserId
        userContext?.let { userIdToClear -> // Ensure userContext (userId) is not null
            viewModelScope.launch(Dispatchers.IO) {
                chatRepository.clearUserMessages(userIdToClear) // Changed from clearSessionMessages
                // The conversationHistory flow will automatically update.
                updateSearchQuery("")
                println("GenAIViewModel: Cleared all messages for UserID: $userIdToClear (initiated via clearCurrentChatSession)")
            }
        } ?: println("GenAIViewModel: Cannot clear chat session, UserID is null.")
    }

    /**
     * Ensures that an analysis response is in the correct format with PATTERNS and TRENDS sections
     */
    private fun ensureCorrectAnalysisFormat(response: String): String {
        // Check if response already has the required format
        if (response.contains("PATTERNS:") && response.contains("TRENDS:")) {
            return response
        }
        
        // Parse the response to extract patterns and trends
        val lines = response.split("\n").map { it.trim() }
        val patternsList = mutableListOf<String>()
        val trendsList = mutableListOf<String>()
        
        var collectingPatterns = false
        var collectingTrends = false
        
        // First try to find pattern/trend headers
        for (line in lines) {
            when {
                line.contains("PATTERN", ignoreCase = true) || 
                line.equals("Patterns:", ignoreCase = true) -> {
                    collectingPatterns = true
                    collectingTrends = false
                    continue
                }
                line.contains("TREND", ignoreCase = true) || 
                line.equals("Trends:", ignoreCase = true) -> {
                    collectingPatterns = false
                    collectingTrends = true
                    continue
                }
            }
            
            // Collect lines that look like list items
            if (line.isNotBlank() && (line.startsWith("-") || line.startsWith("•") || line.matches(Regex("\\d+\\..*")))) {
                val cleanedLine = line.replaceFirst(Regex("^[-•\\d.]+\\s*"), "").trim()
                if (cleanedLine.isNotBlank()) {
                    if (collectingPatterns) {
                        patternsList.add(cleanedLine)
                    } else if (collectingTrends) {
                        trendsList.add(cleanedLine)
                    }
                }
            }
        }
        
        // If no patterns/trends found using headers, try to extract sentences
        if (patternsList.isEmpty() && trendsList.isEmpty()) {
            val sentences = response.split(Regex("[.!?]\\s+")).filter { it.isNotBlank() }
            
            if (sentences.size >= 3) {
                patternsList.addAll(sentences.take(3).map { it.trim() })
            }
            
            if (sentences.size >= 8) {
                trendsList.addAll(sentences.drop(3).take(5).map { it.trim() })
            }
        }
        
        // Build formatted response
        val sb = StringBuilder()
        sb.appendLine("PATTERNS:")
        patternsList.forEach { sb.appendLine("- $it") }
        
        sb.appendLine()
        sb.appendLine("TRENDS:")
        trendsList.forEach { sb.appendLine("- $it") }
        
        return sb.toString()
    }
}