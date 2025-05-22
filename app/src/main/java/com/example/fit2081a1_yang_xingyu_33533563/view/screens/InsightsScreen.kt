package com.example.fit2081a1_yang_xingyu_33533563.view.screens

import android.content.Intent
import android.content.Intent.ACTION_SEND
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fit2081a1_yang_xingyu_33533563.data.legacy.ScoreTypes
import com.example.fit2081a1_yang_xingyu_33533563.data.legacy.User
import com.example.fit2081a1_yang_xingyu_33533563.data.viewmodel.InsightsViewModel
import com.example.fit2081a1_yang_xingyu_33533563.navigation.Screen
import com.example.fit2081a1_yang_xingyu_33533563.view.components.BottomNavigationBar
import com.example.fit2081a1_yang_xingyu_33533563.view.components.TopNavigationBar
import com.example.fit2081a1_yang_xingyu_33533563.view.components.TotalScoreCard
import com.example.fit2081a1_yang_xingyu_33533563.view.components.ScoreProgressBarRow
import com.example.fit2081a1_yang_xingyu_33533563.util.SharedPreferencesManager
import com.example.fit2081a1_yang_xingyu_33533563.util.generateSharedText
import com.example.fit2081a1_yang_xingyu_33533563.view.theme.AccentTeal
import com.example.fit2081a1_yang_xingyu_33533563.view.theme.Success

@Composable
fun InsightScreen(
    viewModel: InsightsViewModel,
    onNavigate: (String) -> Unit,
    onNavigateToCoach: () -> Unit,
) {
    val context = LocalContext.current
    val prefManager = SharedPreferencesManager.getInstance(context)
    val userID = prefManager.getCurrentUser() ?: ""
    val scrollState = rememberScrollState()
    
    // Observe ViewModel states
    val isLoading by viewModel.isLoading.collectAsState()
    val displayableScores by viewModel.displayableScores.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val userNutritionScoresForShare by viewModel.userNutritionScoresForShare.collectAsState()
    
    // Load data using ViewModel
    LaunchedEffect(userID) {
        if (userID.isNotEmpty()) {
            viewModel.setUserId(userID)
        }
    }
    
    // Handle error messages
    errorMessage?.let {
        Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        viewModel.clearErrorMessage() // Consume the error
    }
    
    Scaffold (
        topBar = {
            TopNavigationBar(
                title = "Insights: Food Score",
                showBackButton = false,
                onBackButtonClick = { onNavigate(Screen.Home.route) }
            )
        },
        bottomBar = {
            BottomNavigationBar(
                currentRoute = Screen.Insights.route,
                onNavigate = onNavigate
            )
        }
    ){ innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            if (isLoading && displayableScores.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Total score card
                    val totalScoreData = displayableScores.find { it.displayName == ScoreTypes.TOTAL.displayName }
                    totalScoreData?.let {
                        TotalScoreCard(
                            score = it.scoreValue.toInt(),
                            maxScore = it.maxScore
                        )
                    } ?: if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                    } else {
                        Text("Total score not available.", modifier = Modifier.padding(16.dp))
                    }
                    
                    if (displayableScores.any { it.displayName != ScoreTypes.TOTAL.displayName }) {
                        Text(
                            text = "Score Categories",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .padding(top = 16.dp, bottom = 8.dp)
                                .align(Alignment.Start)
                        )
                        
                        // Card for score categories with elevated style
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                // Display scores using data from ViewModel
                                displayableScores.filter { it.displayName != ScoreTypes.TOTAL.displayName }.forEach { scoreData ->
                                    ScoreProgressBarRow(
                                        displayName = scoreData.displayName,
                                        currentValue = scoreData.scoreValue,
                                        maxValue = scoreData.maxScore
                                    )
                                }
                            }
                        }
                    } else if (!isLoading) {
                        Text("No scores available or user ID not found.", modifier = Modifier.padding(16.dp))
                    }
                    
                    // Use userNutritionScoresForShare from ViewModel for ShareButton
                    userNutritionScoresForShare?.let { nutritionScores ->
                        val dummyUserForShare = User(id = userID, phoneNumber = "", gender = com.example.fit2081a1_yang_xingyu_33533563.data.legacy.Gender.FEMALE, nutritionScores = nutritionScores)
                        ShareButton(dummyUserForShare)
                    }
                    ImproveDietButton(onClick = onNavigateToCoach)
                }
            }
        }
    }
}

@Composable
fun ShareButton(user: User) {
    val context = LocalContext.current
    Button(
        onClick = {
            val shareText: String = generateSharedText(user.nutritionScores)
            val shareIntent = Intent(ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText)
            val chooserIntent = Intent.createChooser(shareIntent, "Share text via")
            context.startActivity(chooserIntent)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = AccentTeal
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp
        )
    ) {
        Icon(
            imageVector = Icons.Filled.Share,
            contentDescription = "Share Icon",
            modifier = Modifier.size(ButtonDefaults.IconSize)
        )
        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
        Text(
            text = "Share With Someone",
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun ImproveDietButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Success
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp
        )
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = "Improve Diet Icon",
            modifier = Modifier.size(ButtonDefaults.IconSize)
        )
        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
        Text(
            text = "Improve My Diet",
            fontWeight = FontWeight.SemiBold
        )
    }
}