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

/**
 * ViewModel for managing user profile data and related information.
 * 
 * This ViewModel handles:
 * - User information loading and display
 * - Persona/character type information
 * - User scores and achievements
 * - Profile data synchronization across the app
 * 
 * @param userRepository Repository for user data operations
 * @param personaRepository Repository for persona information
 * @param userScoreRepository Repository for user score data
 */
class ProfileViewModel(
    private val userRepository: UserRepository,
    private val personaRepository: PersonaRepository,
    private val userScoreRepository: UserScoreRepository
) : ViewModel() {

    // ==================== USER STATE MANAGEMENT ====================
    
    /**
     * Private mutable StateFlow tracking the current user ID.
     * Used to trigger data loading when the user changes.
     */
    private val _userIdStateFlow = MutableStateFlow<String?>(null)

    /**
     * Private mutable StateFlow holding the current user entity.
     * Contains all user information loaded from the database.
     */
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    
    /**
     * Public read-only StateFlow for observing current user data.
     * UI components observe this to display user information.
     */
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    /**
     * Private mutable StateFlow holding the user's selected persona.
     * Contains persona information for personalization and display.
     */
    private val _selectedPersona = MutableStateFlow<PersonaEntity?>(null)

    /**
     * Public read-only StateFlow for the user's total score.
     * Used to display overall progress and achievements.
     */
    private val _userTotalScore = MutableStateFlow<Float?>(null)
    val userTotalScore: StateFlow<Float?> = _userTotalScore.asStateFlow()


    // ==================== USER DATA LOADING ====================
    
    /**
     * Sets the current user ID and triggers data loading.
     * Only reloads data if the user ID has actually changed to avoid unnecessary API calls.
     * 
     * @param userId The user ID to load data for, or null to clear current data
     */
    fun setUserId(userId: String?) {
        if (_userIdStateFlow.value != userId) {
            _userIdStateFlow.value = userId
            loadUserData()
            // Load total score if user ID is provided
            userId?.let { getUserScores(it, ScoreTypes.TOTAL.scoreId) }
        }
    }

    /**
     * Gets the current user's name for display purposes.
     * Provides a simple accessor for UI components that only need the name.
     * 
     * @return String containing the user's name, or null if no user is loaded
     */
    fun getUserName(): String? {
        return _currentUser.value?.userName
    }

    /**
     * Loads user data including profile information and persona.
     * Called automatically when the user ID changes or manually when refresh is needed.
     * Handles errors gracefully by clearing data on failure.
     */
    private fun loadUserData() {
        viewModelScope.launch {
            val userId = _userIdStateFlow.value
            if (userId != null) {
                try {
                    // Load user profile data
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
                    // Clear data on error to maintain consistency
                    _currentUser.value = null
                    _selectedPersona.value = null
                }
            } else {
                // Clear data if no user ID is provided
                _currentUser.value = null
                _selectedPersona.value = null
            }
        }
    }

    // ==================== SCORE MANAGEMENT ====================
    
    /**
     * Retrieves user scores for a specific score type.
     * Used to display various score metrics in the profile.
     * 
     * @param userId The user ID to retrieve scores for
     * @param scoreKey The score type identifier (e.g., TOTAL, FRUITS, etc.)
     */
    fun getUserScores(userId: String, scoreKey: String) {
        viewModelScope.launch {
            _userTotalScore.value = userScoreRepository.getScore(userId, scoreKey)
        }
    }

}