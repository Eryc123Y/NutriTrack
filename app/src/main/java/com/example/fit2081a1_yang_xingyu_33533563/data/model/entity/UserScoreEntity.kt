package com.example.fit2081a1_yang_xingyu_33533563.data.model.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

/**
 * Represents a specific nutrition score for a user.
 * Each user can have multiple scores (one for each ScoreType).
 * This is a weak entity that references both the UserEntity and ScoreTypeDefinitionEntity.
 * We normalised the scoreType in the DB for possible future extension of ScoreType.
 */
@Entity(
    tableName = "user_scores",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["scoreUserId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ScoreTypeDefinitionEntity::class,
            parentColumns = ["scoreDefId"],
            childColumns = ["scoreTypeKey"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    primaryKeys = ["scoreUserId", "scoreTypeKey"],
)
data class UserScoreEntity(
    val scoreUserId: String, // Foreign key to UserEntity
    val scoreTypeKey: String, // Foreign key to ScoreTypeDefinitionEntity
    val scoreValue: Float
)
