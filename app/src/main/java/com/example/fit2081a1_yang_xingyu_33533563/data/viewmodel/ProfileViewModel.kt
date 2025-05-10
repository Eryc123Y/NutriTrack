package com.example.fit2081a1_yang_xingyu_33533563.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.fit2081a1_yang_xingyu_33533563.data.model.entity.PersonaEntity
import com.example.fit2081a1_yang_xingyu_33533563.data.model.entity.UserEntity
import com.example.fit2081a1_yang_xingyu_33533563.data.model.repository.PersonaRepository
import com.example.fit2081a1_yang_xingyu_33533563.data.model.repository.UserRepository
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userRepository: UserRepository,
    private val personaRepository: PersonaRepository
) : ViewModel() {

    private val _userIdLiveData = MutableLiveData<String?>()

    private val _currentUser = MutableLiveData<UserEntity?>()
    val currentUser: LiveData<UserEntity?> = _currentUser

    private val _selectedPersona = MutableLiveData<PersonaEntity?>()
    val selectedPersona: LiveData<PersonaEntity?> = _selectedPersona

    private val _updateStatus = MutableLiveData<String?>()
    val updateStatus: LiveData<String?> = _updateStatus

    fun setUserId(userId: String?) {
        if (_userIdLiveData.value != userId) {
            _userIdLiveData.value = userId
            loadUserData()
        }
    }

    private fun loadUserData() {
        viewModelScope.launch {
            val userId = _userIdLiveData.value
            if (userId != null) {
                try {
                    val user = userRepository.getUserById(userId).firstOrNull()
                    _currentUser.postValue(user)

                    // Load persona if user has selected one
                    if (user?.selectedPersonaId != null) {
                        val persona = personaRepository.getPersonaById(user.selectedPersonaId).firstOrNull()
                        _selectedPersona.postValue(persona)
                    } else {
                        _selectedPersona.postValue(null)
                    }
                } catch (e: Exception) {
                    _currentUser.postValue(null)
                    _selectedPersona.postValue(null)
                }
            } else {
                _currentUser.postValue(null)
                _selectedPersona.postValue(null)
            }
        }
    }

    fun updateUserProfile(userId: String, newName: String, newPhone: String, newGender: String) {
        viewModelScope.launch {
            val user = currentUser.value
            if (user != null && user.userId == userId) {
                val updatedUser = user.copy(name = newName, phoneNumber = newPhone, gender = newGender)
                try {
                    userRepository.updateUser(updatedUser)
                    _updateStatus.postValue("Profile updated successfully.")
                    // Refresh user data
                    loadUserData()
                } catch (e: Exception) {
                    _updateStatus.postValue("Error updating profile: ${e.message}")
                }
            } else {
                _updateStatus.postValue("Error: User not loaded or ID mismatch.")
            }
        }
    }

    fun clearUpdateStatus() {
        _updateStatus.value = null
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