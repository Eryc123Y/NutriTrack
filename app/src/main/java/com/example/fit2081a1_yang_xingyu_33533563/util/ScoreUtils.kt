package com.example.fit2081a1_yang_xingyu_33533563.util

import androidx.compose.ui.graphics.Color
import com.example.fit2081a1_yang_xingyu_33533563.data.legacy.ScoreTypes

fun getGradientColorsForScore(score: Int): List<Color> {
    return when {
        score < 40 -> listOf(Color.Red, Color.Red.copy(red = 0.9f, green = 0.3f))
        score < 70 -> listOf(Color(0xFFFF8C00), Color(0xFFFFAA33))
        else -> listOf(Color(0xFF4CAF50), Color(0xFF8BC34A))
    }
}

fun getColorforScore(score: Int, currentMaxValue: Int): Color {
    if (currentMaxValue == 0) return Color.Gray
    return when {
        score.toFloat() / currentMaxValue < 0.4 -> Color.Red
        score.toFloat() / currentMaxValue < 0.7 -> Color(0xFFFF8C00)
        else -> Color(0xFF4CAF50)
    }
}