package com.example.fit2081a1_yang_xingyu_33533563.ui.screens

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.TargetedFlingBehavior
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
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fit2081a1_yang_xingyu_33533563.data.legacy.FoodCategory
import com.example.fit2081a1_yang_xingyu_33533563.data.legacy.UserTimePref
import com.example.fit2081a1_yang_xingyu_33533563.data.model.entity.FoodCategoryDefinitionEntity
import com.example.fit2081a1_yang_xingyu_33533563.data.model.entity.PersonaEntity
import com.example.fit2081a1_yang_xingyu_33533563.data.viewmodel.QuestionnaireViewModel
import com.example.fit2081a1_yang_xingyu_33533563.ui.components.FoodCategoryCard
import com.example.fit2081a1_yang_xingyu_33533563.ui.components.PageTransitionEffect
import com.example.fit2081a1_yang_xingyu_33533563.ui.components.TimeInput
import com.example.fit2081a1_yang_xingyu_33533563.ui.components.TimePickerRow
import com.example.fit2081a1_yang_xingyu_33533563.ui.components.TopNavigationBar
import com.example.fit2081a1_yang_xingyu_33533563.util.AnimationUtils
import com.example.fit2081a1_yang_xingyu_33533563.util.SharedPreferencesManager
import kotlinx.coroutines.launch
import com.example.fit2081a1_yang_xingyu_33533563.ui.components.pageTransition
import com.example.fit2081a1_yang_xingyu_33533563.ui.components.rememberCustomPagerFlingBehavior
import com.example.fit2081a1_yang_xingyu_33533563.ui.components.PersonaCard

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
    val pageCount = 4
    val pagerState = rememberPagerState { pageCount }
    val coroutineScope = rememberCoroutineScope()

    // Sync animations with frame rate for smoother transitions
    AnimationUtils.SyncAnimationsWithFrames()
    
    // Measure frame time to adaptively adjust animation complexity
    val frameMetrics = AnimationUtils.MeasureFrameTime()
    
    // Custom fling behavior for smoother paging with spring physics
    val flingBehavior = rememberCustomPagerFlingBehavior(pagerState)
    
    // Choose your page transition effect based on device performance
    val transitionEffect = if (frameMetrics.canHandleComplexAnimations) {
        PageTransitionEffect.DEPTH // More complex animation for powerful devices
    } else {
        PageTransitionEffect.FADE // Simpler animation for less powerful devices
    }

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
    
    // Time validation error
    val timeValidationError by viewModel.timeValidationError.collectAsState()
    
    // Save status
    val saveStatus by viewModel.saveStatus.collectAsState()

    // is questionnaire completed
    val isQuestionnaireCompleted by viewModel.isQuestionnaireCompleted.collectAsState()
    // Collect validation state
    val isQuestionnaireValid by viewModel.isQuestionnaireValid.collectAsState()
    
    // Map time preferences to UserTimePref enum - update when any time value changes
    val timePreferences = remember(biggestMealTime, sleepTime, wakeUpTime) {
        mapOf(
            UserTimePref.BIGGEST_MEAL to (biggestMealTime ?: ""),
            UserTimePref.SLEEP to (sleepTime ?: ""),
            UserTimePref.WAKEUP to (wakeUpTime ?: "")
        )
    }

    // Effect to load user preferences when the screen is first displayed
    LaunchedEffect(userID) {
        // Wait for the next frame before loading data to ensure UI is ready
        AnimationUtils.waitForNextFrame()
        viewModel.loadUserPreferences(userID.toString())
    }
    
    // Effect to show toast when save status changes
    LaunchedEffect(saveStatus) {
        saveStatus?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
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
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp)
            ) {
                // Enhanced pager with custom animations
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    pageSpacing = 8.dp,
                    flingBehavior = flingBehavior as TargetedFlingBehavior,
                    pageContent = { page ->
                        // Add a content container with custom page transition
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp)
                                .pageTransition(page, pagerState, transitionEffect)
                                .background(
                                    MaterialTheme.colorScheme.surface,
                                    shape = MaterialTheme.shapes.medium
                                )
                                .padding(16.dp)
                        ) {
                            when (page) {
                                0 -> FoodCategoryPage(
                                    allFoodCategories = allFoodCategories,
                                    selectedFoodKeys = selectedFoodCategoryKeys,
                                    onCategoryToggle = { category, checked ->
                                        viewModel.toggleFoodCategory(category, checked) }
                                )
                                1 -> PersonaPage(
                                    selectedPersona = selectedPersonaId,
                                    onPersonaSelected = { viewModel.selectPersona(it) },
                                    personas = allPersonas
                                )
                                2 -> TimingsPage(
                                    timePreferences = timePreferences,
                                    onTimeSelected = { timePrefType, newTime ->
                                        when (timePrefType) {
                                            UserTimePref.BIGGEST_MEAL -> viewModel.updateBiggestMealTime(newTime)
                                            UserTimePref.SLEEP -> viewModel.updateSleepTime(newTime)
                                            UserTimePref.WAKEUP -> viewModel.updateWakeUpTime(newTime)
                                        }
                                    },
                                    timeValidationError = timeValidationError
                                )
                                3 -> SummaryPage(
                                    checkedState = selectedFoodCategoryKeys,
                                    selectedPersona = selectedPersonaId,
                                    timePreferences = timePreferences,
                                    onSaveClick = {
                                        viewModel.saveAllPreferences(userID.toString())
                                    },
                                    allPersonas = allPersonas
                                )
                            }
                        }
                    }
                )

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
                                pagerState.animateScrollToPage(
                                    page = pagerState.currentPage - 1,
                                    animationSpec = AnimationUtils.smoothSpringSpec
                                )
                            }
                        },
                        enabled = pagerState.currentPage > 0
                    ) {
                        Text("Previous")
                    }

                    // Enhanced page indicators with smoother animations
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
                            
                            val size = if (pagerState.currentPage == iteration) 12.dp else 8.dp
                            
                            Box(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .size(size)
                                    .background(color, shape = CircleShape)
                            )
                        }
                    }

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                if (pagerState.currentPage < 3) {
                                    pagerState.animateScrollToPage(
                                        page = pagerState.currentPage + 1,
                                        animationSpec = AnimationUtils.smoothSpringSpec
                                    )
                                } else {
                                    viewModel.saveAllPreferences(userID.toString())
                                    onSaveComplete()
                                }
                            }
                        },
                        // Enable 'Next' for pages 0,1,2. Enable 'Done' on page 3 only if valid.
                        enabled = if (pagerState.currentPage < 3) true else isQuestionnaireValid
                    ) {
                        // Change text to 'Done' on the summary page (page 3)
                        Text(if (pagerState.currentPage < 3) "Next" else "Done")
                    }
                }
            }
        }
    }
}

