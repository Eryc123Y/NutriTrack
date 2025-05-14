package com.example.fit2081a1_yang_xingyu_33533563.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.fit2081a1_yang_xingyu_33533563.data.model.entity.UserEntity
import com.example.fit2081a1_yang_xingyu_33533563.data.model.repository.UserRepository
import com.example.fit2081a1_yang_xingyu_33533563.util.SharedPreferencesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import com.example.fit2081a1_yang_xingyu_33533563.data.model.repository.PersonaRepository // Keep if ProfileViewModelFactory is kept, otherwise remove

/**
 * A view model class for managing authentication-related data and operations.
 */
class AuthViewModel(
    private val userRepository: UserRepository,
    private val sharedPreferencesManager: SharedPreferencesManager
) : ViewModel() {
    // MutableStateFlow to hold the current user entity. Can be updated and observed.
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    // MutableStateFlow to hold the current user ID.
    private val _currentUserId = MutableStateFlow<String?>(null)
    val currentUserId: StateFlow<String?> = _currentUserId.asStateFlow()

    private val _userIds = MutableStateFlow<List<String>>(emptyList())
    val userIds: StateFlow<List<String>> = _userIds.asStateFlow()

    // MutableStateFlow to hold the login status.
    private val _isLoggedIn = MutableStateFlow<Boolean>(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    // MutableStateFlow to hold the loading status. This is used to indicate whether a
    // loading operation is in progress, helping to improve UI responsiveness and avoid
    // threading issues.
    private val _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // MutableStateFlow to hold any authentication error messages.
    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    // loading the current user when the ViewModel is created. This is nullable if no
    // any user is stored in the shared preferences.
    init {
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val storedUserId = sharedPreferencesManager.getCurrentUser()
                _currentUserId.value = storedUserId
                if (storedUserId != null) {
                    userRepository.getUserById(storedUserId).firstOrNull()?.let { user ->
                        _currentUser.value = user
                        _isLoggedIn.value = true
                    } ?: run {
                        // User ID in prefs but not in DB, treat as logged out
                        _currentUser.value = null
                        _isLoggedIn.value = false
                        sharedPreferencesManager.logout() // Clear inconsistent state
                    }
                } else {
                    _currentUser.value = null
                    _isLoggedIn.value = false
                }
            } catch (e: Exception) {
                _authError.value = "Error loading current user: ${e.message}"
                _currentUser.value = null
                _currentUserId.value = null
                _isLoggedIn.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadAllUserIds() {
        viewModelScope.launch {
            userRepository.getAllUsers().collect { users ->
                _userIds.value = users.map { it.userId }
            }
        }
    }

    fun login(userId: String, value: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _authError.value = null
            try {
                val user = userRepository.getUserById(userId).firstOrNull()
                if (user != null) {
                    sharedPreferencesManager.setCurrentUser(userId)
                    _currentUser.value = user
                    _currentUserId.value = userId
                    _isLoggedIn.value = true
                } else {
                    _authError.value = "Invalid credentials or user not found."
                    _currentUser.value = null
                    _currentUserId.value = null
                    _isLoggedIn.value = false
                }
            } catch (e: Exception) {
                _authError.value = "Login error: ${e.message}"
                 _isLoggedIn.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun register(name: String, userId: String, phone: String, gender: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _authError.value = null
            try {
                val existingUser = userRepository.getUserById(userId).firstOrNull()
                if (existingUser == null) {
                    val newUser = UserEntity(
                        userId = userId,
                        userName = name,
                        userPhoneNumber = phone,
                        userGender = gender,
                    )
                    userRepository.insertUser(newUser)
                    sharedPreferencesManager.setCurrentUser(userId)
                    _currentUser.value = newUser
                    _currentUserId.value = userId
                    _isLoggedIn.value = true
                } else {
                    _authError.value = "User ID already exists."
                }
            } catch (e: Exception) {
                _authError.value = "Registration error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                sharedPreferencesManager.logout()
                _currentUser.value = null
                _currentUserId.value = null
                _isLoggedIn.value = false
            } catch (e: Exception) {
                // Log or handle error if SharedPreferences fails, though unlikely
                _authError.value = "Logout error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearAuthError() {
        _authError.value = null
    }

    class ProfileViewModelFactory(
        private val userRepository: UserRepository,
        private val personaRepository: PersonaRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ProfileViewModel(userRepository, personaRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}