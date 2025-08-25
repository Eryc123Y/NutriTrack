package com.example.fit2081a1_yang_xingyu_33533563.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fit2081a1_yang_xingyu_33533563.data.model.repository.ScoreTypeDefinitionRepository
import com.example.fit2081a1_yang_xingyu_33533563.data.model.repository.UserScoreRepository
import com.example.fit2081a1_yang_xingyu_33533563.data.legacy.NutritionScores
import com.example.fit2081a1_yang_xingyu_33533563.data.legacy.ScoreTypes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

/**
 * ViewModel for managing insights and analytics data.
 * 
 * This ViewModel handles:
 * - Loading and displaying user nutrition scores
 * - Managing score type definitions and display formats
 * - Providing analytics data for the insights screen
 * - Calculating score-based metrics and progress indicators
 * 
 * @param userScoreRepository Repository for user score data operations
 * @param scoreTypeDefinitionRepository Repository for score type definitions
 */
class InsightsViewModel(
    private val userScoreRepository: UserScoreRepository,
    private val scoreTypeDefinitionRepository: ScoreTypeDefinitionRepository
) : ViewModel() {

    /**
     * Helper data class for displaying scores in the UI.
     * Combines score values with display names and maximum values for proper visualization.
     * 
     * @param displayName Human-readable name for the score type
     * @param scoreValue The actual score value achieved by the user
     * @param maxScore The maximum possible score for this type
     */
    data class DisplayableScore(
        val displayName: String,
        val scoreValue: Float,
        val maxScore: Int,
    )

    private val _userId = MutableStateFlow<String?>(null)

    private val _displayableScores = MutableStateFlow<List<DisplayableScore>>(emptyList())
    val displayableScores: StateFlow<List<DisplayableScore>> = _displayableScores.asStateFlow()

    private val _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // New state for NutritionScores for the share feature
    private val _userNutritionScoresForShare = MutableStateFlow<NutritionScores?>(null)
    val userNutritionScoresForShare: StateFlow<NutritionScores?> = _userNutritionScoresForShare.asStateFlow()

    fun setUserId(userId: String?) {
        if (_userId.value != userId) {
            _userId.value = userId
            userId?.let { loadScoresForUser(it) }
        }
    }

    private fun loadScoresForUser(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _userNutritionScoresForShare.value = null // Reset before loading
            try {
                val userScores = userScoreRepository
                    .getScoresByUserId(userId).firstOrNull() ?: emptyList()
                val scoreDefinitions = scoreTypeDefinitionRepository
                    .getAllScoreTypes().firstOrNull() ?: emptyList()

                val newDisplayScores = userScores.mapNotNull { userScore ->
                    scoreDefinitions.find { it.scoreDefId == userScore.scoreTypeKey }
                        ?.let { definition ->
                            DisplayableScore(
                                displayName = definition.scoreTypeName,
                                scoreValue = userScore.scoreValue,
                                maxScore = definition.scoreMaximum
                            )
                        }
                }
                _displayableScores.value = newDisplayScores

                // Populate userNutritionScoresForShare
                val scoreDefIdToScoreTypeEnum = ScoreTypes.entries.associateBy { it.scoreId }
                val scoresForNutritionScores = mutableMapOf<ScoreTypes, Float>()

                userScores.forEach { userScoreEntity ->
                    // Find the ScoreType enum using scoreId (which is scoreTypeKey in UserScoreEntity)
                    val scoreTypeEnum = scoreDefIdToScoreTypeEnum[userScoreEntity.scoreTypeKey]
                    if (scoreTypeEnum != null) {
                        scoresForNutritionScores[scoreTypeEnum] = userScoreEntity.scoreValue
                    }
                }
                _userNutritionScoresForShare.value = NutritionScores(scoresForNutritionScores)

            } catch (e: Exception) {
                _errorMessage.value = "Error loading scores: ${e.message}"
                _displayableScores.value = emptyList() // Clear scores on error
                _userNutritionScoresForShare.value = null // Clear on error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

}