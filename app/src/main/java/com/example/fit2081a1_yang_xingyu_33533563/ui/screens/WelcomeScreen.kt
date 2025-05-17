package com.example.fit2081a1_yang_xingyu_33533563.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fit2081a1_yang_xingyu_33533563.R
import kotlinx.coroutines.delay

@Preview(showBackground = true)
@Composable
fun WelcomeScreen(onNavigateToLogin: () -> Unit = {}) {
    // Navigate to login after a delay
    LaunchedEffect(Unit) {
        delay(3000) // 3 seconds delay
        onNavigateToLogin()
    }

    Surface (
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFE8F5E9)) // Light green background
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center // Center content
        ) {
            Image(
                painter = painterResource(id = R.drawable.splash_logo), // Use splash_logo
                contentDescription = "NutriTrack Pro Logo",
                modifier = Modifier
                    .size(200.dp) // Adjusted size
                    .padding(bottom = 24.dp)
            )

            Text(
                text = "NutriTrack Pro",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                fontSize = 32.sp, // Larger font size
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Spacer(modifier = Modifier.height(50.dp)) // Add some space at the bottom
        }
    }
}
