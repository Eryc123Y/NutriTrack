package com.example.fit2081a1_yang_xingyu_33533563.view.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ModeNight
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fit2081a1_yang_xingyu_33533563.R
import com.example.fit2081a1_yang_xingyu_33533563.data.viewmodel.AuthViewModel
import com.example.fit2081a1_yang_xingyu_33533563.data.viewmodel.ProfileViewModel
import com.example.fit2081a1_yang_xingyu_33533563.navigation.Screen
import com.example.fit2081a1_yang_xingyu_33533563.view.components.BottomNavigationBar
import com.example.fit2081a1_yang_xingyu_33533563.view.components.TopNavigationBar

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
    var isClinicianMode by remember { mutableStateOf(false) }
    var showClinicianLoginDialog by remember { mutableStateOf(false) }
    var clinicianKeyInput by remember { mutableStateOf("") }

    // Collect user data from profileViewModel
    val currentUser by profileViewModel.currentUser.collectAsState()

    // Extract user details, providing defaults if currentUser is null
    val userId = currentUser?.userId ?: "N/A"
    val userName = currentUser?.userName ?: "N/A"
    val userGender = currentUser?.userGender ?: "N/A"


    Scaffold (
        topBar = {
            TopNavigationBar(
                title = if (isClinicianMode) "Clinician Dashboard" else "Settings",
                showBackButton = false, // Set to true if back navigation is needed from dashboard
                onBackButtonClick = onBackClick
            )
        },
        bottomBar = {
            if (!isClinicianMode) { // Hide bottom bar in clinician mode or adapt as needed
                BottomNavigationBar(
                    currentRoute = Screen.Settings.route,
                    onNavigate = onNavigate
                )
            }
        }
    ){ innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            // verticalArrangement = Arrangement.Center // Remove to allow content to flow from top
        ) {
            if (isClinicianMode) {
                ClinicianDashboardContent()
            } else {
                UserSettingsContent(
                    userId = userId,
                    userName = userName,
                    userGender = userGender,
                    onSetAvatarClick = { /* TODO: Implement avatar setting logic */ },
                    onClinicianLoginClick = { showClinicianLoginDialog = true },
                    onLogoutClick = {
                        authViewModel.logout()
                        onLogoutToLogin()
                                    },
                    isDarkMode = isDarkMode, // Pass dark mode state
                    onToggleDarkMode = onToggleDarkMode // Pass dark mode toggle
                )
            }
        }
    }

    if (showClinicianLoginDialog) {
        ClinicianLoginDialog(
            clinicianKeyInput = clinicianKeyInput,
            onClinicianKeyInputChange = { clinicianKeyInput = it },
            onDismissRequest = { showClinicianLoginDialog = false },
            onConfirm = {
                if (clinicianKeyInput == "1234") { // Hardcoded key
                    isClinicianMode = true
                    showClinicianLoginDialog = false
                    clinicianKeyInput = "" // Reset key input
                } else {
                    // Optional: Show error message for incorrect key
                    showClinicianLoginDialog = false // Or keep dialog open and show error
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserSettingsContent(
    userId: String,
    userName: String,
    userGender: String,
    onSetAvatarClick: () -> Unit,
    onClinicianLoginClick: () -> Unit,
    onLogoutClick: () -> Unit,
    isDarkMode: Boolean,
    onToggleDarkMode: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Profile Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Profile", style = MaterialTheme.typography.titleLarge)
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground), // Placeholder
                        contentDescription = "User Avatar",
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                UserInfoRow("User ID:", userId)
                UserInfoRow("Name:", userName)
                UserInfoRow("Gender:", userGender)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = onSetAvatarClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Face, contentDescription = "Set Avatar Icon", modifier = Modifier.padding(end = 8.dp))
                    Text("Set Avatar")
                }
            }
        }

        // App Settings Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("App Settings", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))
                ListItem(
                    headlineContent = { Text("Dark Mode") },
                    leadingContent = {
                        Icon(
                            if (isDarkMode) Icons.Filled.ModeNight else Icons.Filled.WbSunny,
                            contentDescription = "Dark Mode Icon"
                        )
                    },
                    trailingContent = {
                        Switch(
                            checked = isDarkMode,
                            onCheckedChange = onToggleDarkMode
                        )
                    }
                )
                // Add other app settings like notifications, units, etc. here
                // Example:
                // SettingItem(title = "Notification Preferences", onClick = { /* TODO */ })
                // SettingItem(title = "Measurement Units", onClick = { /* TODO */ })
            }
        }
        
        // Clinician Access Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Clinician Portal", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))
                 Button(
                    onClick = onClinicianLoginClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Lock, contentDescription = "Clinician Login Icon", modifier = Modifier.padding(end = 8.dp))
                    Text("Clinician Login")
                }
            }
        }


        // Account Actions Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Account", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onLogoutClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout Icon", modifier = Modifier.padding(end = 8.dp))
                    Text("Log Out")
                }
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
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(0.4f))
        Text(text = value, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(0.6f))
    }
    HorizontalDivider()
}


@Composable
fun ClinicianDashboardContent() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Clinician Dashboard", style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold))
        // Add more clinician-specific UI elements here
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClinicianLoginDialog(
    clinicianKeyInput: String,
    onClinicianKeyInputChange: (String) -> Unit,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Clinician Login") },
        text = {
            OutlinedTextField(
                value = clinicianKeyInput,
                onValueChange = onClinicianKeyInputChange,
                label = { Text("Enter Clinician Key") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
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
