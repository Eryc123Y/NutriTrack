package com.example.fit2081a1_yang_xingyu_33533563

import android.app.Application
import com.example.fit2081a1_yang_xingyu_33533563.data.model.AppDatabase
import com.example.fit2081a1_yang_xingyu_33533563.data.model.repository.FoodCategoryDefinitionRepository
import com.example.fit2081a1_yang_xingyu_33533563.data.model.repository.PersonaRepository
import com.example.fit2081a1_yang_xingyu_33533563.data.model.repository.ScoreTypeDefinitionRepository
import com.example.fit2081a1_yang_xingyu_33533563.data.model.repository.UserFoodCategoryPreferenceRepository
import com.example.fit2081a1_yang_xingyu_33533563.data.model.repository.UserRepository
import com.example.fit2081a1_yang_xingyu_33533563.data.model.repository.UserScoreRepository
import com.example.fit2081a1_yang_xingyu_33533563.data.model.repository.UserTimePreferenceRepository
import com.example.fit2081a1_yang_xingyu_33533563.util.SharedPreferencesManager

class NutriTrackApp: Application() {
    // init DB
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }

    // Instantiate repositories
    val userRepository by lazy { UserRepository(database.userDao()) }
    val personaRepository by lazy { PersonaRepository(database.personaDao()) }
    val userScoreRepository by lazy { UserScoreRepository(database.userScoreDao()) }
    val foodCategoryDefinitionRepository by lazy {
        FoodCategoryDefinitionRepository(database.foodCategoryDefinitionDao())
    }
    val scoreTypeDefinitionRepository by lazy {
        ScoreTypeDefinitionRepository(database.scoreTypeDefinitionDao())
    }
    val userTimePreferenceRepository by lazy {
        UserTimePreferenceRepository(database.userTimePreferenceDao())
    }
    val userFoodCategoryPreferenceRepository by lazy {
        UserFoodCategoryPreferenceRepository(database.userFoodCategoryPreferenceDao())
    }

    val sharedPreferencesManager: SharedPreferencesManager by lazy {
        SharedPreferencesManager.getInstance(this)
    }

    override fun onCreate() {
        super.onCreate()
        // You can perform other initializations here if needed
    }
}