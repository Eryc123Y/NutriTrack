package com.example.fit2081a1_yang_xingyu_33533563.data.viewmodel

import androidx.lifecycle.ViewModel
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

    // MutableStateFlow to hold the current user ID.
    private val _currentUserId = MutableStateFlow<String?>(null)
    val currentUserId: StateFlow<String?> = _currentUserId.asStateFlow()

    private val _currentUserPhoneNumber = MutableStateFlow<String?>(null)

    private val _userIds = MutableStateFlow<List<String>>(emptyList())
    val userIds: StateFlow<List<String>> = _userIds.asStateFlow()

    // MutableStateFlow to hold the login status.
    private val _isLoggedIn = MutableStateFlow<Boolean>(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _isUserRegistered = MutableStateFlow<Boolean?>(null)

    // MutableStateFlow to hold the loading status. This is used to indicate whether a
    // loading operation is in progress, helping to improve UI responsiveness and avoid
    // threading issues.
    private val _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // MutableStateFlow to hold any authentication error messages.
    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    private val _passwordChangeStatus = MutableStateFlow<String?>(null)
    val passwordChangeStatus: StateFlow<String?> = _passwordChangeStatus.asStateFlow()

    // loading the current user when the ViewModel is created. This is nullable if no
    // any user is stored in the shared preferences.
    init {
        resetUserStatus()
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
                        // Don't auto login - requiring explicit login for safety
                        // _isLoggedIn.value = false
                        _isUserRegistered.value = user.userIsRegistered
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

    /**
     * Loads all user IDs from the repository and updates the userIds StateFlow.
     * Used by Login dropdown to show all registered user ids.
     */
    fun loadAllUserIds() {
        viewModelScope.launch {
            userRepository.getAllUsers().collect { users ->
                _userIds.value = users.map { it.userId }
            }
        }
    }

    /**
     * A function to login a user with the given userId and password.
     * It checks the credentials against the database and updates the UI state accordingly.
     *
     * @param userId The ID of the user trying to log in
     * @param password The password provided by the user
     */
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
                        _isUserRegistered.value = user.userIsRegistered
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

    /**
     * A function to register a new user with the given details.
     * It checks if the user ID already exists and updates the UI state accordingly.
     *
     * @param name The name of the user
     * @param userId The ID of the user
     * @param phone The phone number of the user
     * @param password The password provided by the user
     */
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
                    _isUserRegistered.value = true
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

    /**
     * A function to validate the registration input fields.
     * It checks if all fields are filled and if the password meets the criteria.
     *
     * @param name The name of the user
     * @param userId The ID of the user
     * @param phone The phone number of the user
     * @param password The password provided by the user
     * @param confirmPassword The confirmation password provided by the user
     * @return Boolean indicating whether the input is valid or not
     */
    fun validateRegistrationInput(
        name: String,
        userId: String,
        phone: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        // regex generated by AI
        val passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$"
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
            password.length < 8 -> {
                _authError.value = "Password must be at least 8 characters long."
                return false
            }
            !password.matches(Regex(passwordPattern)) -> {
                _authError.value = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character."
                return false
            }
            phone != _currentUserPhoneNumber.value -> {
                _authError.value = "Phone number does not match."
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
            try {
                _currentUser.value = null
                _currentUserId.value = null
                _isLoggedIn.value = false
                _currentUserPhoneNumber.value = null
                _authError.value = null
                
                // Don't clear SharedPreferences here, just ensure user must login again
                loadCurrentUser() // Load the current user from SharedPreferences if exists
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
                // Ensure we clear the persisted user data in SharedPreferences
                sharedPreferencesManager.logout()
                
                // Clear local state
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

    fun clearPasswordChangeStatus() {
        _passwordChangeStatus.value = null
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

    /**
     * Validates new password and its confirmation.
     * Sets _passwordChangeStatus with an error message if validation fails.
     * @return Boolean indicating whether the new password is valid.
     */
    private fun validateNewPassword(newPassword: String, confirmNewPassword: String): Boolean {
        val passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$"
        when {
            newPassword.isBlank() || confirmNewPassword.isBlank() -> {
                _passwordChangeStatus.value = "New password fields cannot be empty."
                return false
            }
            newPassword != confirmNewPassword -> {
                _passwordChangeStatus.value = "New passwords do not match."
                return false
            }
            newPassword.length < 8 -> {
                _passwordChangeStatus.value = "New password must be at least 8 characters long."
                return false
            }
            !newPassword.matches(Regex(passwordPattern)) -> {
                _passwordChangeStatus.value = "New password must contain at least one uppercase " +
                        "letter, one lowercase letter, one digit, and one special character."
                return false
            }
            else -> return true
        }
    }

    fun changePassword(userId: String, currentPassword: String, newPassword: String, confirmNewPassword: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _passwordChangeStatus.value = null // Clear previous status

            if (!validateNewPassword(newPassword, confirmNewPassword)) {
                _isLoading.value = false
                // _passwordChangeStatus is already set by validateNewPassword if it fails
                return@launch
            }

            try {
                val user = userRepository.getUserById(userId).firstOrNull()
                if (user?.userHashedCredential == null) {
                    _passwordChangeStatus.value = "Could not retrieve current user credential."
                    _isLoading.value = false
                    return@launch
                }

                if (verifyPassword(currentPassword, user.userHashedCredential)) {
                    // Current password is correct, proceed to change
                    val newHashedPassword = hashPassword(newPassword)
                    val updatedUser = user.copy(userHashedCredential = newHashedPassword)
                    userRepository.updateUser(updatedUser)
                    _passwordChangeStatus.value = "Password changed successfully."
                } else {
                    _passwordChangeStatus.value = "Incorrect current password."
                }
            } catch (e: Exception) {
                _passwordChangeStatus.value = "Error changing password: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}