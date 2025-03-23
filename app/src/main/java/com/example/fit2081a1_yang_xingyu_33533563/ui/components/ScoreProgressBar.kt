package com.example.fit2081a1_yang_xingyu_33533563.ui.components

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.fit2081a1_yang_xingyu_33533563.data.csv.retrieveUserScore
import com.example.fit2081a1_yang_xingyu_33533563.data.model.ScoreTypes
import com.example.fit2081a1_yang_xingyu_33533563.util.SharedPreferencesManager

@Preview
@Composable
fun ScoreInterface(userID: String = "4") {
    val scoreList = ScoreTypes.entries.toList()
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        for (scoreType in scoreList) {
            ScoreProgressBarRow(scoreType)
        }
    }

}


@Composable
fun ScoreProgressBarRow(scoreType: ScoreTypes) {
    val context = LocalContext.current
    val prefManager = SharedPreferencesManager(context)
    val userID = prefManager.getCurrentUser()
    if (scoreType == ScoreTypes.TOTAL) {
        Row(
            horizontalArrangement = Arrangement.Center
        ) {
            ScoreText(text = scoreType.displayName)
        }
        Row {
            ScoreSlider(context, ScoreTypes.TOTAL, userID.toString())
        }

    } else {
        Row(
        ) {
            Column(
                horizontalAlignment = Alignment.Start
            ){
                ScoreText(text = scoreType.displayName)
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                ScoreSlider(context, scoreType, userID.toString())
            }
        }
    }

}

@Composable
fun ScoreText(text: String, size: Int = 16, weight: String = "normal") {
    Text(
     text = text
    )
}

@Composable
fun ScoreSlider(context: Context, scoreType: ScoreTypes, userID: String) {
    val sliderValue = retrieveUserScore(context, userID, scoreType)
    Slider(
        value = sliderValue,
        onValueChange = {},
        valueRange = 0f..scoreType.maxScore.toFloat()

    )
}