package com.example.fit2081a1_yang_xingyu_33533563.data.model.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

/**
 * Represents a user's preference for a specific food category.
 * This is a weak entity that references both the UserEntity and FoodCategoryDefinitionEntity.
 * Each user can have multiple entries, one for each food category they have a preference for.
 */
@Entity(
    tableName = "user_food_category_preferences",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["foodPrefUserId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = FoodCategoryDefinitionEntity::class,
            parentColumns = ["foodDefId"],
            childColumns = ["foodPrefCategoryKey"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    primaryKeys = ["foodPrefUserId", "foodPrefCategoryKey"],
    indices = [Index(value = ["foodPrefCategoryKey"])]
)
data class UserFoodPreferenceEntity(
    val foodPrefUserId: String, // Foreign key to UserEntity
    val foodPrefCategoryKey: String, // Foreign key to FoodCategoryDefinitionEntity
    val foodPrefCheckedStatus: Boolean
)