package com.example.fit2081a1_yang_xingyu_33533563.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fit2081a1_yang_xingyu_33533563.data.legacy.ScoreTypes
import com.example.fit2081a1_yang_xingyu_33533563.data.model.entity.UserFoodPreferenceEntity
import com.example.fit2081a1_yang_xingyu_33533563.data.model.entity.UserScoreEntity
import com.example.fit2081a1_yang_xingyu_33533563.data.model.repository.FoodCategoryDefinitionRepository
import com.example.fit2081a1_yang_xingyu_33533563.data.model.repository.PersonaRepository
import com.example.fit2081a1_yang_xingyu_33533563.data.model.repository.UserFoodCategoryPreferenceRepository
import com.example.fit2081a1_yang_xingyu_33533563.data.model.repository.UserRepository
import com.example.fit2081a1_yang_xingyu_33533563.data.model.repository.UserScoreRepository
import com.example.fit2081a1_yang_xingyu_33533563.data.model.repository.UserTimePreferenceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Data class to hold user statistics relevant for GenAI prompts.
 * All fields are nullable as they might not always be available.
 */
data class UserStats(
    // Basic user preferences
    val persona: String? = null,
    val biggestMealTime: String? = null,
    val sleepTime: String? = null,
    val wakeUpTime: String? = null,
    
    // All scores as a map of scoreId to scoreValue
    val allScores: Map<String, Float>? = null,
    
    // Food preferences as a map of category IDs to boolean preference values
    val foodPreferences: Map<String, Boolean>? = null
)

class UserStatsViewModel(
    private val userRepository: UserRepository,
    private val userScoreRepository: UserScoreRepository,
    private val personaRepository: PersonaRepository,
    private val userTimePreferenceRepository: UserTimePreferenceRepository,
    private val userFoodCategoryPreferenceRepository: UserFoodCategoryPreferenceRepository
) : ViewModel() {

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _userPersona = MutableStateFlow<String?>(null)
    val userPersona: StateFlow<String?> = _userPersona

    private val _userBiggestMealTime = MutableStateFlow<String?>(null)
    val userBiggestMealTime: StateFlow<String?> = _userBiggestMealTime

    private val _userSleepTime = MutableStateFlow<String?>(null)
    val userSleepTime: StateFlow<String?> = _userSleepTime

    private val _userWakeUpTime = MutableStateFlow<String?>(null)
    val userWakeUpTime: StateFlow<String?> = _userWakeUpTime

    private val _userFruitScore = MutableStateFlow<Float?>(null)
    val userFruitScore: StateFlow<Float?> = _userFruitScore
    
    private val _userScores = MutableStateFlow<Map<String, Float>>(emptyMap())
    val userScores: StateFlow<Map<String, Float>> = _userScores
    
    private val _userFoodPreferences = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val userFoodPreferences: StateFlow<Map<String, Boolean>> = _userFoodPreferences

    /**
     * Get a complete UserStats object for the given user ID.
     * This combines all the individual user statistics into a single object.
     */
    suspend fun getUserStats(userId: String): UserStats {
        // Update all the StateFlows
        getUserPersona(userId)
        getUserBiggestMealTime(userId)
        getUserSleepTime(userId)
        getUserWakeUpTime(userId)
        getUserAllScores(userId)
        getUserFoodPreferences(userId)
        
        // Use a small delay to allow the StateFlows to update
        kotlinx.coroutines.delay(100)
        
        // Get all scores
        val collectedScores = _userScores.value
        
        // Build and return the UserStats object
        return UserStats(
            persona = _userPersona.value,
            biggestMealTime = _userBiggestMealTime.value,
            sleepTime = _userSleepTime.value,
            wakeUpTime = _userWakeUpTime.value,
            allScores = collectedScores,
            foodPreferences = _userFoodPreferences.value
        )
    }

    suspend fun getUserFruitScore(userId: String): Float {
        return userScoreRepository.getScore(userId, ScoreTypes.FRUITS.scoreId)
    }
    
    suspend fun getUserAllScores(userId: String) {
        viewModelScope.launch {
            try {
                val scores = userScoreRepository.getScoresByUserId(userId).firstOrNull() ?: emptyList()
                val scoreMap = scores.associate { it.scoreTypeKey to it.scoreValue }
                _userScores.value = scoreMap
                
                // Update the individual fruit score StateFlow for any legacy consumers
                _userFruitScore.value = scoreMap[ScoreTypes.FRUITS.scoreId]
            } catch (e: Exception) {
                _errorMessage.value = "Error fetching user scores: ${e.message}"
                _userScores.value = emptyMap()
            }
        }
    }
    
    suspend fun getUserFoodPreferences(userId: String) {
        viewModelScope.launch {
            try {
                val preferences = userFoodCategoryPreferenceRepository
                    .getPreferencesByUserId(userId).firstOrNull() ?: emptyList()
                
                val preferencesMap = preferences.associate { 
                    it.foodPrefCategoryKey to it.foodPrefCheckedStatus 
                }
                
                _userFoodPreferences.value = preferencesMap
            } catch (e: Exception) {
                _errorMessage.value = "Error fetching food preferences: ${e.message}"
                _userFoodPreferences.value = emptyMap()
            }
        }
    }

    fun getUserPersona(userId: String) {
        viewModelScope.launch {
            val userPersonaId = userRepository.getUserPersonaId(userId)
            personaRepository.getPersonaById(userPersonaId)
                .map { it?.toString() }
                .catch { e ->
                    _errorMessage.value = "Error fetching user persona: ${e.message}"
                    _userPersona.value = null
                }
                .collect { persona ->
                    _userPersona.value = persona
                }
        }
    }

    fun getUserBiggestMealTime(userId: String) {
        viewModelScope.launch {
            userTimePreferenceRepository.getBiggestMealTime(userId)
                .catch { e ->
                    _errorMessage.value = "Error fetching meal time: ${e.message}"
                    _userBiggestMealTime.value = null
                }
                .collect { mealTime ->
                    _userBiggestMealTime.value = mealTime
                }
        }
    }

    fun getUserSleepTime(userId: String) {
        viewModelScope.launch {
            userTimePreferenceRepository.getSleepTime(userId)
                .catch { e ->
                    _errorMessage.value = "Error fetching sleep time: ${e.message}"
                    _userSleepTime.value = null
                }
                .collect { sleepTime ->
                    _userSleepTime.value = sleepTime
                }
        }
    }

    fun getUserWakeUpTime(userId: String) {
        viewModelScope.launch {
            userTimePreferenceRepository.getWakeUpTime(userId)
                .catch { e ->
                    _errorMessage.value = "Error fetching wake-up time: ${e.message}"
                    _userWakeUpTime.value = null
                }
                .collect { wakeUpTime ->
                    _userWakeUpTime.value = wakeUpTime
                }
        }
    }
}