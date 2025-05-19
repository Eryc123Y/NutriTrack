package com.example.fit2081a1_yang_xingyu_33533563.view

/**
 * A sealed hierarchy describing the state of the text generation.
 * Generic type T is used to allow flexibility in the type of data being handled.
 */
sealed interface UiState {

    object Initial : UiState

    object Loading : UiState

    data class Success(
        val outputText: String,
        val suggestedFollowUps: List<String> = emptyList()
    ) : UiState

    /**
     * Error state with different types of errors.
     * Error messages are always strings for consistency.
     */
    sealed interface Error : UiState {
        data class NetworkError(val errorMessage: String) : Error
        data class ApiError(val errorMessage: String) : Error
        data class UnidentifiedError(val errorMessage: String) : Error
    }
}