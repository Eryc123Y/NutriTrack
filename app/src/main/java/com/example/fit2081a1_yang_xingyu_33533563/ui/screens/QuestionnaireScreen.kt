package com.example.fit2081a1_yang_xingyu_33533563.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fit2081a1_yang_xingyu_33533563.ui.components.CheckboxContainer
import com.example.fit2081a1_yang_xingyu_33533563.ui.components.CreatePersonaButtons
import com.example.fit2081a1_yang_xingyu_33533563.ui.components.PersonaSelectionDropdownField
import com.example.fit2081a1_yang_xingyu_33533563.ui.components.TopNavigationBar
import com.example.fit2081a1_yang_xingyu_33533563.util.SharedPreferencesManager

@Preview(showSystemUi = true)
@Composable
fun QuestionnaireScreen(
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val prefManager = SharedPreferencesManager(context)
    val userID = prefManager.getCurrentUser()
    Scaffold(
        topBar = {
            TopNavigationBar(
                title = "Food Intake Questionnaire",
                showBackButton = true,
                onBackButtonClick = onBackClick
            )
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                QuestionnaireTextRow("Tick all the food categories you can eat", 18)
                CheckboxContainer()
                QuestionnaireTextRow("Your Persona", 18)
                CreatePersonaButtons()
                HorizontalDivider(modifier = Modifier.padding(4.dp))
                QuestionnaireTextRow("Which persona best fits you?", 16)
                PersonaSelectionDropdownField(prefManager, userID.toString())
                QuestionnaireTextRow("Timings", 16)
            }
        }
    }
}

@Composable
fun QuestionnaireTextRow(text: String, fontSize: Int ) {
    Row(
        modifier = Modifier.fillMaxWidth() .padding(4.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            text = text,
            style = TextStyle(fontSize = fontSize.sp, fontWeight = FontWeight.Bold)
        )
    }
}