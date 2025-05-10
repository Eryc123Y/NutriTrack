package com.example.fit2081a1_yang_xingyu_33533563.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Defines the available food categories lookup table.
 */
@Entity(tableName = "food_category_definitions")
data class FoodCategoryDefinitionEntity(
    @PrimaryKey val foodCategoryKey: String, // e.g., "FRUIT", "VEGETABLE"
    val displayName: String,             // e.g., "Fruit", "Vegetables"
    val description: String? = null,
    val displayOrder: Int? = null        // For ordering in UI if needed
    // todo: delete display order from db?
) 