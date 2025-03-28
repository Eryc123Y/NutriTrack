package com.example.fit2081a1_yang_xingyu_33533563.util

import com.example.fit2081a1_yang_xingyu_33533563.data.model.NutritionScores

/**
 * A function to generate a shared text string for sharing.
 */
fun generateSharedText(nutritionScores: NutritionScores): String {
    val scoreList = nutritionScores.scores.entries.joinToString("\n") { (key, value) ->
        "${key.displayName}: ${value.toInt()}"
    }
    return "Nutrition Scores:\n$scoreList"
}