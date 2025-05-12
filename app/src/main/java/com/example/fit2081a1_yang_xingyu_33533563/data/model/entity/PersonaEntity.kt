package com.example.fit2081a1_yang_xingyu_33533563.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a user persona (e.g., "Balanced Eater", "Fitness Enthusiast").
 * Users will select one persona during the questionnaire.
 */
@Entity(tableName = "personas")
data class PersonaEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val displayName: String,
    val description: String? = null
) 