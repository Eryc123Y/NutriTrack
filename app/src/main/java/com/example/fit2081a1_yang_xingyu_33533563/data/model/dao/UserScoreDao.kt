package com.example.fit2081a1_yang_xingyu_33533563.data.model.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.fit2081a1_yang_xingyu_33533563.data.model.entity.UserScoreEntity

@Dao
interface UserScoreDao {
    @Insert
    fun insert(userScore: UserScoreEntity)

    @Update
    fun update(userScore: UserScoreEntity)

    @Delete
    fun delete(userScore: UserScoreEntity)

    @Query("SELECT * FROM user_scores")
    fun getAllUserScores(): LiveData<List<UserScoreEntity>>

    @Query("SELECT * FROM user_scores WHERE userId = :userId")
    fun getScoresByUserId(userId: String): LiveData<List<UserScoreEntity>>

    @Query("SELECT * FROM user_scores WHERE userId = :userId AND scoreTypeKey = :scoreTypeKey")
    fun getScore(userId: String, scoreTypeKey: String): LiveData<UserScoreEntity>

    @Query("DELETE FROM user_scores WHERE userId = :userId")
    fun deleteAllScoresForUser(userId: String)
} 