package com.example.fit2081a1_yang_xingyu_33533563.data.csv

import android.R.attr.phoneNumber
import android.content.Context
import com.example.fit2081a1_yang_xingyu_33533563.data.model.Gender
import com.example.fit2081a1_yang_xingyu_33533563.data.model.NutritionScores
import com.example.fit2081a1_yang_xingyu_33533563.data.model.ScoreTypes
import com.example.fit2081a1_yang_xingyu_33533563.data.model.User
import com.example.fit2081a1_yang_xingyu_33533563.data.model.UserInfo
import java.io.BufferedReader
import java.io.IOException

/**
 * Created by Xingyu Yang
 * This module takes care of processing the CSV file.
 */

/**
 * Read a column from a CSV file by column title
 * @param context: Context
 * @param columnTitle: String
 * @param filePath: String
 * @return List<String>
 */
fun readColumn(context: Context, columnTitle: String, filePath: String = "testUsers.csv"): List<String> {
    val column = mutableListOf<String>()
    try {
        context.assets.open(filePath).use { inputStream ->
            val bufferedReader = BufferedReader(inputStream.reader())
            val lines = bufferedReader.readLines()
            val header = lines[0].split(",").map { it.trim() }
            val columnIndex = header.indexOf(columnTitle) // -1 if not found
            if (columnIndex == -1) {
                throw IllegalArgumentException("Column $columnTitle not found")
            }
            for (line in lines.subList(1, lines.size)) {
                val row = line.split(",").map { it.trim() }
                column.add(row[columnIndex])
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }

    return column
}

/**
 * Create a User instance from a CSV file by user ID
 * @param context: Context
 * @param userId: String
 * @param filePath: String
 * @return User
 */
fun getUserFromCSV(context: Context, userId: String, filePath: String = "testUsers.csv"): User {
    try {
        val userID = UserInfo.USERID.infoName
        val gender = UserInfo.GENDER.infoName
        val phoneNumber = UserInfo.PHONENUMBER.infoName

        context.assets.open(filePath).use { inputStream ->
            val reader = inputStream.reader()
            val lines = reader.readLines()

            // Parse header
            val header = lines[0].split(",").map { it.trim() }

            // Find user by ID
            for (i in 1 until lines.size) {
                val values = lines[i].split(",").map { it.trim() }
                val rowMap = header.zip(values).toMap()

                // Use direct column name "User_ID" instead of enum for consistency
                if (rowMap[userID] == userId) {
                    // Create user with more robust gender handling
                    val id = rowMap[userID] ?: ""
                    val phoneNumber = rowMap[phoneNumber] ?: ""

                    // Determine gender directly from Sex column like retrieveUserScore does
                    val gender = if (rowMap[gender]?.equals("Male", ignoreCase = true) == true)
                        Gender.MALE
                    else
                        Gender.FEMALE

                    val nutritionScores = NutritionScores.fromCsvMap(rowMap, gender)

                    return User(id, phoneNumber, gender, nutritionScores)
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    throw IllegalArgumentException("User with ID $userId not found")
}

/**
 * Retrieve a user's score of one given scoreType from a CSV file by user ID
 * @param context: Context
 * @param userId: String
 * @param filePath: String
 * @param scoreType: ScoreTypes
 * @return Float
 */
fun retrieveUserScore(context: Context,
                      userId: String,
                      scoreType: ScoreTypes,
                      filePath: String = "testUsers.csv"): Float {
    try {
        val userID = UserInfo.USERID.infoName
        val gender = UserInfo.GENDER.infoName

        context.assets.open(filePath).use { inputStream ->
            val reader = inputStream.reader()
            val lines = reader.readLines()

            // Parse header
            val header = lines[0].split(",").map { it.trim() }

            // Find user by ID
            for (i in 1 until lines.size) {
                val values = lines[i].split(",").map { it.trim() }
                val rowMap = header.zip(values).toMap()

                if (rowMap[userID] == userId) {
                    // Directly determine gender from the Sex column
                    val gender = if (rowMap[gender]?.equals("Male", ignoreCase = true) == true)
                        Gender.MALE
                    else
                        Gender.FEMALE

                    // Get the correct column name based on gender
                    val columnName = scoreType.getColumnName(gender)

                    // Return the score value
                    return rowMap[columnName]?.toFloatOrNull() ?: 0f
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    throw IllegalArgumentException("User with ID $userId not found")
}