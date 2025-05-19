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
import com.example.fit2081a1_yang_xingyu_33533563.data.viewmodel.FruitViewModel
import kotlin.jvm.java

/**
 * A generic ViewModelProvider Factory that can create ViewModels with dependencies.
 *
 * @param userRepository The UserRepository instance.
 * @param personaRepository The PersonaRepository instance.
 * // Add other repositories as parameters if other ViewModels need them.
 * // e.g., @param anotherRepository: AnotherRepository
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

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(
                    userRepository,
                    personaRepository,
                    userScoreRepository,
                    userTimePreferenceRepository,
                    sharedPreferencesManager
                ) as T
            }
            modelClass.isAssignableFrom(QuestionnaireViewModel::class.java) -> {
                QuestionnaireViewModel(
                    foodCategoryDefinitionRepository,
                    userFoodCategoryPreferenceRepository,
                    personaRepository,
                    userTimePreferenceRepository,
                    userRepository
                ) as T
            }
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(
                    userRepository,
                    sharedPreferencesManager
                ) as T
            }
            modelClass.isAssignableFrom(InsightsViewModel::class.java) -> {
                InsightsViewModel(
                    userScoreRepository,
                    scoreTypeDefinitionRepository,
                    userRepository
                ) as T
            }
            modelClass.isAssignableFrom(GenAIViewModel::class.java) -> {
                GenAIViewModel(
                    chatRepository,
                    create(UserStatsViewModel::class.java) as UserStatsViewModel
                ) as T
            }
            modelClass.isAssignableFrom(UserStatsViewModel::class.java) -> {
                UserStatsViewModel(
                    userRepository,
                    userScoreRepository,
                    personaRepository,
                    userTimePreferenceRepository,
                    userFoodCategoryPreferenceRepository
                ) as T
            }
            modelClass.isAssignableFrom(FruitViewModel::class.java) -> {
                FruitViewModel(userRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}