package com.example.fit2081a1_yang_xingyu_33533563.view.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.PeopleAlt
import androidx.compose.material.icons.outlined.QuestionAnswer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.example.fit2081a1_yang_xingyu_33533563.data.model.repository.ScoreTypeDefinitionRepository
import com.example.fit2081a1_yang_xingyu_33533563.data.model.repository.UserScoreRepository
import com.example.fit2081a1_yang_xingyu_33533563.data.viewmodel.ClinicianDashboardViewModel
import com.example.fit2081a1_yang_xingyu_33533563.data.viewmodel.InsightsViewModel
import com.example.fit2081a1_yang_xingyu_33533563.view.UiState
import com.example.fit2081a1_yang_xingyu_33533563.view.components.InfoCard
import com.example.fit2081a1_yang_xingyu_33533563.view.components.ScoreProgressBarRow
import com.example.fit2081a1_yang_xingyu_33533563.view.components.TopNavigationBar
import dev.jeziellago.compose.markdowntext.MarkdownText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import com.example.fit2081a1_yang_xingyu_33533563.data.viewmodel.UserScoreDialogViewModel


/**
 * Clinician Dashboard Screen
 * Displays HEIFA score statistics, AI-generated patterns, and user data
 */
@Composable
fun ClinicianDashboardScreen(
    viewModel: ClinicianDashboardViewModel,
    onBackClick: () -> Unit = {}
) {
    // Collect state
    val userStats by viewModel.userStats.collectAsState()
    val patterns by viewModel.patterns.collectAsState()
    val trends by viewModel.trends.collectAsState()
    val maleAverage by viewModel.maleAverageScore.collectAsState()
    val femaleAverage by viewModel.femaleAverageScore.collectAsState()
    val analysisState by viewModel.analysisState.collectAsState()
    val selectedTabIndex by viewModel.selectedTabIndex.collectAsState()
    val qaState by viewModel.qaState.collectAsState()
    val qaResponse by viewModel.qaResponse.collectAsState()
    val clinicianQuery by viewModel.clinicianQuery.collectAsState()

    Scaffold(
        topBar = {
            TopNavigationBar(
                title = "Clinician Dashboard",
                showBackButton = true,
                onBackButtonClick = onBackClick
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Outlined.Dashboard, contentDescription = "Dashboard") },
                    label = { Text("Dashboard") },
                    selected = selectedTabIndex == 0,
                    onClick = { viewModel.updateSelectedTab(0) }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Outlined.PeopleAlt, contentDescription = "User Stats") },
                    label = { Text("User Stats") },
                    selected = selectedTabIndex == 1,
                    onClick = { viewModel.updateSelectedTab(1) }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Outlined.Analytics, contentDescription = "AI Trends") },
                    label = { Text("AI Trends") },
                    selected = selectedTabIndex == 2,
                    onClick = { viewModel.updateSelectedTab(2) }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Outlined.QuestionAnswer, contentDescription = "Ask AI") },
                    label = { Text("Ask AI") },
                    selected = selectedTabIndex == 3,
                    onClick = { viewModel.updateSelectedTab(3) }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // Content based on selected tab
            when (selectedTabIndex) {
                0 -> DashboardContent(
                    maleAverage = maleAverage,
                    femaleAverage = femaleAverage,
                    patterns = patterns,
                    isLoading = analysisState is UiState.Loading,
                    errorMessage = if (analysisState is UiState.Error) {
                        when (analysisState) {
                            is UiState.Error.ApiError -> (analysisState as UiState.Error.ApiError).errorMessage
                            is UiState.Error.NetworkError -> (analysisState as UiState.Error.NetworkError).errorMessage
                            is UiState.Error.UnidentifiedError -> (analysisState as UiState.Error.UnidentifiedError).errorMessage
                            else -> null
                        }
                    } else null
                )
                1 -> UserStatsContent(userStats = userStats)
                2 -> AITrendsContent(trends = trends)
                3 -> AskAIContent(
                    query = clinicianQuery,
                    onQueryChange = { viewModel.updateQuery(it) },
                    onAskQuestion = { viewModel.askQuestion(it) },
                    response = qaResponse,
                    uiState = qaState,
                    onRefreshAnalysis = { viewModel.generateAIAnalysis() }
                )
            }
        }
    }
}

/**
 * The main dashboard content displaying averages and AI-identified patterns
 */
