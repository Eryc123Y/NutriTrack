package com.example.fit2081a1_yang_xingyu_33533563.view.components

// import android.content.Context // Removed unused import
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fit2081a1_yang_xingyu_33533563.util.getColorforScore
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import com.example.fit2081a1_yang_xingyu_33533563.view.theme.Error
import com.example.fit2081a1_yang_xingyu_33533563.view.theme.Success
import com.example.fit2081a1_yang_xingyu_33533563.view.theme.Warning


@Composable
fun ScoreText(
    text: String,
    modifier: Modifier = Modifier,
    size: Int = 16,
    weight: String = "normal",
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    Text(
        text = text,
        fontSize = size.sp,
        fontWeight = if (weight == "bold") FontWeight.Bold else FontWeight.Normal,
        color = color,
        modifier = modifier
    )
}

@Composable
fun ScoreProgressBarRow(
    displayName: String,
    currentValue: Float,
    maxValue: Int
) {
    var visible by remember { mutableStateOf(false) }
    var animatedValue by remember { mutableFloatStateOf(0f) }
    val progress = if (maxValue > 0) currentValue / maxValue.toFloat() else 0f
    
    
    val progressColor = getColorforScore(currentValue.toInt(), maxValue)
    
    // Animation for the progress bar
    val animatedProgress = animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1000, easing = EaseInOut),
        label = "progressAnimation"
    ).value
    
    // Animation for the score value
    val animatedScore = animateFloatAsState(
        targetValue = animatedValue,
        animationSpec = tween(durationMillis = 1000, easing = LinearEasing),
        label = "scoreAnimation"
    ).value
    
    LaunchedEffect(key1 = currentValue) {
        visible = true
        animatedValue = currentValue
    }
    
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(500)) + expandVertically(animationSpec = tween(500))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp) 
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ScoreText(
                    text = displayName,
                    size = 16,
                    weight = "bold",
                    modifier = Modifier.weight(1f)
                )
                
                ScoreText(
                    text = "${animatedScore.toInt()}/$maxValue",
                    size = 16,
                    weight = "normal",
                    color = progressColor 
                )
            }
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .height(16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)) 
            ) {
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress)
                        .height(16.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    progressColor,
                                    progressColor.copy(alpha = 0.8f) 
                                )
                            )
                        )
                )
            }
        }
    }
}

@Composable
fun CircularScoreIndicator(
    score: Int,
    maxScore: Int,
    size: Dp = 160.dp,
    strokeWidth: Dp = 16.dp,
    accentColor: Color = getColorforScore(score, maxScore)
) {
    var animatedScore by remember { mutableIntStateOf(0) }
    val progress = score.toFloat() / maxScore
    
    val animatedProgress = animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(1500),
        label = "progress"
    ).value
    
    LaunchedEffect(key1 = score) {
        animatedScore = score
    }
    
    val scoreAnimation = animateFloatAsState(
        targetValue = animatedScore.toFloat(),
        animationSpec = tween(1500),
        label = "scoreAnimation"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(size)
    ) {
        CircularProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier.fillMaxSize(),
            strokeWidth = strokeWidth,
            trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            color = accentColor
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = scoreAnimation.value.toInt().toString(),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = accentColor
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
    
    val cardBackgroundColor = MaterialTheme.colorScheme.surface
    
    val accentColor = when {
        score < maxScore * 0.4 -> Error
        score < maxScore * 0.7 -> Warning
        else -> Success
    }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = cardBackgroundColor),
        shape = RoundedCornerShape(16.dp) 
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp) 
        ) {
            Text(
                text = "Food Quality Score",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = accentColor,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            CircularScoreIndicator(
                score = score,
                maxScore = maxScore,
                size = 180.dp, 
                strokeWidth = 20.dp,
                accentColor = accentColor
            )

            val scoreEvaluation = when {
                score < maxScore * 0.4 -> "Needs improvement"
                score < maxScore * 0.7 -> "Good progress"
                else -> "Excellent!"
            }
            
            Text(
                text = scoreEvaluation,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = accentColor,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )

            Text(
                text = "Your overall nutritional quality",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
