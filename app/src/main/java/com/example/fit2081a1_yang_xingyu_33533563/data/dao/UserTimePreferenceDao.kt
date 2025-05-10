package com.example.fit2081a1_yang_xingyu_33533563.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.fit2081a1_yang_xingyu_33533563.data.model.UserTimePreferenceEntity

@Dao
interface UserTimePreferenceDao {
    @Insert
    fun insert(timePreference: UserTimePreferenceEntity)

    @Update
    fun update(timePreference: UserTimePreferenceEntity)

    @Delete
    fun delete(timePreference: UserTimePreferenceEntity)

    @Query("SELECT * FROM user_time_preferences")
    fun getAllTimePreferences(): LiveData<List<UserTimePreferenceEntity>>

    @Query("SELECT * FROM user_time_preferences WHERE userId = :userId")
    fun getPreferencesByUserId(userId: String): LiveData<List<UserTimePreferenceEntity>>

    @Query("SELECT * FROM user_time_preferences WHERE userId = :userId")
    fun getPreference(userId: String): LiveData<UserTimePreferenceEntity>

    @Query("DELETE FROM user_time_preferences WHERE userId = :userId")
    fun deleteAllPreferencesForUser(userId: String)
} 