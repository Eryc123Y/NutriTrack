package com.example.fit2081a1_yang_xingyu_33533563.data.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.fit2081a1_yang_xingyu_33533563.data.model.entity.UserScoreEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserScoreDao {
    @Insert
    suspend fun insert(userScore: UserScoreEntity)

    @Update
    suspend fun update(userScore: UserScoreEntity)

    @Delete
    suspend fun delete(userScore: UserScoreEntity)

    @Query("SELECT * FROM user_scores")
    fun getAllUserScores(): Flow<List<UserScoreEntity>>

    @Query("SELECT * FROM user_scores WHERE scoreUserId = :userId")
    fun getScoresByUserId(userId: String): Flow<List<UserScoreEntity>>

    @Query("SELECT * FROM user_scores WHERE scoreUserId = :userId AND scoreTypeKey = :scoreTypeKey")
    fun getScore(userId: String, scoreTypeKey: String): Flow<UserScoreEntity>

    @Query("DELETE FROM user_scores WHERE scoreUserId = :userId")
    suspend fun deleteAllScoresForUser(userId: String)
} 