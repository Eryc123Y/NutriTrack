package com.example.fit2081a1_yang_xingyu_33533563.navigation

import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.EaseInOutBack
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fit2081a1_yang_xingyu_33533563.data.viewmodel.AuthViewModel
import com.example.fit2081a1_yang_xingyu_33533563.data.viewmodel.InsightsViewModel
import com.example.fit2081a1_yang_xingyu_33533563.data.viewmodel.ProfileViewModel
import com.example.fit2081a1_yang_xingyu_33533563.data.viewmodel.QuestionnaireViewModel
import com.example.fit2081a1_yang_xingyu_33533563.data.viewmodel.ViewModelProviderFactory
import com.example.fit2081a1_yang_xingyu_33533563.ui.screens.CoachScreen
import com.example.fit2081a1_yang_xingyu_33533563.ui.screens.HomeScreen
import com.example.fit2081a1_yang_xingyu_33533563.ui.screens.InsightScreen
import com.example.fit2081a1_yang_xingyu_33533563.ui.screens.LoginScreen
import com.example.fit2081a1_yang_xingyu_33533563.ui.screens.QuestionnaireScreen
import com.example.fit2081a1_yang_xingyu_33533563.ui.screens.RegisterScreen
import com.example.fit2081a1_yang_xingyu_33533563.ui.screens.SettingsScreen
import com.example.fit2081a1_yang_xingyu_33533563.ui.screens.WelcomeScreen
import kotlinx.coroutines.launch

/**
 * Created by Xingyu Yang
 * This module defines navigation routes
 */


/**
 * Composable function for navigation of the app
 *
 * Router:
 * WelcomeScreen -> LoginScreen -> MainScreens
 * MainScreens: HomeScreen, InsightsScreen, NutriCoachScreen, SettingsScreen
 * navigator names:
 * WelcomeScreen : welcome
 * LoginScreen : login
 * HomeScreen : home
 * InsightsScreen : insights
 * NutriCoachScreen : nutricoach
 * SettingsScreen : settings
 * @return Unit
 */
