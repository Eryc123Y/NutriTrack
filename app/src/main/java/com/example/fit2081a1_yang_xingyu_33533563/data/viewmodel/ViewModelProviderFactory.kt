package com.example.fit2081a1_yang_xingyu_33533563.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.fit2081a1_yang_xingyu_33533563.data.model.repository.ChatRepository
import com.example.fit2081a1_yang_xingyu_33533563.data.model.repository.FoodCategoryDefinitionRepository
import com.example.fit2081a1_yang_xingyu_33533563.data.model.repository.PersonaRepository
import com.example.fit2081a1_yang_xingyu_33533563.data.model.repository.ScoreTypeDefinitionRepository
import com.example.fit2081a1_yang_xingyu_33533563.data.model.repository.UserFoodCategoryPreferenceRepository
import com.example.fit2081a1_yang_xingyu_33533563.data.model.repository.UserRepository
import com.example.fit2081a1_yang_xingyu_33533563.data.model.repository.UserScoreRepository
import com.example.fit2081a1_yang_xingyu_33533563.data.model.repository.UserTimePreferenceRepository
import com.example.fit2081a1_yang_xingyu_33533563.util.SharedPreferencesManager
import kotlin.jvm.java

/**
 * Custom ViewModelProvider Factory for dependency injection in NutriTrack.
 * 
 * This factory manages the creation of all ViewModels with their required dependencies.
 * It implements the dependency injection pattern, ensuring that each ViewModel receives
 * the appropriate repositories and services it needs to function.
 * 
 * The factory follows the principle of inversion of control, centralizing ViewModel
 * creation and dependency management throughout the application.
 * 
 * @param userRepository Repository for user data operations
 * @param personaRepository Repository for persona/character data
 * @param foodCategoryDefinitionRepository Repository for food category definitions
 * @param userFoodCategoryPreferenceRepository Repository for user food preferences
 * @param userTimePreferenceRepository Repository for user time preferences
 * @param userScoreRepository Repository for user score data
 * @param scoreTypeDefinitionRepository Repository for score type definitions
 * @param sharedPreferencesManager Manager for shared preferences storage
 * @param chatRepository Repository for chat message data
 */
@Suppress("UNCHECKED_CAST")
class ViewModelProviderFactory(
    private val userRepository: UserRepository,
    private val personaRepository: PersonaRepository,
    private val foodCategoryDefinitionRepository: FoodCategoryDefinitionRepository,
    private val userFoodCategoryPreferenceRepository: UserFoodCategoryPreferenceRepository,
    private val userTimePreferenceRepository: UserTimePreferenceRepository,
    private val userScoreRepository: UserScoreRepository,
    private val scoreTypeDefinitionRepository: ScoreTypeDefinitionRepository,
    private val sharedPreferencesManager: SharedPreferencesManager,
    private val chatRepository: ChatRepository
) : ViewModelProvider.Factory {

    /**
     * Creates ViewModel instances with their required dependencies.
     * 
     * This method is called by the Android ViewModel system when a ViewModel
     * is requested. It uses a when-expression to determine which ViewModel
     * to create and injects the appropriate dependencies.
     * 
     * Note: Some ViewModels depend on other ViewModels, creating a hierarchical
     * dependency structure that's resolved recursively.
     * 
     * @param modelClass The class of the ViewModel to create
     * @return Instance of the requested ViewModel with all dependencies injected
     * @throws IllegalArgumentException if the ViewModel class is not recognized
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            // ProfileViewModel: Manages user profile display and basic user information
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(
                    userRepository,
                    personaRepository,
                    userScoreRepository
                ) as T
            }
            
            // QuestionnaireViewModel: Handles the multi-step user preference questionnaire
            modelClass.isAssignableFrom(QuestionnaireViewModel::class.java) -> {
                QuestionnaireViewModel(
                    foodCategoryDefinitionRepository,
                    userFoodCategoryPreferenceRepository,
                    personaRepository,
                    userTimePreferenceRepository,
                    userRepository
                ) as T
            }
            
            // AuthViewModel: Manages user authentication and session management
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(
                    userRepository,
                    sharedPreferencesManager
                ) as T
            }
            
            // InsightsViewModel: Provides analytics and nutrition insights
            modelClass.isAssignableFrom(InsightsViewModel::class.java) -> {
                InsightsViewModel(
                    userScoreRepository,
                    scoreTypeDefinitionRepository
                ) as T
            }
            
            // GenAIViewModel: Handles AI-powered nutrition coaching and chat functionality
            // Note: Depends on UserStatsViewModel for user analytics data
            modelClass.isAssignableFrom(GenAIViewModel::class.java) -> {
                GenAIViewModel(
                    chatRepository,
                    create(UserStatsViewModel::class.java)
                ) as T
            }
            
            // UserStatsViewModel: Provides detailed user statistics and analytics
            modelClass.isAssignableFrom(UserStatsViewModel::class.java) -> {
                UserStatsViewModel(
                    userRepository,
                    userScoreRepository,
                    personaRepository,
                    userTimePreferenceRepository,
                    userFoodCategoryPreferenceRepository
                ) as T
            }
            
            // FruitViewModel: Manages fruit data from external API and user preferences
            modelClass.isAssignableFrom(FruitViewModel::class.java) -> {
                FruitViewModel(userRepository) as T
            }
            
            // ClinicianDashboardViewModel: Provides dashboard for healthcare professionals
            // Note: Depends on GenAIViewModel for AI-powered insights
            modelClass.isAssignableFrom(ClinicianDashboardViewModel::class.java) -> {
                ClinicianDashboardViewModel(
                    userRepository,
                    userScoreRepository,
                    create(GenAIViewModel::class.java)
                ) as T
            }
            
            // Throw exception for unrecognized ViewModel classes
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}