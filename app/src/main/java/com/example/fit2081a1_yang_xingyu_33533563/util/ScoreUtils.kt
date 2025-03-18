package com.example.fit2081a1_yang_xingyu_33533563.util

import androidx.compose.ui.graphics.Color

fun getGradientColorsForScore(score: Int): List<Color> {
    return when {
        score < 40 -> listOf(Color.Red, Color.Red.copy(red = 0.9f, green = 0.3f))
        score < 70 -> listOf(Color(0xFFFF8C00), Color(0xFFFFAA33)) // Dark orange to lighter orange
        else -> listOf(Color(0xFF4CAF50), Color(0xFF8BC34A)) // Dark green to lighter green
    }
}