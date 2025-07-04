# NutriTrack UI Components Documentation

## Overview

This document provides comprehensive documentation for all reusable UI components in the NutriTrack application. All components are built with Jetpack Compose and follow Material Design 3 principles.

## Core Components

### ScoreProgressBar

A customizable progress bar component for displaying nutrition scores with smooth animations.

```kotlin
@Composable
fun ScoreProgressBar(
    score: Float,
    maxScore: Float = 100f,
    color: Color = MaterialTheme.colorScheme.primary,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    strokeWidth: Dp = 8.dp,
    animationDuration: Int = 1000,
    modifier: Modifier = Modifier
)
```

**Parameters:**
- `score`: Current score value
- `maxScore`: Maximum possible score (default: 100f)
- `color`: Progress bar fill color
- `backgroundColor`: Progress bar background color  
- `strokeWidth`: Thickness of the progress bar
- `animationDuration`: Animation duration in milliseconds
- `modifier`: Modifier for styling and layout

**Usage Example:**
```kotlin
ScoreProgressBar(
    score = userScore.value,
    maxScore = 100f,
    color = when {
        userScore.value >= 80 -> Color.Green
        userScore.value >= 60 -> Color.Orange
        else -> Color.Red
    },
    modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
)
```

### TimePickerComponent

A modern time picker component for selecting meal times and preferences.

```kotlin
@Composable
fun TimePickerComponent(
    selectedTime: LocalTime,
    onTimeSelected: (LocalTime) -> Unit,
    label: String = "Select Time",
    enabled: Boolean = true,
    modifier: Modifier = Modifier
)
```

**Parameters:**
- `selectedTime`: Currently selected time
- `onTimeSelected`: Callback when time is selected
- `label`: Label text for the time picker
- `enabled`: Whether the component is interactive
- `modifier`: Modifier for styling and layout

**Usage Example:**
```kotlin
var breakfastTime by remember { mutableStateOf(LocalTime.of(8, 0)) }

TimePickerComponent(
    selectedTime = breakfastTime,
    onTimeSelected = { time -> 
        breakfastTime = time
        viewModel.updateBreakfastTime(time)
    },
    label = "Breakfast Time",
    modifier = Modifier.fillMaxWidth()
)
```

### FoodCategoryCard

A card component for displaying food categories with selection states.

```kotlin
@Composable
fun FoodCategoryCard(
    category: FoodCategoryDefinitionEntity,
    isSelected: Boolean,
    onSelectionChanged: (Boolean) -> Unit,
    preferenceLevel: Int = 3,
    onPreferenceLevelChanged: (Int) -> Unit = {},
    showPreferenceLevel: Boolean = false,
    modifier: Modifier = Modifier
)
```

**Parameters:**
- `category`: Food category data
- `isSelected`: Whether the category is selected
- `onSelectionChanged`: Callback for selection changes
- `preferenceLevel`: User preference level (1-5)
- `onPreferenceLevelChanged`: Callback for preference changes
- `showPreferenceLevel`: Whether to show preference slider
- `modifier`: Modifier for styling and layout

**Usage Example:**
```kotlin
FoodCategoryCard(
    category = foodCategory,
    isSelected = selectedCategories.contains(foodCategory.foodCategoryId),
    onSelectionChanged = { selected ->
        if (selected) {
            selectedCategories.add(foodCategory.foodCategoryId)
        } else {
            selectedCategories.remove(foodCategory.foodCategoryId)
        }
    },
    preferenceLevel = userPreferences[foodCategory.foodCategoryId] ?: 3,
    onPreferenceLevelChanged = { level ->
        viewModel.updatePreference(foodCategory.foodCategoryId, level)
    },
    showPreferenceLevel = true,
    modifier = Modifier.fillMaxWidth()
)
```

### InfoCard

A versatile information display card with icon support.

```kotlin
@Composable
fun InfoCard(
    title: String,
    subtitle: String? = null,
    value: String,
    icon: ImageVector? = null,
    onClick: (() -> Unit)? = null,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    modifier: Modifier = Modifier
)
```

**Parameters:**
- `title`: Main title text
- `subtitle`: Optional subtitle text
- `value`: Value to display prominently
- `icon`: Optional icon to display
- `onClick`: Optional click handler
- `backgroundColor`: Background color of the card
- `contentColor`: Text color
- `modifier`: Modifier for styling and layout