@Composable
fun FoodCategoryPage(
    allFoodCategories: List<FoodCategoryDefinitionEntity>,
    selectedFoodKeys: Map<String, Boolean>,
    onCategoryToggle: (String, Boolean) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top
    ) {
        QuestionnaireTextRow("Tick all the food categories you can eat", 18)
        Spacer(modifier = Modifier.height(16.dp))
        
        if (allFoodCategories.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().weight(1f), contentAlignment = Alignment.Center) {
                Text(
                    text = "Loading food categories...",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 100.dp),
                modifier = Modifier.fillMaxWidth().weight(1f),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(
                    items = allFoodCategories,
                    key = { category -> category.foodDefId }
                ) { category ->
                    val isSelected = selectedFoodKeys[category.foodDefId] ?: false
                    FoodCategoryCard(
                        category = category,
                        isSelected = isSelected,
                        onCategoryClick = { onCategoryToggle(category.foodDefId, !isSelected) }
                    )
                }
            }
        }
    }
}

@Composable
fun PersonaPage(
    selectedPersona: String,
    onPersonaSelected: (String) -> Unit,
    personas: List<PersonaEntity> = emptyList()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Select Your Persona",
            style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Text(
            text = "Choose the lifestyle that best describes you to get personalized meal recommendations.",
            style = TextStyle(fontSize = 16.sp),
            modifier = Modifier.padding(bottom = 24.dp)
        )

        if (personas.isEmpty()) {
            Text("Loading personas...")
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(
                    items = personas,
                    key = { persona -> persona.personaID }
                ) { persona ->
                    PersonaCard(
                        persona = persona,
                        isSelected = selectedPersona == persona.personaID,
                        onPersonaClick = { personaId -> onPersonaSelected(personaId) }
                    )
                }
            }
        }
    }
}

