package com.example.fit2081a1_yang_xingyu_33533563.navigation

import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.EaseInOutBack
import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fit2081a1_yang_xingyu_33533563.data.viewmodel.AuthViewModel
import com.example.fit2081a1_yang_xingyu_33533563.data.viewmodel.InsightsViewModel
import com.example.fit2081a1_yang_xingyu_33533563.data.viewmodel.ProfileViewModel
import com.example.fit2081a1_yang_xingyu_33533563.data.viewmodel.QuestionnaireViewModel
import com.example.fit2081a1_yang_xingyu_33533563.data.viewmodel.ViewModelProviderFactory
import com.example.fit2081a1_yang_xingyu_33533563.data.viewmodel.GenAIViewModel
import com.example.fit2081a1_yang_xingyu_33533563.data.viewmodel.UserStatsViewModel
import com.example.fit2081a1_yang_xingyu_33533563.view.screens.CoachScreen
import com.example.fit2081a1_yang_xingyu_33533563.view.screens.HomeScreen
import com.example.fit2081a1_yang_xingyu_33533563.view.screens.InsightScreen
import com.example.fit2081a1_yang_xingyu_33533563.view.screens.LoginScreen
import com.example.fit2081a1_yang_xingyu_33533563.view.screens.QuestionnaireScreen
import com.example.fit2081a1_yang_xingyu_33533563.view.screens.RegisterScreen
import com.example.fit2081a1_yang_xingyu_33533563.view.screens.SettingsScreen
import com.example.fit2081a1_yang_xingyu_33533563.view.screens.WelcomeScreen
import com.example.fit2081a1_yang_xingyu_33533563.data.viewmodel.FruitViewModel
import com.example.fit2081a1_yang_xingyu_33533563.util.SharedPreferencesManager
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import com.example.fit2081a1_yang_xingyu_33533563.data.viewmodel.ClinicianDashboardViewModel
import com.example.fit2081a1_yang_xingyu_33533563.view.screens.ClinicianDashboardScreen

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
    val fruitViewModel: FruitViewModel = viewModel(factory = viewModelProviderFactory)
    val genAIViewModel: GenAIViewModel = viewModel(factory = viewModelProviderFactory)
    val userStatsViewModel: UserStatsViewModel = viewModel(factory = viewModelProviderFactory)
    val clinicianDashboardViewModel: ClinicianDashboardViewModel = viewModel(factory = viewModelProviderFactory)
    val context = LocalContext.current

    // Collect StateFlow values using correct collectAsState()
    val currentUserId by authViewModel.currentUserId.collectAsState()
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
    val isQuestionnaireCompleted by questionnaireViewModel.isQuestionnaireCompleted.collectAsState()
    val isEditing by questionnaireViewModel.isEditing.collectAsState()
    
    // Navigation controller
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Welcome.route,
        // Enhanced transitions for smoother navigation
        enterTransition = { 
            slideInHorizontally(
                initialOffsetX = { it }, // Adjusted for consistency, lambda for full width
                animationSpec = tween(500, easing = FastOutSlowInEasing) // Increased duration, FastOutSlowInEasing for smooth arrival
            ) + fadeIn(
                animationSpec = tween(450, easing = FastOutSlowInEasing) // Increased duration
            )
        },
        exitTransition = { 
            slideOutHorizontally(
                targetOffsetX = { -it / 3 }, // Slide out less aggressively
                animationSpec = tween(450, easing = EaseInOut) // Increased duration, EaseInOut for symmetry
            ) + fadeOut(
                animationSpec = tween(350, easing = EaseInOut) // Increased duration
            )
        },
        popEnterTransition = { 
            slideInHorizontally(
                initialOffsetX = { -it / 3 }, // Slide in less aggressively
                animationSpec = tween(500, easing = FastOutSlowInEasing) // Increased duration
            ) + fadeIn(
                animationSpec = tween(450, easing = FastOutSlowInEasing) // Increased duration
            )
        },
        popExitTransition = { 
            slideOutHorizontally(
                targetOffsetX = { it }, // Adjusted for consistency
                animationSpec = tween(450, easing = EaseInOut) // Increased duration
            ) + fadeOut(
                animationSpec = tween(350, easing = EaseInOut) // Increased duration
            )
        }
    ) {
        //Welcome
        composable(
            Screen.Welcome.route,
            enterTransition = {
                fadeIn(
                    animationSpec = tween(600, easing = FastOutSlowInEasing) // Increased duration
                ) + scaleIn(
                    initialScale = 0.92f, // Slightly less aggressive scale
                    animationSpec = tween(600, easing = EaseInOutBack) // Increased duration, kept EaseInOutBack for a bit of bounce
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(450, easing = EaseInOut) // Increased duration
                ) + scaleOut(
                    targetScale = 0.95f, // Maintained
                    animationSpec = tween(450, easing = EaseInOut) // Increased duration
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
                            initialOffsetY = { -it / 2 }, // Less aggressive slide
                            animationSpec = tween(500, easing = FastOutSlowInEasing) // Increased duration
                        ) + fadeIn(
                            animationSpec = tween(450, delayMillis = 50, easing = FastOutSlowInEasing) // Increased duration, kept delay
                        )
                    }
                    else -> {
                        // Default transition
                        slideInHorizontally(
                            initialOffsetX = { it }, // Consistent full width
                            animationSpec = tween(500, easing = FastOutSlowInEasing) // Increased duration
                        ) + fadeIn(
                            animationSpec = tween(450, delayMillis = 50, easing = FastOutSlowInEasing) // Increased duration, kept delay
                        )
                    }
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    Screen.Register.route -> {
                        // Going to register
                        slideOutVertically(
                            targetOffsetY = { -it / 2 }, // Less aggressive slide
                            animationSpec = tween(450, easing = EaseInOut) // Increased duration
                        ) + fadeOut(
                            animationSpec = tween(350, easing = EaseInOut) // Increased duration
                        )
                    }
                    else -> {
                        // Default exit
                        slideOutHorizontally(
                            targetOffsetX = { -it / 3 }, // Less aggressive slide
                            animationSpec = tween(450, easing = EaseInOut) // Increased duration
                        ) + fadeOut(
                            animationSpec = tween(350, easing = EaseInOut) // Increased duration
                        )
                    }
                }
            }
        ) {
            LoginScreen(
                viewModel = authViewModel,
                onNavigateToHome = {
                    // Check if user is logged in and proceed with navigation
                    if (currentUserId != null) {
                        // Launch a coroutine to check questionnaire completion status
                        CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
                            val isCompleted = questionnaireViewModel.loadUserPreferencesAndCheckCompletion(currentUserId!!)
                            
                            // Navigate based on questionnaire completion status
                            if (isCompleted) {
                                navController.navigate(Screen.Home.route) {
                                    popUpTo(Screen.Login.route) { inclusive = true }
                                }
                            } else {
                                navController.navigate(Screen.Questionnaire.route) {
                                    popUpTo(Screen.Login.route) { inclusive = true }
                                }
                            }
                        }
                    } else {
                        // If no user is found (error case), navigate to questionnaire as fallback
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
                    initialOffsetY = { it }, // Full height slide
                    animationSpec = tween(500, easing = FastOutSlowInEasing) // Increased duration
                ) + fadeIn(
                    animationSpec = tween(450, delayMillis = 50, easing = FastOutSlowInEasing) // Increased duration, kept delay
                )
            },
            exitTransition = {
                slideOutVertically(
                    targetOffsetY = { it }, // Full height slide
                    animationSpec = tween(450, easing = EaseInOut) // Increased duration
                ) + fadeOut(
                    animationSpec = tween(350, easing = EaseInOut) // Increased duration
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
                            initialScale = 0.85f, // Slightly adjusted
                            animationSpec = tween(550, easing = EaseInOutBack) // Increased duration
                        ) + fadeIn(
                            animationSpec = tween(500, easing = FastOutSlowInEasing) // Increased duration
                        )
                    }
                    Screen.Insights.route, Screen.NutriCoach.route, Screen.Settings.route -> {
                        // Coming from another main screen (bottom navigation)
                        fadeIn(
                            animationSpec = tween(400, easing = FastOutSlowInEasing) // Increased duration
                        ) + scaleIn(
                            initialScale = 0.95f, // Maintained
                            animationSpec = tween(400, easing = EaseInOutBack) // Increased duration
                        )
                    }
                    else -> {
                        // Default transition
                        slideInHorizontally(
                            initialOffsetX = { it }, // Full width slide
                            animationSpec = tween(500, easing = FastOutSlowInEasing) // Increased duration
                        ) + fadeIn(
                            animationSpec = tween(450, delayMillis = 50, easing = FastOutSlowInEasing) // Increased duration
                        )
                    }
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    Screen.Insights.route, Screen.NutriCoach.route, Screen.Settings.route -> {
                        // Going to another main screen (bottom navigation)
                        fadeOut(
                            animationSpec = tween(300, easing = EaseInOut) // Increased duration
                        ) + scaleOut(
                            targetScale = 0.95f, // Maintained
                            animationSpec = tween(300, easing = EaseInOut) // Increased duration
                        )
                    }
                    else -> {
                        // Default exit
                        slideOutHorizontally(
                            targetOffsetX = { -it / 3 }, // Less aggressive slide
                            animationSpec = tween(450, easing = EaseInOut) // Increased duration
                        ) + fadeOut(
                            animationSpec = tween(350, easing = EaseInOut) // Increased duration
                        )
                    }
                }
            }
        ) {
            // Collect current states
            val currentUserIdState by authViewModel.currentUserId.collectAsState()
            val isQuestCompleted by questionnaireViewModel.isQuestionnaireCompleted.collectAsState()
            val isEditingState by questionnaireViewModel.isEditing.collectAsState()
            val isLoggedInState by authViewModel.isLoggedIn.collectAsState()
            
            // Simple check for whether we need to redirect to questionnaire
            val needsQuestionnaireRedirect = isLoggedInState && 
                                            !isQuestCompleted && 
                                            !isEditingState &&
                                            currentUserIdState != null
            
            // Simple LaunchedEffect to handle navigation redirection
            LaunchedEffect(needsQuestionnaireRedirect) {
                if (needsQuestionnaireRedirect) {
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
                            animationSpec = tween(400, easing = FastOutSlowInEasing) // Increased duration
                        ) + scaleIn(
                            initialScale = 0.95f, // Maintained
                            animationSpec = tween(400, easing = EaseInOutBack) // Increased duration
                        )
                    }
                    else -> {
                        // Default transition
                        slideInHorizontally(
                            initialOffsetX = { it }, // Full width slide
                            animationSpec = tween(500, easing = FastOutSlowInEasing) // Increased duration
                        ) + fadeIn(
                            animationSpec = tween(450, delayMillis = 50, easing = FastOutSlowInEasing) // Increased duration
                        )
                    }
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    Screen.Home.route, Screen.NutriCoach.route, Screen.Settings.route -> {
                        // Going to another main screen (bottom navigation)
                        fadeOut(
                            animationSpec = tween(300, easing = EaseInOut) // Increased duration
                        ) + scaleOut(
                            targetScale = 0.95f, // Maintained
                            animationSpec = tween(300, easing = EaseInOut) // Increased duration
                        )
                    }
                    else -> {
                        // Default exit
                        slideOutHorizontally(
                            targetOffsetX = { -it / 3 }, // Less aggressive slide
                            animationSpec = tween(450, easing = EaseInOut) // Increased duration
                        ) + fadeOut(
                            animationSpec = tween(350, easing = EaseInOut) // Increased duration
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
                fadeIn(animationSpec = tween(400, easing = FastOutSlowInEasing)) // Changed from instant to subtle fade
            },
            exitTransition = {
                when (targetState.destination.route) {
                    Screen.Home.route -> {
                        // Going to home with special transition
                        scaleOut(
                            targetScale = 1.05f, // Slightly less aggressive scale out
                            animationSpec = tween(450, easing = EaseInOut) // Increased duration
                        ) + fadeOut(
                            animationSpec = tween(350, easing = EaseInOut) // Increased duration
                        )
                    }
                    else -> {
                        // Default exit
                        slideOutVertically(
                            targetOffsetY = { -it / 2 }, // Less aggressive slide
                            animationSpec = tween(450, easing = EaseInOut) // Increased duration
                        ) + fadeOut(
                            animationSpec = tween(350, easing = EaseInOut) // Increased duration
                        )
                    }
                }
            }
        ) {
            // Determine if we came directly from HomeScreen (edit mode)
            val previousRoute = navController.previousBackStackEntry?.destination?.route
            val isInEditMode = previousRoute == Screen.Home.route || questionnaireViewModel.isEditing.collectAsState().value
            
            QuestionnaireScreen(
                viewModel = questionnaireViewModel,
                onBackClick = {
                    // Simply go back to previous screen
                    navController.popBackStack()
                },
                onSaveComplete = { 
                    // Reset editing mode and navigate to home
                    questionnaireViewModel.setEditingMode(false)
                    
                    // Navigate to home screen when questionnaire is completed
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Questionnaire.route) { inclusive = true }
                    }
                },
                isEditMode = isInEditMode
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
                            animationSpec = tween(400, easing = FastOutSlowInEasing) // Increased duration
                        ) + scaleIn(
                            initialScale = 0.95f, // Maintained
                            animationSpec = tween(400, easing = EaseInOutBack) // Increased duration
                        )
                    }
                    else -> {
                        // Default transition
                        slideInHorizontally(
                            initialOffsetX = { it }, // Full width slide
                            animationSpec = tween(500, easing = FastOutSlowInEasing) // Increased duration
                        ) + fadeIn(
                            animationSpec = tween(450, delayMillis = 50, easing = FastOutSlowInEasing) // Increased duration
                        )
                    }
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    Screen.Home.route, Screen.Insights.route, Screen.Settings.route -> {
                        // Going to another main screen (bottom navigation)
                        fadeOut(
                            animationSpec = tween(300, easing = EaseInOut) // Increased duration
                        ) + scaleOut(
                            targetScale = 0.95f, // Maintained
                            animationSpec = tween(300, easing = EaseInOut) // Increased duration
                        )
                    }
                    else -> {
                        // Default exit
                        slideOutHorizontally(
                            targetOffsetX = { -it / 3 }, // Less aggressive slide
                            animationSpec = tween(450, easing = EaseInOut) // Increased duration
                        ) + fadeOut(
                            animationSpec = tween(350, easing = EaseInOut) // Increased duration
                        )
                    }
                }
            }
        ) {
            CoachScreen(
                onNavigate = { route -> navController.navigate(route) },
                onBackClick = { navController.popBackStack() },
                fruitViewModel = fruitViewModel,
                genAIViewModel = genAIViewModel,
                sharedPreferencesManager = SharedPreferencesManager.getInstance(context = context)
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
                            animationSpec = tween(400, easing = FastOutSlowInEasing) // Increased duration
                        ) + scaleIn(
                            initialScale = 0.95f, // Maintained
                            animationSpec = tween(400, easing = EaseInOutBack) // Increased duration
                        )
                    }
                    else -> {
                        // Default transition
                        slideInHorizontally(
                            initialOffsetX = { it }, // Full width slide
                            animationSpec = tween(500, easing = FastOutSlowInEasing) // Increased duration
                        ) + fadeIn(
                            animationSpec = tween(450, delayMillis = 50, easing = FastOutSlowInEasing) // Increased duration
                        )
                    }
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    Screen.Home.route, Screen.Insights.route, Screen.NutriCoach.route -> {
                        // Going to another main screen (bottom navigation)
                        fadeOut(
                            animationSpec = tween(300, easing = EaseInOut) // Increased duration
                        ) + scaleOut(
                            targetScale = 0.95f, // Maintained
                            animationSpec = tween(300, easing = EaseInOut) // Increased duration
                        )
                    }
                    else -> {
                        // Default exit
                        slideOutHorizontally(
                            targetOffsetX = { -it / 3 }, // Less aggressive slide
                            animationSpec = tween(450, easing = EaseInOut) // Increased duration
                        ) + fadeOut(
                            animationSpec = tween(350, easing = EaseInOut) // Increased duration
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
        
        //ClinicianDashboardScreen
        composable(
            Screen.ClinicianDashboard.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it }, // Full width slide
                    animationSpec = tween(500, easing = FastOutSlowInEasing) // Increased duration
                ) + fadeIn(
                    animationSpec = tween(450, delayMillis = 50, easing = FastOutSlowInEasing) // Increased duration
                )
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it / 3 }, // Less aggressive slide
                    animationSpec = tween(450, easing = EaseInOut) // Increased duration
                ) + fadeOut(
                    animationSpec = tween(350, easing = EaseInOut) // Increased duration
                )
            }
        ) {
            ClinicianDashboardScreen(
                viewModel = clinicianDashboardViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}