**Usage Example:**
```kotlin
InfoCard(
    title = "Daily Calories",
    subtitle = "Recommended intake",
    value = "2,150 kcal",
    icon = Icons.Default.LocalFireDepartment,
    onClick = { navController.navigate("calorie_details") },
    modifier = Modifier.fillMaxWidth()
)
```

### PersonaCard

A specialized card for displaying persona information with images and descriptions.

```kotlin
@Composable
fun PersonaCard(
    persona: PersonaEntity,
    isSelected: Boolean,
    onSelectionChanged: (Boolean) -> Unit,
    showDetails: Boolean = false,
    modifier: Modifier = Modifier
)
```

**Parameters:**
- `persona`: Persona entity data
- `isSelected`: Whether this persona is selected
- `onSelectionChanged`: Callback for selection changes
- `showDetails`: Whether to show detailed description
- `modifier`: Modifier for styling and layout

**Usage Example:**
```kotlin
PersonaCard(
    persona = healthEnthusiastPersona,
    isSelected = selectedPersona?.personaID == healthEnthusiastPersona.personaID,
    onSelectionChanged = { selected ->
        if (selected) {
            selectedPersona = healthEnthusiastPersona
            viewModel.selectPersona(healthEnthusiastPersona.personaID)
        }
    },
    showDetails = true,
    modifier = Modifier.fillMaxWidth()
)
```

### PersonaInfoModal

A modal dialog for displaying detailed persona information.

```kotlin
@Composable
fun PersonaInfoModal(
    persona: PersonaEntity?,
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
)
```

**Parameters:**
- `persona`: Persona to display (null hides modal)
- `isVisible`: Whether the modal is visible
- `onDismiss`: Callback when modal is dismissed
- `onSelect`: Callback when persona is selected
- `modifier`: Modifier for styling and layout

**Usage Example:**
```kotlin
var showPersonaModal by remember { mutableStateOf(false) }
var selectedPersona by remember { mutableStateOf<PersonaEntity?>(null) }

PersonaInfoModal(
    persona = selectedPersona,
    isVisible = showPersonaModal,
    onDismiss = { 
        showPersonaModal = false
        selectedPersona = null
    },
    onSelect = {
        viewModel.selectPersona(selectedPersona!!.personaID)
        showPersonaModal = false
    }
)
```

## Navigation Components

### BottomNavigationBar

A Material Design 3 bottom navigation bar for main app navigation.

```kotlin
@Composable
fun BottomNavigationBar(
    currentRoute: String,
    onNavigateToRoute: (String) -> Unit,
    isDarkMode: Boolean = false,
    modifier: Modifier = Modifier
)
```

**Parameters:**
- `currentRoute`: Currently active route
- `onNavigateToRoute`: Navigation callback
- `isDarkMode`: Whether dark mode is active
- `modifier`: Modifier for styling and layout

**Usage Example:**
```kotlin
BottomNavigationBar(
    currentRoute = currentBackStackEntry?.destination?.route ?: "",
    onNavigateToRoute = { route ->
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    },
    isDarkMode = isDarkMode,
    modifier = Modifier.fillMaxWidth()
)
```

### TopAppBar

A customizable top app bar with back navigation and actions.

```kotlin
@Composable
fun TopAppBar(
    title: String,
    navigationIcon: ImageVector? = Icons.Default.ArrowBack,
    onNavigationClick: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    modifier: Modifier = Modifier
)
```

**Usage Example:**
```kotlin
TopAppBar(
    title = "Settings",
    navigationIcon = Icons.Default.ArrowBack,
    onNavigationClick = { navController.popBackStack() },
    actions = {
        IconButton(onClick = { viewModel.saveSettings() }) {
            Icon(Icons.Default.Save, contentDescription = "Save")
        }
    }
)
```

## Authentication Components

### AuthenticationButton

A specialized button for authentication actions with loading states.

```kotlin
@Composable
fun AuthenticationButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    loadingText: String = "Please wait...",
    buttonType: AuthButtonType = AuthButtonType.Primary,
    modifier: Modifier = Modifier
)

enum class AuthButtonType {
    Primary,
    Secondary,
    Outlined
}
```

