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
import kotlinx.coroutines.flow.combine
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

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

    // Track time validation errors
    private val _timeValidationError = MutableStateFlow<String?>(null)
    val timeValidationError: StateFlow<String?> = _timeValidationError.asStateFlow()

    fun updateBiggestMealTime(time: String?) { 
        _biggestMealTime.value = time
        validateTimesLogic()
    }
    
    fun updateSleepTime(time: String?) { 
        _sleepTime.value = time
        validateTimesLogic()
    }
    
    fun updateWakeUpTime(time: String?) { 
        _wakeUpTime.value = time
        validateTimesLogic()
    }
    
    // Time formatter for parsing the time strings
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    
    /**
     * Validates if the times are in logical order:
     * - Wake up time should be before biggest meal time
     * - Biggest meal time should be before sleep time
     */
    private fun validateTimesLogic() {
        _timeValidationError.value = null
        
        val wakeUpTime = _wakeUpTime.value
        val biggestMealTime = _biggestMealTime.value
        val sleepTime = _sleepTime.value
        
        if (wakeUpTime.isNullOrBlank() || biggestMealTime.isNullOrBlank() || sleepTime.isNullOrBlank()) {
            // Not all times are set yet, don't validate
            return
        }
        
        try {
            val wakeUpLocalTime = LocalTime.parse(wakeUpTime, timeFormatter)
            val biggestMealLocalTime = LocalTime.parse(biggestMealTime, timeFormatter)
            val sleepLocalTime = LocalTime.parse(sleepTime, timeFormatter)
            
            // Validate wake up time is before biggest meal time
            if (wakeUpLocalTime.isAfter(biggestMealLocalTime)) {
                _timeValidationError.value = "Wake up time must be before your biggest meal time"
                return
            }
            
            // Validate biggest meal time is before sleep time
            if (biggestMealLocalTime.isAfter(sleepLocalTime)) {
                _timeValidationError.value = "Your biggest meal time must be before sleep time"
                return
            }
            
        } catch (e: DateTimeParseException) {
            _timeValidationError.value = "Invalid time format provided"
        }
    }

    // Validation State
    val isQuestionnaireValid: StateFlow<Boolean> = combine(
        listOf(
            _foodCategoryKeyBooleanMap,
            _selectedPersonaId,
            _biggestMealTime,
            _sleepTime,
            _wakeUpTime,
            _timeValidationError
        )
    ) { values ->
        @Suppress("UNCHECKED_CAST")
        val foodCategories = values[0] as Map<String, Boolean>
        val personaId = values[1] as String?
        val biggestMeal = values[2] as String?
        val sleep = values[3] as String?
        val wakeUp = values[4] as String?
        val timeError = values[5] as String?

        val isFoodValid = foodCategories.any { it.value } // At least one selected
        val isPersonaValid = !personaId.isNullOrBlank()
        val areTimesValid = !biggestMeal.isNullOrBlank() && 
                            !sleep.isNullOrBlank() && 
                            !wakeUp.isNullOrBlank() &&
                            timeError == null
                            
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

    // Flag to track if we're in editing mode
    private val _isEditing = MutableStateFlow(false)
    val isEditing: StateFlow<Boolean> = _isEditing.asStateFlow()

    // Function to set editing mode
    fun setEditingMode(editing: Boolean) {
        _isEditing.value = editing
        
        // When setting edit mode, we want to also reset the completed state
        // but only if we're entering edit mode, not leaving it
        if (editing) {
            _isQuestionnaireCompleted.value = false
        }
    }

    /**
     * Handles canceling edits - resets editing mode and ensures questionnaire
     * is properly marked as completed if it was before editing started
     */
    suspend fun cancelEditing(userId: String) {
        // Reset editing mode
        _isEditing.value = false
        // Now that loadUserPreferences is suspend, this will complete before cancelEditing returns
        loadUserPreferences(userId)
    }

    /**
     * Saves all user preferences to the database. Also test validation
     * @return Boolean true if save was successful and questionnaire is complete, false otherwise.
     */
    suspend fun saveAllPreferences(userId: String): Boolean {
        // viewModelScope.launch { // No longer need to launch a new coroutine here, as this will be called from one
            if (!isQuestionnaireValid.value) {
                _saveStatus.value = if (_timeValidationError.value != null) {
                    _timeValidationError.value
                } else {
                    "Please ensure all questionnaire sections are completed correctly."
                }
                // return@launch // Becomes return false
                return false
            }
            if (userId.isBlank()) {
                _saveStatus.value = "Error: User ID is missing."
                // return@launch // Becomes return false
                return false
            }
            try {
                // Save food category preferences
                _foodCategoryKeyBooleanMap.value.forEach { (key, selected) ->
                    if (selected) {
                        // Only insert the preference if it's selected
                        userFoodCategoryPreferenceRepository.insert(
                            UserFoodPreferenceEntity(
                                foodPrefUserId = userId,
                                foodPrefCategoryKey = key,
                                foodPrefCheckedStatus = selected)
                        )
                    }
                }

                // Save persona preference
                _selectedPersonaId.value?.let { personaId ->
                    userRepository.getUserById(userId).firstOrNull()?.let { user ->
                        userRepository.updateUser(user.copy(userPersonaId = personaId))
                    } ?: run {
                        _saveStatus.value = "Error: User not found for saving persona."
                        // We should ideally throw or return a specific error, for now, treat as save failure
                        return false 
                    }
                }
                
                // Save time preferences
                userTimePreferenceRepository.insert(
                    UserTimePreferenceEntity(
                        timePrefUserId = userId,
                        biggestMealTime = _biggestMealTime.value,
                        sleepTime = _sleepTime.value,
                        wakeUpTime = _wakeUpTime.value
                    )
                )
                
                // Mark as completed and reset editing mode
                // _isQuestionnaireCompleted.value = true // This state will be managed by checkQuestionnaireCompleted or similar logic
                _isEditing.value = false 
                // Ensure the completion status is correctly updated after save & exiting edit mode
                checkQuestionnaireCompleted() // Call this to update isQuestionnaireCompleted state based on current valid data

                _saveStatus.value = "Preferences saved successfully!"
                return true // Successfully saved
            } catch (e: Exception) {
                _saveStatus.value = "Error saving preferences: ${e.message}"
                return false // Save failed
            }
        // } // End of viewModelScope.launch
    }

    fun resetCompleted() {
        _isQuestionnaireCompleted.value = false
        
        // Force the questionnaire to stay in "not completed" state
        // This ensures it doesn't get automatically marked as completed based on loaded data
//        viewModelScope.launch {
//            // Small delay to ensure this takes precedence over any other state changes
//            kotlinx.coroutines.delay(100)
//            _isQuestionnaireCompleted.value = false
//        }
    }

    // Make this suspend so cancelEditing can await its completion
    suspend fun loadUserPreferences(userId: String) {
        // Reset editing state when loading user preferences (e.g., on login)
        // Only reset if we're not in edit mode - prevents existing state from being overwritten
        // if (!_isEditing.value) { // This condition might be too restrictive if called from cancelEditing
            // We're not in editing mode, so load everything normally
            // viewModelScope.launch { // Removed launch, as this is now a suspend function
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
                    
                    // Validate loaded times
                    validateTimesLogic()
                    
                    // Check if questionnaire is already completed based on loaded preferences
                    // Don't check if we're in edit mode
                    checkQuestionnaireCompleted()
                } catch (e: Exception) {
                    // Handle exceptions during loading, e.g., update a status StateFlow
                     _saveStatus.value = "Error loading preferences: ${e.message}"
                }
            // } // End of removed viewModelScope.launch
        // } else { // Removed else block related to _isEditing check, simplifying the function
            // We're in edit mode, so just reload to make sure the isQuestionnaireCompleted state
            // gets properly updated after checking completion
            // This case is now covered by the main block since _isEditing is set by caller before calling this if needed
            // viewModelScope.launch {
            //    checkQuestionnaireCompleted()
            // }
        // }
    }

    /**
     * Checks if the questionnaire is already completed based on loaded data
     */
    private fun checkQuestionnaireCompleted() {
        // Don't change completion status if we're in edit mode
        if (_isEditing.value) {
            return
        }
        
        // Check if all required preferences are set
        val hasSelectedCategories = _foodCategoryKeyBooleanMap.value.any { it.value }
        val hasSelectedPersona = !_selectedPersonaId.value.isNullOrBlank()
        val hasValidTimes = !_biggestMealTime.value.isNullOrBlank() && 
                         !_sleepTime.value.isNullOrBlank() && 
                         !_wakeUpTime.value.isNullOrBlank() &&
                         _timeValidationError.value == null
                         
        // Only mark as completed if all sections are filled
        _isQuestionnaireCompleted.value = hasSelectedCategories && hasSelectedPersona && hasValidTimes
    }

    fun clearSaveStatus() {
        _saveStatus.value = null
    }
    
    fun clearTimeValidationError() {
        _timeValidationError.value = null
    }
    
    /**
     * Check if there have been any changes made since the questionnaire was loaded
     * Used to determine if we should show a confirmation when abandoning edits
     */
    fun hasUnsavedChanges(userId: String): Boolean {
        // If not in edit mode, no unsaved changes
        if (!_isEditing.value) {
            return false
        }
        
        // Implementation would compare current state to saved state
        // For simplicity, we'll return true in edit mode
        return true
    }

    /**
     * Loads user preferences and returns whether the questionnaire is completed synchronously
     * This eliminates the need for delays in navigation logic
     */
    suspend fun loadUserPreferencesAndCheckCompletion(userId: String): Boolean {
        try {
            // Load food preferences
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

            // Load user data
            val user = userRepository.getUserById(userId).firstOrNull()
            _selectedPersonaId.value = user?.userPersonaId
            
            // Load time preferences
            val timePrefs = userTimePreferenceRepository.getPreference(userId).firstOrNull()
            if (timePrefs != null) {
                _biggestMealTime.value = timePrefs.biggestMealTime
                _sleepTime.value = timePrefs.sleepTime
                _wakeUpTime.value = timePrefs.wakeUpTime
            }
            
            // Check if all required preferences are set
            val hasSelectedCategories = foodCategoryMap.any { it.value }
            val hasSelectedPersona = !_selectedPersonaId.value.isNullOrBlank()
            val hasValidTimes = !_biggestMealTime.value.isNullOrBlank() && 
                         !_sleepTime.value.isNullOrBlank() && 
                         !_wakeUpTime.value.isNullOrBlank()
            
            // Update the state
            // _isQuestionnaireCompleted.value = hasSelectedCategories && hasSelectedPersona && hasValidTimes // This is now handled by checkQuestionnaireCompleted()
            checkQuestionnaireCompleted() // This will update the _isQuestionnaireCompleted StateFlow

            // Return the completion status directly
            return _isQuestionnaireCompleted.value
        } catch (e: Exception) {
            // If there's any error, assume questionnaire is not completed
            _isQuestionnaireCompleted.value = false
            return false
        }
    }
}