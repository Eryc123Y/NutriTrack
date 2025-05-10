package com.example.fit2081a1_yang_xingyu_33533563.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fit2081a1_yang_xingyu_33533563.data.model.entity.UserEntity
import com.example.fit2081a1_yang_xingyu_33533563.data.model.repository.UserRepository
import com.example.fit2081a1_yang_xingyu_33533563.util.SharedPreferencesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

/**
 * A view model class for managing authentication-related data and operations.
 */
class AuthViewModel(

    private val userRepository: UserRepository,
    private val sharedPreferencesManager: SharedPreferencesManager
) : ViewModel() {
    // MutableStateFlow to hold the current user entity. Can be updated and observed.
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser

    // MutableStateFlow to hold the current user ID.
    private val _currentUserId = MutableStateFlow<String?>(null)
    val currentUserId: StateFlow<String?> = _currentUserId

    // MutableStateFlow to hold the login status.
    private val _isLoggedIn = MutableStateFlow<Boolean>(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    // MutableStateFlow to hold the loading status. This is used to indicate whether a
    // loading operation is in progress, helping to improve UI responsiveness and avoid
    // threading issues.
    private val _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // MutableStateFlow to hold any authentication error messages.
    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError

    // loading the current user when the ViewModel is created. This is nullable if no
    // any user is stored in the shared preferences.
    init {
        loadCurrentUser()
    }


    private fun loadCurrentUser() {
        viewModelScope.launch {
            val currentUserId = sharedPreferencesManager.getCurrentUser()
            _currentUserId.value = currentUserId
            currentUserId?.let { id ->
                userRepository.getUserById(id).firstOrNull()?.let { user ->
                    _currentUser.value = user
                }
            }
        }
    }

    fun login(userId: String, userCredential: String) {

        suspend fun validateCredentials(userId: String, userCredential: String): Boolean {
            // Example validation logic. In a real app, you'd check against a database or API.
            return userCredential == userRepository.getCredentialByUserId(userId)
        }
        viewModelScope.launch {
            // Example: Fetch user. In a real app, you'd validate credentials.
            val user = userRepository.getUserById(userId).firstOrNull() // Adjust as per your DAO
            // if user is not null, set the current user by userId
            if (user != null) {
                val isLoginSuccess = validateCredentials(userId, userCredential)
                if (isLoginSuccess) {
                    _isLoggedIn.value = true
                    _isLoading.value = false
                    sharedPreferencesManager.setCurrentUser(userId)
                    _currentUser.value = user
                    _currentUserId.value = userId
                    _authError.value = null
                } else {
                    _isLoggedIn.value = false
                    _isLoading.value = false
                    _authError.value = "Invalid credentials."
                    _currentUser.value = null
                    _currentUserId.value = null
                }
            } else {
                _authError.value = "User is not registered."
                _currentUser.value = null
                _currentUserId.value = null
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _isLoggedIn.value = false
            _currentUser.value = null
            _currentUserId.value = null
            sharedPreferencesManager.logout()
        }
    }

    fun clearAuthError() {
        _authError.value = null
    }
}