**Parameters:**
- `text`: Button text
- `onClick`: Click callback
- `enabled`: Whether button is enabled
- `isLoading`: Whether to show loading state
- `loadingText`: Text to show when loading
- `buttonType`: Visual style of the button
- `modifier`: Modifier for styling and layout

**Usage Example:**
```kotlin
val isLoading by authViewModel.isLoading.observeAsState(false)

AuthenticationButton(
    text = "Sign In",
    onClick = { 
        authViewModel.login(username, password)
    },
    enabled = username.isNotEmpty() && password.isNotEmpty(),
    isLoading = isLoading,
    loadingText = "Signing in...",
    buttonType = AuthButtonType.Primary,
    modifier = Modifier.fillMaxWidth()
)
```

### UserIdDropdown

A dropdown component for selecting user IDs with search functionality.

```kotlin
@Composable
fun UserIdDropdown(
    users: List<String>,
    selectedUserId: String?,
    onUserSelected: (String) -> Unit,
    label: String = "Select User",
    enabled: Boolean = true,
    searchable: Boolean = true,
    modifier: Modifier = Modifier
)
```

**Parameters:**
- `users`: List of available user IDs
- `selectedUserId`: Currently selected user ID
- `onUserSelected`: Callback when user is selected
- `label`: Label for the dropdown
- `enabled`: Whether dropdown is interactive
- `searchable`: Whether to enable search functionality
- `modifier`: Modifier for styling and layout

**Usage Example:**
```kotlin
val registeredUsers by authViewModel.registeredUsers.observeAsState(emptyList())
var selectedUserId by remember { mutableStateOf<String?>(null) }

UserIdDropdown(
    users = registeredUsers.map { it.userId },
    selectedUserId = selectedUserId,
    onUserSelected = { userId ->
        selectedUserId = userId
        authViewModel.setSelectedUser(userId)
    },
    label = "Choose your account",
    searchable = true,
    modifier = Modifier.fillMaxWidth()
)
```

## Animation Components

### PageTransitions

Custom page transition animations for navigation.

```kotlin
object PageTransitions {
    fun slideInFromRight(): EnterTransition = slideInHorizontally(
        initialOffsetX = { fullWidth -> fullWidth },
        animationSpec = tween(300)
    ) + fadeIn(animationSpec = tween(300))
    
    fun slideOutToLeft(): ExitTransition = slideOutHorizontally(
        targetOffsetX = { fullWidth -> -fullWidth },
        animationSpec = tween(300)
    ) + fadeOut(animationSpec = tween(300))
    
    fun fadeInWithScale(): EnterTransition = fadeIn(
        animationSpec = tween(300)
    ) + scaleIn(
        initialScale = 0.8f,
        animationSpec = tween(300)
    )
    
    fun slideUpFromBottom(): EnterTransition = slideInVertically(
        initialOffsetY = { fullHeight -> fullHeight },
        animationSpec = tween(300)
    ) + fadeIn(animationSpec = tween(300))
}
```

**Usage Example:**
```kotlin
composable(
    route = Screen.Home.route,
    enterTransition = { PageTransitions.slideInFromRight() },
    exitTransition = { PageTransitions.slideOutToLeft() }
) {
    HomeScreen(/* ... */)
}
```

### LoadingSpinner

A customizable loading spinner component.

```kotlin
@Composable
fun LoadingSpinner(
    size: Dp = 48.dp,
    color: Color = MaterialTheme.colorScheme.primary,
    strokeWidth: Dp = 4.dp,
    modifier: Modifier = Modifier
)
```

**Usage Example:**
```kotlin
if (uiState is UiState.Loading) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        LoadingSpinner(
            size = 64.dp,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
```

## Form Components

### ValidatedTextField

A text field with built-in validation and error display.

```kotlin
@Composable
fun ValidatedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String = "",
    validator: (String) -> ValidationResult = { ValidationResult.Valid },
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier
)

sealed class ValidationResult {
    object Valid : ValidationResult()
    data class Invalid(val errorMessage: String) : ValidationResult()
}
```

