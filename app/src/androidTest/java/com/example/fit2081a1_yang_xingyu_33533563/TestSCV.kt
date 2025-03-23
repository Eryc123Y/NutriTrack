package com.example.fit2081a1_yang_xingyu_33533563.data.csv

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.fit2081a1_yang_xingyu_33533563.data.model.Gender
import com.example.fit2081a1_yang_xingyu_33533563.data.model.ScoreTypes
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CsvParserTest {

    private lateinit var context: Context
    private val testUserId = "4"

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
    }

    @Test
    fun retrieveUserScore_debugPrintAllScores() {
        // Print all scores for debugging
        println("DEBUG: Scores for user ID $testUserId")
        for (scoreType in ScoreTypes.entries) {
            try {
                val score = retrieveUserScore(context, testUserId, scoreType)
                println("${scoreType.displayName}: $score/${scoreType.maxScore} (Column: ${scoreType.getColumnName(Gender.MALE)})")
            } catch (e: Exception) {
                println("ERROR retrieving ${scoreType.displayName}: ${e.message}")
            }
        }
    }

    @Test
    fun retrieveUserScore_correctValuesForUser4() {
        // Expected values for user ID 4 based on CSV
        val expectedScores = mapOf(
            ScoreTypes.VEGETABLES to 0.5f,
            ScoreTypes.FRUITS to 0.0f,
            ScoreTypes.GRAINS_CEREALS to 1.67f,
            ScoreTypes.WHOLE_GRAINS to 0.0f,
            ScoreTypes.MEAT_ALTERNATIVES to 2.0f,
            ScoreTypes.DAIRY to 0.0f,
            ScoreTypes.WATER to 0.0f,
            ScoreTypes.UNSATURATED_FATS to 2.5f,
            ScoreTypes.SODIUM to 5.0f,
            ScoreTypes.SUGAR to 10.0f,
            ScoreTypes.ALCOHOL to 5.0f,
            ScoreTypes.DISCRETIONARY to 10.0f,
            ScoreTypes.TOTAL to 41.67f
        )

        for ((scoreType, expectedValue) in expectedScores) {
            val actualScore = retrieveUserScore(context, testUserId, scoreType)
            assertEquals("${scoreType.displayName} score incorrect",
                expectedValue,
                actualScore,
                0.1f)
        }
    }

    @Test
    fun fix_retrieveUserScore_implementation() {
        // This test demonstrates how to fix the retrieveUserScore function
        // The function should be updated to use the gender-specific column name

        val user = getUserFromCSV(context, testUserId)
        val gender = if (user.gender.genderName == "Male") Gender.MALE else Gender.FEMALE

        for (scoreType in ScoreTypes.entries) {
            // Manually access CSV using the correct column name
            val columnName = scoreType.getColumnName(gender)
            try {
                context.assets.open("testUsers.csv").use { inputStream ->
                    val reader = inputStream.reader()
                    val lines = reader.readLines()

                    val header = lines[0].split(",").map { it.trim() }

                    for (i in 1 until lines.size) {
                        val values = lines[i].split(",").map { it.trim() }
                        val rowMap = header.zip(values).toMap()

                        if (rowMap["User_ID"] == testUserId) {
                            val expectedValue = rowMap[columnName]?.toFloatOrNull() ?: 0f
                            val actualValue = retrieveUserScore(context, testUserId, scoreType)

                            assertEquals("${scoreType.displayName} score doesn't match column $columnName",
                                expectedValue,
                                actualValue,
                                0.1f)
                        }
                    }
                }
            } catch (e: Exception) {
                println("Failed for column $columnName: ${e.message}")
            }
        }
    }
}