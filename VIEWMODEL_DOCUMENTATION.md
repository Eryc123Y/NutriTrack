# NutriTrack ViewModels & State Management Documentation

## Overview

This document provides comprehensive documentation for all ViewModels in the NutriTrack application, including state management patterns, dependency injection, and best practices for maintaining reactive UI state.

## Architecture Overview

The application follows the MVVM (Model-View-ViewModel) pattern with:
- **Model**: Data layer (Entities, DAOs, Repositories)
- **View**: Composable UI components
- **ViewModel**: Business logic and state management

## State Management Pattern

All ViewModels use a consistent `UiState` sealed interface for state management:

```kotlin
sealed interface UiState {
    object Initial : UiState
    object Loading : UiState
    data class Streaming(val currentMessageContent: String) : UiState
    data class Success(
        val finalMessageContent: String,
        val suggestedFollowUps: List<String> = emptyList()
    ) : UiState
    
    sealed interface Error : UiState {
        data class NetworkError(val errorMessage: String) : Error
        data class ApiError(val errorMessage: String) : Error
        data class UnidentifiedError(val errorMessage: String) : Error
    }
}
```

## ViewModelProviderFactory

The `ViewModelProviderFactory` implements dependency injection for ViewModels:

```kotlin
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
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(userRepository, sharedPreferencesManager) as T
            }
            // ... other ViewModels
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
```

**Usage:**
```kotlin
val viewModel: AuthViewModel = viewModel(factory = viewModelProviderFactory)
```

## Core ViewModels

### AuthViewModel

Handles user authentication, registration, and session management.

```kotlin
class AuthViewModel(
    private val userRepository: UserRepository,
    private val sharedPreferencesManager: SharedPreferencesManager
) : ViewModel() {
    
    private val _loginState = MutableLiveData<UiState>()
    val loginState: LiveData<UiState> = _loginState
    
    private val _registrationState = MutableLiveData<UiState>()
    val registrationState: LiveData<UiState> = _registrationState
    
    private val _currentUser = MutableLiveData<UserEntity?>()
    val currentUser: LiveData<UserEntity?> = _currentUser
    
    private val _registeredUsers = MutableLiveData<List<UserEntity>>()
    val registeredUsers: LiveData<List<UserEntity>> = _registeredUsers
}
```

**Key Methods:**

#### login
```kotlin
suspend fun login(userId: String, password: String) {
    _loginState.value = UiState.Loading
    try {
        val success = userRepository.authenticateUser(userId, password)
        if (success) {
            sharedPreferencesManager.setCurrentUser(userId)
            loadCurrentUser()
            _loginState.value = UiState.Success("Login successful")
        } else {
            _loginState.value = UiState.Error.ApiError("Invalid credentials")
        }
    } catch (e: Exception) {
        _loginState.value = UiState.Error.UnidentifiedError(e.message ?: "Login failed")
    }
}
```

#### register
```kotlin
suspend fun register(
    userId: String,
    userName: String,
    password: String,
    confirmPassword: String
) {
    _registrationState.value = UiState.Loading
    
    try {
        // Validation
        if (password != confirmPassword) {
            _registrationState.value = UiState.Error.ApiError("Passwords do not match")
            return
        }
        
        if (password.length < 8) {
            _registrationState.value = UiState.Error.ApiError("Password must be at least 8 characters")
            return
        }
        
        // Check if user exists and can be registered
        val existingUser = userRepository.getUserById(userId)
        if (existingUser?.userIsRegistered == true) {
            _registrationState.value = UiState.Error.ApiError("User is already registered")
            return
        }
        
        // Register user
        val success = userRepository.registerUser(userId, userName, password)
        if (success) {
            _registrationState.value = UiState.Success("Registration successful")
        } else {
            _registrationState.value = UiState.Error.ApiError("Registration failed")
        }
    } catch (e: Exception) {
        _registrationState.value = UiState.Error.UnidentifiedError(e.message ?: "Registration failed")
    }
}
```

**Usage Example:**
```kotlin
@Composable
fun LoginScreen(authViewModel: AuthViewModel) {
    val loginState by authViewModel.loginState.observeAsState(UiState.Initial)
    
    LaunchedEffect(loginState) {
        if (loginState is UiState.Success) {
            navController.navigate(Screen.Home.route)
        }
    }
    
    when (loginState) {
        is UiState.Loading -> {
            CircularProgressIndicator()
        }
        is UiState.Error -> {
            Text("Error: ${loginState.errorMessage}")
        }
    }
}
```

### GenAIViewModel

Manages AI chat functionality with streaming responses and conversation history.

