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
            val userId = csvRow[UserInfo.USERID.infoName] ?: ""
            val phoneNumber = csvRow[UserInfo.PHONENUMBER.infoName] ?: ""
            val gender = Gender.fromString(csvRow[UserInfo.GENDER.infoName] ?: "")
            val nutritionScores = NutritionScores.fromCsvMap(csvRow, gender)

            return User(userId, phoneNumber, gender, nutritionScores)
        }
    }
}