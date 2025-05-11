package com.example.fit2081a1_yang_xingyu_33533563.ui.components

import android.content.Context
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fit2081a1_yang_xingyu_33533563.data.csv.retrieveUserScore
import com.example.fit2081a1_yang_xingyu_33533563.data.legacy.ScoreTypes
import com.example.fit2081a1_yang_xingyu_33533563.util.SharedPreferencesManager
import com.example.fit2081a1_yang_xingyu_33533563.util.getColorforScore


@Composable
fun ScoreText(
    text: String,
    modifier: Modifier = Modifier,
    size: Int = 16,
    weight: String = "normal"
) {
    Text(
        text = text,
        fontSize = size.sp,
        fontWeight = if (weight == "bold") FontWeight.Bold else FontWeight.Normal,
        modifier = modifier
    )
}

@Composable
fun ScoreProgressIndicator(context: Context, scoreType: ScoreTypes, userID: String, modifier: Modifier = Modifier) {
    val scoreValue = retrieveUserScore(context, userID, scoreType)

    // Convert to 0.0-1.0 range for LinearProgressIndicator
    val progress = scoreValue / scoreType.maxScore.toFloat()

    LinearProgressIndicator(
        progress = { progress },
        modifier = modifier
            .height(10.dp),
        trackColor = MaterialTheme.colorScheme.surfaceVariant,
        color = getColorforScore(scoreValue.toInt(), scoreType),
        gapSize = 0.dp,
        drawStopIndicator = {}
    )
}

@Composable
fun ScoreProgressBarRow(scoreType: ScoreTypes) {
    val context = LocalContext.current
    val prefManager = SharedPreferencesManager.getInstance(context)
    val userID = prefManager.getCurrentUser()
    val score = retrieveUserScore(context, userID.toString(), scoreType).toInt()

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
            ScoreProgressIndicator(
                context = context,
                scoreType = scoreType,
                userID = userID.toString(),
                modifier = Modifier.weight(1f)
            )

            ScoreText(
                text = "$score/${scoreType.maxScore}",
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Composable
fun CircularScoreIndicator(
    score: Int,
    maxScore: Int,
    size: Dp = 160.dp,
    strokeWidth: Dp = 16.dp
) {
    val progress = score.toFloat() / maxScore
    val animatedProgress = animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(1000),
        label = "progress"
    ).value

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(size)
    ) {
        CircularProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier.fillMaxSize(),
            strokeWidth = strokeWidth,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            color = getColorforScore(score, ScoreTypes.TOTAL)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = score.toString(),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "/$maxScore",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun TotalScoreCard(userID: String, context: Context) {
    val totalScoreType = ScoreTypes.TOTAL
    val score = retrieveUserScore(context, userID, totalScoreType).toInt()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Food Quality Score",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            CircularScoreIndicator(
                score = score,
                maxScore = totalScoreType.maxScore
            )

            Text(
                text = "Your overall nutritional quality",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}
