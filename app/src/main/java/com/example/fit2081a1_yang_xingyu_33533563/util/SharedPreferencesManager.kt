package com.example.fit2081a1_yang_xingyu_33533563.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
/**
 * Singleton manager for handling SharedPreferences operations in NutriTrack.
 * 
 * This class manages persistent storage for user session data, including:
 * - Current logged-in user ID
 * - List of known users for the login dropdown
 * - Other app preferences and settings
 * 
 * Uses the singleton pattern to ensure consistent access across the application.
 * 
 * @param context Application context for accessing SharedPreferences
 */
class SharedPreferencesManager(context: Context) {

    /**
     * Enumeration of all preference keys used in the application.
     * Centralizes key management to avoid string duplication and typos.
     */
    enum class PreferenceKey(val key: String) {
        PREFERENCES_FILE("shared_preferences"),    // Main preferences file name
        CURRENT_USER_ID("currentUserID"),         // Currently logged-in user ID
        KNOWN_USERS("known_users"),               // Set of all registered user IDs
    }

    companion object {
        // Volatile reference for thread-safe singleton pattern
        @Volatile
        private var instance: SharedPreferencesManager? = null
        
        /**
         * Gets the singleton instance of SharedPreferencesManager.
         * Uses double-checked locking for thread-safe initialization.
         * 
         * @param context Application context
         * @return Singleton instance of SharedPreferencesManager
         */
        fun getInstance(context: Context): SharedPreferencesManager {
            return instance ?: synchronized(this) {
                instance ?: SharedPreferencesManager(context).also { instance = it }
            }
        }
    }

    // SharedPreferences instance for storing key-value pairs
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        PreferenceKey.PREFERENCES_FILE.key, Context.MODE_PRIVATE
    )

    /**
     * Sets the current logged-in user ID and adds them to known users.
     * This is called during successful login or registration.
     * 
     * @param userId The ID of the user to set as current
     */
    fun setCurrentUser(userId: String) {
        sharedPreferences.edit() {
            putString(PreferenceKey.CURRENT_USER_ID.key, userId)
            addKnownUser(userId)
        }
    }

    /**
     * Retrieves the currently logged-in user ID.
     * Returns null if no user is currently logged in.
     * 
     * @return Current user ID or null if not logged in
     */
    fun getCurrentUser(): String? {
        return sharedPreferences.getString(PreferenceKey.CURRENT_USER_ID.key, null)
    }

    /**
     * Adds a user ID to the set of known users.
     * Used to populate the login dropdown with all registered users.
     * 
     * @param userId The user ID to add to known users
     */
    private fun addKnownUser(userId: String) {
        val knownUsers = getKnownUsers().toMutableSet()
        knownUsers.add(userId)
        sharedPreferences.edit() { putStringSet(PreferenceKey.KNOWN_USERS.key, knownUsers) }
    }

    /**
     * Retrieves the set of all known user IDs.
     * Used to populate the user selection dropdown on the login screen.
     * 
     * @return Set of all known user IDs (empty if none exist)
     */
    fun getKnownUsers(): Set<String> {
        return sharedPreferences.getStringSet(PreferenceKey.KNOWN_USERS.key, emptySet()) ?: emptySet()
    }

    /**
     * Clears the current user session by removing the current user ID.
     * Called during logout to terminate the user session.
     */
    fun logout() {
        sharedPreferences.edit() { remove(PreferenceKey.CURRENT_USER_ID.key) }
    }
}