**Usage Example:**
```kotlin
var email by remember { mutableStateOf("") }

ValidatedTextField(
    value = email,
    onValueChange = { email = it },
    label = "Email Address",
    placeholder = "Enter your email",
    validator = { value ->
        if (android.util.Patterns.EMAIL_ADDRESS.matcher(value).matches()) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid("Please enter a valid email address")
        }
    },
    keyboardType = KeyboardType.Email,
    leadingIcon = {
        Icon(Icons.Default.Email, contentDescription = null)
    },
    modifier = Modifier.fillMaxWidth()
)
```

### PasswordField

A specialized text field for password input with visibility toggle.

```kotlin
@Composable
fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String = "Password",
    placeholder: String = "Enter password",
    validator: (String) -> ValidationResult = { ValidationResult.Valid },
    modifier: Modifier = Modifier
)
```

**Usage Example:**
```kotlin
var password by remember { mutableStateOf("") }

PasswordField(
    value = password,
    onValueChange = { password = it },
    label = "Password",
    validator = { value ->
        when {
            value.length < 8 -> ValidationResult.Invalid("Password must be at least 8 characters")
            !value.any { it.isDigit() } -> ValidationResult.Invalid("Password must contain at least one number")
            !value.any { it.isUpperCase() } -> ValidationResult.Invalid("Password must contain at least one uppercase letter")
            else -> ValidationResult.Valid
        }
    },
    modifier = Modifier.fillMaxWidth()
)
```

## Chart Components

### NutritionChart

A chart component for displaying nutrition data over time.

```kotlin
@Composable
fun NutritionChart(
    data: List<ScoreData>,
    chartType: ChartType = ChartType.Line,
    timeRange: TimeRange = TimeRange.Week,
    showGridLines: Boolean = true,
    animationDuration: Int = 1000,
    modifier: Modifier = Modifier
)

enum class ChartType { Line, Bar, Area }
enum class TimeRange { Day, Week, Month, Year }

data class ScoreData(
    val timestamp: Long,
    val value: Float,
    val scoreType: String
)
```

**Usage Example:**
```kotlin
val scoreHistory by insightsViewModel.scoreHistory.observeAsState(emptyList())

NutritionChart(
    data = scoreHistory,
    chartType = ChartType.Line,
    timeRange = TimeRange.Week,
    showGridLines = true,
    modifier = Modifier
        .fillMaxWidth()
        .height(200.dp)
)
```

## Best Practices

### Component Composition
- Always use `@Composable` functions for UI components
- Pass data down and events up (unidirectional data flow)
- Use `remember` for state that should survive recomposition
- Use `LaunchedEffect` for side effects

### State Management
```kotlin
@Composable
fun MyComponent() {
    // ✅ Good: Use remember for local state
    var isExpanded by remember { mutableStateOf(false) }
    
    // ✅ Good: Use viewModel for business logic
    val viewModel: MyViewModel = viewModel()
    val uiState by viewModel.uiState.observeAsState()
    
    // ✅ Good: Handle loading states
    when (uiState) {
        is UiState.Loading -> LoadingSpinner()
        is UiState.Success -> SuccessContent(uiState.data)
        is UiState.Error -> ErrorContent(uiState.message)
    }
}
```

### Accessibility
- Always provide `contentDescription` for images and icons
- Use semantic properties for screen readers
- Ensure sufficient color contrast
- Support keyboard navigation

```kotlin
Icon(
    imageVector = Icons.Default.Favorite,
    contentDescription = "Add to favorites", // ✅ Accessibility
    modifier = Modifier.semantics {
        role = Role.Button
        stateDescription = if (isFavorite) "Favorited" else "Not favorited"
    }
)
```

### Performance
- Use `LazyColumn` and `LazyRow` for large lists
- Implement proper key management for list items
- Use `derivedStateOf` for computed state
- Avoid unnecessary recompositions with stable parameters

```kotlin
@Composable
fun OptimizedList(items: List<Item>) {
    LazyColumn {
        items(
            items = items,
            key = { item -> item.id } // ✅ Stable keys
        ) { item ->
            ItemCard(
                item = item,
                onClick = { /* handle click */ }
            )
        }
    }
}
```

This comprehensive UI components documentation provides detailed information about all reusable components in the NutriTrack application, including usage examples, parameters, and best practices.