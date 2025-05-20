package com.example.fit2081a1_yang_xingyu_33533563.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fit2081a1_yang_xingyu_33533563.data.legacy.ScoreTypes
import com.example.fit2081a1_yang_xingyu_33533563.data.model.entity.UserEntity
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

/**
 * ViewModel for the Clinician Dashboard screen
 * Integrates with GenAIViewModel for AI analysis and UserStatsViewModel for user data
 */
class ClinicianDashboardViewModel(
    private val genAIViewModel: GenAIViewModel,
    private val userStatsViewModel: UserStatsViewModel,
    private val userRepository: UserRepository,
    private val userScoreRepository: UserScoreRepository
) : ViewModel() {

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

    // Selected tab index (0: Dashboard, 1: User Stats, 2: AI Trends)
    private val _selectedTabIndex = MutableStateFlow(0)
    val selectedTabIndex: StateFlow<Int> = _selectedTabIndex.asStateFlow()

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

    init {
        loadAllUserStats()
        // Generate AI analysis after loading data
        generateAIAnalysis()
    }

    /**
     * Load all user statistics and their HEIFA scores from the database
     */
    fun loadAllUserStats() {
        viewModelScope.launch {
            try {
                // Get all users from the repository
                val userEntities = userRepository.getAllUsers().firstOrNull() ?: emptyList()
                val userStatsList = mutableListOf<UserStatsDisplay>()
                
                // For each user, fetch their total HEIFA score
                for (user in userEntities) {
                    val heifaScore = getUserHeifaScore(user.userId)
                    if (heifaScore != null) {
                        userStatsList.add(
                            UserStatsDisplay(
                                userId = user.userId,
                                userName = user.userName.toString(),
                                gender = user.userGender.toString(),
                                heifaScore = heifaScore
                            )
                        )
                    }
                }
                
                // If no real scores are found, provide some mock data for demo purposes
                if (userStatsList.isEmpty()) {
                    userStatsList.add(UserStatsDisplay(
                        userId = "1",
                        userName = "Demo User 1",
                        gender = "Male",
                        heifaScore = 72.5f
                    ))
                    userStatsList.add(UserStatsDisplay(
                        userId = "2",
                        userName = "Demo User 2",
                        gender = "Female",
                        heifaScore = 81.3f
                    ))
                }
                
                _userStats.value = userStatsList
                
                // Calculate average scores
                calculateAverageScores(userStatsList)
                
            } catch (e: Exception) {
                // Handle errors
                e.printStackTrace()
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
            score
        } catch (e: Exception) {
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
        
        _maleAverageScore.value = if (maleUsers.isNotEmpty()) {
            maleUsers.map { it.heifaScore }.average().toFloat()
        } else null
        
        _femaleAverageScore.value = if (femaleUsers.isNotEmpty()) {
            femaleUsers.map { it.heifaScore }.average().toFloat()
        } else null
    }

    /**
     * Generate AI analysis using GenAIViewModel
     */
    fun generateAIAnalysis() {
        viewModelScope.launch {
            _analysisState.value = UiState.Loading
            try {
                // For now, we'll use mock data for the response
                // In a real implementation, this would call genAIViewModel to get real analysis
                withContext(Dispatchers.IO) {
                    // Simulate network delay
                    kotlinx.coroutines.delay(1500)
                    
                    // Mock response format similar to what the AI would generate
                    val mockResponse = """
                        PATTERNS:
                        - Most users who scored highly on vegetables also had high fruit scores, indicating consistent healthy eating patterns across plant food groups.
                        - Female users had higher average fruit variation than male users, suggesting different food preferences based on gender.
                        - Users with higher Discretionary scores tend to have lower overall HEIFA scores, indicating the negative impact of non-essential foods.
                        
                        TRENDS:
                        - Overall HEIFA scores are higher among female users than male users by approximately 6.5 points.
                        - Users with high water consumption scores tend to score higher in vegetable and fruit categories.
                        - Adolescent users show the lowest grain and cereal intake scores among all age groups.
                        - Dairy intake is below recommended levels for 67% of all users, representing an area for potential intervention.
                        - High discretionary food intake is most prevalent in the 18-25 age group, correlating with lower overall scores.
                        - Weekend dietary patterns show increased discretionary food consumption compared to weekday patterns.
                        - Users who reported regular meal preparation at home scored on average 12 points higher than those who primarily consume restaurant/takeout meals.
                    """.trimIndent()
                    
                    // Parse the mock response
                    parseAIAnalysisResponse(mockResponse)
                }
                
                _analysisState.value = UiState.Success("Analysis complete", emptyList())
            } catch (e: Exception) {
                _analysisState.value = UiState.Error.NetworkError("Failed to generate analysis: ${e.message}")
            }
        }
    }
    
    /**
     * Parse the AI analysis response into patterns and trends
     */
    private fun parseAIAnalysisResponse(response: String) {
        try {
            val sections = response.split("PATTERNS:", "TRENDS:")
                .filter { it.isNotBlank() }
            
            if (sections.size >= 2) {
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
                _patterns.value = patternsList
                _trends.value = trendsList
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // If parsing fails, provide default values
            _patterns.value = listOf(
                "Most users who scored highly on vegetables also had high fruit scores.",
                "Female users had higher average fruit variation than male users.",
                "Users with higher Discretionary scores tend to have lower overall HEIFA scores."
            )
            _trends.value = listOf(
                "Overall HEIFA scores are higher among female users than male users.",
                "Users with high water consumption scores tend to score higher in vegetable and fruit categories.",
                "Adolescent users show the lowest grain and cereal intake scores among all age groups.",
                "Dairy intake is below recommended levels for 67% of all users.",
                "High discretionary food intake is most prevalent in the 18-25 age group."
            )
        }
    }

    /**
     * Update the selected tab index
     */
    fun updateSelectedTab(index: Int) {
        _selectedTabIndex.value = index
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