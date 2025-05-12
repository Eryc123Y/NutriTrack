package com.example.fit2081a1_yang_xingyu_33533563.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Defines the available score types in the system.
 */
@Entity(tableName = "score_type_definitions")
data class ScoreTypeDefinitionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val displayName: String,
    val maxScore: Float,
    val description: String? = null,
    val displayOrder: Int? = null
) 