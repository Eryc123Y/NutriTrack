package com.example.fit2081a1_yang_xingyu_33533563.view.screens

import android.R.attr.thickness
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fit2081a1_yang_xingyu_33533563.navigation.Screen
import com.example.fit2081a1_yang_xingyu_33533563.view.components.BottomNavigationBar
import com.example.fit2081a1_yang_xingyu_33533563.view.components.TopNavigationBar
import com.example.fit2081a1_yang_xingyu_33533563.viewmodel.CoachViewModel

@Preview(showBackground = true)
@Composable
fun CoachScreen(
    onNavigate: (String) -> Unit = {},
    onBackClick: () -> Unit = {},
    coachViewModel: CoachViewModel = viewModel() // Obtain ViewModel instance
) {
    var fruitNameInput by remember { mutableStateOf("") }
    val fruitDetails by coachViewModel.fruitDetails.collectAsState()
    val isLoading by coachViewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopNavigationBar(
                title = "NutriCoach",
                showBackButton = false,
                onBackButtonClick = onBackClick
            )
        },
        bottomBar = {
            BottomNavigationBar(
                currentRoute = Screen.NutriCoach.route,
                onNavigate = onNavigate
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp), // Add some padding around the content
            horizontalAlignment = Alignment.CenterHorizontally,
            // verticalArrangement = Arrangement.Center // Remove this to allow content to flow from top
        ) {
            Text(
                "Ask NutriCoach about a fruit!",
                style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.SemiBold),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = fruitNameInput,
                onValueChange = { fruitNameInput = it },
                label = { Text("Enter fruit name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    coachViewModel.fetchFruitDetails(fruitNameInput)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading && fruitNameInput.isNotBlank()
            ) {
                Text(if (isLoading) "Searching..." else "Get Fruit Details")
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                CircularProgressIndicator()
            }

            fruitDetails?.let { response ->
                // Check if the first item in fruitInfo has a key "Error"
                response.fruitInfo.firstOrNull()?.get("Error")?.let { errorMessage ->
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = TextStyle(fontSize = 16.sp),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                } ?: run {
                    // If no error, display the fruit information
                    if (response.fruitInfo.isNotEmpty()) {
                        Text(
                            "Fruit Information:",
                            style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Medium),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        LazyColumn(modifier = Modifier.fillMaxWidth()) {
                            items(response.fruitInfo) { infoMap ->
                                infoMap.forEach { (key, value) ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("$key:", fontWeight = FontWeight.Bold)
                                        Text(value)
                                    }
                                    HorizontalDivider()
                                }
                            }
                        }
                    } else if (!isLoading) {
                         // Handles cases where fruitInfo is empty but no explicit error was set (e.g. after clearing)
                        Text(
                            "No information to display. Try searching for a fruit.",
                             style = TextStyle(fontSize = 16.sp),
                             modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            } ?: run {
                if (!isLoading) {
                     Text(
                        "Enter a fruit name above and click search.",
                         style = TextStyle(fontSize = 16.sp),
                         modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}