```kotlin
class GenAIViewModel(
    private val chatRepository: ChatRepository,
    private val userStatsViewModel: UserStatsViewModel
) : ViewModel() {
    
    private val _uiState = MutableLiveData<UiState>(UiState.Initial)
    val uiState: LiveData<UiState> = _uiState
    
    private val _chatHistory = MutableLiveData<List<ChatMessageEntity>>()
    val chatHistory: LiveData<List<ChatMessageEntity>> = _chatHistory
    
    private val generativeModel = GenerativeModel(
        modelName = "gemini-pro",
        apiKey = BuildConfig.API_KEY
    )
}
```

**Key Methods:**

#### sendMessage
```kotlin
fun sendMessage(message: String, userId: String) {
    viewModelScope.launch {
        try {
            _uiState.value = UiState.Loading
            
            // Save user message
            val userMessage = ChatMessageEntity(
                message = message,
                isUserMessage = true,
                userId = userId,
                timestamp = System.currentTimeMillis()
            )
            chatRepository.insertChatMessage(userMessage)
            
            // Get user context for AI
            val userContext = buildUserContext(userId)
            val prompt = buildPrompt(message, userContext)
            
            // Stream AI response
            generativeModel.generateContentStream(prompt).collect { chunk ->
                _uiState.value = UiState.Streaming(chunk.text ?: "")
            }
            
            // Save AI response
            val aiResponse = ChatMessageEntity(
                message = (uiState.value as? UiState.Success)?.finalMessageContent ?: "",
                isUserMessage = false,
                userId = userId,
                timestamp = System.currentTimeMillis()
            )
            chatRepository.insertChatMessage(aiResponse)
            
        } catch (e: Exception) {
            _uiState.value = UiState.Error.ApiError(e.message ?: "Failed to send message")
        }
    }
}
```

#### getChatHistory
```kotlin
fun getChatHistory(userId: String): Flow<List<ChatMessageEntity>> {
    return chatRepository.getChatHistory(userId)
}
```

**Usage Example:**
```kotlin
@Composable
fun ChatScreen(genAIViewModel: GenAIViewModel, userId: String) {
    val uiState by genAIViewModel.uiState.observeAsState(UiState.Initial)
    val chatHistory by genAIViewModel.getChatHistory(userId).collectAsState(initial = emptyList())
    
    LazyColumn {
        items(chatHistory) { message ->
            ChatBubble(
                message = message,
                isFromUser = message.isUserMessage
            )
        }
        
        // Show streaming response
        if (uiState is UiState.Streaming) {
            item {
                ChatBubble(
                    message = ChatMessageEntity(
                        message = uiState.currentMessageContent,
                        isUserMessage = false,
                        userId = userId,
                        timestamp = System.currentTimeMillis()
                    ),
                    isFromUser = false,
                    isStreaming = true
                )
            }
        }
    }
}
```

### QuestionnaireViewModel

Manages the multi-step questionnaire flow for user preferences and persona selection.

```kotlin
class QuestionnaireViewModel(
    private val foodCategoryDefinitionRepository: FoodCategoryDefinitionRepository,
    private val userFoodCategoryPreferenceRepository: UserFoodCategoryPreferenceRepository,
    private val personaRepository: PersonaRepository,
    private val userTimePreferenceRepository: UserTimePreferenceRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    
    private val _currentStep = MutableLiveData(0)
    val currentStep: LiveData<Int> = _currentStep
    
    private val _foodCategories = MutableLiveData<List<FoodCategoryDefinitionEntity>>()
    val foodCategories: LiveData<List<FoodCategoryDefinitionEntity>> = _foodCategories
    
    private val _selectedPreferences = MutableLiveData<Map<String, Int>>()
    val selectedPreferences: LiveData<Map<String, Int>> = _selectedPreferences
    
    private val _personas = MutableLiveData<List<PersonaEntity>>()
    val personas: LiveData<List<PersonaEntity>> = _personas
    
    private val _selectedPersona = MutableLiveData<PersonaEntity?>()
    val selectedPersona: LiveData<PersonaEntity?> = _selectedPersona
    
    private val _timePreferences = MutableLiveData<Map<String, LocalTime>>()
    val timePreferences: LiveData<Map<String, LocalTime>> = _timePreferences
    
    val totalSteps = 4 // Food preferences, Persona, Time preferences, Summary
}
```

**Key Methods:**

#### nextStep / previousStep
```kotlin
fun nextStep() {
    val current = _currentStep.value ?: 0
    if (current < totalSteps - 1) {
        _currentStep.value = current + 1
    }
}

fun previousStep() {
    val current = _currentStep.value ?: 0
    if (current > 0) {
        _currentStep.value = current - 1
    }
}
```

