package com.example.fit2081a1_yang_xingyu_33533563.data.model.repository

import com.example.fit2081a1_yang_xingyu_33533563.data.model.dao.FoodCategoryDefinitionDao
import com.example.fit2081a1_yang_xingyu_33533563.data.model.entity.FoodCategoryDefinitionEntity
import kotlinx.coroutines.flow.Flow

class FoodCategoryDefinitionRepository(private val foodCategoryDefinitionDao: FoodCategoryDefinitionDao) {

    suspend fun insert(foodCategory: FoodCategoryDefinitionEntity) {
        foodCategoryDefinitionDao.insert(foodCategory)
    }

    suspend fun update(foodCategory: FoodCategoryDefinitionEntity) {
        foodCategoryDefinitionDao.update(foodCategory)
    }

    suspend fun delete(foodCategory: FoodCategoryDefinitionEntity) {
        foodCategoryDefinitionDao.delete(foodCategory)
    }

    fun getAllFoodCategories(): Flow<List<FoodCategoryDefinitionEntity>> {
        return foodCategoryDefinitionDao.getAllFoodCategories()
    }

    fun getFoodCategoryByKey(categoryKey: String): Flow<FoodCategoryDefinitionEntity> {
        return foodCategoryDefinitionDao.getFoodCategoryByKey(categoryKey)
    }
} 