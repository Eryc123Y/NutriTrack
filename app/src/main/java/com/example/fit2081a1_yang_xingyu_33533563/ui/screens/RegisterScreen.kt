package com.example.fit2081a1_yang_xingyu_33533563.ui.screens

import android.R.attr.text
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.fit2081a1_yang_xingyu_33533563.data.viewmodel.AuthViewModel
import com.example.fit2081a1_yang_xingyu_33533563.ui.components.authentification.AuthenticationButton
import com.example.fit2081a1_yang_xingyu_33533563.ui.components.authentification.UserIdDropdown


@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onRegistrationComplete: () -> Unit,
    onBackToLogin: () -> Unit
) {
    val userIdState = remember { mutableStateOf("") }
    val phoneNumberState = remember { mutableStateOf("") }
    val userNameState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }
    val confirmPasswordState = remember { mutableStateOf("") }
    val isLoading = viewModel.isLoading.collectAsState().value
    val isLoggedIn = viewModel.isLoggedIn.collectAsState().value
    var errorMessage = viewModel.authError.collectAsState().value

    val logInButtonShape = MaterialTheme.shapes.medium

    // Clear error message when any input field changes
    LaunchedEffect(userIdState.value, phoneNumberState.value, passwordState.value,
        confirmPasswordState.value) {
        viewModel.clearAuthError()
    }

    // Observe the login userId state, and update its phone number for registration confirmation
    LaunchedEffect(userIdState.value) {
        if (userIdState.value.isNotBlank()) {
            viewModel.loadUserPhoneNumber(userIdState.value)
        }
    }
    
    // Navigate to Questionnaire when user is successfully registered and logged in
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            // After successful registration and login, navigate to questionnaire
            onRegistrationComplete()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(modifier = Modifier.fillMaxSize()
            .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Register",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
            )

            UserIdDropdown(
                userIdState = userIdState,
                userIds = viewModel.userIds.collectAsState().value, // Replace with actual user IDs
                buttonShape = logInButtonShape,
                //modifier = Modifier.padding(bottom = 16.dp)
            )

            RegistrationTextField(
                fieldName = "Phone Number",
                fieldValue = phoneNumberState,
                isPassword = false
            )

            RegistrationTextField(
                fieldName = "User Name",
                fieldValue = userNameState,
                isPassword = false
            )

            RegistrationTextField(
                fieldName = "Password",
                fieldValue = passwordState,
                isPassword = true
            )

            RegistrationTextField(
                fieldName = "Confirm Password",
                fieldValue = confirmPasswordState,
                isPassword = true
            )

            // Display error message
            errorMessage?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Text(
                text = "This app is only for pre-registered users. Please have your " +
                        "ID and phone number before continuing",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Justify
            )

            AuthenticationButton(
                text = "Register",
                onClick = {
                    if (viewModel.validateRegistrationInput(
                            name = userNameState.value,
                            userId = userIdState.value,
                            phone = phoneNumberState.value,
                            password = passwordState.value,
                            confirmPassword = confirmPasswordState.value
                        )) {
                        viewModel.register(
                            name = userNameState.value,
                            userId = userIdState.value,
                            phone = phoneNumberState.value,
                            password = passwordState.value
                        )
                        // Registration success/failure will be handled by the LaunchedEffect observing isLoggedIn
                    }
                },
                isLoading = isLoading,
            )

            AuthenticationButton(
                text = "Back to Login",
                onClick = onBackToLogin,
            )

        }
    }
}

@Composable
fun RegistrationTextField(
    fieldName: String,
    fieldValue: MutableState<String>,
    isPassword: Boolean = false,
) {
    OutlinedTextField(
        value = fieldValue.value,
        onValueChange = { fieldValue.value = it },
        label = { Text(fieldName) },
        placeholder = { Text("Enter your $fieldName") },
        visualTransformation = if (isPassword) PasswordVisualTransformation()
        else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = if (isPassword) KeyboardType.Password
        else KeyboardType.Text),
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium
    )
}