#### updateFoodPreference
```kotlin
fun updateFoodPreference(categoryId: String, preferenceLevel: Int) {
    val currentPreferences = _selectedPreferences.value?.toMutableMap() ?: mutableMapOf()
    currentPreferences[categoryId] = preferenceLevel
    _selectedPreferences.value = currentPreferences
}
```

#### submitQuestionnaire
```kotlin
suspend fun submitQuestionnaire(userId: String): Boolean {
    return try {
        // Save food preferences
        selectedPreferences.value?.forEach { (categoryId, level) ->
            val preference = UserFoodPreferenceEntity(
                userId = userId,
                foodCategoryId = categoryId,
                preferenceLevel = level,
                lastUpdated = System.currentTimeMillis()
            )
            userFoodCategoryPreferenceRepository.insertPreference(preference)
        }
        
        // Save persona selection
        selectedPersona.value?.let { persona ->
            val user = userRepository.getUserById(userId)
            user?.let {
                val updatedUser = it.copy(userPersonaId = persona.personaID)
                userRepository.updateUser(updatedUser)
            }
        }
        
        // Save time preferences
        timePreferences.value?.forEach { (mealTime, time) ->
            val timePreference = UserTimePreferenceEntity(
                userId = userId,
                mealTime = mealTime,
                preferredTime = time.toString(),
                isEnabled = true,
                lastUpdated = System.currentTimeMillis()
            )
            userTimePreferenceRepository.insertTimePreference(timePreference)
        }
        
        true
    } catch (e: Exception) {
        false
    }
}
```

### ClinicianDashboardViewModel

Manages clinician dashboard functionality with patient overview and AI recommendations.

```kotlin
class ClinicianDashboardViewModel(
    private val userRepository: UserRepository,
    private val userScoreRepository: UserScoreRepository,
    private val genAIViewModel: GenAIViewModel
) : ViewModel() {
    
    private val _patients = MutableLiveData<List<UserEntity>>()
    val patients: LiveData<List<UserEntity>> = _patients
    
    private val _selectedPatient = MutableLiveData<UserEntity?>()
    val selectedPatient: LiveData<UserEntity?> = _selectedPatient
    
    private val _patientScores = MutableLiveData<List<UserScoreEntity>>()
    val patientScores: LiveData<List<UserScoreEntity>> = _patientScores
    
    private val _recommendations = MutableLiveData<String>()
    val recommendations: LiveData<String> = _recommendations
}
```

**Key Methods:**

#### loadPatients
```kotlin
fun loadPatients() {
    viewModelScope.launch {
        try {
            val registeredUsers = userRepository.getAllRegisteredUsers()
            _patients.value = registeredUsers
        } catch (e: Exception) {
            // Handle error
        }
    }
}
```

#### selectPatient
```kotlin
fun selectPatient(patient: UserEntity) {
    _selectedPatient.value = patient
    loadPatientScores(patient.userId)
}

private fun loadPatientScores(userId: String) {
    viewModelScope.launch {
        try {
            val scores = userScoreRepository.getUserScores(userId)
            _patientScores.value = scores
        } catch (e: Exception) {
            // Handle error
        }
    }
}
```

#### generateRecommendations
```kotlin
fun generateRecommendations(patientId: String) {
    viewModelScope.launch {
        try {
            val patient = userRepository.getUserById(patientId)
            val scores = userScoreRepository.getUserScores(patientId)
            
            val context = buildClinicianContext(patient, scores)
            val prompt = "Based on this patient data, provide professional nutrition recommendations: $context"
            
            genAIViewModel.sendMessage(prompt, "clinician_$patientId")
            
            // Observe AI response
            genAIViewModel.uiState.observeForever { state ->
                if (state is UiState.Success) {
                    _recommendations.value = state.finalMessageContent
                }
            }
        } catch (e: Exception) {
            // Handle error
        }
    }
}
```

### UserStatsViewModel

Manages user statistics and score calculations.

```kotlin
class UserStatsViewModel(
    private val userRepository: UserRepository,
    private val userScoreRepository: UserScoreRepository,
    private val personaRepository: PersonaRepository,
    private val userTimePreferenceRepository: UserTimePreferenceRepository,
    private val userFoodCategoryPreferenceRepository: UserFoodCategoryPreferenceRepository
) : ViewModel() {
    
    private val _userStats = MutableLiveData<UserStats>()
    val userStats: LiveData<UserStats> = _userStats
    
    private val _scoreHistory = MutableLiveData<List<UserScoreEntity>>()
    val scoreHistory: LiveData<List<UserScoreEntity>> = _scoreHistory
    
    data class UserStats(
        val currentScore: Float,
        val averageScore: Float,
        val improvementRate: Float,
        val totalEntries: Int,
        val lastUpdated: Long
    )
}
```

