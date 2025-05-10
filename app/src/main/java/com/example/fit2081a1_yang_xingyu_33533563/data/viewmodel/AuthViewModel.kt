package com.example.fit2081a1_yang_xingyu_33533563.data.viewmodel

import android.content.Context
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
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.example.fit2081a1_yang_xingyu_33533563.data.model.repository.PersonaRepository

/**
 * A view model class for managing authentication-related data and operations.
 */
class AuthViewModel(
    private val userRepository: UserRepository,
    private val sharedPreferencesManager: SharedPreferencesManager
) : ViewModel() {
    // MutableStateFlow to hold the current user entity. Can be updated and observed.
    private val _currentUser = MutableLiveData<UserEntity?>()
    val currentUser: LiveData<UserEntity?> = _currentUser

    // MutableStateFlow to hold the current user ID.
    private val _currentUserId = MutableLiveData<String?>()
    val currentUserId: LiveData<String?> = _currentUserId

    // MutableStateFlow to hold the login status.
    private val _isLoggedIn = MutableStateFlow<Boolean>(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    // MutableStateFlow to hold the loading status. This is used to indicate whether a
    // loading operation is in progress, helping to improve UI responsiveness and avoid
    // threading issues.
    private val _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // MutableStateFlow to hold any authentication error messages.
    private val _authError = MutableLiveData<String?>()
    val authError: LiveData<String?> = _authError

    // loading the current user when the ViewModel is created. This is nullable if no
    // any user is stored in the shared preferences.
    init {
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            val StoredUserId = sharedPreferencesManager.getCurrentUser()
            _currentUserId.postValue(StoredUserId) // Use postValue if from background thread, setValue if from main
            StoredUserId?.let { id ->
                // Assuming userRepository.getUserById() returns Flow; adapt if it returns LiveData directly
                userRepository.getUserById(id).firstOrNull()?.let { user ->
                     _currentUser.postValue(user)
                } ?: _currentUser.postValue(null) // Ensure LiveData is updated if user not found
            } ?: _currentUser.postValue(null)
        }
    }

    fun login(userId: String /*, password or other credentials */) {
        viewModelScope.launch {
            val user = userRepository.getUserById(userId).firstOrNull() 
            if (user != null) {
                sharedPreferencesManager.setCurrentUser(userId)
                _currentUser.postValue(user)
                _currentUserId.postValue(userId)
                _authError.postValue(null)
            } else {
                _authError.postValue("Invalid credentials or user not found.")
                _currentUser.postValue(null)
                _currentUserId.postValue(null)
            }
        }
    }

    fun register(name: String, userId: String, phone: String, gender: String /*, other details */) {
        viewModelScope.launch {
            val existingUser = userRepository.getUserById(userId).firstOrNull()
            if (existingUser == null) {
                val newUser = UserEntity(
                    userId = userId,
                    name = name,
                    phoneNumber = phone,
                    gender = gender,
                    isCurrentLoggedIn = true 
                )
                userRepository.insertUser(newUser) // Ensure this is suspend
                sharedPreferencesManager.setCurrentUser(userId)
                _currentUser.postValue(newUser)
                _currentUserId.postValue(userId)
                _authError.postValue(null)
            } else {
                _authError.postValue("User ID already exists.")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            sharedPreferencesManager.logout()
            _currentUser.postValue(null)
            _currentUserId.postValue(null)
        }
    }

    fun clearAuthError() {
        _authError.value = null // Can use .value if sure it's called from main thread
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