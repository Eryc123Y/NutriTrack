package com.example.fit2081a1_yang_xingyu_33533563.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.fit2081a1_yang_xingyu_33533563.data.model.entity.FoodCategoryDefinitionEntity
import com.example.fit2081a1_yang_xingyu_33533563.data.model.entity.PersonaEntity
import com.example.fit2081a1_yang_xingyu_33533563.data.model.entity.UserFoodPreferenceEntity
import com.example.fit2081a1_yang_xingyu_33533563.data.model.entity.UserTimePreferenceEntity
import com.example.fit2081a1_yang_xingyu_33533563.data.model.repository.FoodCategoryDefinitionRepository
import com.example.fit2081a1_yang_xingyu_33533563.data.model.repository.PersonaRepository
import com.example.fit2081a1_yang_xingyu_33533563.data.model.repository.UserFoodCategoryPreferenceRepository
import com.example.fit2081a1_yang_xingyu_33533563.data.model.repository.UserRepository
import com.example.fit2081a1_yang_xingyu_33533563.data.model.repository.UserTimePreferenceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.combine
import kotlin.jvm.java

class QuestionnaireViewModel(
    private val foodCategoryDefinitionRepository: FoodCategoryDefinitionRepository,
    private val userFoodCategoryPreferenceRepository: UserFoodCategoryPreferenceRepository,
    private val personaRepository: PersonaRepository,
    private val userTimePreferenceRepository: UserTimePreferenceRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    // Food Categories Functions
    val allFoodCategories: StateFlow<List<FoodCategoryDefinitionEntity>> =
        foodCategoryDefinitionRepository.getAllFoodCategories()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    private val _foodCategoryKeyBooleanMap = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val foodCategoryKeyBooleanMap: StateFlow<Map<String, Boolean>> = _foodCategoryKeyBooleanMap.asStateFlow()

    fun toggleFoodCategory(foodId: String, isSelected: Boolean) {
        val currentMap = _foodCategoryKeyBooleanMap.value.toMutableMap()
        currentMap[foodId] = isSelected
        _foodCategoryKeyBooleanMap.value = currentMap
    }

    // Persona Page Functions
    val allPersonas: StateFlow<List<PersonaEntity>> =
        personaRepository.getAllPersonas()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    private val _selectedPersonaId = MutableStateFlow<String?>(null)
    val selectedPersonaId: StateFlow<String?> = _selectedPersonaId.asStateFlow()

    fun selectPersona(personaId: String) {
        _selectedPersonaId.value = personaId
    }

    // Time Preferences
    private val _biggestMealTime = MutableStateFlow<String?>(null)
    val biggestMealTime: StateFlow<String?> = _biggestMealTime.asStateFlow()

    private val _sleepTime = MutableStateFlow<String?>(null)
    val sleepTime: StateFlow<String?> = _sleepTime.asStateFlow()

    private val _wakeUpTime = MutableStateFlow<String?>(null)
    val wakeUpTime: StateFlow<String?> = _wakeUpTime.asStateFlow()

    fun updateBiggestMealTime(time: String?) { _biggestMealTime.value = time }
    fun updateSleepTime(time: String?) { _sleepTime.value = time }
    fun updateWakeUpTime(time: String?) { _wakeUpTime.value = time }

    // Validation State
    val isQuestionnaireValid: StateFlow<Boolean> = combine(
        _foodCategoryKeyBooleanMap,
        _selectedPersonaId,
        _biggestMealTime,
        _sleepTime,
        _wakeUpTime
    ) { foodCategories, personaId, biggestMeal, sleep, wakeUp ->
        val isFoodValid = foodCategories.any { it.value } // At least one selected
        val isPersonaValid = !personaId.isNullOrBlank()
        val areTimesValid = !biggestMeal.isNullOrBlank() && 
                            !sleep.isNullOrBlank() && 
                            !wakeUp.isNullOrBlank()
        isFoodValid && isPersonaValid && areTimesValid
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    // Saving
    private val _isQuestionnaireCompleted = MutableStateFlow(false)
    val isQuestionnaireCompleted: StateFlow<Boolean> = _isQuestionnaireCompleted.asStateFlow()

    private val _saveStatus = MutableStateFlow<String?>(null)
    val saveStatus: StateFlow<String?> = _saveStatus.asStateFlow()

    /**
     * Saves all user preferences to the database. Also test validation
     */
    fun saveAllPreferences(userId: String) {
        viewModelScope.launch {
            if (!isQuestionnaireValid.value) {
                _saveStatus.value = "Please ensure all questionnaire sections are completed correctly."
                return@launch
            }
            if (userId.isBlank()) {
                _saveStatus.value = "Error: User ID is missing."
                return@launch
            }
            try {
                //userFoodCategoryPreferenceRepository.deleteAllPreferencesForUser(userId)
                _foodCategoryKeyBooleanMap.value.forEach { (key, selected) ->
                    userFoodCategoryPreferenceRepository.insert(
                        UserFoodPreferenceEntity(
                            foodPrefUserId = userId,
                            foodPrefCategoryKey = key,
                            foodPrefCheckedStatus = selected)
                    )
                }

                _selectedPersonaId.value?.let { personaId ->
                    userRepository.getUserById(userId).firstOrNull()?.let { user ->
                        userRepository.updateUser(user.copy(userPersonaId = personaId))
                    } ?: run {
                        _saveStatus.value = "Error: User not found for saving persona."
                    }
                }
                
                //userTimePreferenceRepository.deleteAllPreferencesForUser(userId)
                // If already exist record, will cover it
                userTimePreferenceRepository.insert(
                    UserTimePreferenceEntity(
                        timePrefUserId = userId,
                        biggestMealTime = _biggestMealTime.value,
                        sleepTime = _sleepTime.value,
                        wakeUpTime = _wakeUpTime.value
                    )
                )
                _isQuestionnaireCompleted.value = true
                _saveStatus.value = "Preferences saved successfully!"
            } catch (e: Exception) {
                _saveStatus.value = "Error saving preferences: ${e.message}"
            }
        }
    }

    fun loadUserPreferences(userId: String) {
        viewModelScope.launch {
            try {
                val foodPrefs = userFoodCategoryPreferenceRepository
                    .getPreferencesByUserId(userId).firstOrNull()
                
                // Create a proper Map<String, Boolean> from preferences
                val foodCategoryMap = mutableMapOf<String, Boolean>()
                
                // Get all food categories first to ensure we have a complete map
                val allCategories = foodCategoryDefinitionRepository.getAllFoodCategories().firstOrNull() ?: emptyList()
                allCategories.forEach { category ->
                    foodCategoryMap[category.foodDefId] = false
                }
                
                // Then update with user's selections
                foodPrefs?.forEach { pref ->
                    foodCategoryMap[pref.foodPrefCategoryKey] = pref.foodPrefCheckedStatus
                }
                
                _foodCategoryKeyBooleanMap.value = foodCategoryMap

                val user = userRepository.getUserById(userId).firstOrNull()
                _selectedPersonaId.value = user?.userPersonaId
            
                val timePref = userTimePreferenceRepository.getPreference(userId).firstOrNull()
                _biggestMealTime.value = timePref?.biggestMealTime
                _sleepTime.value = timePref?.sleepTime
                _wakeUpTime.value = timePref?.wakeUpTime
            } catch (e: Exception) {
                // Handle exceptions during loading, e.g., update a status StateFlow
                 _saveStatus.value = "Error loading preferences: ${e.message}"
            }
        }
    }

    fun clearSaveStatus() {
        _saveStatus.value = null
    }

    class QuestionnaireViewModelFactory(
        private val foodCategoryDefinitionRepository: FoodCategoryDefinitionRepository,
        private val userFoodCategoryPreferenceRepository: UserFoodCategoryPreferenceRepository,
        private val personaRepository: PersonaRepository,
        private val userTimePreferenceRepository: UserTimePreferenceRepository,
        private val userRepository: UserRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(QuestionnaireViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return QuestionnaireViewModel(
                    foodCategoryDefinitionRepository,
                    userFoodCategoryPreferenceRepository,
                    personaRepository,
                    userTimePreferenceRepository,
                    userRepository
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}