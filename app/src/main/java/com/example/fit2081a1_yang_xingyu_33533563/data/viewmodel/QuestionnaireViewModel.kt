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
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData

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
    val allFoodCategories: LiveData<List<FoodCategoryDefinitionEntity>> = foodCategoryDefinitionRepository.getAllFoodCategories().asLiveData()

    // MutableStateFlow to hold the selected food category keys
    // This is used to track the currently selected food categories, e.g., for creating
    // UI components.
    private val _selectedFoodCategoryKeys = MutableLiveData<Set<String>>(emptySet())
    // StateFlow to expose the selected food category keys
    val selectedFoodCategoryKeys: LiveData<Set<String>> = _selectedFoodCategoryKeys

    // Function to toggle the selection state of a food category
    fun toggleFoodCategory(categoryKey: String, isSelected: Boolean) {
        val currentSelection = _selectedFoodCategoryKeys.value?.toMutableSet() ?: mutableSetOf()
        if (isSelected) {
            currentSelection.add(categoryKey)
        } else {
            currentSelection.remove(categoryKey)
        }
        _selectedFoodCategoryKeys.value = currentSelection
    }

    // Persona Page Functions
    val allPersonas: LiveData<List<PersonaEntity>> = personaRepository.getAllPersonas().asLiveData()

    // Persona selected, used for populating UI
    private val _selectedPersonaId = MutableLiveData<String?>()
    val selectedPersonaId: LiveData<String?> = _selectedPersonaId

    // Function to select a persona
    fun selectPersona(personaId: String) {
        _selectedPersonaId.value = personaId
    }

    // --- Time Preferences ---
    // Holding individual time strings, combine them into UserTimePreferenceEntity on save
    private val _biggestMealTime = MutableLiveData<String?>()
    val biggestMealTime: LiveData<String?> = _biggestMealTime

    private val _sleepTime = MutableLiveData<String?>()
    val sleepTime: LiveData<String?> = _sleepTime

    private val _wakeUpTime = MutableLiveData<String?>()
    val wakeUpTime: LiveData<String?> = _wakeUpTime

    fun updateBiggestMealTime(time: String?) { _biggestMealTime.value = time }
    fun updateSleepTime(time: String?) { _sleepTime.value = time }
    fun updateWakeUpTime(time: String?) { _wakeUpTime.value = time }

    // --- Saving ---
    private val _saveStatus = MutableLiveData<String?>()
    val saveStatus: LiveData<String?> = _saveStatus

    fun saveAllPreferences(userId: String) {
        viewModelScope.launch {
            if (userId.isBlank()) {
                _saveStatus.postValue("Error: User ID is missing.")
                return@launch
            }
            try {
                userFoodCategoryPreferenceRepository.deleteAllPreferencesForUser(userId)
                _selectedFoodCategoryKeys.value?.forEach { key ->
                    userFoodCategoryPreferenceRepository.insert(
                        UserFoodPreferenceEntity(userId = userId, foodCategoryKey = key, isChecked = true)
                    )
                }

                _selectedPersonaId.value?.let { personaId ->
                    // Assuming userRepository.getUserById() returns Flow, if it returns LiveData, adapt
                    userRepository.getUserById(userId).firstOrNull()?.let { user ->
                        userRepository.updateUser(user.copy(selectedPersonaId = personaId))
                    } ?: _saveStatus.postValue("Error: User not found for saving persona.")
                }
                
                userTimePreferenceRepository.deleteAllPreferencesForUser(userId)
                userTimePreferenceRepository.insert(
                    UserTimePreferenceEntity(
                        userId = userId,
                        biggestMealTime = _biggestMealTime.value,
                        sleepTime = _sleepTime.value,
                        wakeUpTime = _wakeUpTime.value
                    )
                )
                _saveStatus.postValue("Preferences saved successfully!")
            } catch (e: Exception) {
                _saveStatus.postValue("Error saving preferences: ${e.message}")
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
            val foodPrefs = userFoodCategoryPreferenceRepository.getPreferencesByUserId(userId).firstOrNull()
            _selectedFoodCategoryKeys.postValue(foodPrefs?.filter { it.isChecked }?.map { it.foodCategoryKey }?.toSet() ?: emptySet())

            val user = userRepository.getUserById(userId).firstOrNull()
            _selectedPersonaId.postValue(user?.selectedPersonaId)
            
            val timePref = userTimePreferenceRepository.getPreference(userId).firstOrNull()
            _biggestMealTime.postValue(timePref?.biggestMealTime)
            _sleepTime.postValue(timePref?.sleepTime)
            _wakeUpTime.postValue(timePref?.wakeUpTime)
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