@Composable
fun DashboardContent(
    maleAverage: Float?,
    femaleAverage: Float?,
    patterns: List<String>,
    isLoading: Boolean,
    errorMessage: String?
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // HEIFA Averages
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "HEIFA Score Averages",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    if (maleAverage != null && femaleAverage != null) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            ScoreDisplay(
                                title = "Male Average",
                                score = maleAverage,
                                modifier = Modifier.weight(1f)
                            )

                            HorizontalDivider(
                                modifier = Modifier
                                    .height(48.dp)
                                    .width(1.dp),color =
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                            )
                            
                            ScoreDisplay(
                                title = "Female Average",
                                score = femaleAverage,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    } else {
                        Text(
                            text = "No data available",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
        
        // AI-Generated Patterns
        item {
            InfoCard(
                title = "AI Identified Patterns",
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else if (errorMessage != null) {
                    Text(
                        text = errorMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                } else if (patterns.isEmpty()) {
                    Text(
                        text = "No patterns identified yet",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        patterns.forEachIndexed { index, pattern ->
                            PatternItem(
                                number = index + 1,
                                pattern = pattern
                            )
                            if (index < patterns.size - 1) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Display for a single score
 */
@Composable
fun ScoreDisplay(
    title: String,
    score: Float,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = String.format("%.1f", score),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Display for a single AI-identified pattern
 */
@Composable
fun PatternItem(
    number: Int,
    pattern: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "$number.",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(end = 8.dp)
        )
        MarkdownText(
            markdown = pattern,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

/**
 * Content for User Stats tab
 */
@Composable
fun UserStatsContent(
    userStats: List<ClinicianDashboardViewModel.UserStatsDisplay>
) {
    var selectedUser by remember { mutableStateOf<ClinicianDashboardViewModel.UserStatsDisplay?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Add hint card at the top
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "User Display Information",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "• Registered users are shown with their full name\n• Unregistered users are displayed as \"user+id\"",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
        
        if (userStats.isEmpty()) {
            item {
                Text(
                    text = "No user data available",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            items(userStats) { userStat ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedUser = userStat },
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (userStat.userName.toString() == "null")
                                    "unregistered user" else userStat.userName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Gender: ${userStat.gender}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        
                        Text(
                            text = String.format("%.1f", userStat.heifaScore),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
    
    // Show dialog when a user is selected
    selectedUser?.let { user ->
        UserScoreDialog(
            user = user,
            onDismiss = { selectedUser = null }
        )
    }
}

/**
 * Dialog showing detailed scores for a user
 */
@Composable
fun UserScoreDialog(
    user: ClinicianDashboardViewModel.UserStatsDisplay,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val application = context.applicationContext as android.app.Application
    val database = com.example.fit2081a1_yang_xingyu_33533563.data.model.AppDatabase.getDatabase(application)
    val userScoreRepository = UserScoreRepository(database.userScoreDao())
    val scoreTypeDefinitionRepository = ScoreTypeDefinitionRepository(database.scoreTypeDefinitionDao())
    
    val viewModel = remember {
        UserScoreDialogViewModel(
            userId = user.userId,
            userScoreRepository = userScoreRepository,
            scoreTypeDefinitionRepository = scoreTypeDefinitionRepository
        )
    }
    
    val scores by viewModel.scores.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        ),
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(16.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        titleContentColor = MaterialTheme.colorScheme.primary,
        textContentColor = MaterialTheme.colorScheme.onSurface,
        title = {
            Text(
                text = if (user.userName.toString() == "null") "User Details" else "${user.userName}'s Details",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                } else if (scores.isEmpty()) {
                    Text(
                        text = "No detailed scores available for this user",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Text(
                        text = "HEIFA Score Breakdown",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    // Display each score with a progress bar
                    scores.forEach { score ->
                        ScoreProgressBarRow(
                            displayName = score.displayName,
                            currentValue = score.scoreValue,
                            maxValue = score.maxScore
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 4.dp
                )
            ) {
                Text(
                    "Close",
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    )
}

/**
 * Content for AI Trends tab
 */
@Composable
fun AITrendsContent(
    trends: List<String>
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (trends.isEmpty()) {
            item {
                Text(
                    text = "No trend analysis available",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            item {
                Text(
                    text = "AI Analysis: Overall Dietary Trends",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            
            items(trends) { trend ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    MarkdownText(
                        markdown = trend,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(20.dp)
                    )
                }
            }
        }
    }
}

/**
 * Content for Ask AI tab
 * Allows clinicians to ask questions about patient statistics
 */
@Composable
fun AskAIContent(
    query: String,
    onQueryChange: (String) -> Unit,
    onAskQuestion: (String) -> Unit,
    response: String,
    uiState: UiState,
    onRefreshAnalysis: () -> Unit
) {
    var inputQuery by remember { mutableStateOf(query) }
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Ask AI about Patient Statistics",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Ask questions about patterns, trends, or insights from your patients' nutritional data.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputQuery,
                    onValueChange = { inputQuery = it },
                    modifier = Modifier.weight(1f),
                    label = { Text("Your question") },
                    placeholder = { Text("e.g., What dietary patterns are common among males?") }
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Button(
                    onClick = { 
                        if (inputQuery.isNotBlank()) {
                            onAskQuestion(inputQuery)
                        }
                    }
                ) {
                    Icon(
                        Icons.AutoMirrored.Outlined.Send,
                        contentDescription = "Ask"
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            if (uiState is UiState.Loading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (uiState is UiState.Error) {
                val errorText = when (uiState) {
                    is UiState.Error.NetworkError -> uiState.errorMessage
                    is UiState.Error.ApiError -> uiState.errorMessage
                    is UiState.Error.UnidentifiedError -> uiState.errorMessage
                }
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Error",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = errorText,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
        }
        
        // Response section
        if (response.isNotBlank() || uiState is UiState.Streaming) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "AI Response",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        MarkdownText(
                            markdown = response,
                            style = MaterialTheme.typography.bodyLarge,
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        if (uiState !is UiState.Streaming && uiState !is UiState.Loading) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(onClick = onRefreshAnalysis) {
                                    Text("Refresh Analysis")
                                }
                            }
                        }
                    }
                }
            }
        }
        
        // Sample questions section
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Sample Questions",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Column {
                        SampleQuestion(
                            question = "What are the main differences in dietary habits between male and female patients?",
                            onAsk = {
                                inputQuery = it
                                onQueryChange(it)
                            }
                        )
                        
                        SampleQuestion(
                            question = "Which nutritional components show the largest deficiency across patients?",
                            onAsk = {
                                inputQuery = it
                                onQueryChange(it)
                            }
                        )
                        
                        SampleQuestion(
                            question = "What interventions would you recommend based on the current nutritional data?",
                            onAsk = {
                                inputQuery = it
                                onQueryChange(it)
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * A clickable sample question
 */
@Composable
fun SampleQuestion(
    question: String,
    onAsk: (String) -> Unit
) {
    TextButton(
        onClick = { onAsk(question) },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "\"$question\"",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primary
        )
    }
    HorizontalDivider(
        modifier = Modifier.padding(vertical = 4.dp),
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
    )
}