package com.example.fit2081a1_yang_xingyu_33533563.data.model

/**
 * Created by Xingyu Yang
 * This module stores user information
 */

data class User(
    val id: String,
    val phoneNumber: String,
    val gender: Gender,
    val nutritionScores: NutritionScores
) {
    companion object {
        fun fromCsvRow(csvRow: Map<String, String>): User {
            val userId = csvRow["User_ID"] ?: ""
            val phoneNumber = csvRow["PhoneNumber"] ?: ""
            val gender = Gender.fromString(csvRow["Sex"] ?: "")
            val nutritionScores = NutritionScores.fromCsvMap(csvRow, gender)

            return User(userId, phoneNumber, gender, nutritionScores)
        }
    }
}