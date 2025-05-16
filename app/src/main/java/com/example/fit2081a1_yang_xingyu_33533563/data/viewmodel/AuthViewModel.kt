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
import com.example.fit2081a1_yang_xingyu_33533563.util.hashPassword
import com.example.fit2081a1_yang_xingyu_33533563.util.verifyPassword

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

    private val _currentUserPhoneNumber = MutableStateFlow<String?>(null)
    val currentUserPhoneNumber: StateFlow<String?> = _currentUserPhoneNumber.asStateFlow()

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
        resetUserStatus()
        loadCurrentUser()
        loadAllUserIds()
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
                        //_isLoggedIn.value = true
                        _currentUserPhoneNumber.value = user.userPhoneNumber
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

    fun login(userId: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _authError.value = null
            try {
                val user = userRepository.getUserById(userId).firstOrNull()

                if (user != null) {
                    // Get the hashed credential from the database
                    val hashedPassword = user.userHashedCredential

                    // Verify the provided password against the stored hash
                    if (hashedPassword != null && verifyPassword(password, hashedPassword)) {
                        // if password is correct
                        sharedPreferencesManager.setCurrentUser(userId)
                        _currentUser.value = user
                        _currentUserId.value = userId
                        _isLoggedIn.value = true
                    } else {
                        // Invalid password
                        _authError.value = "Invalid password."
                        _isLoggedIn.value = false
                    }
                } else {
                    _authError.value = "User not found, please register."
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

    fun register(name: String, userId: String, phone: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _authError.value = null
            try {
                val isUserRegistered = userRepository.getUserIsRegistered(userId)
                if (isUserRegistered == false) {
                    val userGender = userRepository.getUserGender(userId)
                    val newUser = UserEntity(
                        userId = userId,
                        userName = name,
                        userPhoneNumber = phone,
                        userHashedCredential = hashPassword(password),
                        userIsRegistered = true,
                        userGender = userGender
                    )

                    userRepository.updateUser(newUser)
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

    fun validateRegistrationInput(
        name: String,
        userId: String,
        phone: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        when {
            name.isBlank() || userId.isBlank() || phone.isBlank() ||
                    password.isBlank() -> {
                _authError.value = "All fields are required"
                return false
            }
            password != confirmPassword -> {
                _authError.value = "Passwords do not match"
                return false
            }
            password.length < 0 -> {
                // todo: add regex for password
                _authError.value = "Password must be at least 6 characters"
                return false
            }
            phone != _currentUserPhoneNumber.value -> {
                _authError.value = "Phone number does not match, should be " +
                        "${_currentUserPhoneNumber.value}"
                return false
            }
            else -> return true
        }
    }

    /**
     * A function to reset related status after a user quit the app but not logout
     * In this case, the user will need to re-login, but shared preferences still preserve the
     * current user ID
     */
    fun resetUserStatus() {
        viewModelScope.launch {
            _isLoading.value = true
            _isLoggedIn.value = false
            try {
                _currentUser.value = null
                _currentUserId.value = null
                _isLoggedIn.value = false
                _currentUserPhoneNumber.value = null
                _authError.value = null
            } catch (e: Exception) {
                // Log or handle error if SharedPreferences fails, though unlikely
                _authError.value = "Reset user status error: ${e.message}"
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
                _currentUserPhoneNumber.value = null
                _authError.value = null
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

    fun loadUserPhoneNumber(userId: String) {
        viewModelScope.launch {
            try {
                val user = userRepository.getUserById(userId).firstOrNull()
                if (user != null) {
                    _currentUserPhoneNumber.value = user.userPhoneNumber
                } else {
                    _authError.value = "User ID not found."
                }
            } catch (e: Exception) {
                _authError.value = "Error loading user phone number: ${e.message}"
            }
        }
    }

    class ProfileViewModelFactory(
        private val userRepository: UserRepository,
        private val sharedPreferencesManager: SharedPreferencesManager,
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AuthViewModel(userRepository, sharedPreferencesManager) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}