package com.example.fit2081a1_yang_xingyu_33533563.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.fit2081a1_yang_xingyu_33533563.data.legacy.ScoreTypes
import com.example.fit2081a1_yang_xingyu_33533563.data.model.entity.PersonaEntity
import com.example.fit2081a1_yang_xingyu_33533563.data.model.entity.UserEntity
import com.example.fit2081a1_yang_xingyu_33533563.data.model.entity.UserScoreEntity
import com.example.fit2081a1_yang_xingyu_33533563.data.model.repository.PersonaRepository
import com.example.fit2081a1_yang_xingyu_33533563.data.model.repository.UserRepository
import com.example.fit2081a1_yang_xingyu_33533563.data.model.repository.UserScoreRepository
import com.example.fit2081a1_yang_xingyu_33533563.data.model.repository.UserTimePreferenceRepository
import com.example.fit2081a1_yang_xingyu_33533563.util.SharedPreferencesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userRepository: UserRepository,
    private val personaRepository: PersonaRepository,
    private val userScoreRepository: UserScoreRepository,
    private val userTimePreferenceRepository: UserTimePreferenceRepository,
    private val sharedPreferencesManager: SharedPreferencesManager
) : ViewModel() {

    private val _userIdStateFlow = MutableStateFlow<String?>(null)
    // Not directly exposed, used internally to trigger loads

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    private val _selectedPersona = MutableStateFlow<PersonaEntity?>(null)
    val selectedPersona: StateFlow<PersonaEntity?> = _selectedPersona.asStateFlow()

    private val _updateStatus = MutableStateFlow<String?>(null)
    val updateStatus: StateFlow<String?> = _updateStatus.asStateFlow()

    private val _userTotalScore = MutableStateFlow<Float?>(null)
    val userTotalScore: StateFlow<Float?> = _userTotalScore.asStateFlow()


    fun setUserId(userId: String?) {
        if (_userIdStateFlow.value != userId) {
            _userIdStateFlow.value = userId
            loadUserData()
            userId?.let { getUserScores(it, ScoreTypes.TOTAL.scoreId) }
        }
    }

    fun getUserId(): String? {
        return _userIdStateFlow.value
    }

    fun getUserName(): String? {
        return _currentUser.value?.userName
    }

    private fun loadUserData() {
        viewModelScope.launch {
            val userId = _userIdStateFlow.value
            if (userId != null) {
                try {
                    val user = userRepository.getUserById(userId).firstOrNull()
                    _currentUser.value = user

                    // Load persona if user has selected one
                    if (user?.userPersonaId != null) {
                        val persona = personaRepository.getPersonaById(user.userPersonaId).firstOrNull()
                        _selectedPersona.value = persona
                    } else {
                        _selectedPersona.value = null
                    }
                } catch (e: Exception) {
                    _currentUser.value = null
                    _selectedPersona.value = null
                    // Consider propagating error to UI via another StateFlow if needed
                }
            } else {
                _currentUser.value = null
                _selectedPersona.value = null
            }
        }
    }

    fun getUserScores(userId: String, scoreKey: String) {
        viewModelScope.launch {
            _userTotalScore.value = userScoreRepository.getScore(userId, scoreKey)
        }

    }

    fun updateUserProfile(userId: String, newName: String, newPhone: String, newGender: String) {
        viewModelScope.launch {
            // Use _currentUser.value directly as it reflects the latest loaded state
            val user = _currentUser.value
            if (user != null && user.userId == userId) {
                val updatedUser = user.copy(userName = newName, userPhoneNumber = newPhone, userGender = newGender)
                try {
                    userRepository.updateUser(updatedUser)
                    _updateStatus.value = "Profile updated successfully."
                    // Refresh user data by re-triggering loadUserData
                    // This ensures currentUser and selectedPersona are updated
                    loadUserData()
                } catch (e: Exception) {
                    _updateStatus.value = "Error updating profile: ${e.message}"
                }
            } else {
                _updateStatus.value = "Error: User not loaded or ID mismatch."
            }
        }
    }

    fun clearUpdateStatus() {
        _updateStatus.value = null
    }

}