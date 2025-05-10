package com.example.fit2081a1_yang_xingyu_33533563.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.fit2081a1_yang_xingyu_33533563.data.model.FoodCategoryDefinitionEntity

@Dao
interface FoodCategoryDefinitionDao {
    @Insert
    fun insert(foodCategory: FoodCategoryDefinitionEntity)

    @Update
    fun update(foodCategory: FoodCategoryDefinitionEntity)

    @Delete
    fun delete(foodCategory: FoodCategoryDefinitionEntity)

    @Query("SELECT * FROM food_category_definitions ORDER BY displayOrder ASC")
    fun getAllFoodCategories(): LiveData<List<FoodCategoryDefinitionEntity>>

    @Query("SELECT * FROM food_category_definitions WHERE foodCategoryKey = :categoryKey")
    fun getFoodCategoryByKey(categoryKey: String): LiveData<FoodCategoryDefinitionEntity>
} 