package com.example.fit2081a1_yang_xingyu_33533563.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.EaseInOutBack
import androidx.compose.animation.core.EaseOutQuint
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
import androidx.compose.ui.platform.LocalContext
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
import com.example.fit2081a1_yang_xingyu_33533563.util.SharedPreferencesManager

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
fun AppNavigation(viewModelProviderFactory: ViewModelProviderFactory) {
    // create ViewModel instances
    val authViewModel = viewModelProviderFactory.create(AuthViewModel::class.java)
    val insightsViewModel = viewModelProviderFactory.create(InsightsViewModel::class.java)
    val profileViewModel = viewModelProviderFactory.create(ProfileViewModel::class.java)
    val questionnaireViewModel = viewModelProviderFactory.create(QuestionnaireViewModel::class.java)

    val navController = rememberNavController()
    val context = LocalContext.current
    val prefManager = SharedPreferencesManager.getInstance(context)
    var currentUser = prefManager.getCurrentUser()

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
                    if (prefManager.getKnownUsers().contains(currentUser)) {
                        navController.navigate(Screen.Home.route)
                    } else {
                        navController.navigate(Screen.Questionnaire.route)
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
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route)
                },
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
            }
        ) {
            HomeScreen(
                viewModel = profileViewModel,
                onNavigate = { route -> navController.navigate(route) }
            )
        }
        //InsightScreen
        composable(Screen.Insights.route) {
            InsightScreen(
                viewModel = insightsViewModel,
                onNavigate = { route -> navController.navigate(route) }
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
            QuestionnaireScreen(
                viewModel = questionnaireViewModel,
                onBackClick = { navController.popBackStack() },
                onSaveComplete = { navController.navigate(Screen.Home.route) }
            )
        }

        //NutriCoachScreen
        composable(Screen.NutriCoach.route) {
            CoachScreen(
                onNavigate = { route -> navController.navigate(route) }
            )
        }

        //SettingsScreen
        composable(Screen.Settings.route) {
            SettingsScreen(
                profileViewModel = profileViewModel,
                onNavigate = { route -> navController.navigate(route) }
            )
        }
    }
}