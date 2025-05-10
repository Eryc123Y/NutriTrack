package com.example.fit2081a1_yang_xingyu_33533563.data.model.repository

import com.example.fit2081a1_yang_xingyu_33533563.data.model.dao.UserScoreDao
import com.example.fit2081a1_yang_xingyu_33533563.data.model.entity.UserScoreEntity
import kotlinx.coroutines.flow.Flow

class UserScoreRepository(private val userScoreDao: UserScoreDao) {

    suspend fun insert(userScore: UserScoreEntity) {
        userScoreDao.insert(userScore)
    }

    suspend fun update(userScore: UserScoreEntity) {
        userScoreDao.update(userScore)
    }

    suspend fun delete(userScore: UserScoreEntity) {
        userScoreDao.delete(userScore)
    }

    fun getAllUserScores(): Flow<List<UserScoreEntity>> {
        return userScoreDao.getAllUserScores()
    }

    fun getScoresByUserId(userId: String): Flow<List<UserScoreEntity>> {
        return userScoreDao.getScoresByUserId(userId)
    }

    fun getScore(userId: String, scoreTypeKey: String): Flow<UserScoreEntity> {
        return userScoreDao.getScore(userId, scoreTypeKey)
    }

    suspend fun deleteAllScoresForUser(userId: String) {
        userScoreDao.deleteAllScoresForUser(userId)
    }
} 