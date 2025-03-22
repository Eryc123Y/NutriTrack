package com.example.fit2081a1_yang_xingyu_33533563.util

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.fit2081a1_yang_xingyu_33533563.data.model.Gender
import com.example.fit2081a1_yang_xingyu_33533563.data.model.NutritionScores
import com.example.fit2081a1_yang_xingyu_33533563.data.model.ScoreTypes
import com.example.fit2081a1_yang_xingyu_33533563.data.model.User
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

        prefManager = SharedPreferencesManager(context)
    }

    @Test
    fun testCurrentUser() {
        // Test saving and retrieving current user ID
        assertNull(prefManager.getCurrentUser())

        prefManager.setCurrentUser("test123")
        assertEquals("test123", prefManager.getCurrentUser())
    }

}