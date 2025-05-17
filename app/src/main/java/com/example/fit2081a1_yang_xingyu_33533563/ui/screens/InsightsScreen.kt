package com.example.fit2081a1_yang_xingyu_33533563.ui.screens

import android.R.attr.onClick
import android.content.Intent
import android.content.Intent.ACTION_SEND
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.fit2081a1_yang_xingyu_33533563.data.csv.getUserFromCSV
import com.example.fit2081a1_yang_xingyu_33533563.data.csv.retrieveUserScore
import com.example.fit2081a1_yang_xingyu_33533563.data.legacy.ScoreTypes
import com.example.fit2081a1_yang_xingyu_33533563.data.legacy.User
import com.example.fit2081a1_yang_xingyu_33533563.data.viewmodel.InsightsViewModel
import com.example.fit2081a1_yang_xingyu_33533563.navigation.Screen
import com.example.fit2081a1_yang_xingyu_33533563.ui.components.BottomNavigationBar
import com.example.fit2081a1_yang_xingyu_33533563.ui.components.ScoreText
import com.example.fit2081a1_yang_xingyu_33533563.ui.components.TopNavigationBar
import com.example.fit2081a1_yang_xingyu_33533563.ui.components.TotalScoreCard
import com.example.fit2081a1_yang_xingyu_33533563.ui.components.ScoreProgressBarRow
import com.example.fit2081a1_yang_xingyu_33533563.util.SharedPreferencesManager
import com.example.fit2081a1_yang_xingyu_33533563.util.generateSharedText
import com.example.fit2081a1_yang_xingyu_33533563.util.getColorforScore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
            modifier = Modifier.fillMaxSize().padding(innerPadding),
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
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Total score card
                    TotalScoreCard(userID, context)
                    
                    if (displayableScores.isNotEmpty()) {
                        Text(
                            text = "Score Categories",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                        )
                        
                        // Display scores using data from ViewModel
                        displayableScores.filter { it.displayName != ScoreTypes.TOTAL.displayName }.forEach { scoreData ->
                            ScoreProgressBarRow(
                                displayName = scoreData.displayName,
                                currentValue = scoreData.scoreValue,
                                maxValue = scoreData.maxScore
                            )
                        }
                    } else if (!isLoading) {
                        Text("No scores available or user ID not found.", modifier = Modifier.padding(16.dp))
                    }
                    
                    // User data for ShareButton might need to be fetched or passed differently if not using local getUserFromCSV
                    var userForShare by remember { mutableStateOf<User?>(null) }
                    LaunchedEffect(userID) {
                        if (userID.isNotEmpty()) {
                             userForShare = withContext(Dispatchers.IO) {
                                getUserFromCSV(context, userID)
                            }
                        }
                    }
                    userForShare?.let { userData ->
                        ShareButton(userData)
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
        modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
    ) {
        Text("Share With Someone")
    }
}

@Composable
fun ImproveDietButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Button(
        onClick = onClick,
        modifier = modifier.padding(bottom = 16.dp)
    ) {
        Text("Improve My Diet")
    }
}