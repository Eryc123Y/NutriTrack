package com.example.fit2081a1_yang_xingyu_33533563.data.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.fit2081a1_yang_xingyu_33533563.data.model.entity.UserTimePreferenceEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for user time preferences.
 *
 * This interface defines methods for interacting with the user_time_preferences table in the database.
 */
@Dao
interface UserTimePreferenceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE) // if already exists, replace it
    suspend fun insert(timePreference: UserTimePreferenceEntity)

    @Update
    suspend fun update(timePreference: UserTimePreferenceEntity)

    @Delete
    suspend fun delete(timePreference: UserTimePreferenceEntity)

    @Query("SELECT * FROM user_time_preferences")
    fun getAllTimePreferences(): Flow<List<UserTimePreferenceEntity>>

    /**
     * Get all time preferences for a specific user by userId.
     */
    @Query("SELECT * FROM user_time_preferences WHERE timePrefUserId = :userId")
    fun getPreferencesByUserId(userId: String): Flow<List<UserTimePreferenceEntity>>

    /**
     * Get the biggest meal time for a specific user by userId.
     */
    @Query("SELECT biggestMealTime FROM user_time_preferences WHERE timePrefUserId = :userId")
    fun getBiggestMealTime(userId: String): Flow<String?>

    /**
     * Get the sleep time for a specific user by userId.
     */
    @Query("SELECT sleepTime FROM user_time_preferences WHERE timePrefUserId = :userId")
    fun getSleepTime(userId: String): Flow<String?>

    /**
     * Get the wake-up time for a specific user by userId.
     */
    @Query("SELECT wakeUpTime FROM user_time_preferences WHERE timePrefUserId = :userId")
    fun getWakeUpTime(userId: String): Flow<String?>

    @Query("SELECT * FROM user_time_preferences WHERE timePrefUserId = :userId")
    fun getPreference(userId: String): Flow<UserTimePreferenceEntity>

    @Query("DELETE FROM user_time_preferences WHERE timePrefUserId = :userId")
    suspend fun deleteAllPreferencesForUser(userId: String)
} 