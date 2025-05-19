package com.example.fit2081a1_yang_xingyu_33533563.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fit2081a1_yang_xingyu_33533563.api.FruitResponse
import com.example.fit2081a1_yang_xingyu_33533563.api.FruitViceRepoProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FruitViewModel : ViewModel() {

    private val repository = FruitViceRepoProvider.getRepository()

    // StateFlow to hold the fruit details or error messages
    private val _fruitDetails = MutableStateFlow<FruitResponse?>(null)
    val fruitDetails: StateFlow<FruitResponse?> = _fruitDetails.asStateFlow()

    // StateFlow for loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    /**
     * Fetches fruit details from the repository and updates the StateFlow.
     * @param fruitName The name of the fruit to search for.
     */
    fun fetchFruitDetails(fruitName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val trimmedFruitName = fruitName.trim()
            if (trimmedFruitName.isNotEmpty()) {
                _fruitDetails.value = repository.getFruitDetails(trimmedFruitName)
            } else {
                // Optionally, set an error or specific state for empty input
                _fruitDetails.value =
                    FruitResponse(listOf(mapOf("Error" to "Please enter a fruit name.")))
            }
            _isLoading.value = false
        }
    }

    /**
     * Clears the fruit details and error messages.
     */
    fun clearFruitDetails() {
        _fruitDetails.value = null
    }
}