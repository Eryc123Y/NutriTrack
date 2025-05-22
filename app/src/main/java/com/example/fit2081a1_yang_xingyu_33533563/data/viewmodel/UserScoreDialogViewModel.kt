package com.example.fit2081a1_yang_xingyu_33533563.data.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fit2081a1_yang_xingyu_33533563.data.model.repository.ScoreTypeDefinitionRepository
import com.example.fit2081a1_yang_xingyu_33533563.data.model.repository.UserScoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

/**
 * ViewModel for the UserScoreDialog
 * Handles loading and displaying detailed scores for a specific user
 */
class UserScoreDialogViewModel(
    private val userId: String,
    private val userScoreRepository: UserScoreRepository,
    private val scoreTypeDefinitionRepository: ScoreTypeDefinitionRepository
) : ViewModel() {
    
    private val _scores = MutableStateFlow<List<InsightsViewModel.DisplayableScore>>(emptyList())
    val scores: StateFlow<List<InsightsViewModel.DisplayableScore>> = _scores.asStateFlow()
    
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    init {
        loadUserScores()
    }
    
    private fun loadUserScores() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val userScores = userScoreRepository.getScoresByUserId(userId).firstOrNull() ?: emptyList()
                val scoreDefinitions = scoreTypeDefinitionRepository.getAllScoreTypes().firstOrNull() ?: emptyList()
                
                val displayableScores = userScores.mapNotNull { userScore ->
                    scoreDefinitions.find { it.scoreDefId == userScore.scoreTypeKey }
                        ?.let { definition ->
                            InsightsViewModel.DisplayableScore(
                                displayName = definition.scoreTypeName,
                                scoreValue = userScore.scoreValue,
                                maxScore = definition.scoreMaximum
                            )
                        }
                }.sortedBy { it.displayName != "Total HEIFA Score" } // Put Total score first
                
                _scores.value = displayableScores
            } catch (e: Exception) {
                Log.e("UserScoreDialogViewModel", "Error loading scores: ${e.message}")
                _scores.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
} 