@Composable
fun AppNavigation(
    viewModelProviderFactory: ViewModelProviderFactory,
    isDarkMode: Boolean,
    onToggleDarkMode: (Boolean) -> Unit
) {
    val authViewModel: AuthViewModel = viewModel(factory = viewModelProviderFactory)
    val insightsViewModel: InsightsViewModel = viewModel(factory = viewModelProviderFactory)
    val profileViewModel: ProfileViewModel = viewModel(factory = viewModelProviderFactory)
    val questionnaireViewModel: QuestionnaireViewModel = viewModel(factory = viewModelProviderFactory)

    // Collect StateFlow values using correct collectAsState()
    val currentUserId by authViewModel.currentUserId.collectAsState()
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
    val isQuestionnaireCompleted by questionnaireViewModel.isQuestionnaireCompleted.collectAsState()
    val isEditing by questionnaireViewModel.isEditing.collectAsState()
    
    // Track if user canceled questionnaire editing
    val userCanceledEdit = remember { mutableStateOf(false) }
    
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Welcome.route,
        // Enhanced transitions for smoother navigation
        enterTransition = { 
            slideInHorizontally(
                initialOffsetX = { 800 },
                animationSpec = tween(400, easing = LinearOutSlowInEasing)
            ) + fadeIn(
                animationSpec = tween(350, 50, FastOutSlowInEasing)
            )
        },
        exitTransition = { 
            slideOutHorizontally(
                targetOffsetX = { -200 },
                animationSpec = tween(350, easing = EaseInOut)
            ) + fadeOut(
                animationSpec = tween(250)
            )
        },
        popEnterTransition = { 
            slideInHorizontally(
                initialOffsetX = { -200 },
                animationSpec = tween(400, easing = LinearOutSlowInEasing)
            ) + fadeIn(
                animationSpec = tween(350, 50, FastOutSlowInEasing)
            )
        },
        popExitTransition = { 
            slideOutHorizontally(
                targetOffsetX = { 800 },
                animationSpec = tween(350, easing = EaseInOut)
            ) + fadeOut(
                animationSpec = tween(250)
            )
        }
    ) {
        //Welcome
        composable(
            Screen.Welcome.route,
            enterTransition = {
                fadeIn(
                    animationSpec = tween(500, easing = LinearOutSlowInEasing)
                ) + scaleIn(
                    initialScale = 0.88f,
                    animationSpec = tween(500, easing = EaseInOutBack)
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(350)
                ) + scaleOut(
                    targetScale = 0.95f,
                    animationSpec = tween(350, easing = EaseInOut)
                )
            }
        ) {
            WelcomeScreen(onNavigateToLogin = {
                navController.navigate(Screen.Login.route)
            })
        }
        //LoginScreen
        composable(
            Screen.Login.route,
            enterTransition = {
                when (initialState.destination.route) {
                    Screen.Register.route -> {
                        // Coming back from register
                        slideInVertically(
                            initialOffsetY = { -200 },
                            animationSpec = tween(450, easing = LinearOutSlowInEasing)
                        ) + fadeIn(
                            animationSpec = tween(350, 50)
                        )
                    }
                    else -> {
                        // Default transition
                        slideInHorizontally(
                            initialOffsetX = { 800 },
                            animationSpec = tween(450, easing = LinearOutSlowInEasing)
                        ) + fadeIn(
                            animationSpec = tween(350, 50, FastOutSlowInEasing)
                        )
                    }
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    Screen.Register.route -> {
                        // Going to register
                        slideOutVertically(
                            targetOffsetY = { -200 },
                            animationSpec = tween(350, easing = EaseInOut)
                        ) + fadeOut(
                            animationSpec = tween(250)
                        )
                    }
                    else -> {
                        // Default exit
                        slideOutHorizontally(
                            targetOffsetX = { -200 },
                            animationSpec = tween(350, easing = EaseInOut)
                        ) + fadeOut(
                            animationSpec = tween(250)
                        )
                    }
                }
            }
        ) {
            LoginScreen(
                viewModel = authViewModel,
                onNavigateToHome = {
                    // Access collected state values instead of .value directly
                    if (currentUserId != null) {
                        // Load user preferences and check if questionnaire is completed
                        questionnaireViewModel.loadUserPreferences(currentUserId!!)
                        
                        // Use a proper coroutine scope with cleaned-up imports
                        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
                            // Add a delay to ensure preferences are loaded
                            kotlinx.coroutines.delay(300)
                            
                            // Check completion status 
                            if (questionnaireViewModel.isQuestionnaireCompleted.value) {
                                navController.navigate(Screen.Home.route) {
                                    popUpTo(Screen.Login.route) { inclusive = true }
                                }
                            } else {
                                // If questionnaire is not completed, navigate to questionnaire
                                navController.navigate(Screen.Questionnaire.route) {
                                    popUpTo(Screen.Login.route) { inclusive = true }
                                }
                            }
                        }
                    } else {
                        // If no user is found, navigate to questionnaire (as part of new user flow)
                        navController.navigate(Screen.Questionnaire.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                },
                onNavigateToRegisterScreen = {
                    navController.navigate(Screen.Register.route)
                },
            )
        }
        composable(
            Screen.Register.route,
            enterTransition = {
                slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(450, easing = LinearOutSlowInEasing)
                ) + fadeIn(
                    animationSpec = tween(350, 50)
                )
            },
            exitTransition = {
                slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(350, easing = EaseInOut)
                ) + fadeOut(
                    animationSpec = tween(250)
                )
            }
        ) {
            RegisterScreen(
                viewModel = authViewModel,
                onRegistrationComplete = {
                    // After registration, always go to questionnaire
                    questionnaireViewModel.resetCompleted() // Ensure the questionnaire is marked as not completed
                    navController.navigate(Screen.Questionnaire.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onBackToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        //HomeScreen
        composable(
            Screen.Home.route,
            enterTransition = {
                when (initialState.destination.route) {
                    Screen.Questionnaire.route -> {
                        // Coming from questionnaire with a special transition
                        scaleIn(
                            initialScale = 0.8f,
                            animationSpec = tween(500, easing = EaseInOutBack)
                        ) + fadeIn(
                            animationSpec = tween(450)
                        )
                    }
                    Screen.Insights.route, Screen.NutriCoach.route, Screen.Settings.route -> {
                        // Coming from another main screen (bottom navigation)
                        fadeIn(
                            animationSpec = tween(300, easing = FastOutSlowInEasing)
                        ) + scaleIn(
                            initialScale = 0.95f,
                            animationSpec = tween(300, easing = EaseInOutBack)
                        )
                    }
                    else -> {
                        // Default transition
                        slideInHorizontally(
                            initialOffsetX = { 800 },
                            animationSpec = tween(450, easing = LinearOutSlowInEasing)
                        ) + fadeIn(
                            animationSpec = tween(350, 50, FastOutSlowInEasing)
                        )
                    }
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    Screen.Insights.route, Screen.NutriCoach.route, Screen.Settings.route -> {
                        // Going to another main screen (bottom navigation)
                        fadeOut(
                            animationSpec = tween(200)
                        ) + scaleOut(
                            targetScale = 0.95f,
                            animationSpec = tween(200)
                        )
                    }
                    else -> {
                        // Default exit
                        slideOutHorizontally(
                            targetOffsetX = { -200 },
                            animationSpec = tween(350, easing = EaseInOut)
                        ) + fadeOut(
                            animationSpec = tween(250)
                        )
                    }
                }
            }
        ) {
            // Check if this is a new user from registration who hasn't completed questionnaire
            // Only perform this check when coming from a relevant screen
            val previousRoute = navController.previousBackStackEntry?.destination?.route
            
            // Reset the canceled edit flag when navigating to Home from somewhere other than Questionnaire
            LaunchedEffect(previousRoute) {
                // Don't reset the flag immediately after coming from questionnaire
                // This gives time for the userCanceledEdit flag to take effect
                if (previousRoute != Screen.Questionnaire.route && previousRoute != null) {
                    userCanceledEdit.value = false
                }
            }
            
            // Skip the redirection check if user explicitly canceled editing
            // or if coming directly from the questionnaire screen
            val shouldRedirectToQuestionnaire = previousRoute != Screen.Questionnaire.route && 
                                              isLoggedIn && 
                                              !userCanceledEdit.value && 
                                              !isQuestionnaireCompleted && 
                                              !isEditing && 
                                              currentUserId != null
                                              
            // Use LaunchedEffect with a key that includes all relevant state
            LaunchedEffect(shouldRedirectToQuestionnaire) {
                if (shouldRedirectToQuestionnaire) {
                    // Navigate to questionnaire only if all conditions are met
                    navController.navigate(Screen.Questionnaire.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            }
            
            HomeScreen(
                profileViewModel = profileViewModel,
                onNavigate = { route -> navController.navigate(route) },
                questionnaireViewModel = questionnaireViewModel
            )
        }
        //InsightScreen
        composable(
            Screen.Insights.route,
            enterTransition = {
                when (initialState.destination.route) {
                    Screen.Home.route, Screen.NutriCoach.route, Screen.Settings.route -> {
                        // Coming from another main screen (bottom navigation)
                        fadeIn(
                            animationSpec = tween(300, easing = FastOutSlowInEasing)
                        ) + scaleIn(
                            initialScale = 0.95f,
                            animationSpec = tween(300, easing = EaseInOutBack)
                        )
                    }
                    else -> {
                        // Default transition
                        slideInHorizontally(
                            initialOffsetX = { 800 },
                            animationSpec = tween(450, easing = LinearOutSlowInEasing)
                        ) + fadeIn(
                            animationSpec = tween(350, 50, FastOutSlowInEasing)
                        )
                    }
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    Screen.Home.route, Screen.NutriCoach.route, Screen.Settings.route -> {
                        // Going to another main screen (bottom navigation)
                        fadeOut(
                            animationSpec = tween(200)
                        ) + scaleOut(
                            targetScale = 0.95f,
                            animationSpec = tween(200)
                        )
                    }
                    else -> {
                        // Default exit
                        slideOutHorizontally(
                            targetOffsetX = { -200 },
                            animationSpec = tween(350, easing = EaseInOut)
                        ) + fadeOut(
                            animationSpec = tween(250)
                        )
                    }
                }
            }
        ) {
            InsightScreen(
                viewModel = insightsViewModel,
                onNavigate = { route -> navController.navigate(route) },
                onNavigateToCoach = {
                    navController.navigate(Screen.NutriCoach.route)
                }
            )
        }

        //QuestionnaireScreen
        composable(
            Screen.Questionnaire.route,
            enterTransition = {
                slideInVertically(
                    initialOffsetY = { -200 },
                    animationSpec = tween(450, easing = LinearOutSlowInEasing)
                ) + fadeIn(
                    animationSpec = tween(350, 50)
                )
            },
            exitTransition = {
                when (targetState.destination.route) {
                    Screen.Home.route -> {
                        // Going to home with special transition
                        scaleOut(
                            targetScale = 1.1f,
                            animationSpec = tween(350, easing = EaseInOut)
                        ) + fadeOut(
                            animationSpec = tween(250)
                        )
                    }
                    else -> {
                        // Default exit
                        slideOutVertically(
                            targetOffsetY = { -200 },
                            animationSpec = tween(350, easing = EaseInOut)
                        ) + fadeOut(
                            animationSpec = tween(250)
                        )
                    }
                }
            }
        ) {
            // Keep track of the previous route to determine if we're coming from Home (editing) or not
            val previousRoute = navController.previousBackStackEntry?.destination?.route
            val isEditMode = previousRoute == Screen.Home.route || isEditing
            
            // If coming from home, ensure the editing mode is set properly
            LaunchedEffect(previousRoute) {
                if (previousRoute == Screen.Home.route) {
                    questionnaireViewModel.setEditingMode(true)
                }
            }
            
            QuestionnaireScreen(
                viewModel = questionnaireViewModel,
                onBackClick = { 
                    // If we're in edit mode, allow going back to home
                    if (isEditMode) {
                        // Use the improved cancelEditing method for better state handling
                        questionnaireViewModel.cancelEditing(currentUserId ?: "")
                        userCanceledEdit.value = true
                        
                        // Navigate back to home
                        navController.popBackStack()
                    } else {
                        // For new users, don't allow going back - they must complete questionnaire
                        // Show a toast or message here if needed
                    }
                },
                onSaveComplete = { 
                    // Reset editing mode first
                    questionnaireViewModel.setEditingMode(false)
                    userCanceledEdit.value = false // Clear canceled flag on successful save
                    
                    // Navigate to home screen when questionnaire is completed
                    navController.navigate(Screen.Home.route) {
                        // Clear back stack to prevent returning to questionnaire
                        popUpTo(Screen.Questionnaire.route) { inclusive = true }
                    }
                },
                isEditMode = isEditMode
            )
        }

        //NutriCoachScreen
        composable(
            Screen.NutriCoach.route,
            enterTransition = {
                when (initialState.destination.route) {
                    Screen.Home.route, Screen.Insights.route, Screen.Settings.route -> {
                        // Coming from another main screen (bottom navigation)
                        fadeIn(
                            animationSpec = tween(300, easing = FastOutSlowInEasing)
                        ) + scaleIn(
                            initialScale = 0.95f,
                            animationSpec = tween(300, easing = EaseInOutBack)
                        )
                    }
                    else -> {
                        // Default transition
                        slideInHorizontally(
                            initialOffsetX = { 800 },
                            animationSpec = tween(450, easing = LinearOutSlowInEasing)
                        ) + fadeIn(
                            animationSpec = tween(350, 50, FastOutSlowInEasing)
                        )
                    }
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    Screen.Home.route, Screen.Insights.route, Screen.Settings.route -> {
                        // Going to another main screen (bottom navigation)
                        fadeOut(
                            animationSpec = tween(200)
                        ) + scaleOut(
                            targetScale = 0.95f,
                            animationSpec = tween(200)
                        )
                    }
                    else -> {
                        // Default exit
                        slideOutHorizontally(
                            targetOffsetX = { -200 },
                            animationSpec = tween(350, easing = EaseInOut)
                        ) + fadeOut(
                            animationSpec = tween(250)
                        )
                    }
                }
            }
        ) {
            CoachScreen(
                onNavigate = { route -> navController.navigate(route) }
            )
        }

        //SettingsScreen
        composable(
            Screen.Settings.route,
            enterTransition = {
                when (initialState.destination.route) {
                    Screen.Home.route, Screen.Insights.route, Screen.NutriCoach.route -> {
                        // Coming from another main screen (bottom navigation)
                        fadeIn(
                            animationSpec = tween(300, easing = FastOutSlowInEasing)
                        ) + scaleIn(
                            initialScale = 0.95f,
                            animationSpec = tween(300, easing = EaseInOutBack)
                        )
                    }
                    else -> {
                        // Default transition
                        slideInHorizontally(
                            initialOffsetX = { 800 },
                            animationSpec = tween(450, easing = LinearOutSlowInEasing)
                        ) + fadeIn(
                            animationSpec = tween(350, 50, FastOutSlowInEasing)
                        )
                    }
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    Screen.Home.route, Screen.Insights.route, Screen.NutriCoach.route -> {
                        // Going to another main screen (bottom navigation)
                        fadeOut(
                            animationSpec = tween(200)
                        ) + scaleOut(
                            targetScale = 0.95f,
                            animationSpec = tween(200)
                        )
                    }
                    else -> {
                        // Default exit
                        slideOutHorizontally(
                            targetOffsetX = { -200 },
                            animationSpec = tween(350, easing = EaseInOut)
                        ) + fadeOut(
                            animationSpec = tween(250)
                        )
                    }
                }
            }
        ) {
            SettingsScreen(
                profileViewModel = profileViewModel,
                authViewModel = authViewModel,
                onNavigate = { route -> navController.navigate(route) },
                onBackClick = { navController.popBackStack() },
                onLogoutToLogin = { navController.navigate(Screen.Login.route) },
                isDarkMode = isDarkMode,
                onToggleDarkMode = onToggleDarkMode
            )
        }
    }
}