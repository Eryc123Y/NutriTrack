package com.example.fit2081a1_yang_xingyu_33533563.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fit2081a1_yang_xingyu_33533563.ui.screens.HomeScreen
import com.example.fit2081a1_yang_xingyu_33533563.ui.screens.LoginScreen
import com.example.fit2081a1_yang_xingyu_33533563.ui.screens.WelcomeScreen

/**
 * Created by Xingyu Yang
 * This module defines navigation routes
 */

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
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
            HomeScreen()
        }
    }
}