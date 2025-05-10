package com.example.fit2081a1_yang_xingyu_33533563.data.viewmodel

import androidx.lifecycle.ViewModel
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

class QuestionnaireViewModel(
    private val foodCategoryDefinitionRepository: FoodCategoryDefinitionRepository,
    private val userFoodCategoryPreferenceRepository: UserFoodCategoryPreferenceRepository,
    private val personaRepository: PersonaRepository,
    private val userTimePreferenceRepository: UserTimePreferenceRepository,
    private val userRepository: UserRepository

) : ViewModel() {

    init {
        // Load user preferences when the ViewModel is created from the DB
        viewModelScope.launch {
            loadUserPreferences(_selectedPersonaId.value!!)
        }
    }

    // Food Categories Functions
    val allFoodCategories: StateFlow<List<FoodCategoryDefinitionEntity>> =
        foodCategoryDefinitionRepository.getAllFoodCategories()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // MutableStateFlow to hold the selected food category keys
    // This is used to track the currently selected food categories, e.g., for creating
    // UI components.
    private val _selectedFoodCategoryKeys = MutableStateFlow<Set<String>>(emptySet())
    // StateFlow to expose the selected food category keys
    val selectedFoodCategoryKeys: StateFlow<Set<String>> = _selectedFoodCategoryKeys.asStateFlow()

    // Function to toggle the selection state of a food category
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
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Persona selected, used for populating UI
    private val _selectedPersonaId = MutableStateFlow<String?>(null)
    val selectedPersonaId: StateFlow<String?> = _selectedPersonaId.asStateFlow()

    // Function to select a persona
    fun selectPersona(personaId: String) {
        _selectedPersonaId.value = personaId
    }

    // --- Time Preferences ---
    // Holding individual time strings, combine them into UserTimePreferenceEntity on save
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
    private val _saveStatus = MutableStateFlow<String?>(null) // e.g., "Success", "Error: ..."
    val saveStatus: StateFlow<String?> = _saveStatus.asStateFlow()

    fun saveAllPreferences(userId: String) {
        viewModelScope.launch {
            try {
                // 1. Save Food Category Preferences
                // Clear existing ones first if that's the desired behavior
                userFoodCategoryPreferenceRepository.deleteAllPreferencesForUser(userId)
                _selectedFoodCategoryKeys.value.forEach { key ->
                    userFoodCategoryPreferenceRepository.insert(
                        UserFoodPreferenceEntity(userId = userId, foodCategoryKey = key, isChecked = true)
                    )
                }

                // 2. Save Selected Persona (update UserEntity)
                _selectedPersonaId.value?.let { personaId ->
                    userRepository.getUserById(userId).firstOrNull()?.let { user ->
                        userRepository.updateUser(user.copy(selectedPersonaId = personaId))
                    }
                }

                // 3. Save Time Preferences
                // Check if a preference already exists for the user to decide insert vs update
                // For simplicity, this example assumes inserting a new one or updating if one exists.
                // A more robust way is to fetch existing, update, or insert new.
                // Let's assume an existing one should be deleted first for simplicity here, or use an upsert.
                userTimePreferenceRepository.deleteAllPreferencesForUser(userId) // Simplification
                userTimePreferenceRepository.insert(
                    UserTimePreferenceEntity(
                        userId = userId,
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

    /**
     * Loads user preferences from the database.
     * This function is called when entering the questionnaire.
     *
     * @param userId The ID of the current user.
     */
    fun loadUserPreferences(userId: String) {
        viewModelScope.launch {
            // Load food categories
            val foodPrefs = userFoodCategoryPreferenceRepository.getPreferencesByUserId(userId).firstOrNull()
            _selectedFoodCategoryKeys.value = foodPrefs?.filter { it.isChecked }?.map { it.foodCategoryKey }?.toSet() ?: emptySet()

            // Load persona
            val user = userRepository.getUserById(userId).firstOrNull()
            _selectedPersonaId.value = user?.selectedPersonaId

            // Load time preferences
            val timePref = userTimePreferenceRepository.getPreference(userId).firstOrNull()
            _biggestMealTime.value = timePref?.biggestMealTime
            _sleepTime.value = timePref?.sleepTime
            _wakeUpTime.value = timePref?.wakeUpTime
        }
    }

    fun clearSaveStatus() {
        _saveStatus.value = null
    }
}