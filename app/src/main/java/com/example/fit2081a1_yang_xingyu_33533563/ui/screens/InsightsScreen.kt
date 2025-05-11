package com.example.fit2081a1_yang_xingyu_33533563.ui.screens

import android.content.Intent
import android.content.Intent.ACTION_SEND
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
import com.example.fit2081a1_yang_xingyu_33533563.navigation.Screen
import com.example.fit2081a1_yang_xingyu_33533563.ui.components.BottomNavigationBar
import com.example.fit2081a1_yang_xingyu_33533563.ui.components.ScoreText
import com.example.fit2081a1_yang_xingyu_33533563.ui.components.TopNavigationBar
import com.example.fit2081a1_yang_xingyu_33533563.ui.components.TotalScoreCard
import com.example.fit2081a1_yang_xingyu_33533563.util.SharedPreferencesManager
import com.example.fit2081a1_yang_xingyu_33533563.util.generateSharedText
import com.example.fit2081a1_yang_xingyu_33533563.util.getColorforScore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Preview(showBackground = true)
@Composable
fun InsightScreen(
    onNavigate: (String) -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val prefManager = SharedPreferencesManager(context)
    val userID = prefManager.getCurrentUser() ?: ""
    val scrollState = rememberScrollState()
    
    // State for loading and user data
    var isLoading by remember { mutableStateOf(true) }
    var user by remember { mutableStateOf<User?>(null) }
    var scores by remember { mutableStateOf<Map<ScoreTypes, Float>>(emptyMap()) }
    
    // Load data asynchronously
    LaunchedEffect(userID) {
        try {
            // Load user data
            user = withContext(Dispatchers.IO) {
                getUserFromCSV(context, userID)
            }
            
            // Load all scores in parallel
            scores = withContext(Dispatchers.IO) {
                ScoreTypes.entries.associateWith { scoreType ->
                    retrieveUserScore(context, userID, scoreType)
                }
            }
        } catch (e: Exception) {
            // Handle error appropriately
            e.printStackTrace()
        } finally {
            isLoading = false
        }
    }
    
    Scaffold (
        topBar = {
            TopNavigationBar(
                title = "Insights: Food Score",
                showBackButton = false,
                onBackButtonClick = onBackClick
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
            if (isLoading) {
                // Show loading indicator
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
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Total score card
                    TotalScoreCard(userID, context)
                    
                    // Category scores
                    Text(
                        text = "Score Categories",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(16.dp)
                    )
                    
                    // Display scores using the pre-loaded data
                    val scoreList = ScoreTypes.entries.filter { it != ScoreTypes.TOTAL }
                    for (scoreType in scoreList) {
                        ScoreProgressBarRow(
                            scoreType = scoreType,
                            score = scores[scoreType] ?: 0f
                        )
                    }
                    
                    user?.let { userData ->
                        ShareButton(userData)
                    }
                    ImproveDietButton()
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
        }
    ) {
        Text("Share With Someone")
    }
}

@Composable
fun ImproveDietButton(
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.padding(16.dp)
    ) {
        Text("Improve My Diet")
    }
}

// Modified ScoreProgressBarRow to accept pre-loaded score
@Composable
fun ScoreProgressBarRow(
    scoreType: ScoreTypes,
    score: Float
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Category name
        ScoreText(
            text = scoreType.displayName,
            size = 16,
            weight = "bold",
            modifier = Modifier.padding(bottom = 4.dp)
        )

        // Progress bar and score value in one consistent row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Use pre-loaded score instead of fetching
            val progress = score / scoreType.maxScore.toFloat()
            
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .weight(1f)
                    .height(10.dp),
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                color = getColorforScore(score.toInt(), scoreType),
                gapSize = 0.dp,
                drawStopIndicator = {}
            )

            ScoreText(
                text = "${score.toInt()}/${scoreType.maxScore}",
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}