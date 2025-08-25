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
import kotlinx.coroutines.flow.combine
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

/**
 * ViewModel for managing the user questionnaire and preference system.
 * 
 * This ViewModel handles the multi-step questionnaire process including:
 * - Food category preferences selection
 * - Persona/character type selection  
 * - Time preferences (wake up, biggest meal, sleep times)
 * - Validation of all inputs
 * - Saving and loading user preferences
 * - Edit mode functionality
 * 
 * @param foodCategoryDefinitionRepository Repository for food category definitions
 * @param userFoodCategoryPreferenceRepository Repository for user food preferences
 * @param personaRepository Repository for persona definitions
 * @param userTimePreferenceRepository Repository for user time preferences
 * @param userRepository Repository for user data operations
 */
class QuestionnaireViewModel(
    private val foodCategoryDefinitionRepository: FoodCategoryDefinitionRepository,
    private val userFoodCategoryPreferenceRepository: UserFoodCategoryPreferenceRepository,
    personaRepository: PersonaRepository,
    private val userTimePreferenceRepository: UserTimePreferenceRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    // ==================== FOOD CATEGORIES SECTION ====================
    
    /**
     * StateFlow holding all available food categories from the database.
     * Updates automatically when the database changes.
     */
    val allFoodCategories: StateFlow<List<FoodCategoryDefinitionEntity>> =
        foodCategoryDefinitionRepository.getAllFoodCategories()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    /**
     * Private mutable StateFlow tracking selected food categories.
     * Maps food category IDs to boolean selection state.
     */
    private val _foodCategoryKeyBooleanMap = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    
    /**
     * Public read-only StateFlow for food category selections.
     * Used by UI to display current selections and observe changes.
     */
    val foodCategoryKeyBooleanMap: StateFlow<Map<String, Boolean>> = _foodCategoryKeyBooleanMap.asStateFlow()

    /**
     * Toggles the selection state of a food category.
     * Updates the internal map and notifies observers of the change.
     * 
     * @param foodId The unique identifier of the food category
     * @param isSelected Boolean indicating whether the category should be selected
     */
    fun toggleFoodCategory(foodId: String, isSelected: Boolean) {
        val currentMap = _foodCategoryKeyBooleanMap.value.toMutableMap()
        currentMap[foodId] = isSelected
        _foodCategoryKeyBooleanMap.value = currentMap
    }

    // ==================== PERSONA SELECTION SECTION ====================
    
    /**
     * StateFlow holding all available persona types from the database.
     * Used to populate the persona selection UI.
     */
    val allPersonas: StateFlow<List<PersonaEntity>> =
        personaRepository.getAllPersonas()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    /**
     * Private mutable StateFlow tracking the selected persona ID.
     * Null indicates no persona has been selected yet.
     */
    private val _selectedPersonaId = MutableStateFlow<String?>(null)
    
    /**
     * Public read-only StateFlow for the selected persona.
     * UI observes this to show the current selection.
     */
    val selectedPersonaId: StateFlow<String?> = _selectedPersonaId.asStateFlow()

    /**
     * Sets the selected persona for the user.
     * Updates the internal state and notifies observers.
     * 
     * @param personaId The unique identifier of the selected persona
     */
    fun selectPersona(personaId: String) {
        _selectedPersonaId.value = personaId
    }

    // ==================== TIME PREFERENCES SECTION ====================
    
    /**
     * Private mutable StateFlow for biggest meal time with default value (12:00).
     * Represents when the user typically has their largest meal.
     */
    private val _biggestMealTime = MutableStateFlow("12:00") // Default value
    val biggestMealTime: StateFlow<String?> = _biggestMealTime.asStateFlow()

    /**
     * Private mutable StateFlow for sleep time with default value (22:00).
     * Represents when the user typically goes to sleep.
     */
    private val _sleepTime = MutableStateFlow("22:00") // Default value
    val sleepTime: StateFlow<String?> = _sleepTime.asStateFlow()

    /**
     * Private mutable StateFlow for wake up time with default value (07:00).
     * Represents when the user typically wakes up.
     */
    private val _wakeUpTime = MutableStateFlow("07:00") // Default value
    val wakeUpTime: StateFlow<String?> = _wakeUpTime.asStateFlow()

    /**
     * Private mutable StateFlow for time validation error messages.
     * Null indicates no validation errors.
     */
    private val _timeValidationError = MutableStateFlow<String?>(null)
    val timeValidationError: StateFlow<String?> = _timeValidationError.asStateFlow()

    /**
     * Updates the biggest meal time and triggers validation.
     * Converts null input to empty string for consistency.
     * 
     * @param time The new biggest meal time in HH:mm format or null
     */
    fun updateBiggestMealTime(time: String?) { 
        _biggestMealTime.value = time.toString()
        validateTimesLogic()
    }
    
    /**
     * Updates the sleep time and triggers validation.
     * Converts null input to empty string for consistency.
     * 
     * @param time The new sleep time in HH:mm format or null
     */
    fun updateSleepTime(time: String?) { 
        _sleepTime.value = time.toString()
        validateTimesLogic()
    }
    
    /**
     * Updates the wake up time and triggers validation.
     * Converts null input to empty string for consistency.
     * 
     * @param time The new wake up time in HH:mm format or null
     */
    fun updateWakeUpTime(time: String?) { 
        _wakeUpTime.value = time.toString()
        validateTimesLogic()
    }
    
    // ==================== TIME VALIDATION SECTION ====================
    
    /**
     * Time formatter for parsing time strings in 24-hour format (HH:mm).
     * Used to validate and compare time inputs.
     */
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    
    /**
     * Validates if the time preferences are in logical order.
     * Ensures times are in HH:mm format (24-hour) and follow logical constraints:
     * - Wake up time, biggest meal time, and sleep time should be in chronological order
     * - Handles both same-day and overnight sleep scenarios
     * - All times must be provided and in valid format
     */
    private fun validateTimesLogic() {
        _timeValidationError.value = null // Reset error at the beginning

        val wakeUpStr = _wakeUpTime.value
        val biggestMealStr = _biggestMealTime.value
        val sleepStr = _sleepTime.value

        // Check for blank strings first, as parsing them will fail
        if (wakeUpStr.isBlank()) {
            _timeValidationError.value = "Wake up time is required (HH:mm)."
            return
        }
        if (biggestMealStr.isBlank()) {
            _timeValidationError.value = "Biggest meal time is required (HH:mm)."
            return
        }
        if (sleepStr.isBlank()) {
            _timeValidationError.value = "Sleep time is required (HH:mm)."
            return
        }
        
        // AI-enhanced validation logic for complex time scenarios
        try {
            val wakeUp = LocalTime.parse(wakeUpStr, timeFormatter)
            val meal = LocalTime.parse(biggestMealStr, timeFormatter)
            val sleep = LocalTime.parse(sleepStr, timeFormatter)

            // Wake up and sleep times cannot be the same
            if (wakeUp == sleep) {
                _timeValidationError.value = "Wake up and sleep times cannot be the same."
                return
            }

            // Scenario 1: Sleep is on the same day as wake up (e.g., wake 07:00, sleep 22:00)
            if (sleep.isAfter(wakeUp)) {
                // Meal must be strictly between wakeUp and sleep
                if (!meal.isAfter(wakeUp) || !meal.isBefore(sleep)) {
                    _timeValidationError.value = "Biggest meal time must be after wake up and before sleep time."
                    return
                }
            }
            // Scenario 2: Sleep is on the next day (e.g., wake 22:00, sleep 07:00)
            else { 
                // Meal can be after wakeUp (day1) OR before sleep (day2)
                // It cannot be between sleep (early morning day2) and wakeUp (late morning/evening day1)
                if (meal.isAfter(sleep) && meal.isBefore(wakeUp)) {
                    _timeValidationError.value = "Biggest meal time must be within your awake period (e.g., after wake up on day 1 or before sleep on day 2)."
                    return
                }
            }
            // All checks passed
            _timeValidationError.value = null

        } catch (e: DateTimeParseException) {
            e.printStackTrace()
            _timeValidationError.value = "Invalid time format. Please use HH:mm (e.g., 07:00 or 15:30)."
        }
    }

    // ==================== VALIDATION STATE SECTION ====================
    
    /**
     * Combined StateFlow that determines if the entire questionnaire is valid.
     * Combines validation results from all sections (food, persona, time).
     * Updates automatically when any component changes.
     */
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

        // Validate each section
        val isFoodValid = foodCategories.any { it.value } // At least one food category selected
        val isPersonaValid = !personaId.isNullOrBlank() // Persona must be selected
        val areTimesValid = !biggestMeal.isNullOrBlank() && 
                            !sleep.isNullOrBlank() && 
                            !wakeUp.isNullOrBlank() &&
                            timeError == null // All times provided and no validation errors
                            
        // All sections must be valid for the questionnaire to be complete
        isFoodValid && isPersonaValid && areTimesValid
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    // ==================== SAVING & EDITING SECTION ====================
    
    /**
     * Private mutable StateFlow tracking questionnaire completion status.
     * True indicates user has completed all required sections.
     */
    private val _isQuestionnaireCompleted = MutableStateFlow(false)
    val isQuestionnaireCompleted: StateFlow<Boolean> = _isQuestionnaireCompleted.asStateFlow()

    /**
     * Private mutable StateFlow for save operation status messages.
     * Contains success/error messages or null if no save operation occurred.
     */
    private val _saveStatus = MutableStateFlow<String?>(null)
    val saveStatus: StateFlow<String?> = _saveStatus.asStateFlow()

    /**
     * Flag to track if we're in editing mode (modifying existing preferences).
     * Used to differentiate between initial setup and preference editing.
     */
    private val _isEditing = MutableStateFlow(false)
    val isEditing: StateFlow<Boolean> = _isEditing.asStateFlow()

    /**
     * Sets the editing mode for the questionnaire.
     * When entering edit mode, resets completion status to force re-validation.
     * 
     * @param editing Boolean indicating whether to enter or exit edit mode
     */
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
     * is properly marked as completed if it was before editing started.
     * Reloads existing preferences to restore the original state.
     * 
     * @param userId The user ID whose preferences are being edited
     */
    suspend fun cancelEditing(userId: String) {
        // Reset editing mode
        _isEditing.value = false
        // Reload original preferences to restore pre-edit state
        loadUserPreferences(userId)
    }

    /**
     * Saves all user preferences to the database after validation.
     * Performs comprehensive validation before saving and provides detailed error messages.
     * 
     * @param userId The user ID to save preferences for
     * @return Boolean true if save was successful and questionnaire is complete, false otherwise
     */
    suspend fun saveAllPreferences(userId: String): Boolean {
        // Validate questionnaire before attempting to save
        if (!isQuestionnaireValid.value) {
            _saveStatus.value = if (_timeValidationError.value != null) {
                _timeValidationError.value
            } else {
                "Please ensure all questionnaire sections are completed correctly."
            }
            return false
        }
        
        // Validate user ID
        if (userId.isBlank()) {
            _saveStatus.value = "Error: User ID is missing."
            return false
        }
        
        try {
            // Save food category preferences - only save selected categories
            _foodCategoryKeyBooleanMap.value.forEach { (key, selected) ->
                if (selected) {
                    userFoodCategoryPreferenceRepository.insert(
                        UserFoodPreferenceEntity(
                            foodPrefUserId = userId,
                            foodPrefCategoryKey = key,
                            foodPrefCheckedStatus = selected)
                    )
                }
            }

            // Save persona preference to user entity
            _selectedPersonaId.value?.let { personaId ->
                userRepository.getUserById(userId).firstOrNull()?.let { user ->
                    userRepository.updateUser(user.copy(userPersonaId = personaId))
                } ?: run {
                    _saveStatus.value = "Error: User not found for saving persona."
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

            // Exit editing mode and check completion status
            _isEditing.value = false
            checkQuestionnaireCompleted()

            _saveStatus.value = "Preferences saved successfully!"
            return true // Successfully saved
        } catch (e: Exception) {
            _saveStatus.value = "Error saving preferences: ${e.message}"
            return false // Save failed
        }
    }

    // ==================== UTILITY METHODS SECTION ====================
    
    /**
     * Resets the questionnaire completion status.
     * Used when starting a new questionnaire or forcing re-completion.
     */
    fun resetCompleted() {
        _isQuestionnaireCompleted.value = false
    }

    /**
     * Loads user preferences from the database for editing.
     * Populates all StateFlows with existing user data for the questionnaire.
     * 
     * @param userId The user ID to load preferences for
     */
    suspend fun loadUserPreferences(userId: String) {
        try {
            // Load food category preferences
            val foodPrefs = userFoodCategoryPreferenceRepository
                .getPreferencesByUserId(userId).firstOrNull()

            // Create a complete map of all food categories with selection status
            val foodCategoryMap = mutableMapOf<String, Boolean>()
            
            // Get all available food categories first to ensure complete map
            val allCategories = foodCategoryDefinitionRepository.getAllFoodCategories().firstOrNull() ?: emptyList()
            allCategories.forEach { category ->
                foodCategoryMap[category.foodDefId] = false // Default to not selected
            }
            
            // Update with user's actual selections
            foodPrefs?.forEach { pref ->
                foodCategoryMap[pref.foodPrefCategoryKey] = pref.foodPrefCheckedStatus
            }
            
            _foodCategoryKeyBooleanMap.value = foodCategoryMap

            // Load user persona selection
            val user = userRepository.getUserById(userId).firstOrNull()
            _selectedPersonaId.value = user?.userPersonaId
        
            // Load time preferences
            val timePref = userTimePreferenceRepository.getPreference(userId).firstOrNull()
            _biggestMealTime.value = timePref?.biggestMealTime.toString()
            _sleepTime.value = timePref?.sleepTime.toString()
            _wakeUpTime.value = timePref?.wakeUpTime.toString()
            
            // Validate loaded times to ensure they meet logical constraints
            validateTimesLogic()
            
            // Check if questionnaire is already completed based on loaded preferences
            checkQuestionnaireCompleted()
        } catch (e: Exception) {
            // Handle exceptions during loading with user feedback
            _saveStatus.value = "Error loading preferences: ${e.message}"
        }
    }

    /**
     * Checks if the questionnaire is already completed based on loaded data.
     * This method is called after loading preferences to determine completion status.
     * Does not update status if in edit mode to allow for modifications.
     */
    private fun checkQuestionnaireCompleted() {
        // Don't change completion status if we're in edit mode
        if (_isEditing.value) {
            return
        }
        
        // Check if all required sections are properly filled
        val hasSelectedCategories = _foodCategoryKeyBooleanMap.value.any { it.value }
        val hasSelectedPersona = !_selectedPersonaId.value.isNullOrBlank()
        val hasValidTimes = _biggestMealTime.value.isNotBlank() &&
                _sleepTime.value.isNotBlank() &&
                _wakeUpTime.value.isNotBlank() &&
                _timeValidationError.value == null
                        
        // Only mark as completed if all sections are valid and filled
        _isQuestionnaireCompleted.value = hasSelectedCategories && hasSelectedPersona && hasValidTimes
    }

    /**
     * Clears the save status message.
     * Called after displaying the message to the user.
     */
    fun clearSaveStatus() {
        _saveStatus.value = null
    }

    /**
     * Checks if there have been any changes made since the questionnaire was loaded.
     * Used to determine if we should show a confirmation dialog when abandoning edits.
     * 
     * @return Boolean indicating if there are unsaved changes (true if in edit mode)
     */
    fun hasUnsavedChanges(): Boolean {
        // If not in edit mode, no unsaved changes
        return _isEditing.value
    }

    /**
     * Loads user preferences and returns whether the questionnaire is completed synchronously.
     * This optimized method eliminates delays in navigation logic by returning completion status directly.
     * Used during app startup and navigation to determine if user needs to complete questionnaire.
     * 
     * @param userId The user ID to load preferences for
     * @return Boolean indicating if the questionnaire is completed
     */
    suspend fun loadUserPreferencesAndCheckCompletion(userId: String): Boolean {
        try {
            // Load food preferences
            val foodPrefs = userFoodCategoryPreferenceRepository
                .getPreferencesByUserId(userId).firstOrNull()
            
            // Create a complete Map<String, Boolean> from preferences
            val foodCategoryMap = mutableMapOf<String, Boolean>()
            
            // Get all food categories first to ensure complete map
            val allCategories = foodCategoryDefinitionRepository.getAllFoodCategories().firstOrNull() ?: emptyList()
            allCategories.forEach { category ->
                foodCategoryMap[category.foodDefId] = false
            }
            
            // Update with user's actual selections
            foodPrefs?.forEach { pref ->
                foodCategoryMap[pref.foodPrefCategoryKey] = pref.foodPrefCheckedStatus
            }
            
            _foodCategoryKeyBooleanMap.value = foodCategoryMap

            // Load user data and persona
            val user = userRepository.getUserById(userId).firstOrNull()
            _selectedPersonaId.value = user?.userPersonaId
            
            // Load time preferences
            val timePrefs = userTimePreferenceRepository.getPreference(userId).firstOrNull()
            if (timePrefs != null) {
                _biggestMealTime.value = timePrefs.biggestMealTime.toString()
                _sleepTime.value = timePrefs.sleepTime.toString()
                _wakeUpTime.value = timePrefs.wakeUpTime.toString()
            }
            
            // Check completion status using the consolidated method
            checkQuestionnaireCompleted()

            // Return the completion status directly for immediate use in navigation
            return _isQuestionnaireCompleted.value
        } catch (e: Exception) {
            e.printStackTrace()
            // If there's any error, assume questionnaire is not completed
            _isQuestionnaireCompleted.value = false
            return false
        }
    }
}