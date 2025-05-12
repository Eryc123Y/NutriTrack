package com.example.fit2081a1_yang_xingyu_33533563.data.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.fit2081a1_yang_xingyu_33533563.data.model.entity.UserFoodPreferenceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserFoodCategoryPreferenceDao {
    @Insert
    fun insert(preference: UserFoodPreferenceEntity)

    @Update
    fun update(preference: UserFoodPreferenceEntity)

    @Delete
    fun delete(preference: UserFoodPreferenceEntity)

    @Query("SELECT * FROM user_food_category_preferences WHERE foodPrefUserId = :userId")
    fun getPreferencesByUserId(userId: String): Flow<List<UserFoodPreferenceEntity>>

    @Query("SELECT * FROM user_food_category_preferences WHERE foodPrefUserId = :userId AND foodPrefCategoryKey = :categoryKey")
    fun getPreference(userId: String, categoryKey: String):
            Flow<UserFoodPreferenceEntity>

    @Query("DELETE FROM user_food_category_preferences WHERE foodPrefUserId = :userId")
    suspend fun deleteAllPreferencesForUser(userId: String)
} 