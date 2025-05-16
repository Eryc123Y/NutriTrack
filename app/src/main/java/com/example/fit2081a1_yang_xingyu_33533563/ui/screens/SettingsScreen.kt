package com.example.fit2081a1_yang_xingyu_33533563.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fit2081a1_yang_xingyu_33533563.R
import com.example.fit2081a1_yang_xingyu_33533563.navigation.Screen
import com.example.fit2081a1_yang_xingyu_33533563.ui.components.BottomNavigationBar
import com.example.fit2081a1_yang_xingyu_33533563.ui.components.TopNavigationBar
import com.example.fit2081a1_yang_xingyu_33533563.data.viewmodel.ProfileViewModel
import com.example.fit2081a1_yang_xingyu_33533563.data.model.entity.UserEntity

/**
 * Created by Eric
 * This module contains setting layout
 */


/**
 * Composable function for the Settings screen
 * @param profileViewModel ViewModel for managing profile data
 * @param onNavigate callback function for navigating to other screens
 * @param onBackClick callback function for navigating back to the previous screen
 * @return Unit
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    profileViewModel: ProfileViewModel,
    onNavigate: (String) -> Unit = {},
    onBackClick: () -> Unit = {}
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
                .padding(16.dp), // Add some padding around the content
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
                    onClinicianLoginClick = { showClinicianLoginDialog = true }
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

@Composable
fun UserSettingsContent(
    userId: String,
    userName: String,
    userGender: String,
    onSetAvatarClick: () -> Unit,
    onClinicianLoginClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Avatar Section
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground), // Replace with actual avatar or placeholder
            contentDescription = "User Avatar",
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Button(onClick = onSetAvatarClick) {
            Text("Set Avatar")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // User Info Section
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                UserInfoRow("User ID:", userId)
                UserInfoRow("Name:", userName)
                UserInfoRow("Gender:", userGender)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Clinician Login Button
        Button(onClick = onClinicianLoginClick) {
            Text("Clinician Login")
        }
    }
}

@Composable
fun UserInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.3f))
        Text(text = value, modifier = Modifier.weight(0.7f))
    }
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
                singleLine = true
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
