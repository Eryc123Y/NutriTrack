package com.example.fit2081a1_yang_xingyu_33533563.data.legacy

/**
 * Created by Xingyu Yang
 * This module stores user information
 */

data class User(
    val id: String,
    val phoneNumber: String,
    val gender: Gender,
    val nutritionScores: NutritionScores
)