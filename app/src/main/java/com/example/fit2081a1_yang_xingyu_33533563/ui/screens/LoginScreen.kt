package com.example.fit2081a1_yang_xingyu_33533563.ui.screens

import com.example.fit2081a1_yang_xingyu_33533563.R
import android.widget.Toast
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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


private const val usernameStatic: String = "012345"
private const val userphoneStatic: String = "1"

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun LoginScreen(onNavigateToHome: () -> Unit = {}) {
    var userID by remember { mutableStateOf("")}
    var phoneNumber by remember { mutableStateOf("") }
    val context = LocalContext.current
    val logInButtonShape = MaterialTheme.shapes.medium

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
            var expanded by remember { mutableStateOf(false) }
            var selectedId by remember { mutableStateOf("") }

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedId,
                    onValueChange = {selectedId = it},
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable, true),
                    shape = logInButtonShape,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    label = { Text("My ID (Provided by your Clinician)") },
                    placeholder = { Text("Select your ID") }
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    listOf("012345", "123456", "234567").forEach { id ->
                        DropdownMenuItem(
                            text = { Text(id) },
                            onClick = {
                                selectedId = id
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Phone number") },
                placeholder = { Text("Enter your phone number") },
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
            Button(
                onClick = {
                    if (selectedId == usernameStatic && phoneNumber == userphoneStatic) {
                        onNavigateToHome()
                    } else {
                        Toast.makeText(context, "Unauthorised User", Toast.LENGTH_SHORT).show()
                    }
                },
                shape = logInButtonShape,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Continue")
            }

            HorizontalDivider(modifier = Modifier.padding(8.dp), thickness = 2.dp)

        }
    }
}