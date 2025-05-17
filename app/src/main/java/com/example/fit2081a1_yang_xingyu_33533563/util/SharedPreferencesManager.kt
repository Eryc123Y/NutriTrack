package com.example.fit2081a1_yang_xingyu_33533563.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.fit2081a1_yang_xingyu_33533563.data.legacy.FoodCategory
import com.example.fit2081a1_yang_xingyu_33533563.data.legacy.NutritionScores
import com.example.fit2081a1_yang_xingyu_33533563.data.legacy.ScoreTypes
import com.example.fit2081a1_yang_xingyu_33533563.data.legacy.UserTimePref

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
        USER_PREFIX("user_"),
    }

    companion object {
        @Volatile
        private var instance: SharedPreferencesManager? = null
        fun getInstance(context: Context): SharedPreferencesManager {
            return instance ?: synchronized(this) {
                instance ?: SharedPreferencesManager(context).also { instance = it }
            }
        }
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

    fun logout() {
        sharedPreferences.edit() { remove(PreferenceKey.CURRENT_USER_ID.key) }
    }

    fun saveAdminCode(adminCode: String) {
        // Use the lazy-initialized sharedPreferences object
        sharedPreferences.edit {
            putString("admin_code", adminCode)
        } // or editor.commit()
    }
}