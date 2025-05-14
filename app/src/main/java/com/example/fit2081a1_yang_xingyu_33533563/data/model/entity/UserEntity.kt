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
            parentColumns = ["personaID"],
            childColumns = ["userPersonaId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index(value = ["userPersonaId"])]
)
data class UserEntity(
    @PrimaryKey val userId: String,      // from csv or generated
    val userName: String? = null,                    // To be chosen upon registration or from CSV
    val userPhoneNumber: String,             // from csv
    val userGender: String?,                  // from csv (needs TypeConverter: Male/Female)
    val userPersonaId: String? = null, // Foreign key to PersonaEntity
    val userHashedCredential: String? = null, // to be used for login, set upon registration
    val userIsRegistered: Boolean = false, // to be set to true upon registration
)
