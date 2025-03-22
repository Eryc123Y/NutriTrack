package com.example.fit2081a1_yang_xingyu_33533563.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fit2081a1_yang_xingyu_33533563.R


@Preview(showBackground = true)
@Composable
fun WelcomeScreen(onNavigateToLogin: () -> Unit = {}) {
    val displayText = "This app provides general health and nutrition information for educational " +
            "purposes only. It is not intended as medical advice diagnosis, or treatment. Always " +
            "consult a qualified healthcare professional before making any changes to your diet, " +
            "exercise, or health regimen.\n\nUse this app at your own risk. If you’d like to an " +
            "Accredited Practicing Dietitian (APD), please visit the Monash Nutrition/Dietetics " +
            "Clinic (discounted rates for students): " +
            "https://www.monash.edu/medicine/scs/nutrition/clinics/nutrition"

    Surface (
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top

        ) {
            Image(
                painter = painterResource(
                    id = R.drawable.logo),
                contentDescription = "FIT2081 Logo",
                modifier = Modifier.size(300.dp)
                    .padding(top = 100.dp)
                    .padding(bottom = 24.dp)
            )

            Text(
                text = displayText,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Justify,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Button(
                onClick = onNavigateToLogin,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
                    .padding(bottom = 24.dp),
                content = {Text("Login")}
            )

            Text(
                text = "Designed by Yang Xingyu (33533563)"
            )
        }
    }



}
