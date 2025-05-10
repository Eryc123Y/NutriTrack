package com.example.fit2081a1_yang_xingyu_33533563.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.fit2081a1_yang_xingyu_33533563.data.model.UserEntity

@Dao
interface UserDao {
    @Insert
    fun insert(user: UserEntity)

    @Update
    fun update(user: UserEntity)

    @Delete
    fun delete(user: UserEntity)

    @Query("SELECT * FROM users")
    fun getAllUsers(): LiveData<List<UserEntity>>

    @Query("SELECT * FROM users WHERE userId = :userId")
    fun getUserById(userId: String): LiveData<UserEntity>
} 