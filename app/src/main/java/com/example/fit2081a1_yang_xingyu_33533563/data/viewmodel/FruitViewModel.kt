package com.example.fit2081a1_yang_xingyu_33533563.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fit2081a1_yang_xingyu_33533563.api.FruitResponse
import com.example.fit2081a1_yang_xingyu_33533563.api.FruitViceRepoProvider
import com.example.fit2081a1_yang_xingyu_33533563.data.model.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FruitViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val fruitApiRepository = FruitViceRepoProvider.getRepository()

    // StateFlow to hold the fruit details or error messages
    private val _fruitDetails = MutableStateFlow<FruitResponse?>(null)
    val fruitDetails: StateFlow<FruitResponse?> = _fruitDetails.asStateFlow()

    // StateFlow for loading state (used for fruit API calls)
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // StateFlow for user's fruit serving size
    private val _userFruitServingsize = MutableStateFlow<Float?>(null)
    val userFruitServingsize: StateFlow<Float?> = _userFruitServingsize.asStateFlow()

    // StateFlow to determine if fruit advice query panel should be shown
    val shouldShowFruitViceQuery: StateFlow<Boolean> = _userFruitServingsize.map { servings ->
        servings == null || servings < 2
    }.stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000), false)


    /**
     * Fetches fruit details from the API repository and updates the StateFlow.
     * @param fruitName The name of the fruit to search for.
     */
    fun fetchFruitDetails(fruitName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val trimmedFruitName = fruitName.trim()
            if (trimmedFruitName.isNotEmpty()) {
                _fruitDetails.value = fruitApiRepository.getFruitDetails(trimmedFruitName)
            } else {
                _fruitDetails.value =
                    FruitResponse(listOf(mapOf("Error" to "Please enter a fruit name.")))
            }
            _isLoading.value = false
        }
    }

    /**
     * Loads the user's fruit serving size from the UserRepository.
     * @param userId The ID of the current user.
     */
    fun loadUserFruitServingsize(userId: String) {
        viewModelScope.launch {
            // Consider adding a separate loading state for this if it can be slow
            userRepository.getUserById(userId)
                .map { it?.userFruitServingsize }
                .catch { e ->
                    // Handle error, e.g., log it or update an error StateFlow
                    // For now, setting to null to reflect data couldn't be loaded
                    _userFruitServingsize.value = null
                    // Optionally log e.message
                }
                .collect { fruitServingsize ->
                    _userFruitServingsize.value = fruitServingsize
                }
        }
    }

    /**
     * Clears the fruit details and error messages.
     */
    fun clearFruitDetails() {
        _fruitDetails.value = null
    }
}