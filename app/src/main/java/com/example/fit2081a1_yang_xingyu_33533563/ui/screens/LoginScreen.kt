package com.example.fit2081a1_yang_xingyu_33533563.ui.screens

import com.example.fit2081a1_yang_xingyu_33533563.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.fit2081a1_yang_xingyu_33533563.data.viewmodel.AuthViewModel
import com.example.fit2081a1_yang_xingyu_33533563.ui.components.authentification.AuthenticationButton
import com.example.fit2081a1_yang_xingyu_33533563.ui.components.authentification.UserIdDropdown


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToRegisterScreen: () -> Unit,
) {
    // UI states
    val userIdState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }
    val isLoading = viewModel.isLoading.collectAsState().value
    var errorMessage = viewModel.authError.collectAsState().value
    val isLoggedIn = viewModel.isLoggedIn.collectAsState().value

    val logInButtonShape = MaterialTheme.shapes.medium

    // observing the login status, if the user is logged in, navigate to the home screen

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(modifier = Modifier.fillMaxSize()
            .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // image
            Image(
                painter = painterResource(
                    id = R.drawable.logo),
                contentDescription = "FIT2081 Logo",
                modifier = Modifier.size(200.dp)
            )

            Text(
                text = "Log in",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // new username box
            UserIdDropdown(
                userIdState = userIdState,
                userIds = viewModel.userIds.collectAsState().value,
                buttonShape = logInButtonShape,
                // todo add modifier if needed
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = passwordState.value,
                onValueChange = { passwordState.value = it },
                label = { Text("Password") },
                placeholder = { Text("Enter your password") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                shape = logInButtonShape
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "This app is only for pre-registered users. Please have your " +
                        "ID and phone number before continuing",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Justify
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Login Button
            AuthenticationButton(
                text = "Log in",
                onClick = {
                    viewModel.login(userIdState.value, passwordState.value)
                    if (isLoggedIn) {
                        onNavigateToHome()
                    } else {
                        errorMessage = "Login failed. Please check your credentials or register."
                    }
                },
                isLoading = isLoading,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Register Button
            AuthenticationButton(
                text = "Register",
                onClick = onNavigateToRegisterScreen,
                isLoading = false,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Show error message if any
            errorMessage?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            HorizontalDivider(modifier = Modifier.padding(8.dp), thickness = 2.dp)
        }
    }
}

@Composable
fun ShowLoadingEffect() {
    CircularProgressIndicator(
        modifier = Modifier.size(24.dp),
        color = MaterialTheme.colorScheme.onPrimary
    )
}