package com.example.fit2081a1_yang_xingyu_33533563.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fit2081a1_yang_xingyu_33533563.data.legacy.ScoreTypes
import com.example.fit2081a1_yang_xingyu_33533563.data.model.entity.PersonaEntity
import com.example.fit2081a1_yang_xingyu_33533563.data.model.entity.UserEntity
import com.example.fit2081a1_yang_xingyu_33533563.data.model.repository.PersonaRepository
import com.example.fit2081a1_yang_xingyu_33533563.data.model.repository.UserRepository
import com.example.fit2081a1_yang_xingyu_33533563.data.model.repository.UserScoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userRepository: UserRepository,
    private val personaRepository: PersonaRepository,
    private val userScoreRepository: UserScoreRepository
) : ViewModel() {

    private val _userIdStateFlow = MutableStateFlow<String?>(null)

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    private val _selectedPersona = MutableStateFlow<PersonaEntity?>(null)

    private val _userTotalScore = MutableStateFlow<Float?>(null)
    val userTotalScore: StateFlow<Float?> = _userTotalScore.asStateFlow()


    fun setUserId(userId: String?) {
        if (_userIdStateFlow.value != userId) {
            _userIdStateFlow.value = userId
            loadUserData()
            userId?.let { getUserScores(it, ScoreTypes.TOTAL.scoreId) }
        }
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
                } catch (_: Exception) {
                    _currentUser.value = null
                    _selectedPersona.value = null
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

}