package com.example.fit2081a1_yang_xingyu_33533563.data.model.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.fit2081a1_yang_xingyu_33533563.data.model.entity.UserFoodCategoryPreferenceEntity

@Dao
interface UserFoodCategoryPreferenceDao {
    @Insert
    fun insert(preference: UserFoodCategoryPreferenceEntity)

    @Update
    fun update(preference: UserFoodCategoryPreferenceEntity)

    @Delete
    fun delete(preference: UserFoodCategoryPreferenceEntity)

    @Query("SELECT * FROM user_food_category_preferences WHERE userId = :userId")
    fun getPreferencesByUserId(userId: String): LiveData<List<UserFoodCategoryPreferenceEntity>>

    @Query("SELECT * FROM user_food_category_preferences WHERE userId = :userId AND foodCategoryKey = :categoryKey")
    fun getPreference(userId: String, categoryKey: String): LiveData<UserFoodCategoryPreferenceEntity>

    @Query("DELETE FROM user_food_category_preferences WHERE userId = :userId")
    fun deleteAllPreferencesForUser(userId: String)
} 