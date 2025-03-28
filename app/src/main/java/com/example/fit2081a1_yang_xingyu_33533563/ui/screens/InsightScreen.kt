package com.example.fit2081a1_yang_xingyu_33533563.ui.screens

import android.content.Intent
import android.content.Intent.ACTION_SEND
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import com.example.fit2081a1_yang_xingyu_33533563.data.csv.getUserFromCSV
import com.example.fit2081a1_yang_xingyu_33533563.data.model.ScoreTypes
import com.example.fit2081a1_yang_xingyu_33533563.data.model.User
import com.example.fit2081a1_yang_xingyu_33533563.navigation.Screen
import com.example.fit2081a1_yang_xingyu_33533563.ui.components.BottomNavigationBar
import com.example.fit2081a1_yang_xingyu_33533563.ui.components.ScoreProgressBarRow
import com.example.fit2081a1_yang_xingyu_33533563.ui.components.TopNavigationBar
import com.example.fit2081a1_yang_xingyu_33533563.ui.components.TotalScoreCard
import com.example.fit2081a1_yang_xingyu_33533563.util.SharedPreferencesManager
import com.example.fit2081a1_yang_xingyu_33533563.util.generateSharedText

@Preview(showBackground = true)
@Composable
fun InsightsScreen(
    onNavigate: (String) -> Unit = {},
    onBackClick: () -> Unit = {}
) {
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
        val scrollState = rememberScrollState()
        Surface(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            val context = LocalContext.current
            val prefManager = SharedPreferencesManager(context)
            val userID = prefManager.getCurrentUser() ?: ""
            val user: User = getUserFromCSV(context, userID)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // circular progress card for total score
                TotalScoreCard(userID, context)

                // Category scores title
                Text(
                    text = "Score Categories",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp)
                )

                // Display all scores except total (which is already shown in the card)
                val scoreList = ScoreTypes.entries.filter { it != ScoreTypes.TOTAL }
                for (scoreType in scoreList) {
                    ScoreProgressBarRow(scoreType)
                }
                ShareButton(user)
                ImproveDietButton()

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