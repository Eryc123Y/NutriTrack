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

    @Query("SELECT userHashedCredential FROM users WHERE userId = :userId")
    suspend fun getHashedCredentialByUserId(userId: String): String?

    @Query("SELECT userIsRegistered FROM users WHERE userId = :userId")
    suspend fun getUserIsRegistered(userId: String): Boolean?

    // update userIsRegistered to true
    @Query("UPDATE users SET userIsRegistered = 1 WHERE userId = :userId")
    suspend fun updateUserIsRegistered(userId: String)

    // get userGender
    @Query("SELECT userGender FROM users WHERE userId = :userId")
    suspend fun getUserGender(userId: String): String?

    // update userHashedCredential
    @Query("UPDATE users SET userHashedCredential = :hashedCredential WHERE userId = :userId")
    suspend fun updateUserHashedCredential(userId: String, hashedCredential: String)

    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE userId = :userId")
    fun getUserById(userId: String): Flow<UserEntity?>

} 