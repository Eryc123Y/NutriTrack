package com.example.fit2081a1_yang_xingyu_33533563.data.model.repository

import com.example.fit2081a1_yang_xingyu_33533563.data.model.dao.UserFoodCategoryPreferenceDao
import com.example.fit2081a1_yang_xingyu_33533563.data.model.entity.UserFoodPreferenceEntity
import kotlinx.coroutines.flow.Flow

class UserFoodCategoryPreferenceRepository(private val dao: UserFoodCategoryPreferenceDao) {

    suspend fun insert(preference: UserFoodPreferenceEntity) {
        dao.insert(preference)
    }

    suspend fun update(preference: UserFoodPreferenceEntity) {
        dao.update(preference)
    }

    suspend fun delete(preference: UserFoodPreferenceEntity) {
        dao.delete(preference)
    }

    fun getPreferencesByUserId(userId: String): Flow<List<UserFoodPreferenceEntity>> {
        return dao.getPreferencesByUserId(userId)
    }

    fun getPreference(userId: String, categoryKey: String): Flow<UserFoodPreferenceEntity> {
        return dao.getPreference(userId, categoryKey)
    }

    suspend fun deleteAllPreferencesForUser(userId: String) {
        dao.deleteAllPreferencesForUser(userId)
    }
} 