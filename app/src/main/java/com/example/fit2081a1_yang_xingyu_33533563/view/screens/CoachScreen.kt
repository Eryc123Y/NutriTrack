package com.example.fit2081a1_yang_xingyu_33533563.view.screens

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fit2081a1_yang_xingyu_33533563.data.viewmodel.GenAIViewModel
import com.example.fit2081a1_yang_xingyu_33533563.data.viewmodel.UserStatsViewModel
import com.example.fit2081a1_yang_xingyu_33533563.navigation.Screen
import com.example.fit2081a1_yang_xingyu_33533563.view.components.BottomNavigationBar
import com.example.fit2081a1_yang_xingyu_33533563.view.components.TopNavigationBar
import com.example.fit2081a1_yang_xingyu_33533563.data.viewmodel.FruitViewModel

@Composable
fun CoachScreen(
    onNavigate: (String) -> Unit = {},
    onBackClick: () -> Unit = {},
    fruitViewModel: FruitViewModel,
    genAIViewModel: GenAIViewModel,
    userStatsViewModel: UserStatsViewModel
) {
    var fruitNameInput by remember { mutableStateOf("") }
    val fruitDetails by fruitViewModel.fruitDetails.collectAsState()
    val isLoading by fruitViewModel.isLoading.collectAsState()
    val shouldShowFruitViceQuery by userStatsViewModel.shouldShowFruitViceQuery.collectAsState()

    // TODO: Replace with actual userId from AuthViewModel or similar
    val currentUserId = "1" // Placeholder, ensure this is the actual logged-in user's ID

    LaunchedEffect(key1 = currentUserId) {
        if (currentUserId.isNotBlank()) {
            userStatsViewModel.loadUserFruitServingsize(currentUserId)
        }
    }

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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (shouldShowFruitViceQuery) {
                Text(
                    "You are recommended to increase fruit intake.Ask NutriCoach about a fruit!",
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
                        fruitViewModel.fetchFruitDetails(fruitNameInput)
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
            } else {
                Text(
                    "Great job on your fruit intake! No specific advice needed here right now.",
                    style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Medium),
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}