package com.example.fit2081a1_yang_xingyu_33533563.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fit2081a1_yang_xingyu_33533563.data.legacy.ScoreTypes
import com.example.fit2081a1_yang_xingyu_33533563.data.model.repository.PersonaRepository
import com.example.fit2081a1_yang_xingyu_33533563.data.model.repository.UserScoreRepository
import com.example.fit2081a1_yang_xingyu_33533563.data.model.repository.UserTimePreferenceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class UserStatsViewModel(
    private val userScoreRepository: UserScoreRepository,
    private val userPersonaRepository: PersonaRepository,
    private val userTimePreferenceRepository: UserTimePreferenceRepository
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

    suspend fun getUserFruitScore(userId: String): Float {
        return userScoreRepository.getScore(userId, ScoreTypes.FRUITS.scoreId)
    }

    fun getUserPersona(userId: String) {
        viewModelScope.launch {
            userPersonaRepository.getPersonaById(userId)
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