**Key Methods:**

#### loadUserStats
```kotlin
fun loadUserStats(userId: String) {
    viewModelScope.launch {
        try {
            val scores = userScoreRepository.getUserScores(userId)
            val currentScore = scores.firstOrNull()?.userScore ?: 0f
            val averageScore = scores.map { it.userScore }.average().toFloat()
            val improvementRate = calculateImprovementRate(scores)
            
            _userStats.value = UserStats(
                currentScore = currentScore,
                averageScore = averageScore,
                improvementRate = improvementRate,
                totalEntries = scores.size,
                lastUpdated = System.currentTimeMillis()
            )
            
            _scoreHistory.value = scores
        } catch (e: Exception) {
            // Handle error
        }
    }
}
```

#### calculateNutritionScore
```kotlin
suspend fun calculateNutritionScore(userId: String): Float {
    return try {
        val preferences = userFoodCategoryPreferenceRepository.getUserPreferences(userId)
        val timePreferences = userTimePreferenceRepository.getUserTimePreferences(userId)
        val user = userRepository.getUserById(userId)
        
        ScoreUtils.calculateNutritionScore(preferences, timePreferences, user)
    } catch (e: Exception) {
        0f
    }
}
```

## State Management Best Practices

### 1. Reactive UI Updates

Use LiveData and Flow for reactive UI updates:

```kotlin
@Composable
fun MyScreen(viewModel: MyViewModel) {
    // ✅ Observe LiveData
    val uiState by viewModel.uiState.observeAsState(UiState.Initial)
    
    // ✅ Collect Flow
    val dataFlow by viewModel.dataFlow.collectAsState(initial = emptyList())
    
    // React to state changes
    LaunchedEffect(uiState) {
        when (uiState) {
            is UiState.Success -> {
                // Handle success
            }
            is UiState.Error -> {
                // Show error message
            }
        }
    }
}
```

### 2. Error Handling

Implement consistent error handling across ViewModels:

```kotlin
private fun handleError(exception: Exception, operation: String) {
    val errorState = when (exception) {
        is IOException -> UiState.Error.NetworkError("Network error during $operation")
        is HttpException -> UiState.Error.ApiError("API error during $operation")
        else -> UiState.Error.UnidentifiedError("Unknown error during $operation")
    }
    _uiState.value = errorState
}
```

### 3. Loading States

Always provide loading states for better UX:

```kotlin
suspend fun performOperation() {
    _uiState.value = UiState.Loading
    try {
        val result = repository.getData()
        _uiState.value = UiState.Success(result)
    } catch (e: Exception) {
        handleError(e, "data loading")
    }
}
```

### 4. Memory Management

Properly manage resources and prevent memory leaks:

```kotlin
class MyViewModel : ViewModel() {
    private val viewModelJob = SupervisorJob()
    private val viewModelScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}
```

## Testing ViewModels

### Unit Test Example

```kotlin
@Test
fun `login with valid credentials should update state to success`() = runTest {
    // Given
    val userId = "testUser"
    val password = "password123"
    coEvery { userRepository.authenticateUser(userId, password) } returns true
    
    // When
    authViewModel.login(userId, password)
    
    // Then
    val state = authViewModel.loginState.getOrAwaitValue()
    assertThat(state).isInstanceOf(UiState.Success::class.java)
    verify { sharedPreferencesManager.setCurrentUser(userId) }
}
```

### Testing with TestObserver

```kotlin
@Test
fun `should emit loading then success states`() = runTest {
    val testObserver = authViewModel.loginState.test()
    
    authViewModel.login("user", "pass")
    
    testObserver.assertValues(
        UiState.Loading,
        UiState.Success("Login successful")
    )
}
```

## Performance Optimization

### 1. Use derivedStateOf for computed state

```kotlin
val isFormValid by derivedStateOf {
    username.isNotEmpty() && password.length >= 8
}
```

### 2. Implement proper state preservation

```kotlin
class MyViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    var searchQuery: String by savedStateHandle.saveable { mutableStateOf("") }
    
    private val _results = savedStateHandle.getLiveData<List<Result>>("results")
    val results: LiveData<List<Result>> = _results
}
```

### 3. Use Flow for one-shot operations

```kotlin
fun searchUsers(query: String): Flow<List<User>> = flow {
    emit(userRepository.searchUsers(query))
}.flowOn(Dispatchers.IO)
```

This comprehensive ViewModels documentation provides detailed information about state management, dependency injection, and best practices for maintaining reactive UI state in the NutriTrack application.