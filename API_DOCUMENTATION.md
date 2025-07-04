# NutriTrack Application - API Documentation

## Table of Contents
1. [Application Overview](#application-overview)
2. [Architecture](#architecture)
3. [Getting Started](#getting-started)
4. [Navigation System](#navigation-system)
5. [Data Layer](#data-layer)
6. [ViewModels & State Management](#viewmodels--state-management)
7. [UI Components](#ui-components)
8. [Screens](#screens)
9. [Utilities](#utilities)
10. [API Integration](#api-integration)
11. [Theme & Styling](#theme--styling)
12. [Usage Examples](#usage-examples)

## Application Overview

NutriTrack is a comprehensive nutrition tracking Android application built with modern Android development tools and libraries. The app provides personalized nutrition coaching, user insights, and clinician dashboard functionality.

### Key Features
- User authentication and registration
- Personalized nutrition questionnaires
- AI-powered nutrition coaching
- Score tracking and insights
- Clinician dashboard for health professionals
- Dark/light theme support
- Real-time chat with AI coach

### Technology Stack
- **UI Framework**: Jetpack Compose
- **Database**: Room with SQLite
- **Architecture**: MVVM with Repository pattern
- **Navigation**: Navigation Compose
- **Dependency Injection**: Manual DI with Factory pattern
- **Image Loading**: Coil
- **Networking**: Retrofit
- **AI Integration**: Generative AI
- **Security**: BCrypt for password hashing

## Architecture

The application follows Clean Architecture principles with clear separation of concerns:

```
├── data/
│   ├── model/
│   │   ├── entity/     # Room entities
│   │   ├── dao/        # Data Access Objects
│   │   └── repository/ # Repository implementations
│   ├── viewmodel/      # ViewModels
│   ├── csv/           # CSV parsing utilities
│   └── legacy/        # Legacy data models
├── view/
│   ├── components/    # Reusable UI components
│   ├── screens/       # Screen composables
│   └── theme/         # Theme configuration
├── navigation/        # Navigation setup
├── api/              # Network API interfaces
└── util/             # Utility classes
```

## Getting Started

### Application Class

The main application class initializes all dependencies and repositories:

```kotlin
class NutriTrackApp : Application() {
    lateinit var database: AppDatabase
    lateinit var viewModelProviderFactory: ViewModelProviderFactory
    // ... other repositories
    
    override fun onCreate() {
        super.onCreate()
        initializeComponents()
    }
}
```

### Main Activity

The entry point activity sets up the Compose UI and navigation:

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Splash screen and edge-to-edge setup
        setContent {
            val app = application as NutriTrackApp
            var isDarkMode by remember { mutableStateOf(false) }
            FIT2081A1Theme(darkTheme = isDarkMode) {
                AppNavigation(
                    viewModelProviderFactory = app.viewModelProviderFactory,
                    isDarkMode = isDarkMode,
                    onToggleDarkMode = { newValue -> isDarkMode = newValue }
                )
            }
        }
    }
}
```

**Usage:**
- The MainActivity automatically handles splash screen, dark mode toggling, and navigation setup
- No direct interaction needed - serves as the app's entry point

## Navigation System

### Screen Enum

Defines all available screens in the application:

```kotlin
enum class Screen(val route: String) {
    Welcome("welcome"),
    Login("login"),
    Register("register"),
    Home("home"),
    Insights("insights"),
    NutriCoach("nutricoach"),
    Settings("settings"),
    Questionnaire("questionnaire"),
    ClinicianDashboard("clinician_dashboard"),
}
```

**Usage:**
```kotlin
// Navigate to a screen
navController.navigate(Screen.Home.route)

// Navigate with pop up to
navController.navigate(Screen.Login.route) {
    popUpTo(Screen.Welcome.route) { inclusive = true }
}
```

### Navigation Graph

The `AppNavigation` composable in `NavGraph.kt` sets up the navigation between screens with animations and transitions.

**Key Features:**
- Smooth page transitions with custom animations
- Proper state management across navigation
- Deep linking support
- Navigation arguments handling

## Data Layer

### Database Configuration

The Room database is configured in `AppDatabase.kt`:

```kotlin
@Database(
    entities = [
        UserEntity::class,
        PersonaEntity::class,
        UserFoodPreferenceEntity::class,
        UserScoreEntity::class,
        UserTimePreferenceEntity::class,
        FoodCategoryDefinitionEntity::class,
        ScoreTypeDefinitionEntity::class,
        ChatMessageEntity::class
    ],
    version = 4,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    // DAO abstract methods
    abstract fun userDao(): UserDao
    abstract fun personaDao(): PersonaDao
    // ... other DAOs
    
    companion object {
        fun getDatabase(context: Context): AppDatabase {
            // Singleton pattern implementation
        }
    }
}
```

### Entities

#### UserEntity
Represents a user in the system:

```kotlin
@Entity(
    tableName = "users",
    foreignKeys = [ForeignKey(/* ... */)],
    indices = [Index(value = ["userPersonaId"])]
)
data class UserEntity(
    @PrimaryKey val userId: String,
    val userName: String? = null,
    val userPhoneNumber: String,
    val userGender: String?,
    val userFruitServingsize: Float? = null,
    val userPersonaId: String? = null,
    val userHashedCredential: String? = null,
    val userIsRegistered: Boolean = false,
)
```

**Usage:**
```kotlin
// Create a new user
val user = UserEntity(
    userId = "user123",
    userName = "John Doe",
    userPhoneNumber = "+1234567890",
    userGender = "Male",
    userIsRegistered = true
)

// Insert into database
userRepository.insertUser(user)
```

#### Other Entities
- `PersonaEntity`: User personality/profile types
- `UserFoodPreferenceEntity`: Food category preferences
- `UserScoreEntity`: Nutrition scores over time
- `UserTimePreferenceEntity`: Time-based preferences
- `ChatMessageEntity`: Chat conversation history
- `FoodCategoryDefinitionEntity`: Food category definitions
- `ScoreTypeDefinitionEntity`: Score type definitions

### Data Access Objects (DAOs)

#### UserDao
Provides database operations for users:

```kotlin
@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE userId = :userId")
    suspend fun getUserById(userId: String): UserEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)
    
    @Query("SELECT * FROM users WHERE userIsRegistered = 1")
    suspend fun getAllRegisteredUsers(): List<UserEntity>
    
    @Update
    suspend fun updateUser(user: UserEntity)
    
    @Query("DELETE FROM users WHERE userId = :userId")
    suspend fun deleteUserById(userId: String)
}
```

**Usage:**
```kotlin
// Get user by ID
val user = userDao.getUserById("user123")

// Insert new user
userDao.insertUser(userEntity)

// Update existing user
userDao.updateUser(updatedUser)
```

### Repositories

#### UserRepository
Encapsulates user data operations:

```kotlin
class UserRepository(private val userDao: UserDao) {
    suspend fun getUserById(userId: String): UserEntity? = userDao.getUserById(userId)
    
    suspend fun insertUser(user: UserEntity) = userDao.insertUser(user)
    
    suspend fun getAllRegisteredUsers(): List<UserEntity> = userDao.getAllRegisteredUsers()
    
    suspend fun updateUser(user: UserEntity) = userDao.updateUser(user)
    
    suspend fun deleteUserById(userId: String) = userDao.deleteUserById(userId)
    
    suspend fun authenticateUser(userId: String, password: String): Boolean {
        // Authentication logic with BCrypt
    }
}
```

**Usage:**
```kotlin
// Repository usage in ViewModel
class AuthViewModel(private val userRepository: UserRepository) {
    suspend fun login(userId: String, password: String): Boolean {
        return userRepository.authenticateUser(userId, password)
    }
}
```

## ViewModels & State Management

### UI State Management

All ViewModels use a common `UiState` sealed interface for consistent state management:

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

### ViewModelProviderFactory

A factory for creating ViewModels with dependencies:

```kotlin
class ViewModelProviderFactory(
    private val userRepository: UserRepository,
    private val personaRepository: PersonaRepository,
    // ... other repositories
) : ViewModelProvider.Factory {
    
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(userRepository, sharedPreferencesManager) as T
            }
            // ... other ViewModels
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
```

### Key ViewModels

#### AuthViewModel
Handles user authentication:

```kotlin
class AuthViewModel(
    private val userRepository: UserRepository,
    private val sharedPreferencesManager: SharedPreferencesManager
) : ViewModel() {
    
    private val _loginState = MutableLiveData<UiState>()
    val loginState: LiveData<UiState> = _loginState
    
    suspend fun login(userId: String, password: String) {
        _loginState.value = UiState.Loading
        try {
            val success = userRepository.authenticateUser(userId, password)
            if (success) {
                sharedPreferencesManager.setCurrentUser(userId)
                _loginState.value = UiState.Success("Login successful")
            } else {
                _loginState.value = UiState.Error.ApiError("Invalid credentials")
            }
        } catch (e: Exception) {
            _loginState.value = UiState.Error.UnidentifiedError(e.message ?: "Unknown error")
        }
    }
}
```

**Usage:**
```kotlin
@Composable
fun LoginScreen(authViewModel: AuthViewModel) {
    val loginState by authViewModel.loginState.observeAsState(UiState.Initial)
    
    when (loginState) {
        is UiState.Loading -> CircularProgressIndicator()
        is UiState.Success -> {
            // Navigate to home screen
        }
        is UiState.Error -> {
            Text("Error: ${loginState.errorMessage}")
        }
    }
}
```

#### GenAIViewModel
Handles AI chat functionality:

```kotlin
class GenAIViewModel(
    private val chatRepository: ChatRepository,
    private val userStatsViewModel: UserStatsViewModel
) : ViewModel() {
    
    fun sendMessage(message: String, userId: String) {
        // Send message to AI and handle streaming response
    }
    
    fun getChatHistory(userId: String): Flow<List<ChatMessage>> {
        return chatRepository.getChatHistory(userId)
    }
}
```

## UI Components

### Reusable Components

#### ScoreProgressBar
A custom progress bar for displaying nutrition scores:

```kotlin
@Composable
fun ScoreProgressBar(
    score: Float,
    maxScore: Float = 100f,
    color: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier
) {
    // Custom progress bar implementation with animations
}
```

**Usage:**
```kotlin
ScoreProgressBar(
    score = 75f,
    maxScore = 100f,
    color = Color.Green,
    modifier = Modifier.fillMaxWidth()
)
```

#### TimePickerComponent
A time picker component for user preferences:

```kotlin
@Composable
fun TimePickerComponent(
    selectedTime: LocalTime,
    onTimeSelected: (LocalTime) -> Unit,
    modifier: Modifier = Modifier
) {
    // Time picker implementation
}
```

#### FoodCategoryCard
Displays food category information:

```kotlin
@Composable
fun FoodCategoryCard(
    category: FoodCategory,
    isSelected: Boolean,
    onSelectionChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    // Food category card implementation
}
```

#### NavigationBars
Bottom navigation and tab bars:

```kotlin
@Composable
fun BottomNavigationBar(
    currentRoute: String,
    onNavigateToRoute: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Bottom navigation implementation
}
```

### Authentication Components

#### AuthenticationButton
Reusable authentication button:

```kotlin
@Composable
fun AuthenticationButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    // Authentication button implementation
}
```

#### UserIdDropdown
Dropdown for selecting user IDs:

```kotlin
@Composable
fun UserIdDropdown(
    users: List<String>,
    selectedUserId: String?,
    onUserSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // User dropdown implementation
}
```

## Screens

### WelcomeScreen
The initial landing screen:

```kotlin
@Composable
fun WelcomeScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    // Welcome screen implementation with animations
}
```

### LoginScreen
User authentication screen:

```kotlin
@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onNavigateToRegister: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    // Login form with validation and error handling
}
```

### HomeScreen
Main dashboard screen:

```kotlin
@Composable
fun HomeScreen(
    userStatsViewModel: UserStatsViewModel,
    onNavigateToInsights: () -> Unit,
    onNavigateToCoach: () -> Unit
) {
    // Home dashboard with user stats and quick actions
}
```

### QuestionnaireScreen
Comprehensive questionnaire for user preferences:

```kotlin
@Composable
fun QuestionnaireScreen(
    questionnaireViewModel: QuestionnaireViewModel,
    onComplete: () -> Unit
) {
    // Multi-step questionnaire with progress tracking
}
```

### CoachScreen (NutriCoach)
AI-powered nutrition coaching:

```kotlin
@Composable
fun CoachScreen(
    genAIViewModel: GenAIViewModel,
    isDarkMode: Boolean
) {
    // Chat interface with AI coach
}
```

### ClinicianDashboardScreen
Dashboard for healthcare professionals:

```kotlin
@Composable
fun ClinicianDashboardScreen(
    clinicianDashboardViewModel: ClinicianDashboardViewModel
) {
    // Clinician dashboard with patient overview
}
```

## Utilities

### SharedPreferencesManager
Manages app preferences and user sessions:

```kotlin
class SharedPreferencesManager(context: Context) {
    enum class PreferenceKey(val key: String) {
        PREFERENCES_FILE("shared_preferences"),
        CURRENT_USER_ID("currentUserID"),
        KNOWN_USERS("known_users"),
    }
    
    fun setCurrentUser(userId: String)
    fun getCurrentUser(): String?
    fun getKnownUsers(): Set<String>
    fun logout()
}
```

**Usage:**
```kotlin
val prefsManager = SharedPreferencesManager.getInstance(context)
prefsManager.setCurrentUser("user123")
val currentUser = prefsManager.getCurrentUser()
```

### EncryptionUtils
Password hashing and validation:

```kotlin
object EncryptionUtils {
    fun hashPassword(password: String): String {
        // BCrypt password hashing
    }
    
    fun verifyPassword(password: String, hashedPassword: String): Boolean {
        // BCrypt password verification
    }
}
```

### AnimationUtils
Custom animations for UI transitions:

```kotlin
object AnimationUtils {
    fun slideInFromRight(): EnterTransition
    fun slideOutToLeft(): ExitTransition
    fun fadeInWithScale(): EnterTransition
}
```

### ScoreUtils
Nutrition score calculations:

```kotlin
object ScoreUtils {
    fun calculateNutritionScore(preferences: UserPreferences): Float
    fun getScoreCategory(score: Float): ScoreCategory
}
```

### ImageUtils
Image processing utilities:

```kotlin
object ImageUtils {
    fun processProfileImage(bitmap: Bitmap): Bitmap
    fun generatePlaceholderImage(text: String): ImageBitmap
}
```

### InitDataUtils
Database initialization with default data:

```kotlin
object InitDataUtils {
    suspend fun initializeDatabaseAsNeeded(
        context: Context,
        userRepository: UserRepository,
        // ... other repositories
    ) {
        // Initialize database with default data from CSV files
    }
}
```

## API Integration

### FruitVice API Integration

#### FruitViceService
Retrofit service interface:

```kotlin
interface FruitViceService {
    @GET("fruit/{name}")
    suspend fun getFruitDetails(@Path("name") fruitName: String): FruityViceApiDto
}
```

#### FruitViceRepo
Repository for fruit data:

```kotlin
class FruitViceRepo {
    suspend fun getFruitDetails(fruitName: String): Result<FruityViceApiDto> {
        // API call with error handling
    }
}
```

**Usage:**
```kotlin
class FruitViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val fruitRepo = FruitViceRepo()
    
    suspend fun searchFruit(name: String) {
        val result = fruitRepo.getFruitDetails(name)
        // Handle result
    }
}
```

## Theme & Styling

### Color Scheme
Defined in `Color.kt`:

```kotlin
// Light theme colors
val LightPrimary = Color(0xFF6750A4)
val LightSecondary = Color(0xFF625B71)
val LightBackground = Color(0xFFFFFBFE)

// Dark theme colors
val DarkPrimary = Color(0xFFD0BCFF)
val DarkSecondary = Color(0xFFCCC2DC)
val DarkBackground = Color(0xFF1C1B1F)
```

### Typography
Defined in `Type.kt`:

```kotlin
val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 57.sp
    ),
    // ... other text styles
)
```

### Theme Configuration
Main theme setup in `Theme.kt`:

```kotlin
@Composable
fun FIT2081A1Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> darkColorScheme(/* dark colors */)
        else -> lightColorScheme(/* light colors */)
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
```

## Usage Examples

### Complete User Registration Flow

```kotlin
// In AuthViewModel
suspend fun registerUser(
    userId: String,
    userName: String,
    phoneNumber: String,
    password: String
) {
    val hashedPassword = EncryptionUtils.hashPassword(password)
    val user = UserEntity(
        userId = userId,
        userName = userName,
        userPhoneNumber = phoneNumber,
        userHashedCredential = hashedPassword,
        userIsRegistered = true
    )
    
    userRepository.insertUser(user)
    sharedPreferencesManager.setCurrentUser(userId)
}
```

### Setting Up Navigation with ViewModels

```kotlin
@Composable
fun AppNavigation(
    viewModelProviderFactory: ViewModelProviderFactory,
    isDarkMode: Boolean,
    onToggleDarkMode: (Boolean) -> Unit
) {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = Screen.Welcome.route
    ) {
        composable(Screen.Login.route) {
            val authViewModel: AuthViewModel = viewModel(factory = viewModelProviderFactory)
            LoginScreen(
                authViewModel = authViewModel,
                onNavigateToHome = { navController.navigate(Screen.Home.route) }
            )
        }
        // ... other destinations
    }
}
```

### AI Chat Implementation

```kotlin
@Composable
fun ChatInterface(genAIViewModel: GenAIViewModel, userId: String) {
    val chatHistory by genAIViewModel.getChatHistory(userId).collectAsState(initial = emptyList())
    var messageText by remember { mutableStateOf("") }
    
    Column {
        LazyColumn {
            items(chatHistory) { message ->
                ChatMessageCard(message = message)
            }
        }
        
        Row {
            TextField(
                value = messageText,
                onValueChange = { messageText = it },
                placeholder = { Text("Type your message...") }
            )
            Button(
                onClick = {
                    genAIViewModel.sendMessage(messageText, userId)
                    messageText = ""
                }
            ) {
                Text("Send")
            }
        }
    }
}
```

### Custom Score Visualization

```kotlin
@Composable
fun UserScoreCard(userScore: UserScoreEntity) {
    Card {
        Column {
            Text(
                text = userScore.scoreTypeName,
                style = MaterialTheme.typography.headlineSmall
            )
            
            ScoreProgressBar(
                score = userScore.userScore,
                maxScore = 100f,
                color = when {
                    userScore.userScore >= 80 -> Color.Green
                    userScore.userScore >= 60 -> Color.Orange
                    else -> Color.Red
                }
            )
            
            Text(
                text = "${userScore.userScore}/100",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
```

---

## API Reference Summary

### Key Public Classes and Interfaces

1. **Application**: `NutriTrackApp`, `MainActivity`
2. **Navigation**: `Screen`, `AppNavigation`
3. **Database**: `AppDatabase`, All Entity classes, All DAO interfaces
4. **Repositories**: All Repository classes in `data.model.repository`
5. **ViewModels**: All ViewModel classes in `data.viewmodel`
6. **UI Components**: All Composable functions in `view.components`
7. **Screens**: All Screen Composables in `view.screens`
8. **Utilities**: All utility classes in `util` package
9. **API**: `FruitViceService`, `FruitViceRepo`
10. **Theme**: `FIT2081A1Theme`, Color definitions, Typography

### Dependencies Required

Add these to your `build.gradle.kts`:

```kotlin
dependencies {
    implementation("androidx.room:room-runtime:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    kapt("androidx.room:room-compiler:$room_version")
    
    implementation("androidx.compose.ui:ui:$compose_version")
    implementation("androidx.compose.material3:material3:$material3_version")
    implementation("androidx.navigation:navigation-compose:$nav_version")
    
    implementation("com.squareup.retrofit2:retrofit:$retrofit_version")
    implementation("com.squareup.retrofit2:converter-gson:$retrofit_version")
    
    implementation("io.coil-kt:coil-compose:$coil_version")
    implementation("org.mindrot:jbcrypt:$bcrypt_version")
}
```

This documentation provides a comprehensive overview of all public APIs, functions, and components in the NutriTrack application with practical usage examples and clear explanations of the architecture and design patterns used.