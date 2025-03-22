package com.example.fit2081a1_yang_xingyu_33533563.data.csv

import android.content.Context
import com.example.fit2081a1_yang_xingyu_33533563.data.model.User
import com.example.fit2081a1_yang_xingyu_33533563.data.model.UserInfo
import java.io.BufferedReader
import java.io.IOException

/**
 * Created by Xingyu Yang
 * This module takes care of processing the CSV file.
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

fun getUserFromCSV(context: Context, userId: String, filePath: String = "testUsers.csv"): User {
    try {
        context.assets.open(filePath).use { inputStream ->
            val reader = inputStream.reader()
            val lines = reader.readLines()

            // Parse header
            val header = lines[0].split(",").map { it.trim() }

            // Find user by ID
            for (i in 1 until lines.size) {
                val values = lines[i].split(",").map { it.trim() }
                val rowMap = header.zip(values).toMap()

                if (rowMap[UserInfo.USERID.infoName] == userId) {
                    return User.fromCsvRow(rowMap)
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    // Create empty user if not found
    throw IllegalArgumentException("User with ID $userId not found")
}



