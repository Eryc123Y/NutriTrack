package com.example.fit2081a1_yang_xingyu_33533563.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fit2081a1_yang_xingyu_33533563.ui.screens.CoachScreen
import com.example.fit2081a1_yang_xingyu_33533563.ui.screens.HomeScreen
import com.example.fit2081a1_yang_xingyu_33533563.ui.screens.InsightsScreen
import com.example.fit2081a1_yang_xingyu_33533563.ui.screens.LoginScreen
import com.example.fit2081a1_yang_xingyu_33533563.ui.screens.QuestionnaireScreen
import com.example.fit2081a1_yang_xingyu_33533563.ui.screens.SettingsScreen
import com.example.fit2081a1_yang_xingyu_33533563.ui.screens.WelcomeScreen

/**
 * Created by Xingyu Yang
 * This module defines navigation routes
 */


/**
 * Composable function for navigation of the app
 * @return Unit
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
 */
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "welcome",
        // default transition
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { slideInHorizontally(initialOffsetX = { -1000 }, animationSpec = tween(300)) },
        popExitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }, animationSpec = tween(300)) }

        // previously used transitions, only put it here for future reference
//        enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }, animationSpec = tween(300)) },
//        exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }, animationSpec = tween(300)) },

    ) {
        //Welcome
        composable("welcome") {
            WelcomeScreen(onNavigateToLogin = {
                navController.navigate("login")
            })
        }
        //LoginScreen
        composable("login") {
            LoginScreen(onNavigateToHome = {
                navController.navigate("home")
            })
        }
        //HomeScreen
        composable("home") {
            HomeScreen(
                onNavigate = { route -> navController.navigate(route) }
            )
        }
        //InsightScreen
        composable("insights") {
            InsightsScreen(
                onNavigate = { route -> navController.navigate(route) },
                onBackClick = { navController.popBackStack() }
            )
        }

        //QuestionnaireScreen
        composable("questionnaire") {
            QuestionnaireScreen(
                onNavigate = { route -> navController.navigate(route) },
                onBackClick = { navController.popBackStack() }
            )
        }

        // not implemented in A1
        composable("nutricoach") {
            CoachScreen(
                onNavigate = { route -> navController.navigate(route) },
                onBackClick = { navController.popBackStack() }
            )
        }

        // not implemented in A1
        composable("settings") {
            SettingsScreen(
                onNavigate = { route -> navController.navigate(route) },
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}