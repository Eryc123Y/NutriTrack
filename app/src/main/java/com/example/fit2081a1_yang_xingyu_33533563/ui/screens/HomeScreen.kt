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


@Preview(showBackground = true)
@Composable
fun HomeScreen(
    onNavigate: (String) -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {},
        bottomBar = {
            BottomNavigationBar(
                currentRoute = "home",
                onNavigate = onNavigate
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Home Screen", style = TextStyle(fontSize = 24.sp))
        }
    }
}