package com.example.fit2081a1_yang_xingyu_33533563.data.model.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Represents a user's preference for a specific food category.
 * Each user can have multiple entries, one for each food category they have a preference for.
 */
@Entity(
    tableName = "user_food_category_preferences",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = FoodCategoryDefinitionEntity::class,
            parentColumns = ["foodCategoryKey"],
            childColumns = ["foodCategoryKey"],
            onDelete = ForeignKey.RESTRICT // Don't delete a user pref if category is removed; handle manually or disallow category deletion
        )
    ],
    indices = [Index(value = ["userId", "foodCategoryKey"], unique = true)]
)
data class UserFoodCategoryPreferenceEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String,
    val foodCategoryKey: String, // Foreign key to FoodCategoryDefinitionEntity
    val isChecked: Boolean           // True if the user likes/eats this category
)
