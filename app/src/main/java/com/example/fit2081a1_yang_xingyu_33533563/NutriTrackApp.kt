package com.example.fit2081a1_yang_xingyu_33533563

import android.app.Application
import android.util.Log
import com.example.fit2081a1_yang_xingyu_33533563.data.model.AppDatabase
import com.example.fit2081a1_yang_xingyu_33533563.data.model.repository.ChatRepository
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

/**
 * Application class for NutriTrack that manages the application lifecycle
 * and initializes core components like database, repositories, and ViewModels.
 * 
 * This class serves as the dependency injection container for the entire application,
 * providing centralized access to all repositories and data sources.
 */
class NutriTrackApp : Application() {
    // Application scope for database operations using SupervisorJob for structured concurrency
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    // Database and repositories - use lateinit for lazy initialization
    // These are initialized in onCreate() and available throughout the app lifecycle
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
    lateinit var chatRepository: ChatRepository

    /**
     * Called when the application is starting.
     * Initializes all core components including database, repositories, and ViewModel factory.
     */
    override fun onCreate() {
        super.onCreate()
        initializeComponents()
    }

    /**
     * Called when the application is terminating.
     * Performs cleanup operations including user logout if AuthViewModel is initialized.
     */
    override fun onTerminate() {
        super.onTerminate()
        if (::authViewModel.isInitialized) {
            authViewModel.logout()
            Log.d("NutriTrackApp", "App is terminating, performing logout.")
        }
    }
    
    /**
     * Initializes all core application components in the correct order.
     * This method sets up the database, repositories, and ViewModel factory.
     */
    private fun initializeComponents() {
        // Initialize Room database with all necessary DAOs
        database = AppDatabase.getDatabase(applicationContext)
        
        // Initialize repositories with their corresponding DAOs
        foodCategoryDefinitionRepository = FoodCategoryDefinitionRepository(database.foodCategoryDefinitionDao())
        personaRepository = PersonaRepository(database.personaDao())
        userRepository = UserRepository(database.userDao())
        scoreTypeDefinitionRepository = ScoreTypeDefinitionRepository(database.scoreTypeDefinitionDao())
        userFoodCategoryPreferenceRepository = UserFoodCategoryPreferenceRepository(database.userFoodCategoryPreferenceDao())
        userTimePreferenceRepository = UserTimePreferenceRepository(database.userTimePreferenceDao())
        userScoreRepository = UserScoreRepository(database.userScoreDao())
        chatRepository = ChatRepository(database.chatMessageDao())
        
        // Initialize ViewModel factory with all required dependencies
        viewModelProviderFactory = ViewModelProviderFactory(
            userRepository = userRepository,
            personaRepository = personaRepository,
            foodCategoryDefinitionRepository = foodCategoryDefinitionRepository,
            userFoodCategoryPreferenceRepository = userFoodCategoryPreferenceRepository,
            userTimePreferenceRepository = userTimePreferenceRepository,
            userScoreRepository = userScoreRepository,
            scoreTypeDefinitionRepository = scoreTypeDefinitionRepository,
            chatRepository = chatRepository,
            sharedPreferencesManager = SharedPreferencesManager.getInstance(applicationContext),
        )

        // Initialize database with initial data if needed
        initializeDatabase()
    }

    /**
     * Initializes the database with default data using a coroutine.
     * This runs on the IO dispatcher to avoid blocking the main thread.
     */
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