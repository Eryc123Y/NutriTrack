package com.example.fit2081a1_yang_xingyu_33533563.data.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.fit2081a1_yang_xingyu_33533563.data.model.entity.FoodCategoryDefinitionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodCategoryDefinitionDao {
    @Insert
    suspend fun insert(foodCategory: FoodCategoryDefinitionEntity)

    @Update
    suspend fun update(foodCategory: FoodCategoryDefinitionEntity)

    @Delete
    suspend fun delete(foodCategory: FoodCategoryDefinitionEntity)

    @Query("SELECT * FROM food_category_definitions")
    fun getAllFoodCategories(): Flow<List<FoodCategoryDefinitionEntity>>

    @Query("SELECT * FROM food_category_definitions WHERE foodDefId = :categoryKey")
    fun getFoodCategoryByKey(categoryKey: String): Flow<FoodCategoryDefinitionEntity>
} 