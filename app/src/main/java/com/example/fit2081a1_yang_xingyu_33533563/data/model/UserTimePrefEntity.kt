package com.example.fit2081a1_yang_xingyu_33533563.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Represents a user's preferences for specific time-related questions.
 */
@Entity(
    tableName = "user_time_preferences",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["userId"], unique = true)]
)
data class UserTimePreferenceEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String,                  // Foreign key to UserEntity
    val biggestMealTime: String? = null, // Time of biggest meal (e.g., "08:00")
    val sleepTime: String? = null,       // Time user goes to sleep (e.g., "23:00")
    val wakeUpTime: String? = null       // Time user wakes up (e.g., "07:00")
)
