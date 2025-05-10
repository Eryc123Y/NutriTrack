package com.example.fit2081a1_yang_xingyu_33533563.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Defines the available score types in the system.
 */
@Entity(tableName = "score_type_definitions")
data class ScoreTypeDefinitionEntity(
    @PrimaryKey val scoreTypeKey: String, // e.g., "TOTAL_SCORE", "VEGETABLES_SCORE"
    val displayName: String,          // e.g., "Total Food Quality Score", "Vegetables"
    val maxScore: Float,              // Maximum possible score for this type
    val description: String? = null,
    val displayOrder: Int? = null     // For ordering in UI if needed
) 