@Composable
fun TimingsPage(
    timePreferences: Map<UserTimePref, String>,
    onTimeSelected: (UserTimePref, String) -> Unit,
    timeValidationError: String? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Your Time Preferences",
            style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Text(
            text = "Tell us about your daily schedule to help us plan your meals better.",
            style = TextStyle(fontSize = 16.sp),
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                TimeInput(
                    text = UserTimePref.WAKEUP.questionDescription,
                    initialTime = timePreferences[UserTimePref.WAKEUP] ?: "",
                    onTimeSelected = { time -> onTimeSelected(UserTimePref.WAKEUP, time) }
                )
                
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                )
                
                TimeInput(
                    text = UserTimePref.BIGGEST_MEAL.questionDescription,
                    initialTime = timePreferences[UserTimePref.BIGGEST_MEAL] ?: "",
                    onTimeSelected = { time -> onTimeSelected(UserTimePref.BIGGEST_MEAL, time) }
                )
                
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                )
                
                TimeInput(
                    text = UserTimePref.SLEEP.questionDescription,
                    initialTime = timePreferences[UserTimePref.SLEEP] ?: "",
                    onTimeSelected = { time -> onTimeSelected(UserTimePref.SLEEP, time) }
                )
                
                // Display time validation error if any
                timeValidationError?.let { error ->
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = error,
                        style = TextStyle(fontSize = 14.sp),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "These preferences help us suggest meals at optimal times based on your daily " +
                    "schedule.\nNote: Wake up time should be before biggest meal time, which " +
                    "should be before sleep time.",
            style = TextStyle(fontSize = 14.sp, fontStyle = FontStyle.Italic),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun SummaryPage(
    checkedState: Map<String, Boolean>,
    selectedPersona: String,
    timePreferences: Map<UserTimePref, String>,
    onSaveClick: () -> Unit,
    allPersonas: List<PersonaEntity> = emptyList()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Summary",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 8.dp).align(Alignment.CenterHorizontally)
        )

        // Food categories summary Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Selected Food Categories",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                val selectedCategories = checkedState.entries
                    .filter { it.value }
                    .map { FoodCategory.fromFoodDefId(it.key).foodName }

                if (selectedCategories.isNotEmpty()) {
                    selectedCategories.forEach { categoryName ->
                        Text(
                            text = "• $categoryName",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
                        )
                    }
                } else {
                    Text(
                        text = "None selected",
                        style = MaterialTheme.typography.bodyLarge,
                        fontStyle = FontStyle.Italic
                    )
                }
            }
        }

        // Persona summary Card
        val personaEntity = allPersonas.find { it.personaID == selectedPersona }
        if (personaEntity != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Selected Persona",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = personaEntity.personaName,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    personaEntity.personaDescription?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        } else if (selectedPersona.isNotEmpty()){
             Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Selected Persona",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(text = "ID: $selectedPersona (Name not found)", style = MaterialTheme.typography.bodyLarge)
                }
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                     Text(
                        text = "Selected Persona",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(text = "None selected", style = MaterialTheme.typography.bodyLarge, fontStyle = FontStyle.Italic)
                }
            }
        }


        // Time preferences summary Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Your Preferred Times",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                UserTimePref.entries.forEach { pref ->
                    val time = timePreferences[pref] ?: ""
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = pref.timePrefName, style = MaterialTheme.typography.bodyLarge)
                        Text(
                            text = time.ifEmpty { "Not set" },
                            style = MaterialTheme.typography.bodyLarge,
                            fontStyle = if (time.isEmpty()) FontStyle.Italic else FontStyle.Normal
                        )
                    }
                    if (pref != UserTimePref.entries.last()) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    }
                }
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


