package com.example.fit2081a1_yang_xingyu_33533563.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.fit2081a1_yang_xingyu_33533563.navigation.Screen
import com.example.fit2081a1_yang_xingyu_33533563.ui.components.BottomNavigationBar
import com.example.fit2081a1_yang_xingyu_33533563.ui.components.ScoreInterface
import com.example.fit2081a1_yang_xingyu_33533563.ui.components.TopNavigationBar
import com.example.fit2081a1_yang_xingyu_33533563.util.SharedPreferencesManager

@Preview(showBackground = true)
@Composable
fun InsightsScreen(
    onNavigate: (String) -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    Scaffold (
        topBar = {
            TopNavigationBar(
                title = "Insights: Food Score",
                showBackButton = false,
                onBackButtonClick = onBackClick
            )
        },
        bottomBar = {
            BottomNavigationBar(
                currentRoute = Screen.Insights.route,
                onNavigate = onNavigate
            )
        }
    ){ innerPadding ->
        Surface(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            val context = LocalContext.current
            val prefManager = SharedPreferencesManager(context)
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val userID = prefManager.getCurrentUser()
                ScoreInterface(userID.toString())
            }
        }
    }
}

@Composable
fun ScoreProgressBars() {

}

@Composable
fun ShareAndImproveDiet() {

}