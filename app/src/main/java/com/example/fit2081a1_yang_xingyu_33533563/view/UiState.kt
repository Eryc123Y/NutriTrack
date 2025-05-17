package com.example.fit2081a1_yang_xingyu_33533563.view

/**
 * A sealed hierarchy describing the state of the text generation.
 * Generic type T is used to allow flexibility in the type of data being handled.
 */
sealed interface UiState<T> {

    object Initial : UiState<Nothing>

    object Loading : UiState<Nothing>

    data class Success<T>(val outputText: T) : UiState<T>

    /**
     * Error state with different types of errors
     */
    sealed interface Error<T> : UiState<T> {
        data class NetworkError<T>(val errorMessage: T) : Error<T>
        data class ApiError<T>(val errorMessage: T) : Error<T>
        data class UnidentifiedError<T>(val errorMessage: T) : Error<T>
    }
}