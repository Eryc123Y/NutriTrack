package com.example.fit2081a1_yang_xingyu_33533563.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Represents a user's preference for a specific time-related question.
 */
@Entity(
    tableName = "user_time_preferences",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TimePreferenceDefinitionEntity::class,
            parentColumns = ["timePrefKey"],
            childColumns = ["timePrefKey"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index(value = ["userId", "timePrefKey"], unique = true)]
)
data class UserTimePreferenceEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String,                  // Foreign key to UserEntity
    val timePrefKey: String,             // Foreign key to TimePreferenceDefinitionEntity
    val timeValue: String?               // The actual time string, e.g., "08:00"
)
