package com.example.fit2081a1_yang_xingyu_33533563.data.model.repository

import com.example.fit2081a1_yang_xingyu_33533563.data.model.dao.UserTimePreferenceDao
import com.example.fit2081a1_yang_xingyu_33533563.data.model.entity.UserTimePreferenceEntity
import kotlinx.coroutines.flow.Flow

class UserTimePreferenceRepository(private val userTimePreferenceDao: UserTimePreferenceDao) {

    suspend fun insert(timePreference: UserTimePreferenceEntity) {
        userTimePreferenceDao.insert(timePreference)
    }

    suspend fun update(timePreference: UserTimePreferenceEntity) {
        userTimePreferenceDao.update(timePreference)
    }

    suspend fun delete(timePreference: UserTimePreferenceEntity) {
        userTimePreferenceDao.delete(timePreference)
    }

    fun getAllTimePreferences(): Flow<List<UserTimePreferenceEntity>> {
        return userTimePreferenceDao.getAllTimePreferences()
    }

    fun getBiggestMealTime(userId: String): Flow<String?> {
        return userTimePreferenceDao.getBiggestMealTime(userId)
    }

    fun getSleepTime(userId: String): Flow<String?> {
        return userTimePreferenceDao.getSleepTime(userId)
    }

    fun getWakeUpTime(userId: String): Flow<String?> {
        return userTimePreferenceDao.getWakeUpTime(userId)
    }

    fun getPreferencesByUserId(userId: String): Flow<List<UserTimePreferenceEntity>> {
        return userTimePreferenceDao.getPreferencesByUserId(userId)
    }

    fun getPreference(userId: String): Flow<UserTimePreferenceEntity> {
        return userTimePreferenceDao.getPreference(userId)
    }

    suspend fun deleteAllPreferencesForUser(userId: String) {
        userTimePreferenceDao.deleteAllPreferencesForUser(userId)
    }
} 