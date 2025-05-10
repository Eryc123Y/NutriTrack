package com.example.fit2081a1_yang_xingyu_33533563.data.model.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Represents a user in the system.
 * Has a relationship with PersonaEntity.
 * Other user-specific data like scores and preferences are in separate tables linking back to this UserEntity via userId.
 */
@Entity(
    tableName = "users",
    foreignKeys = [
        ForeignKey(
            entity = PersonaEntity::class,
            parentColumns = ["personaId"],
            childColumns = ["selectedPersonaId"],
            onDelete = ForeignKey.SET_NULL // If a persona is deleted, the user's personaId becomes null
        )
    ],
    indices = [Index(value = ["selectedPersonaId"])]
)
data class UserEntity(
    @PrimaryKey val userId: String,      // from csv or generated
    val name: String,                    // To be chosen upon registration or from CSV
    val phoneNumber: String,             // from csv
    val gender: String,                  // from csv (needs TypeConverter: Male/Female)
    val selectedPersonaId: String? = null, // Foreign key to PersonaEntity
    val currentLoggedInUser: Boolean = false // true if this is the currently active user
)
