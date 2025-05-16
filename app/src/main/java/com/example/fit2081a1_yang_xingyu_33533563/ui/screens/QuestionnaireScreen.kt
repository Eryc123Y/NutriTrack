package com.example.fit2081a1_yang_xingyu_33533563.ui.screens

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fit2081a1_yang_xingyu_33533563.data.legacy.FoodCategory
import com.example.fit2081a1_yang_xingyu_33533563.data.legacy.Persona
import com.example.fit2081a1_yang_xingyu_33533563.data.legacy.UserTimePref
import com.example.fit2081a1_yang_xingyu_33533563.data.viewmodel.QuestionnaireViewModel
import com.example.fit2081a1_yang_xingyu_33533563.ui.components.CheckboxWithText
import com.example.fit2081a1_yang_xingyu_33533563.ui.components.PersonaButton
import com.example.fit2081a1_yang_xingyu_33533563.ui.components.PersonaSelectionDropdownField
import com.example.fit2081a1_yang_xingyu_33533563.ui.components.TimePickerRow
import com.example.fit2081a1_yang_xingyu_33533563.ui.components.TopNavigationBar
import com.example.fit2081a1_yang_xingyu_33533563.util.SharedPreferencesManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun QuestionnaireScreen(
    viewModel: QuestionnaireViewModel,
    onBackClick: () -> Unit = {},
    onSaveComplete: () -> Unit = {}
) {

    val context = LocalContext.current
    val prefManager = SharedPreferencesManager.getInstance(context)
    val userID = prefManager.getCurrentUser()
    
    // We store only 4 pages: food categories, persona, time prefs, summary
    val pagerState = rememberPagerState { 4 }
    val coroutineScope = rememberCoroutineScope()

    // Food category state - using ViewModel
    val allFoodCategories = viewModel.allFoodCategories.collectAsState().value
    val selectedFoodCategoryKeys = viewModel.foodCategoryKeyBooleanMap.collectAsState().value

    // Persona state - using ViewModel
    val allPersonas = viewModel.allPersonas.collectAsState().value
    val selectedPersonaId = viewModel.selectedPersonaId.collectAsState().value ?: ""

    // Time preferences state - using ViewModel
    val biggestMealTime by viewModel.biggestMealTime.collectAsState()
    val sleepTime by viewModel.sleepTime.collectAsState()
    val wakeUpTime by viewModel.wakeUpTime.collectAsState()
    
    // Save status
    val saveStatus by viewModel.saveStatus.collectAsState()
    
    // Map time preferences to UserTimePref enum
    val timePreferences = remember {
        mapOf(
            UserTimePref.BIGGEST_MEAL to (biggestMealTime ?: ""),
            UserTimePref.SLEEP to (sleepTime ?: ""),
            UserTimePref.WAKEUP to (wakeUpTime ?: "")
        )
    }

    // Effect to load user preferences when the screen is first displayed
    LaunchedEffect(userID) {
        viewModel.loadUserPreferences(userID.toString())
    }
    
    // Effect to show toast when save status changes
    LaunchedEffect(saveStatus) {
        saveStatus?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            if (it.startsWith("Preferences saved")) {
                onSaveComplete()
            }
            viewModel.clearSaveStatus()
        }
    }

    Scaffold(
        topBar = {
            TopNavigationBar(
                title = "Food Intake Questionnaire (${pagerState.currentPage + 1}/4)",
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
                modifier = Modifier.fillMaxSize()
            ) {
                // Pager content
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) { page ->
                    when (page) {
                        0 -> FoodCategoryPage(
                            selectedFoodKeys = selectedFoodCategoryKeys,
                            onCheckedChange = { category, checked ->
                                viewModel.toggleFoodCategory(category.foodDefId, checked) }
                        )
                        1 -> PersonaPage(
                            selectedPersona = selectedPersonaId,
                            onPersonaSelected = { viewModel.selectPersona(it) }
                        )
                        2 -> TimingsPage(
                            timePreferences = timePreferences,
                            onTimeSelected = { timePrefType, newTime ->
                                when (timePrefType) {
                                    UserTimePref.BIGGEST_MEAL -> viewModel.updateBiggestMealTime(newTime)
                                    UserTimePref.SLEEP -> viewModel.updateSleepTime(newTime)
                                    UserTimePref.WAKEUP -> viewModel.updateWakeUpTime(newTime)
                                }
                            }
                        )
                        3 -> SummaryPage(
                            checkedState = selectedFoodCategoryKeys,
                            selectedPersona = selectedPersonaId,
                            timePreferences = timePreferences,
                            onSaveClick = {
                                viewModel.saveAllPreferences(userID.toString())
                            }
                        )
                    }
                }

                // Navigation buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                            }
                        },
                        enabled = pagerState.currentPage > 0
                    ) {
                        Text("Previous")
                    }

                    // Page indicators
                    Row(
                        modifier = Modifier
                            .wrapContentWidth()
                            .align(Alignment.CenterVertically),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        repeat(4) { iteration ->
                            val color = if (pagerState.currentPage == iteration)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                            Box(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .size(10.dp)
                                    .background(color, shape = MaterialTheme.shapes.small)
                            )
                        }
                    }

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                if (pagerState.currentPage < 3) {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            }
                        },
                        enabled = pagerState.currentPage < 3
                    ) {
                        Text("Next")
                    }
                }
            }
        }
    }
}

