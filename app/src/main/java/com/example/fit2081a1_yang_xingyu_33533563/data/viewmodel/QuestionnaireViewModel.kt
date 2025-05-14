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
import kotlin.jvm.java

class QuestionnaireViewModel(
    private val foodCategoryDefinitionRepository: FoodCategoryDefinitionRepository,
    private val userFoodCategoryPreferenceRepository: UserFoodCategoryPreferenceRepository,
    private val personaRepository: PersonaRepository,
    private val userTimePreferenceRepository: UserTimePreferenceRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    /**
     * Optionally preload user preferences when a valid userId is available.
     * Call [loadUserPreferences] explicitly from the UI layer once you know the
     * currently-logged-in user rather than forcing it at construction time to
     * avoid null crashes.
     */
    init {
        /* No-op for now – wait until a userId is supplied. */
    }

    // Food Categories Functions
    val allFoodCategories: StateFlow<List<FoodCategoryDefinitionEntity>> =
        foodCategoryDefinitionRepository.getAllFoodCategories()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    private val _selectedFoodCategoryKeys = MutableStateFlow<Set<String>>(emptySet())
    val selectedFoodCategoryKeys: StateFlow<Set<String>> = _selectedFoodCategoryKeys.asStateFlow()

    fun toggleFoodCategory(categoryKey: String, isSelected: Boolean) {
        val currentSelection = _selectedFoodCategoryKeys.value.toMutableSet()
        if (isSelected) {
            currentSelection.add(categoryKey)
        } else {
            currentSelection.remove(categoryKey)
        }
        _selectedFoodCategoryKeys.value = currentSelection
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

    // --- Time Preferences ---
    private val _biggestMealTime = MutableStateFlow<String?>(null)
    val biggestMealTime: StateFlow<String?> = _biggestMealTime.asStateFlow()

    private val _sleepTime = MutableStateFlow<String?>(null)
    val sleepTime: StateFlow<String?> = _sleepTime.asStateFlow()

    private val _wakeUpTime = MutableStateFlow<String?>(null)
    val wakeUpTime: StateFlow<String?> = _wakeUpTime.asStateFlow()

    fun updateBiggestMealTime(time: String?) { _biggestMealTime.value = time }
    fun updateSleepTime(time: String?) { _sleepTime.value = time }
    fun updateWakeUpTime(time: String?) { _wakeUpTime.value = time }

    // --- Saving ---
    private val _saveStatus = MutableStateFlow<String?>(null)
    val saveStatus: StateFlow<String?> = _saveStatus.asStateFlow()

    fun saveAllPreferences(userId: String) {
        viewModelScope.launch {
            if (userId.isBlank()) {
                _saveStatus.value = "Error: User ID is missing."
                return@launch
            }
            try {
                userFoodCategoryPreferenceRepository.deleteAllPreferencesForUser(userId)
                _selectedFoodCategoryKeys.value.forEach { key ->
                    userFoodCategoryPreferenceRepository.insert(
                        UserFoodPreferenceEntity(foodPrefUserId = userId, foodPrefCategoryKey = key, foodPrefCheckedStatus = true)
                    )
                }

                _selectedPersonaId.value?.let { personaId ->
                    userRepository.getUserById(userId).firstOrNull()?.let { user ->
                        userRepository.updateUser(user.copy(userPersonaId = personaId))
                    } ?: run {
                        _saveStatus.value = "Error: User not found for saving persona."
                        // Potentially return or handle error more gracefully
                    }
                }
                
                userTimePreferenceRepository.deleteAllPreferencesForUser(userId)
                userTimePreferenceRepository.insert(
                    UserTimePreferenceEntity(
                        timePrefUserId = userId,
                        biggestMealTime = _biggestMealTime.value,
                        sleepTime = _sleepTime.value,
                        wakeUpTime = _wakeUpTime.value
                    )
                )
                _saveStatus.value = "Preferences saved successfully!"
            } catch (e: Exception) {
                _saveStatus.value = "Error saving preferences: ${e.message}"
            }
        }
    }

    fun loadUserPreferences(userId: String) {
        viewModelScope.launch {
            try {
                val foodPrefs = userFoodCategoryPreferenceRepository.getPreferencesByUserId(userId).firstOrNull()
                _selectedFoodCategoryKeys.value = foodPrefs?.filter { it.foodPrefCheckedStatus }?.map { it.foodPrefCategoryKey }?.toSet() ?: emptySet()

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

    inline fun<reified T> test(t:T){

        var klass = T::class.java
        when(klass.name) {

        }
    }

    fun<T> test(t_class:Class<T>){
        var klass = t_class
        when(klass.name) {

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