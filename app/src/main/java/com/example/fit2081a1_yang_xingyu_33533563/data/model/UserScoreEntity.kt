package com.example.fit2081a1_yang_xingyu_33533563.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Represents a specific nutrition score for a user.
 * Each user can have multiple scores (one for each ScoreType).
 */
@Entity(
    tableName = "user_scores",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE // If user is deleted, their scores are also deleted
        ),
        ForeignKey(
            entity = ScoreTypeDefinitionEntity::class,
            parentColumns = ["scoreTypeKey"],
            childColumns = ["scoreTypeKey"],
            onDelete = ForeignKey.RESTRICT // Don't delete a user score if type is removed; handle manually or disallow type deletion
        )
    ],
    indices = [Index(value = ["userId", "scoreTypeKey"], unique = true)] // Each user has one entry per score type
)
data class UserScoreEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String, // Foreign key to UserEntity
    val scoreTypeKey: String, // Foreign key to ScoreTypeDefinitionEntity
    val scoreValue: Float
)
