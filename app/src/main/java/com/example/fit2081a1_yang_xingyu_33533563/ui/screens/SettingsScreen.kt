package com.example.fit2081a1_yang_xingyu_33533563.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.fit2081a1_yang_xingyu_33533563.ui.components.BottomNavigationBar
import com.example.fit2081a1_yang_xingyu_33533563.ui.components.TopNavigationBar

/**
 * Created by Eric
 * This module contains setting layout
 */


/**
 * Composable function for the Settings screen
 * @param onNavigate callback function for navigating to other screens
 * @param onBackClick callback function for navigating back to the previous screen
 * @return Unit
 */
@Preview(showSystemUi = true)
@Composable
fun SettingsScreen(
    onNavigate: (String) -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    Scaffold (
        topBar = {
            TopNavigationBar(
                title = "Settings",
                showBackButton = false,
                onBackButtonClick = onBackClick
            )
        },
        bottomBar = {
            BottomNavigationBar(
                currentRoute = "settings",
                onNavigate = onNavigate
            )
        }
    ){ innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Settings", style = TextStyle(fontSize = 24.sp))
        }
    }
}