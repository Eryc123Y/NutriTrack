package com.example.fit2081a1_yang_xingyu_33533563.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fit2081a1_yang_xingyu_33533563.data.model.repository.ScoreTypeDefinitionRepository
import com.example.fit2081a1_yang_xingyu_33533563.data.model.repository.UserRepository
import com.example.fit2081a1_yang_xingyu_33533563.data.model.repository.UserScoreRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// Helper data class for richer UI display
data class DisplayableScore(
    val scoreTypeKey: String,
    val displayName: String,
    val scoreValue: Float,
    val maxScore: Float,
    val description: String?
)

@OptIn(ExperimentalCoroutinesApi::class)
class InsightsViewModel(
    private val userScoreRepository: UserScoreRepository,
    private val scoreTypeDefinitionRepository: ScoreTypeDefinitionRepository,
    private val userRepository: UserRepository // If user-specific info is needed on this screen
    // Assuming currentUserId is provided or observed from AuthViewModel
) : ViewModel() {

    private val _userIdFlow = MutableStateFlow<String?>(null)

    fun setUserId(userId: String?) {
        _userIdFlow.value = userId
    }

    val displayableScores: StateFlow<List<DisplayableScore>> = _userIdFlow.flatMapLatest { userId ->
        if (userId == null) {
            flowOf(emptyList())
        } else {
            combine(
                userScoreRepository.getScoresByUserId(userId),
                scoreTypeDefinitionRepository.getAllScoreTypes()
            ) { userScores, scoreDefinitions ->
                userScores.mapNotNull { userScore ->
                    scoreDefinitions.find { it.scoreTypeKey == userScore.scoreTypeKey }
                        ?.let { definition ->
                            DisplayableScore(
                                scoreTypeKey = userScore.scoreTypeKey,
                                displayName = definition.displayName,
                                scoreValue = userScore.scoreValue,
                                maxScore = definition.maxScore,
                                description = definition.description
                            )
                        }
                }
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // You might also expose raw scores or definitions if needed
    // val userScores: StateFlow<List<UserScoreEntity>> = ...
    // val scoreDefinitions: StateFlow<List<ScoreTypeDefinitionEntity>> = ...

    // Example function to trigger score calculation or refresh if needed
    fun refreshScores(userId: String) {
        viewModelScope.launch {
            // Logic to re-calculate or fetch latest scores if applicable
            // For now, relying on Flow to update automatically
            // This function can be used if scores are calculated on demand
        }
    }
}