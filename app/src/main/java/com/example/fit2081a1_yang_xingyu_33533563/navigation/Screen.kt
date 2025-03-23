package com.example.fit2081a1_yang_xingyu_33533563.navigation

/**
 * Created by Xingyu Yang
 * This file contains enum class Screen that map a screen component to a router name string
 */

/**
 * Enum class Screen、
 * Screen is used to map a screen component to a router name string
 * Used for navigation in the app
 * @param route: String
 */
enum class Screen(val route: String) {
    Login("login"),
    Home("home"),
    Insights("insights"),
    NutriCoach("nutricoach"),
    Settings("settings")
}