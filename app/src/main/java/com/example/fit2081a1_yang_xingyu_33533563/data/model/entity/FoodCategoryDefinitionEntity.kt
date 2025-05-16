package com.example.fit2081a1_yang_xingyu_33533563.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Defines the available food categories lookup table.
 */
@Entity(tableName = "food_category_definitions")
data class FoodCategoryDefinitionEntity(
    @PrimaryKey
    val foodDefId: String,
    val foodCategoryName: String,
) 