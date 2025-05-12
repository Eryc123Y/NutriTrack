package com.example.fit2081a1_yang_xingyu_33533563.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Defines the available score types in the system.
 */
@Entity(tableName = "score_type_definitions")
data class ScoreTypeDefinitionEntity(
    @PrimaryKey(autoGenerate = true) val scoreDefId: Int = 0,
    val scoreTypeName: String,
    val scoreMaximum: Int,
) 