package com.example.fit2081a1_yang_xingyu_33533563.data.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.fit2081a1_yang_xingyu_33533563.data.model.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert
    suspend fun insert(user: UserEntity)

    @Update
    suspend fun update(user: UserEntity)

    @Delete
    suspend fun delete(user: UserEntity)

    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE userId = :userId")
    suspend fun getUserById(userId: String): Flow<UserEntity>

    @Query("SELECT credential FROM users WHERE userId = :userId")
    suspend fun getCredentialByUserId(userId: String): String?

    @Query("SELECT isCurrentLoggedIn FROM users WHERE userId = :userId")
    suspend fun isUserLoggedIn(userId: String): Boolean
} 