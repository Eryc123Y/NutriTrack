package com.example.fit2081a1_yang_xingyu_33533563.view.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ModeNight
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fit2081a1_yang_xingyu_33533563.data.viewmodel.AuthViewModel
import com.example.fit2081a1_yang_xingyu_33533563.data.viewmodel.ProfileViewModel
import com.example.fit2081a1_yang_xingyu_33533563.navigation.Screen
import com.example.fit2081a1_yang_xingyu_33533563.util.verifyClinicianCode
import com.example.fit2081a1_yang_xingyu_33533563.view.components.BottomNavigationBar
import com.example.fit2081a1_yang_xingyu_33533563.view.components.InfoCard
import com.example.fit2081a1_yang_xingyu_33533563.view.components.TopNavigationBar
import com.example.fit2081a1_yang_xingyu_33533563.view.theme.AccentTeal
import com.example.fit2081a1_yang_xingyu_33533563.view.theme.Error

/**
 * Created by Eric
 * This module contains setting layout
 */
// todo: add userId and phone number to the settings screen
/**
 * Composable function for the Settings screen
 * @param profileViewModel ViewModel for managing profile data
 * @param authViewModel ViewModel for managing authentication
 * @param onNavigate callback function for navigating to other screens
 * @param onBackClick callback function for navigating back to the previous screen
 * @param onToggleDarkMode callback function to notify theme change
 * @param isDarkMode current dark mode state
 * @return Unit
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    profileViewModel: ProfileViewModel,
    authViewModel: AuthViewModel, // Added AuthViewModel
    onNavigate: (String) -> Unit = {},
    onBackClick: () -> Unit = {},
    onLogoutToLogin: () -> Unit = {}, // Added logout callback
    onToggleDarkMode: (Boolean) -> Unit, // Callback to notify theme change
    isDarkMode: Boolean // Current dark mode state
) {
    var showClinicianLoginDialog by remember { mutableStateOf(false) }
    var clinicianKeyInput by remember { mutableStateOf("") }
    var showInvalidCodeError by remember { mutableStateOf(false) }

    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var currentPasswordInput by remember { mutableStateOf("") }
    var newPasswordInput by remember { mutableStateOf("") }
    var confirmNewPasswordInput by remember { mutableStateOf("") }

    // Collect user data from profileViewModel
    val currentUser by profileViewModel.currentUser.collectAsState()

    // Extract user details, providing defaults if currentUser is null
    val userId = currentUser?.userId ?: "N/A"
    val userName = currentUser?.userName ?: "N/A"
    val userGender = currentUser?.userGender ?: "N/A"
    val userPhoneNumber = currentUser?.userPhoneNumber ?: "N/A"


    Scaffold (
        topBar = {
            TopNavigationBar(
                title = "Settings",
                showBackButton = false,
                onBackButtonClick = onBackClick
            )
        },
        bottomBar = {
            BottomNavigationBar(
                currentRoute = Screen.Settings.route,
                onNavigate = onNavigate
            )
        }
    ){ innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            UserSettingsContent(
                userId = userId,
                userName = userName,
                userGender = userGender,
                userPhoneNumber = userPhoneNumber,
                onClinicianLoginClick = { showClinicianLoginDialog = true },
                onLogoutClick = {
                    authViewModel.logout()
                    onLogoutToLogin()
                },
                onChangePasswordClick = {
                    authViewModel.clearPasswordChangeStatus()
                    currentPasswordInput = ""
                    newPasswordInput = ""
                    confirmNewPasswordInput = ""
                    showChangePasswordDialog = true
                },
                isDarkMode = isDarkMode,
                onToggleDarkMode = onToggleDarkMode
            )
        }
    }

    if (showClinicianLoginDialog) {
        ClinicianLoginDialog(
            clinicianKeyInput = clinicianKeyInput,
            onClinicianKeyInputChange = { 
                clinicianKeyInput = it 
                showInvalidCodeError = false // Reset error when input changes
            },
            onDismissRequest = { 
                showClinicianLoginDialog = false
                showInvalidCodeError = false
                clinicianKeyInput = ""
            },
            onConfirm = {
                if (verifyClinicianCode(clinicianKeyInput)) {
                    // Navigate to the Clinician Dashboard screen
                    onNavigate(Screen.ClinicianDashboard.route)
                    showClinicianLoginDialog = false
                    clinicianKeyInput = "" // Reset key input
                    showInvalidCodeError = false
                } else {
                    showInvalidCodeError = true
                }
            },
            showError = showInvalidCodeError
        )
    }

    if (showChangePasswordDialog && currentUser != null) {
        ChangePasswordDialog(
            currentPassword = currentPasswordInput,
            newPassword = newPasswordInput,
            confirmNewPassword = confirmNewPasswordInput,
            onCurrentPasswordChange = { currentPasswordInput = it },
            onNewPasswordChange = { newPasswordInput = it },
            onConfirmNewPasswordChange = { confirmNewPasswordInput = it },
            passwordChangeStatus = authViewModel.passwordChangeStatus.collectAsState().value,
            onDismissRequest = { 
                showChangePasswordDialog = false
                authViewModel.clearPasswordChangeStatus()
             },
            onConfirm = {
                currentUser?.userId?.let {
                    authViewModel.changePassword(
                        it,
                        currentPasswordInput,
                        newPasswordInput,
                        confirmNewPasswordInput
                    )
                }
            },
            isLoading = authViewModel.isLoading.collectAsState().value
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserSettingsContent(
    userId: String,
    userName: String,
    userGender: String,
    userPhoneNumber: String,
    onClinicianLoginClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onChangePasswordClick: () -> Unit,
    isDarkMode: Boolean,
    onToggleDarkMode: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp) // Increased spacing between cards
    ) {
        // Profile Section
        InfoCard(title = "Profile") {
            UserInfoRow("User ID:", userId)
            UserInfoRow("Name:", userName)
            UserInfoRow("Gender:", userGender)
            UserInfoRow("Phone Number:", userPhoneNumber)
        }

        // App Settings Section
        InfoCard(title = "App Settings") {
            ListItem(
                headlineContent = { 
                    Text(
                        "Dark Mode", 
                        fontWeight = FontWeight.Medium
                    ) 
                },
                leadingContent = {
                    Icon(
                        if (isDarkMode) Icons.Filled.ModeNight else Icons.Filled.WbSunny,
                        contentDescription = "Dark Mode Icon",
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                trailingContent = {
                    Switch(
                        checked = isDarkMode,
                        onCheckedChange = onToggleDarkMode
                    )
                }
            )
        }
        
        // Clinician Access Section
        InfoCard(title = "Clinician Portal") {
             Button(
                onClick = onClinicianLoginClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentTeal
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 2.dp
                )
            ) {
                Icon(
                    Icons.Default.Lock, 
                    contentDescription = "Clinician Login Icon", 
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    "Clinician Login",
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Account Actions Section
        InfoCard(title = "Account") {
            Button(
                onClick = onChangePasswordClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 2.dp
                )
            ) {
                Icon(
                    Icons.Default.Lock, 
                    contentDescription = "Change Password Icon", 
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    "Change Password",
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(modifier = Modifier.height(12.dp)) // Increased spacing
            Button(
                onClick = onLogoutClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Error
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 2.dp
                )
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ExitToApp, 
                    contentDescription = "Logout Icon", 
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    "Log Out",
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp)) // Bottom spacer
    }
}

@Composable
fun UserInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp), // Increased vertical padding
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label, 
            fontWeight = FontWeight.SemiBold, 
            style = MaterialTheme.typography.bodyLarge, 
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(0.4f)
        )
        Text(
            text = value, 
            style = MaterialTheme.typography.bodyMedium, 
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.6f)
        )
    }
    HorizontalDivider(
        thickness = 0.5.dp,
        color = MaterialTheme.colorScheme.outlineVariant
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClinicianLoginDialog(
    clinicianKeyInput: String,
    onClinicianKeyInputChange: (String) -> Unit,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    showError: Boolean = false
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { 
            Text(
                "Clinician Login",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineSmall
            ) 
        },
        text = {
            Column {
                OutlinedTextField(
                    value = clinicianKeyInput,
                    onValueChange = onClinicianKeyInputChange,
                    label = { Text("Enter Clinician Key") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    isError = showError,
                    shape = RoundedCornerShape(8.dp)
                )
                if (showError) {
                    Text(
                        text = "Invalid clinician code. Please try again.",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentTeal
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Login")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordDialog(
    currentPassword: String,
    newPassword: String,
    confirmNewPassword: String,
    onCurrentPasswordChange: (String) -> Unit,
    onNewPasswordChange: (String) -> Unit,
    onConfirmNewPasswordChange: (String) -> Unit,
    passwordChangeStatus: String?,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    isLoading: Boolean
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { 
            Text(
                "Change Password",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineSmall
            ) 
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = currentPassword,
                    onValueChange = onCurrentPasswordChange,
                    label = { Text("Current Password") },
                    singleLine = true,
                    visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = onNewPasswordChange,
                    label = { Text("New Password") },
                    singleLine = true,
                    visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = confirmNewPassword,
                    onValueChange = onConfirmNewPasswordChange,
                    label = { Text("Confirm New Password") },
                    singleLine = true,
                    visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
                passwordChangeStatus?.let {
                    val isError = !it.contains("success", ignoreCase = true)
                    Text(
                        text = it,
                        color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm, 
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    ) 
                } else {
                    Text("Confirm")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel")
            }
        }
    )
}