@Composable
fun FoodCategoryPage(
    selectedFoodKeys: Map<String, Boolean>,
    onCheckedChange: (FoodCategory, Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        QuestionnaireTextRow("Tick all the food categories you can eat", 18)
        Spacer(modifier = Modifier.height(16.dp))
        CheckboxContainer(
            checkedState = selectedFoodKeys,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
fun PersonaPage(
    selectedPersona: String,
    onPersonaSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        QuestionnaireTextRow("Your Persona", 18)
        Spacer(modifier = Modifier.height(16.dp))
        CreatePersonaButtons()
        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
        QuestionnaireTextRow("Which persona best fits you?", 16)
        Spacer(modifier = Modifier.height(8.dp))
        PersonaSelectionDropdownField(
            selectedPersona = selectedPersona,
            onPersonaSelected = onPersonaSelected
        )
    }
}

@Composable
fun TimingsPage(
    timePreferences: Map<UserTimePref, String>,
    onTimeSelected: (UserTimePref, String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        QuestionnaireTextRow("Timings", 18)
        Spacer(modifier = Modifier.height(16.dp))
        TimePickerInterface(
            modifier = Modifier.fillMaxWidth(),
            timePreferences = timePreferences,
            onTimeSelected = onTimeSelected
        )
    }
}

@Composable
fun SummaryPage(
    checkedState: Map<String, Boolean>,
    selectedPersona: String,
    timePreferences: Map<UserTimePref, String>,
    onSaveClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "Summary",
                style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Food categories summary
            Text(
                text = "Selected Food Categories:",
                style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(vertical = 8.dp)
            )
            val selectedCategories = checkedState.keys.map { key -> 
                FoodCategory.entries.find { it.foodDefId == key }?.foodName ?: key
            }
            Text(
                text = if (selectedCategories.isNotEmpty())
                    selectedCategories.joinToString(", ")
                else "None selected"
            )

            // Persona summary
            Text(
                text = "Selected Persona:",
                style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Text(text = selectedPersona.ifEmpty { "None selected" })

            // Time preferences summary
            Text(
                text = "Time Preferences:",
                style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(vertical = 8.dp)
            )
            timePreferences.forEach { (timePref, time) ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = timePref.timePrefName)
                    Text(text = time.ifEmpty { "Not set" })
                }
            }
        }

        // Save button
        Button(
            onClick = onSaveClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Text("Save All Preferences")
        }
    }
}

@Composable
fun CheckboxContainer(
    checkedState: Map<String, Boolean>,
    onCheckedChange: (FoodCategory, Boolean) -> Unit
) {
    val foodCategories = remember { FoodCategory.entries.toList() }

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        foodCategories.forEachIndexed { index, category ->
            item {
                CheckboxWithText(
                    text = category.foodName,
                    checked = checkedState[category.foodDefId] == true,
                    onCheckedChange = { checked -> onCheckedChange(category, checked) }
                )
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

@Composable
fun TimePickerInterface(
    modifier: Modifier,
    timePreferences: Map<UserTimePref, String>,
    onTimeSelected: (UserTimePref, String) -> Unit
) {
    for (timePrefType in UserTimePref.entries) {
        TimePickerRow(
            modifier = modifier,
            text = timePrefType.questionDescription,
            initialTime = timePreferences[timePrefType] ?: "",
            onTimeSelected = { newTime -> onTimeSelected(timePrefType, newTime) }
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
        personas.forEach { persona ->
            item {
                PersonaButton(persona)
            }
        }
    }
}

