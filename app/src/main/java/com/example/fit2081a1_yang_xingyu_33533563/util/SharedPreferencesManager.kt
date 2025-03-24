package com.example.fit2081a1_yang_xingyu_33533563.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.fit2081a1_yang_xingyu_33533563.data.model.NutritionScores
import com.example.fit2081a1_yang_xingyu_33533563.data.model.ScoreTypes
import com.example.fit2081a1_yang_xingyu_33533563.data.model.UserTimePref
import com.example.fit2081a1_yang_xingyu_33533563.ui.components.FoodCategory

/**
 * Created by Xingyu Yang
 * SharedPreferencesManager is used to manage the shared preferences
 * of the app, for example, the questionnaire responses.
 */
class SharedPreferencesManager(context: Context) {

    enum class PreferenceKey(val key: String) {
        PREFERENCES_FILE("shared_preferences"),
        CURRENT_USER_ID("currentUserID"),
        KNOWN_USERS("known_users"),
        USER_PREFIX("user_")
    }

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        PreferenceKey.PREFERENCES_FILE.key, Context.MODE_PRIVATE
    )

    fun setCurrentUser(userId: String) {
        sharedPreferences.edit() {
            putString(PreferenceKey.CURRENT_USER_ID.key, userId)
            addKnownUser(userId)
        }
    }

    fun getCurrentUser(): String? {
        return sharedPreferences.getString(PreferenceKey.CURRENT_USER_ID.key, null)
    }

    private fun addKnownUser(userId: String) {
        val knownUsers = getKnownUsers().toMutableSet()
        knownUsers.add(userId)
        sharedPreferences.edit() { putStringSet(PreferenceKey.KNOWN_USERS.key, knownUsers) }
    }

    fun getKnownUsers(): Set<String> {
        return sharedPreferences.getStringSet(PreferenceKey.KNOWN_USERS.key, emptySet()) ?: emptySet()
    }

    private fun generateUserKey(userId: String, key: String): String {
        return "${PreferenceKey.USER_PREFIX.key}${userId}_$key"
    }

    fun saveUserScores(userId: String, scores: NutritionScores) {
        sharedPreferences.edit() {
            for (scoreType in ScoreTypes.entries) {
                val score = scores.getScore(scoreType)
                val key = generateUserKey(userId, scoreType.displayName)
                putFloat(key, score)
            }
        }
    }

    fun getUserScores(userId: String): NutritionScores {
        val scoreMap = mutableMapOf<ScoreTypes, Float>()
        for (scoreType in ScoreTypes.entries) {
            val key = generateUserKey(userId, scoreType.displayName)
            val score = sharedPreferences.getFloat(key, 0f)
            scoreMap[scoreType] = score
        }
        return NutritionScores(scoreMap)
    }

    fun getUserPersona(userId: String): String {
        val key = generateUserKey(userId, "persona")
        return sharedPreferences.getString(key, "") ?: ""
    }

    fun setUserPersona(userId: String, persona: String) {
        val key = generateUserKey(userId, "persona")
        sharedPreferences.edit() { putString(key, persona) }
    }

    fun getCheckboxState(userId: String, category: FoodCategory): Boolean {
        val key = generateUserKey(userId, category.foodName)
        return sharedPreferences.getBoolean(key, false)
    }

    fun setCheckboxState(userId: String, checkedState: Map<FoodCategory, Boolean>) {
        sharedPreferences.edit {
            for ((category, isChecked) in checkedState) {
                val key = generateUserKey(userId, category.foodName)
                putBoolean(key, isChecked)
            }
        }
    }

    fun getTimePref(userId: String, timePrefType: UserTimePref): String {
        val key = generateUserKey(userId, timePrefType.timePrefName)
        return sharedPreferences.getString(key, "") ?: ""
    }

    fun setTimePref(userId: String, timePrefType: UserTimePref, time: String) {
        val key = generateUserKey(userId, timePrefType.timePrefName)
        sharedPreferences.edit() { putString(key, time) }
    }



    // Clear specific user data
    fun clearUserData(userId: String) {
        sharedPreferences.edit() {
            sharedPreferences.all.keys
                .filter { it.startsWith("user_${userId}_") }
                .forEach { remove(it) }
        }
    }

    // Just logout (remove current user reference)
    fun logout() {
        sharedPreferences.edit() { remove(PreferenceKey.CURRENT_USER_ID.key) }
    }
}