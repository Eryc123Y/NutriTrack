package com.example.fit2081a1_yang_xingyu_33533563.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.fit2081a1_yang_xingyu_33533563.data.model.NutritionScores
import com.example.fit2081a1_yang_xingyu_33533563.data.model.ScoreTypes

/**
 * Created by Xingyu Yang
 * SharedPreferencesManager is used to manage the shared preferences
 * of the app, for example, the questionnaire responses.
 */
class SharedPreferencesManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        "shared_preferences", Context.MODE_PRIVATE
    )

    fun setCurrentUser(userId: String) {
        sharedPreferences.edit() { putString("currentUserID", userId) }
    }

    fun getCurrentUser(): String? {
        return sharedPreferences.getString("currentUserID", null)
    }

    private fun addKnownUser(userId: String) {
        val knownUsers = getKnownUsers().toMutableSet()
        knownUsers.add(userId)
        sharedPreferences.edit() { putStringSet("known_users", knownUsers) }
    }

    fun getKnownUsers(): Set<String> {
        return sharedPreferences.getStringSet("known_users", emptySet()) ?: emptySet()
    }

    private fun generateUserKey(userId: String, key: String): String {
        return "user_${userId}_$key"
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

    fun saveQuestionnaireStatus() {
        // todo: implement
    }

    fun getQuestionnaireStatus() {
        // todo: implement
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
        sharedPreferences.edit() { remove("current_user_id") }
    }


}