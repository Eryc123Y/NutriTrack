package com.example.fit2081a1_yang_xingyu_33533563.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.fit2081a1_yang_xingyu_33533563.data.model.repository.ScoreTypeDefinitionRepository
import com.example.fit2081a1_yang_xingyu_33533563.data.model.repository.UserRepository
import com.example.fit2081a1_yang_xingyu_33533563.data.model.repository.UserScoreRepository
import com.example.fit2081a1_yang_xingyu_33533563.data.legacy.NutritionScores
import com.example.fit2081a1_yang_xingyu_33533563.data.legacy.ScoreTypes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlin.jvm.java

// Assuming the filename is InsightViewModel.kt based on previous exploration
class InsightsViewModel(
    private val userScoreRepository: UserScoreRepository,
    private val scoreTypeDefinitionRepository: ScoreTypeDefinitionRepository,
    private val userRepository: UserRepository // Added, as it was in the factory and constructor case in ViewModelProviderFactory
) : ViewModel() {

    // A helper data class for displaying scores - remains the same
    data class DisplayableScore(
        val displayName: String,
        val scoreValue: Float,
        val maxScore: Int,
    )

    private val _userId = MutableStateFlow<String?>(null)
    // Not directly exposed, used to trigger loads. If UI needs to observe it, expose as StateFlow.
    // val userId: StateFlow<String?> = _userId.asStateFlow()

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
                // Assuming repository methods return Flow, using firstOrNull for one-shot read
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

                // Populate userNutritionScoresForShare (new logic)
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
                // e.printStackTrace() // Avoid in ViewModel, propagate error to UI
                _errorMessage.value = "Error loading scores: ${e.message}"
                _displayableScores.value = emptyList() // Clear scores on error
                _userNutritionScoresForShare.value = null // Clear on error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refreshScores() {
        _userId.value?.let {
            loadScoresForUser(it)
        } ?: run {
            _errorMessage.value = "Cannot refresh: User ID not set."
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

}