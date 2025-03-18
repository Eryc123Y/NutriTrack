package com.example.fit2081a1_yang_xingyu_33533563.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fit2081a1_yang_xingyu_33533563.ui.screens.HomeScreen
import com.example.fit2081a1_yang_xingyu_33533563.ui.screens.InsightsScreen
import com.example.fit2081a1_yang_xingyu_33533563.ui.screens.LoginScreen
import com.example.fit2081a1_yang_xingyu_33533563.ui.screens.NutriCoachScreen
import com.example.fit2081a1_yang_xingyu_33533563.ui.screens.SettingsScreen
import com.example.fit2081a1_yang_xingyu_33533563.ui.screens.WelcomeScreen

/**
 * Created by Xingyu Yang
 * This module defines navigation routes
 */

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "welcome"
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
                onNavigate = { route -> navController.navigate(route) },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("insights") {
            InsightsScreen(
                onNavigate = { route -> navController.navigate(route) },
                onBackClick = { navController.popBackStack() }
            )
        }

        // not implemented in A1
        composable("nutricoach") {
            NutriCoachScreen(
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