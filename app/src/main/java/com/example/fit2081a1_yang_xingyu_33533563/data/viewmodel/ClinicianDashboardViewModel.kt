package com.example.fit2081a1_yang_xingyu_33533563.data.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fit2081a1_yang_xingyu_33533563.data.legacy.ScoreTypes
import com.example.fit2081a1_yang_xingyu_33533563.data.model.repository.UserRepository
import com.example.fit2081a1_yang_xingyu_33533563.data.model.repository.UserScoreRepository
import com.example.fit2081a1_yang_xingyu_33533563.view.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.json.JSONArray
import org.json.JSONObject

/**
 * ViewModel for the Clinician Dashboard screen
 * Integrates with GenAIViewModel for AI analysis and UserStatsViewModel for user data
 */
class ClinicianDashboardViewModel(
    private val userRepository: UserRepository,
    private val userScoreRepository: UserScoreRepository,
    private val genAIViewModel: GenAIViewModel
) : ViewModel() {

    companion object {
        private const val TAG = "ClinicianDashboardVM"
    }

    // AI Analysis state
    private val _analysisState = MutableStateFlow<UiState>(UiState.Initial)
    val analysisState: StateFlow<UiState> = _analysisState.asStateFlow()

    // Patterns identified by AI
    private val _patterns = MutableStateFlow<List<String>>(emptyList())
    val patterns: StateFlow<List<String>> = _patterns.asStateFlow()

    // Trends identified by AI
    private val _trends = MutableStateFlow<List<String>>(emptyList())
    val trends: StateFlow<List<String>> = _trends.asStateFlow()

    // User stats (for displaying in the dashboard)
    private val _userStats = MutableStateFlow<List<UserStatsDisplay>>(emptyList())
    val userStats: StateFlow<List<UserStatsDisplay>> = _userStats.asStateFlow()

    // Average HEIFA scores
    private val _maleAverageScore = MutableStateFlow<Float?>(null)
    val maleAverageScore: StateFlow<Float?> = _maleAverageScore.asStateFlow()

    private val _femaleAverageScore = MutableStateFlow<Float?>(null)
    val femaleAverageScore: StateFlow<Float?> = _femaleAverageScore.asStateFlow()

    // Selected tab index (0: Dashboard, 1: User Stats, 2: AI Trends, 3: Ask AI)
    private val _selectedTabIndex = MutableStateFlow(0)
    val selectedTabIndex: StateFlow<Int> = _selectedTabIndex.asStateFlow()
    
    // Current query for AI Q&A
    private val _clinicianQuery = MutableStateFlow("")
    val clinicianQuery: StateFlow<String> = _clinicianQuery.asStateFlow()
    
    // AI Q&A state
    private val _qaState = MutableStateFlow<UiState>(UiState.Initial)
    val qaState: StateFlow<UiState> = _qaState.asStateFlow()
    
    // AI Q&A response
    private val _qaResponse = MutableStateFlow<String>("")
    val qaResponse: StateFlow<String> = _qaResponse.asStateFlow()

    // Current system prompt for AI analysis
    private val clinicianAnalysisPrompt = """
        You are acting as a dietary data analyst for clinicians viewing a dashboard of patient nutritional data.
        
        Analyze the provided HEIFA (Healthy Eating Index for Australian Adults) data for a group of users and identify:
        
        1. Exactly 3 interesting, non-obvious, and distinct patterns related to dietary habits, demographics, or scoring correlations.
        2. Overall trends and summaries in the dataset for more detailed analysis.
        
        Your analysis should focus on correlations between different dietary components and demographic factors. Look for patterns that would be relevant for clinical decision-making.
        
        Present the 3 patterns as concise, clear text statements (1-2 sentences each).
        
        Present the overall trends as 5-7 distinct, actionable insights that could guide clinical interventions.
        
        Format your response as follows:
        PATTERNS:
        - Pattern 1
        - Pattern 2
        - Pattern 3
        
        TRENDS:
        - Trend 1
        - Trend 2
        - Trend 3
        - Trend 4
        - Trend 5
        (and so on)
    """.trimIndent()
    
    private val clinicianQAPrompt = """
        You are acting as a nutritional expert AI assistant for a clinical dietitian or healthcare provider.
        
        The clinician will ask questions about patterns, trends, and insights from the nutritional data of their patients.
        You have access to statistical summary data of all patients' HEIFA (Healthy Eating Index for Australian Adults) scores.
        
        When answering:
        1. Be concise and focused on clinical relevance
        2. Provide evidence-based interpretations
        3. Suggest potential interventions when appropriate
        4. Avoid making definitive medical diagnoses
        5. Respect patient privacy by not discussing individual identifiable cases
        
        The data includes demographic information and nutritional scores across various categories such as:
        - Total HEIFA score
        - Discretionary food intake
        - Vegetable consumption and variety
        - Fruit consumption and variety
        - Grain consumption and wholegrains ratio
        - Protein intake (meat and alternatives)
        - Dairy consumption
        - Water intake
        - Fat consumption (including saturated vs unsaturated)
        - Sodium intake
        - Added sugars
        - Alcohol consumption
        
        Answer the clinician's query based on the provided statistical data.
    """.trimIndent()

    init {
        // Load stats first; analysis will be triggered after data is loaded
        loadAllUserStats()
        observeGenAIState()
    }
    
    /**
     * Load all user statistics and their HEIFA scores from the database
     */
    fun loadAllUserStats() {
        viewModelScope.launch {
            try {
                // Get all users from the repository
                val userEntities = userRepository.getAllUsers().firstOrNull() ?: emptyList()
                Log.d(TAG, "loadAllUserStats: Found ${userEntities.size} users")
                
                val userStatsList = mutableListOf<UserStatsDisplay>()
                
                // For each user, fetch their total HEIFA score
                for (user in userEntities) {
                    try {
                        Log.d(TAG, "Processing user: ${user.userId}, gender: ${user.userGender}")
                        val heifaScore = getUserHeifaScore(user.userId)
                        Log.d(TAG, "User ${user.userId} HEIFA score: $heifaScore")
                        
                        userStatsList.add(
                            UserStatsDisplay(
                                userId = user.userId,
                                userName = user.userName ?: "User ${user.userId}",
                                gender = user.userGender ?: "Unknown",
                                heifaScore = heifaScore ?: 0f
                            )
                        )
                    } catch (e: Exception) {
                        // Log the error but continue processing other users
                        Log.e(TAG, "Error processing user ${user.userId}: ${e.message}")
                        e.printStackTrace()
                    }
                }
                
                _userStats.value = userStatsList
                Log.d(TAG, "loadAllUserStats: Created ${userStatsList.size} user stats records")
                
                // Calculate average scores
                calculateAverageScores(userStatsList)
                
                // Generate AI analysis after loading data
                generateAIAnalysis()
                
            } catch (e: Exception) {
                // Handle errors
                Log.e(TAG, "loadAllUserStats error: ${e.message}")
                e.printStackTrace()
                _analysisState.value = UiState.Error.NetworkError("Failed to load user data: ${e.message}")
            }
        }
    }
    
    /**
     * Get the HEIFA total score for a specific user
     */
    private suspend fun getUserHeifaScore(userId: String): Float? {
        return try {
            // ScoreTypes.TOTAL represents the total HEIFA score with scoreId "1"
            val score = userScoreRepository.getScore(userId, ScoreTypes.TOTAL.scoreId)
            Log.d(TAG, "getUserHeifaScore for user $userId: $score")
            score
        } catch (e: Exception) {
            // Return null rather than throwing an exception
            Log.e(TAG, "getUserHeifaScore error for user $userId: ${e.message}")
            e.printStackTrace()
            null
        }
    }

    /**
     * Calculate average HEIFA scores for males and females
     */
    private fun calculateAverageScores(userStatsList: List<UserStatsDisplay>) {
        val maleUsers = userStatsList.filter { it.gender.equals("Male", ignoreCase = true) }
        val femaleUsers = userStatsList.filter { it.gender.equals("Female", ignoreCase = true) }
        
        Log.d(TAG, "calculateAverageScores: Male users: ${maleUsers.size}, Female users: ${femaleUsers.size}")
        
        _maleAverageScore.value = if (maleUsers.isNotEmpty()) {
            val avg = maleUsers.map { it.heifaScore }.average().toFloat()
            Log.d(TAG, "Male average score: $avg")
            avg
        } else null
        
        _femaleAverageScore.value = if (femaleUsers.isNotEmpty()) {
            val avg = femaleUsers.map { it.heifaScore }.average().toFloat()
            Log.d(TAG, "Female average score: $avg")
            avg
        } else null
    }

    /**
     * Generate AI analysis using GenAIViewModel
     */
    fun generateAIAnalysis() {
        viewModelScope.launch {
            _analysisState.value = UiState.Loading
            _patterns.value = emptyList()
            _trends.value = emptyList()
            try {
                val userStatsJson = convertUserStatsToJson(_userStats.value)
                Log.d(TAG, "generateAIAnalysis: User stats JSON: $userStatsJson")
                
                val response = withContext(Dispatchers.IO) {
                    // Create a listener for the AI response
                    val responseDeferred = kotlinx.coroutines.CompletableDeferred<String>()
                    
                    // Set up a collector for the GenAIViewModel uiState with a timeout
                    val job = launch {
                        try {
                            withTimeout(60000) { // 1 minute timeout
                                genAIViewModel.uiState.collect { state ->
                                    when (state) {
                                        is UiState.Success -> {
                                            responseDeferred.complete(state.finalMessageContent)
                                        }
                                        is UiState.Error -> {
                                            val errorMsg = when(state) {
                                                is UiState.Error.ApiError -> state.errorMessage
                                                is UiState.Error.NetworkError -> state.errorMessage
                                                is UiState.Error.UnidentifiedError -> state.errorMessage
                                            }
                                            responseDeferred.completeExceptionally(Exception(errorMsg))
                                        }
                                        is UiState.Streaming -> {
                                            // Do nothing, wait for final response
                                        }
                                        else -> { /* Continue collecting until we get Success or Error */ }
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            if (!responseDeferred.isCompleted) {
                                responseDeferred.completeExceptionally(e)
                            }
                        }
                    }
                    
                    // Send the analysis request with system instructions
                    genAIViewModel.sendAnalysisRequest(
                        userStatsJson = userStatsJson,
                        systemPrompt = clinicianAnalysisPrompt
                    )
                    
                    try {
                        // Wait for the response and cancel the collector
                        val result = responseDeferred.await()
                        job.cancel()
                        result
                    } catch (e: Exception) {
                        job.cancel()
                        throw e
                    }
                }
                
                // Parse the AI response
                Log.d(TAG, "generateAIAnalysis: Got AI response: $response")
                parseAIAnalysisResponse(response)
                _analysisState.value = UiState.Success("Analysis complete", emptyList())
                
            } catch (e: Exception) {
                Log.e(TAG, "generateAIAnalysis error: ${e.message}")
                e.printStackTrace()
                _analysisState.value = UiState.Error.NetworkError("Failed to generate analysis: ${e.message}")
            }
        }
    }
    
    /**
     * Convert user stats to JSON format for AI analysis
     */
    private fun convertUserStatsToJson(userStats: List<UserStatsDisplay>): String {
        val jsonArray = JSONArray()
        
        userStats.forEach { userStat ->
            val userJson = JSONObject()
            userJson.put("userId", userStat.userId)
            userJson.put("userName", userStat.userName)
            userJson.put("gender", userStat.gender)
            userJson.put("heifaScore", userStat.heifaScore)
            jsonArray.put(userJson)
        }
        
        val statsObject = JSONObject()
        statsObject.put("maleAverageScore", _maleAverageScore.value ?: 0)
        statsObject.put("femaleAverageScore", _femaleAverageScore.value ?: 0)
        statsObject.put("users", jsonArray)
        
        val jsonString = statsObject.toString()
        Log.d(TAG, "convertUserStatsToJson: Generated JSON: $jsonString")
        return jsonString
    }
    
    /**
     * Ask a question about patient statistics
     */
    fun askQuestion(query: String) {
        if (query.isBlank()) return

        _clinicianQuery.value = query // Set the current query
        _qaState.value = UiState.Loading // Set loading state immediately

        viewModelScope.launch {
            try {
                val userStatsJson = convertUserStatsToJson(_userStats.value)
                genAIViewModel.sendClinicianQuestion(
                    query = query,
                    userStatsJson = userStatsJson,
                    systemPrompt = clinicianQAPrompt
                )
            } catch (e: Exception) {
                e.printStackTrace()
                val errorMsg = "Failed to send question: ${e.message}"
                _qaState.value = UiState.Error.NetworkError(errorMsg)
                _qaResponse.value = errorMsg
            }
        }
    }
    
    /**
     * Parse the AI analysis response into patterns and trends
     */
    private fun parseAIAnalysisResponse(response: String) {
        try {
            // First try the expected format
            val expectedSections = response.split("PATTERNS:", "TRENDS:")
                .filter { it.isNotBlank() }
            
            if (expectedSections.size >= 2) {
                // Process in expected format
                processExpectedFormat(expectedSections)
            } else {
                // Try alternative parsing approach
                processAlternativeFormat(response)
            }
            
            // If after parsing we still have empty patterns/trends, treat as error
            if (_patterns.value.isEmpty() || _trends.value.isEmpty()) {
                throw IllegalStateException("Unable to parse AI analysis response.")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    private fun processExpectedFormat(sections: List<String>) {
        // Parse patterns (first section)
        val patternsText = sections[0].trim()
        val patternsList = patternsText.split("\n")
            .map { it.trim() }
            .filter { it.startsWith("-") }
            .map { it.removePrefix("-").trim() }
        
        // Parse trends (second section)
        val trendsText = sections[1].trim()
        val trendsList = trendsText.split("\n")
            .map { it.trim() }
            .filter { it.startsWith("-") }
            .map { it.removePrefix("-").trim() }
        
        // Update state flows
        if (patternsList.isNotEmpty()) {
            _patterns.value = patternsList
        }
        
        if (trendsList.isNotEmpty()) {
            _trends.value = trendsList
        }
    }

    private fun processAlternativeFormat(response: String) {
        // Alternative parsing logic for when the response doesn't follow the expected format
        // Look for patterns and trends in a more flexible way
        
        val lines = response.split("\n").map { it.trim() }
        val patternsList = mutableListOf<String>()
        val trendsList = mutableListOf<String>()
        
        var collectingPatterns = false
        var collectingTrends = false
        
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
        
        // If we found patterns or trends, update the state flows
        if (patternsList.isNotEmpty()) {
            _patterns.value = patternsList
        }
        
        if (trendsList.isNotEmpty()) {
            _trends.value = trendsList
        }
        
        // No fallback – leave lists empty if parsing fails
    }

    /**
     * Update the selected tab index
     */
    fun updateSelectedTab(index: Int) {
        _selectedTabIndex.value = index
    }
    
    /**
     * Update the current clinician query
     */
    fun updateQuery(query: String) {
        _clinicianQuery.value = query
    }

    // Add this new function to observe GenAIViewModel's state for Q&A
    private fun observeGenAIState() {
        // Track if we're currently processing a question
        var processingQuestion = false
        
        viewModelScope.launch {
            genAIViewModel.uiState.collect { state ->
                // For Q&A, only update state if we have an active query or are processing a question
                val hasActiveQuery = _clinicianQuery.value.isNotBlank()
                
                if (hasActiveQuery) {
                    processingQuestion = true
                }
                
                if (processingQuestion) {
                    _qaState.value = state
                    
                    when (state) {
                        is UiState.Success -> {
                            _qaResponse.value = state.finalMessageContent
                            // Reset processing flag when success is received
                            processingQuestion = false
                        }
                        is UiState.Error -> {
                            _qaResponse.value = when (state) {
                                is UiState.Error.ApiError -> state.errorMessage
                                is UiState.Error.NetworkError -> state.errorMessage
                                is UiState.Error.UnidentifiedError -> state.errorMessage
                            }
                            // Reset processing flag when error is received
                            processingQuestion = false
                        }
                        is UiState.Streaming -> {
                            _qaResponse.value = state.currentMessageContent
                        }
                        // For other states, keep processing flag as is
                        else -> {}
                    }
                }
            }
        }
    }

    /**
     * Data class for displaying user stats in the dashboard
     */
    data class UserStatsDisplay(
        val userId: String,
        val userName: String,
        val gender: String,
        val heifaScore: Float
    )
} 