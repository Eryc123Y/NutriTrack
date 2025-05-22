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

fun getGradientColorsForScore(score: Int): List<Color> {
    return when {
        score < 40 -> listOf(Error, Error.copy(red = 1f, green = 0.4f, blue = 0.4f))
        score < 70 -> listOf(Warning, AccentAmber)
        else -> listOf(Success, AccentLime)
    }
}

fun getColorforScore(score: Int, currentMaxValue: Int): Color {
    if (currentMaxValue == 0) return Color.Gray
    
    val percentage = score.toFloat() / currentMaxValue
    return when {
        percentage < 0.4 -> Error
        percentage < 0.7 -> Warning
        percentage < 0.85 -> AccentTeal
        else -> Success
    }
}