package com.example.fit2081a1_yang_xingyu_33533563.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fit2081a1_yang_xingyu_33533563.data.model.Persona
import com.example.fit2081a1_yang_xingyu_33533563.data.model.UserTimePref
import com.example.fit2081a1_yang_xingyu_33533563.ui.components.CheckboxWithText
import com.example.fit2081a1_yang_xingyu_33533563.ui.components.FoodCategory
import com.example.fit2081a1_yang_xingyu_33533563.ui.components.PersonaButton
import com.example.fit2081a1_yang_xingyu_33533563.ui.components.PersonaSelectionDropdownField
import com.example.fit2081a1_yang_xingyu_33533563.ui.components.TimePickerRow
import com.example.fit2081a1_yang_xingyu_33533563.ui.components.TopNavigationBar
import com.example.fit2081a1_yang_xingyu_33533563.util.SharedPreferencesManager
import kotlin.collections.forEach
import kotlin.collections.set

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
                modifier = Modifier
                    .fillMaxSize().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                QuestionnaireTextRow("Tick all the food categories you can eat", 18)
                CheckboxContainer(prefManager, userID.toString())
                QuestionnaireTextRow("Your Persona", 18)
                CreatePersonaButtons()
                HorizontalDivider(modifier = Modifier.padding(4.dp))
                QuestionnaireTextRow("Which persona best fits you?", 16)
                PersonaSelectionDropdownField(prefManager, userID.toString())
                QuestionnaireTextRow("Timings", 16)
                TimePickerInterface(
                    modifier = Modifier.fillMaxWidth().padding(4.dp),
                    sharedPref = prefManager,
                    userID = userID.toString()
                )
            }
        }
    }
}
@Composable
fun CheckboxContainer(prefManager: SharedPreferencesManager, userID: String) {
    // use a list of FoodCategory to generate checkboxes by iterating over the list
    val foodCategories = remember { FoodCategory.entries.toList() }

    // initialize a map to store the checked state of each checkbox from SharedPreferences
    val checkedState = remember {
        mutableStateMapOf<FoodCategory, Boolean>().apply {
            FoodCategory.entries.forEach { category ->
                this[category] = prefManager.getCheckboxState(userID, category)
            }
        }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(foodCategories.size) { index ->
            val category = foodCategories[index]
            CheckboxWithText(
                text = category.foodName,
                checked = checkedState[category] == true,
                onCheckedChange = { checked ->
                    checkedState[category] = checked
                    prefManager.setCheckboxState(userID, checkedState)
                }
            )
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

@Composable
fun TimePickerInterface(modifier: Modifier, sharedPref: SharedPreferencesManager, userID: String) {
    for (timePrefType in UserTimePref.entries) {
        TimePickerRow(
            modifier = modifier,
            text = timePrefType.questionDescription,
            userID = userID,
            timePrefType = timePrefType,
            sharedPref = sharedPref
        )
    }
}

@Composable
fun CreatePersonaButtons() {
    val personas: List<Persona> = Persona.entries
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier.fillMaxWidth() .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        personas.forEach(){ persona ->
            item {
                PersonaButton(persona)
            }
        }
    }
}