package com.example.fit2081a1_yang_xingyu_33533563.view.components

// import android.content.Context // Removed unused import
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
fun ScoreProgressBarRow(
    displayName: String,
    currentValue: Float,
    maxValue: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        ScoreText(
            text = displayName,
            size = 16,
            weight = "bold",
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val progress = if (maxValue > 0) currentValue / maxValue.toFloat() else 0f
            
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .weight(1f)
                    .height(10.dp),
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                color = getColorforScore(currentValue.toInt(), maxValue),
                gapSize = 0.dp,
                drawStopIndicator = {}
            )

            ScoreText(
                text = "${currentValue.toInt()}/$maxValue",
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
            color = getColorforScore(score, maxScore)
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
fun TotalScoreCard(
    score: Int,
    maxScore: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
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
                maxScore = maxScore
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
