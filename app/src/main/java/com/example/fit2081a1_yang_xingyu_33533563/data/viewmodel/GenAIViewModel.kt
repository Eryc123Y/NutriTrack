package com.example.fit2081a1_yang_xingyu_33533563.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fit2081a1_yang_xingyu_33533563.view.UiState
import com.example.fit2081a1_yang_xingyu_33533563.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * GenAIViewModel is a ViewModel class that handles the logic for the GenAI feature.
 * It communicates with the repository to fetch and update data.
 */
class GenAIViewModel: ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Initial)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val genAIModel = GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey = BuildConfig.API_KEY
    )

    fun sendRequest(prompt: String) {
        _uiState.value = UiState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = genAIModel.generateContent(
                    content {
                        text(prompt)
                    }
                )
                
                if (response.text != null) {
                    _uiState.value = UiState.Success(response.text!!)
                } else {
                    _uiState.value = UiState.Error.UnidentifiedError("No response generated")
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error.NetworkError(e.message ?: "Unknown error occurred")
            }
        }
    }

}