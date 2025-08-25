package com.example.fit2081a1_yang_xingyu_33533563.util

import androidx.compose.ui.graphics.Color
import com.example.fit2081a1_yang_xingyu_33533563.data.legacy.ScoreTypes
import com.example.fit2081a1_yang_xingyu_33533563.view.theme.AccentAmber
import com.example.fit2081a1_yang_xingyu_33533563.view.theme.AccentCyan
import com.example.fit2081a1_yang_xingyu_33533563.view.theme.AccentLime
import com.example.fit2081a1_yang_xingyu_33533563.view.theme.AccentTeal
import com.example.fit2081a1_yang_xingyu_33533563.view.theme.Error
import com.example.fit2081a1_yang_xingyu_33533563.view.theme.Success
import com.example.fit2081a1_yang_xingyu_33533563.view.theme.Warning

/**
 * Returns gradient colors for score visualization based on the score value.
 * Uses a traffic light system: red for low scores, amber for medium, green for high.
 * 
 * @param score The numeric score to evaluate (0-100 scale)
 * @return List of two colors for gradient effect
 */
fun getGradientColorsForScore(score: Int): List<Color> {
    return when {
        score < 40 -> listOf(Error, Error.copy(red = 1f, green = 0.4f, blue = 0.4f)) // Red gradient for poor scores
        score < 70 -> listOf(Warning, AccentAmber) // Amber gradient for medium scores
        else -> listOf(Success, AccentLime) // Green gradient for good scores
    }
}

/**
 * Returns a single color based on score percentage relative to maximum value.
 * Provides visual feedback using color coding for different performance levels.
 * 
 * @param score The current score value
 * @param currentMaxValue The maximum possible score value
 * @return Color representing the score level (Gray for invalid input)
 */
fun getColorforScore(score: Int, currentMaxValue: Int): Color {
    // Handle division by zero case
    if (currentMaxValue == 0) return Color.Gray
    
    // Calculate percentage of score relative to maximum
    val percentage = score.toFloat() / currentMaxValue
    
    return when {
        percentage < 0.4 -> Error        // Red for scores below 40%
        percentage < 0.7 -> Warning     // Orange for scores 40-70%
        percentage < 0.85 -> AccentTeal  // Teal for scores 70-85%
        else -> Success                 // Green for scores 85% and above
    }
}