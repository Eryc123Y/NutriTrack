package com.example.fit2081a1_yang_xingyu_33533563.util

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.fit2081a1_yang_xingyu_33533563.data.legacy.NutritionScores
import com.example.fit2081a1_yang_xingyu_33533563.data.legacy.ScoreTypes
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SharedPreferencesManagerTest {
    private lateinit var prefManager: SharedPreferencesManager
    private lateinit var context: Context

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        // Clear existing preferences
        context.getSharedPreferences("shared_preferences", Context.MODE_PRIVATE)
            .edit().clear().apply()

        prefManager = SharedPreferencesManager.getInstance(context)
    }

    @Test
    fun testCurrentUser() {
        // Test saving and retrieving current user ID
        assertNull(prefManager.getCurrentUser())

        prefManager.setCurrentUser("test123")
        assertEquals("test123", prefManager.getCurrentUser())
    }

    @Test
    fun testKnownUsers() {
        // Initially no known users
        assertTrue(prefManager.getKnownUsers().isEmpty())

        // Setting current user should add to known users
        prefManager.setCurrentUser("user1")
        assertEquals(1, prefManager.getKnownUsers().size)
        assertTrue(prefManager.getKnownUsers().contains("user1"))

        // Setting another user should add it too
        prefManager.setCurrentUser("user2")
        assertEquals(2, prefManager.getKnownUsers().size)
        assertTrue(prefManager.getKnownUsers().contains("user2"))

        // Setting same user again shouldn't duplicate
        prefManager.setCurrentUser("user1")
        assertEquals(2, prefManager.getKnownUsers().size)
    }

    @Test
    fun testUserScores() {
        val userId = "user123"

        // Create test scores
        val scoreMap = mutableMapOf<ScoreTypes, Float>()
        scoreMap[ScoreTypes.TOTAL] = 85.5f
        scoreMap[ScoreTypes.VEGETABLES] = 7.5f
        scoreMap[ScoreTypes.GRAINS_CEREALS] = 6.0f
        val scores = NutritionScores(scoreMap)

        // Save scores
        prefManager.saveUserScores(userId, scores)

        // Retrieve scores
        val retrievedScores = prefManager.getUserScores(userId)

        // Verify scores were saved correctly
        assertEquals(85.5f, retrievedScores.getScore(ScoreTypes.TOTAL))
        assertEquals(7.5f, retrievedScores.getScore(ScoreTypes.VEGETABLES))
        assertEquals(6.0f, retrievedScores.getScore(ScoreTypes.GRAINS_CEREALS))

        // Default value for missing scores should be 0f
        assertEquals(0f, retrievedScores.getScore(ScoreTypes.WATER))
    }

    @Test
    fun testMultipleUserScores() {
        // Create and save scores for user1
        val user1Id = "user1"
        val user1Scores = NutritionScores(mapOf(
            ScoreTypes.TOTAL to 80.0f,
            ScoreTypes.VEGETABLES to 8.5f
        ))
        prefManager.saveUserScores(user1Id, user1Scores)

        // Create and save scores for user2
        val user2Id = "user2"
        val user2Scores = NutritionScores(mapOf(
            ScoreTypes.TOTAL to 65.0f,
            ScoreTypes.VEGETABLES to 5.5f
        ))
        prefManager.saveUserScores(user2Id, user2Scores)

        // Verify each user has their own scores
        val retrieved1 = prefManager.getUserScores(user1Id)
        val retrieved2 = prefManager.getUserScores(user2Id)

        assertEquals(80.0f, retrieved1.getScore(ScoreTypes.TOTAL))
        assertEquals(8.5f, retrieved1.getScore(ScoreTypes.VEGETABLES))

        assertEquals(65.0f, retrieved2.getScore(ScoreTypes.TOTAL))
        assertEquals(5.5f, retrieved2.getScore(ScoreTypes.VEGETABLES))
    }

    @Test
    fun testClearUserData() {
        val userId = "testUser"

        // Set up test data
        prefManager.setCurrentUser(userId)
        prefManager.saveUserScores(userId, NutritionScores(mapOf(
            ScoreTypes.TOTAL to 75.0f
        )))

        // Verify data exists
        assertEquals(userId, prefManager.getCurrentUser())
        assertEquals(75.0f, prefManager.getUserScores(userId).getScore(ScoreTypes.TOTAL))

        // Clear user data
        prefManager.clearUserData(userId)

        // Scores should be cleared
        assertEquals(0f, prefManager.getUserScores(userId).getScore(ScoreTypes.TOTAL))

        // Current user ID should remain (clearUserData doesn't affect this)
        assertEquals(userId, prefManager.getCurrentUser())
    }

    @Test
    fun testLogout() {
        // Set current user
        prefManager.setCurrentUser("testUser")
        assertEquals("testUser", prefManager.getCurrentUser())

        // Logout
        prefManager.logout()

        // Current user should be cleared
        assertNull(prefManager.getCurrentUser())
    }
}