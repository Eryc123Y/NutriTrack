package com.example.fit2081a1_yang_xingyu_33533563

import android.app.Application
import android.util.Log
import com.example.fit2081a1_yang_xingyu_33533563.data.model.AppDatabase
import com.example.fit2081a1_yang_xingyu_33533563.data.model.repository.FoodCategoryDefinitionRepository
import com.example.fit2081a1_yang_xingyu_33533563.data.model.repository.PersonaRepository
import com.example.fit2081a1_yang_xingyu_33533563.data.model.repository.ScoreTypeDefinitionRepository
import com.example.fit2081a1_yang_xingyu_33533563.data.model.repository.UserFoodCategoryPreferenceRepository
import com.example.fit2081a1_yang_xingyu_33533563.data.model.repository.UserRepository
import com.example.fit2081a1_yang_xingyu_33533563.data.model.repository.UserScoreRepository
import com.example.fit2081a1_yang_xingyu_33533563.data.model.repository.UserTimePreferenceRepository
import com.example.fit2081a1_yang_xingyu_33533563.data.viewmodel.AuthViewModel
import com.example.fit2081a1_yang_xingyu_33533563.data.viewmodel.ViewModelProviderFactory
import com.example.fit2081a1_yang_xingyu_33533563.util.InitDataUtils
import com.example.fit2081a1_yang_xingyu_33533563.util.SharedPreferencesManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class NutriTrackApp : Application() {
    // Application scope for database operations
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    // Database and repositories , use lateinit for lazy initialization
    lateinit var database: AppDatabase
    lateinit var foodCategoryDefinitionRepository: FoodCategoryDefinitionRepository
    lateinit var personaRepository: PersonaRepository
    lateinit var userRepository: UserRepository
    lateinit var scoreTypeDefinitionRepository: ScoreTypeDefinitionRepository
    lateinit var userFoodCategoryPreferenceRepository: UserFoodCategoryPreferenceRepository
    lateinit var userTimePreferenceRepository: UserTimePreferenceRepository
    lateinit var userScoreRepository: UserScoreRepository
    lateinit var viewModelProviderFactory: ViewModelProviderFactory
    lateinit var authViewModel: AuthViewModel

    override fun onCreate() {
        super.onCreate()
        initializeComponents()
    }

    override fun onTerminate() {
        super.onTerminate()
        if (::authViewModel.isInitialized) {
            authViewModel.logout()
            Log.d("NutriTrackApp", "App is terminating, performing logout.")
        }
    }
    
    private fun initializeComponents() {
        // Initialize database
        database = AppDatabase.getDatabase(applicationContext)
        
        // Initialize repositories
        foodCategoryDefinitionRepository = FoodCategoryDefinitionRepository(database.foodCategoryDefinitionDao())
        personaRepository = PersonaRepository(database.personaDao())
        userRepository = UserRepository(database.userDao())
        scoreTypeDefinitionRepository = ScoreTypeDefinitionRepository(database.scoreTypeDefinitionDao())
        userFoodCategoryPreferenceRepository = UserFoodCategoryPreferenceRepository(database.userFoodCategoryPreferenceDao())
        userTimePreferenceRepository = UserTimePreferenceRepository(database.userTimePreferenceDao())
        userScoreRepository = UserScoreRepository(database.userScoreDao())
        
        // Initialize ViewModel factory
        viewModelProviderFactory = ViewModelProviderFactory(
            userRepository = userRepository,
            personaRepository = personaRepository,
            foodCategoryDefinitionRepository = foodCategoryDefinitionRepository,
            userFoodCategoryPreferenceRepository = userFoodCategoryPreferenceRepository,
            userTimePreferenceRepository = userTimePreferenceRepository,
            userScoreRepository = userScoreRepository,
            scoreTypeDefinitionRepository = scoreTypeDefinitionRepository,
            sharedPreferencesManager = SharedPreferencesManager.getInstance(applicationContext)
        )

        // Initialize database with data
        initializeDatabase()
    }

    private fun initializeDatabase() {
        applicationScope.launch {
            InitDataUtils.initializeDatabaseAsNeeded(
                applicationContext,
                userRepository,
                foodCategoryDefinitionRepository,
                scoreTypeDefinitionRepository,
                personaRepository,
                userFoodCategoryPreferenceRepository,
                userTimePreferenceRepository,
                userScoreRepository
            )
        }
    }
}