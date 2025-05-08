package com.example.fit2081a1_yang_xingyu_33533563.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a user persona (e.g., "Balanced Eater", "Fitness Enthusiast").
 * Users will select one persona during the questionnaire.
 */
@Entity(tableName = "personas")
data class PersonaEntity(
    @PrimaryKey val personaId: String, // A unique ID for the persona, e.g., "BALANCED_EATER"
    val displayName: String,        // User-friendly name, e.g., "Balanced Eater"
    val description: String? = null // Optional detailed description of the persona
) 