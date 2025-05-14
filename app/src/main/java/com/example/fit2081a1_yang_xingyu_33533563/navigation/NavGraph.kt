package com.example.fit2081a1_yang_xingyu_33533563.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
        // default transition
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { slideInHorizontally(initialOffsetX = { -1200 }, animationSpec = tween(300)) },
        popExitTransition = { slideOutHorizontally(targetOffsetX = { 1200 }, animationSpec = tween(300)) }

        // previously used transitions, only put it here for future reference
//        enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }, animationSpec = tween(300)) },
//        exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }, animationSpec = tween(300)) },

    ) {
        //Welcome
        composable(Screen.Welcome.route) {
            WelcomeScreen(onNavigateToLogin = {
                navController.navigate(Screen.Login.route)
            })
        }
        //LoginScreen
        composable(Screen.Login.route) {
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
        composable(Screen.Register.route) {
            RegisterScreen(
                viewModel = authViewModel,
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route)
                },
            )

        }
        //HomeScreen
        composable(Screen.Home.route) {
            HomeScreen(
                profileViewModel = profileViewModel,
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
        composable(Screen.Questionnaire.route) {
            QuestionnaireScreen(
                viewModel = questionnaireViewModel,
                onBackClick = { navController.popBackStack() },
                onSaveComplete = {}
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