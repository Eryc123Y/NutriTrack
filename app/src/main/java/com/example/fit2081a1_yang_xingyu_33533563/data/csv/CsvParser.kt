package com.example.fit2081a1_yang_xingyu_33533563.data.csv

import android.content.Context
// Unused imports can be removed later if CsvParser no longer needs them for readColumn
// import com.example.fit2081a1_yang_xingyu_33533563.data.legacy.Gender
// import com.example.fit2081a1_yang_xingyu_33533563.data.legacy.NutritionScores
// import com.example.fit2081a1_yang_xingyu_33533563.data.legacy.ScoreTypes
// import com.example.fit2081a1_yang_xingyu_33533563.data.legacy.User
// import com.example.fit2081a1_yang_xingyu_33533563.data.legacy.UserInfo
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
            if (lines.isEmpty()) { // Handle empty CSV file
                throw IOException("CSV file is empty: $filePath")
            }
            val header = lines[0].split(",").map { it.trim() }
            val columnIndex = header.indexOf(columnTitle) // -1 if not found
            if (columnIndex == -1) {
                // Consider logging this instead of crashing if a column might be optional
                // For InitDataUtils, these columns are expected.
                throw IllegalArgumentException("Column '$columnTitle' not found in CSV header of $filePath")
            }
            for (line in lines.subList(1, lines.size)) {
                val row = line.split(",")
                // Ensure row has enough columns before accessing by columnIndex
                // to prevent IndexOutOfBoundsException if a row is malformed (shorter than header)
                if (columnIndex < row.size) {
                    column.add(row[columnIndex].trim()) // Trim individual cell values too
                } else {
                    // Handle malformed row, e.g., add an empty string or log a warning
                    column.add("") // Add empty string for missing cell in this column
                    // System.err.println("Warning: Malformed row in $filePath. Expected at least ${columnIndex + 1} columns, got ${row.size}. Line: '$line'")
                }
            }
        }
    } catch (e: IOException) {
        // Log or rethrow IOException if needed, printStackTrace is okay for this context
        e.printStackTrace()
        // Optionally rethrow or return empty list to signal failure more clearly
        // throw e // or return emptyList()
    } catch (e: IllegalArgumentException) {
        // This is already thrown if column title is not found
        e.printStackTrace()
        throw e // Rethrow to ensure InitDataUtils knows the column is missing
    }

    return column
}

// Removed getUserFromCSV function

// Removed retrieveUserScore function