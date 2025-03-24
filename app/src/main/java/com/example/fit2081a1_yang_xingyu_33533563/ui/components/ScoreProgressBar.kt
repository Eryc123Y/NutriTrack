package com.example.fit2081a1_yang_xingyu_33533563.ui.components

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fit2081a1_yang_xingyu_33533563.data.csv.retrieveUserScore
import com.example.fit2081a1_yang_xingyu_33533563.data.model.ScoreTypes
import com.example.fit2081a1_yang_xingyu_33533563.util.SharedPreferencesManager
import com.example.fit2081a1_yang_xingyu_33533563.util.getColorforScore
import kotlin.text.toFloat


@Composable
fun ScoreInterface(userID: String) {
    val scoreList = ScoreTypes.entries.toList()
    Column(
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(vertical = 16.dp)
    ) {
        for (scoreType in scoreList) {
            ScoreProgressBarRow(scoreType, userID)
        }
    }
}

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
            .height(10.dp)
            .clip(RoundedCornerShape(5.dp)),
        trackColor = MaterialTheme.colorScheme.surfaceVariant,
        color = getColorforScore(scoreValue.toInt(), scoreType)
    )
}

@Composable
fun ScoreProgressBarRow(scoreType: ScoreTypes, userID: String) {
    val context = LocalContext.current
    val prefManager = SharedPreferencesManager(context)
    val userID = prefManager.getCurrentUser()

    if (scoreType == ScoreTypes.TOTAL) {
        // Title row for TOTAL
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            ScoreText(
                text = scoreType.displayName,
                size = 16,
                weight = "bold",
                modifier = Modifier.padding(bottom = 2.dp)
            )
        }
        // Progress row for TOTAL
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ScoreProgressIndicator(
                context = context,
                scoreType = scoreType,
                userID = userID.toString(),
                modifier = Modifier.weight(1f)
            )

            val score = retrieveUserScore(context, userID.toString(), scoreType).toInt()
            ScoreText(
                text = "$score/${scoreType.maxScore}",
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    } else {
        // Standard row for other scores
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            ScoreText(
                text = scoreType.displayName,
                modifier = Modifier.width(120.dp)
            )

            ScoreProgressIndicator(
                context = context,
                scoreType = scoreType,
                userID = userID.toString(),
                modifier = Modifier.weight(1f)
            )

            val score = runCatching {
                retrieveUserScore(context, userID.toString(), scoreType).toInt()
            }.getOrDefault(0)

            ScoreText(
                text = "$score/${scoreType.maxScore}",
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}