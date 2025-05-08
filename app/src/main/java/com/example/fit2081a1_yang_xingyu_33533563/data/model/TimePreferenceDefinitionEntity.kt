package com.example.fit2081a1_yang_xingyu_33533563.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Defines the available time preference types in the system.
 */
@Entity(tableName = "time_preference_definitions")
data class TimePreferenceDefinitionEntity(
    @PrimaryKey val timePrefKey: String, // e.g., "BIGGEST_MEAL", "SLEEP_TIME"
    val displayName: String,           // e.g., "Biggest Meal Time", "Sleep Time"
    val questionDescription: String,   // Original question asked to the user
    val displayOrder: Int? = null      // For ordering in UI if needed
) 