package com.example.fit2081a1_yang_xingyu_33533563.view.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fit2081a1_yang_xingyu_33533563.data.viewmodel.FruitViewModel
import com.example.fit2081a1_yang_xingyu_33533563.data.viewmodel.GenAIViewModel
import com.example.fit2081a1_yang_xingyu_33533563.navigation.Screen
import com.example.fit2081a1_yang_xingyu_33533563.util.SharedPreferencesManager
import com.example.fit2081a1_yang_xingyu_33533563.view.UiState
import com.example.fit2081a1_yang_xingyu_33533563.view.components.BottomNavigationBar
import com.example.fit2081a1_yang_xingyu_33533563.view.components.InfoCard
import com.example.fit2081a1_yang_xingyu_33533563.view.components.TopNavigationBar
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.fit2081a1_yang_xingyu_33533563.api.FruitResponse
import dev.jeziellago.compose.markdowntext.MarkdownText
import kotlinx.coroutines.delay
import kotlin.collections.filter
import kotlin.collections.isNotEmpty

@Composable
fun CoachScreen(
    onNavigate: (String) -> Unit = {},
    onBackClick: () -> Unit = {},
    fruitViewModel: FruitViewModel,
    genAIViewModel: GenAIViewModel,
    sharedPreferencesManager: SharedPreferencesManager
) {
    var fruitNameInput by remember { mutableStateOf("") }
    val fruitDetails by fruitViewModel.fruitDetails.collectAsState()
    val isLoadingFruit by fruitViewModel.isLoading.collectAsState()
    val shouldShowFruitViceQuery by fruitViewModel.shouldShowFruitViceQuery.collectAsState()

    val currentUserIdString = sharedPreferencesManager.getCurrentUser()
    val scrollState = rememberScrollState() // For the main screen scroll

    LaunchedEffect(currentUserIdString) {
        if (currentUserIdString?.isNotBlank() == true) {
            fruitViewModel.loadUserFruitServingsize(currentUserIdString)
            genAIViewModel.setUserId(currentUserIdString)
            genAIViewModel.startNewSession() // Start a new chat session when user ID is confirmed
        }
    }

    Scaffold(
        topBar = {
            TopNavigationBar(
                title = "NutriCoach",
                showBackButton = false,
                onBackButtonClick = onBackClick
            )
        },
        bottomBar = {
            BottomNavigationBar(
                currentRoute = Screen.NutriCoach.route,
                onNavigate = onNavigate
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Conditional display for Fruit Advice section
            if (shouldShowFruitViceQuery) {
                FruitViceQueryPanel(
                    fruitNameInput = fruitNameInput,
                    onFruitNameInputChange = { fruitNameInput = it },
                    fruitViewModel = fruitViewModel,
                    isLoading = isLoadingFruit,
                    fruitDetails = fruitDetails
                )
            } else {
                FruitIntakeGoalMetCard()
            }

            Spacer(modifier = Modifier.height(16.dp))

            AiChatPanel(genAIViewModel = genAIViewModel, currentUserIdString = currentUserIdString)

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun FruitIntakeGoalMetCard() {
    InfoCard(title = "Fruit Intake Goal Met") {
        Text(
            "Great job on your fruit intake!",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            "You've met your fruit goals. Keep up the healthy habits!",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
    }
}

@Composable
fun FruitViceQueryPanel(
    fruitNameInput: String,
    onFruitNameInputChange: (String) -> Unit,
    fruitViewModel: FruitViewModel,
    isLoading: Boolean,
    fruitDetails: FruitResponse?
) {
    InfoCard(title = "Need Fruit Advice?") {
        Text(
            "You are recommended to increase fruit intake. Ask NutriCoach about a fruit!",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = fruitNameInput,
            onValueChange = onFruitNameInputChange,
            label = { Text("Enter fruit name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (fruitNameInput.isNotBlank()) {
                    fruitViewModel.fetchFruitDetails(fruitNameInput)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading && fruitNameInput.isNotBlank()
        ) {
            Text(if (isLoading) "Searching..." else "Get Fruit Details")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }

        fruitDetails?.let { response ->
            response.fruitInfo.firstOrNull()?.get("Error")?.let { errorMessage ->
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 8.dp)
                )
            } ?: run {
                if (response.fruitInfo.isNotEmpty()) {
                    Text(
                        "Fruit Information:",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Medium),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 200.dp) // Constrained height for fruit info
                    ) {
                        items(response.fruitInfo) { infoMap ->
                            infoMap.forEach { (key, value) ->
                                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 2.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("$key:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                                        Text(value, style = MaterialTheme.typography.bodySmall, textAlign = androidx.compose.ui.text.style.TextAlign.End)
                                    }
                                    HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))
                                }
                            }
                        }
                    }
                } else if (!isLoading) {
                    Text(
                        "No information to display. Try searching for a fruit.",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiChatPanel(genAIViewModel: GenAIViewModel, currentUserIdString: String?) {
    var chatInput by remember { mutableStateOf("") }
    val conversationHistory by genAIViewModel.conversationHistory.collectAsState()
    val genAiUiState by genAIViewModel.uiState.collectAsState()
    val isLoadingAi = genAiUiState is UiState.Loading
    val searchQuery by genAIViewModel.searchQuery.collectAsState()
    var showClearConfirmationDialog by remember { mutableStateOf(false) }
    var showAllAiResponsesDialog by remember { mutableStateOf(false) }

    val chatListState = rememberLazyListState()

    val staticSuggestedQuestions = listOf(
        "Give me a motivational message!",
        "How's my overall nutrition score?",
        "Any nutrition tips for me?",
        "How can I eat healthier?",
    )

    val filteredHistory = remember(conversationHistory, searchQuery) {
        if (searchQuery.isBlank()) {
            conversationHistory
        } else {
            conversationHistory.filter {
                it.message.contains(searchQuery, ignoreCase = true)
            }
        }
    }
    
    val aiResponses = remember(conversationHistory) {
        conversationHistory.filter { !it.isUserMessage }
    }

    LaunchedEffect(filteredHistory.size, genAiUiState) { // Added genAiUiState to scroll correctly during streaming
        if (searchQuery.isBlank()) {
            val targetIndex = if (genAiUiState is UiState.Streaming || genAiUiState is UiState.Loading) {
                filteredHistory.size // Scroll to the loading/streaming bubble
            } else if (filteredHistory.isNotEmpty()){
                filteredHistory.size -1 // Scroll to the last actual message
            } else {
                -1 // No items
            }
            if (targetIndex >= 0) {
                try {
                    if (genAiUiState is UiState.Streaming) {
                        // Delay slightly when streaming to allow UI to catch up with rapid updates
                        // especially if TYPING_DELAY_MS in ViewModel is very low.
                        delay(100L) 
                        chatListState.scrollToItem(targetIndex) // Use scrollToItem for immediate jump during streaming
                    } else {
                        chatListState.animateScrollToItem(targetIndex) // Use animate for other cases
                    }
                } catch (e: Exception) {
                    // Handle potential exceptions if the list is modified during scroll
                    Log.e("CoachScreen", "Error scrolling to item: ${e.message}")
                }
            }
        }
    }
    
    if (showClearConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showClearConfirmationDialog = false },
            title = { Text("Clear Chat History?") },
            text = { Text("Are you sure you want to clear the current chat session? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        genAIViewModel.clearCurrentChatSession()
                        genAIViewModel.updateSearchQuery("")
                        showClearConfirmationDialog = false
                    }
                ) { Text("Confirm") }
            },
            dismissButton = {
                Button(onClick = { showClearConfirmationDialog = false }) { Text("Cancel") }
            }
        )
    }
    
    if (showAllAiResponsesDialog) {
        AlertDialog(
            onDismissRequest = { showAllAiResponsesDialog = false },
            title = { Text("All AI Responses") },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    if (aiResponses.isEmpty()) {
                        Text("No AI responses found.")
                    } else {
                        aiResponses.forEachIndexed { index, response ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                                )
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(
                                        text = "Response ${index + 1}",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    MarkdownText(response.message)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Time: ${response.timestamp}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            if (index < aiResponses.size - 1) {
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showAllAiResponsesDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    InfoCard(
        title = "NutriCoach Chat",
        icon = Icons.AutoMirrored.Filled.Chat,
        onHistoryClick = { showAllAiResponsesDialog = true },
        onClearClick = { showClearConfirmationDialog = true }
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { genAIViewModel.updateSearchQuery(it) },
            label = { Text("Search chat history") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { genAIViewModel.updateSearchQuery("") }) {
                        Icon(Icons.Filled.Clear, contentDescription = "Clear search")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            singleLine = true
        )

        LazyColumn(
            state = chatListState,
            modifier = Modifier.fillMaxWidth().height(300.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, shape = MaterialTheme.shapes.medium)
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (filteredHistory.isEmpty() && genAiUiState !is UiState.Streaming && genAiUiState !is UiState.Loading && searchQuery.isBlank()) {
                item {
                    Text(
                        "Ask NutriCoach about healthy eating habits...",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        modifier = Modifier.align(Alignment.CenterHorizontally).padding(8.dp)
                    )
                }
            } else if (filteredHistory.isEmpty() && searchQuery.isNotBlank()) {
                 item {
                    Text(
                       "No messages found matching your search.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        modifier = Modifier.align(Alignment.CenterHorizontally).padding(8.dp)
                    )
                }
            }else {
                items(filteredHistory) { message ->
                    if (message.isUserMessage) {
                        UserMessageBubble(text = message.message)
                    } else {
                        // Use MarkdownText for historical AI messages
                        AiMessageBubble(text = message.message, useMarkdown = true)
                    }
                }
            }

            if (genAiUiState is UiState.Loading && searchQuery.isBlank()) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Surface(
                            shape = MaterialTheme.shapes.medium,
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier.widthIn(max = 280.dp)
                        ) {
                            Row(modifier = Modifier.padding(
                                horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically){
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "NutriCoach is thinking...",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    }
                }
            }

            // When AI is streaming, display the animated text
            if (genAiUiState is UiState.Streaming && searchQuery.isBlank()) {
                item {
                    AiMessageBubble(
                        text = (genAiUiState as UiState.Streaming).currentMessageContent,
                        useMarkdown = true // AI responses should be markdown
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        
        val currentUiState = genAiUiState
        if (currentUiState is UiState.Success && currentUiState.suggestedFollowUps.isNotEmpty()) {
            Text("NutriCoach suggests:", style = MaterialTheme.typography.labelMedium, modifier = Modifier.align(Alignment.Start).padding(bottom = 4.dp))
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(currentUiState.suggestedFollowUps) { question ->
                    AssistChip(
                        onClick = { 
                            chatInput = question 
                        },
                        label = { Text(question, style = MaterialTheme.typography.bodySmall) }
                    )
                }
            }
        } else if (!isLoadingAi) {
             Text("Suggestions:", style = MaterialTheme.typography.labelMedium, modifier = Modifier.align(Alignment.Start).padding(bottom = 4.dp))
             LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(staticSuggestedQuestions) { question ->
                    AssistChip(
                        onClick = { 
                            chatInput = question 
                        },
                        label = { Text(question, style = MaterialTheme.typography.bodySmall) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        
        if (genAiUiState is UiState.Error) {
            Text(
                text = genAiUiState.toString(),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        OutlinedTextField(
            value = chatInput,
            onValueChange = { chatInput = it },
            label = { Text("Ask NutriCoach...") },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(
                    onClick = {
                        if (chatInput.isNotBlank() && !isLoadingAi) {
                            genAIViewModel.sendRequest(chatInput, currentUserIdString)
                            chatInput = ""
                        }
                    },
                    enabled = chatInput.isNotBlank() && !isLoadingAi
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send message",
                        tint = if (chatInput.isNotBlank()
                            && !isLoadingAi) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    )
                }
            }
        )
    }
}

@Composable
fun AiMessageBubble(text: String, useMarkdown: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Card(
            modifier = Modifier.weight(1f, fill = false), // Prevents the card from taking full width if text is short
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Box(modifier = Modifier.padding(12.dp)) {
                if (useMarkdown) {
                    MarkdownText(
                        markdown = text,
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
        Spacer(modifier = Modifier.weight(0.2f)) // Pushes the bubble to the left by adding space on the right
    }
}

@Composable
fun UserMessageBubble(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
        horizontalArrangement = Arrangement.End
    ) {
        Spacer(modifier = Modifier.weight(0.2f)) // Pushes the bubble to the right by adding space on the left
        Card(
            modifier = Modifier.weight(1f, fill = false), // Prevents the card from taking full width if text is short
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
        ) {
            Box(modifier = Modifier.padding(12.dp)) {
                MarkdownText(
                    markdown = text,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun InfoCard(
    title: String,
    icon: ImageVector? = null,
    onHistoryClick: (() -> Unit)? = null,
    onClearClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (icon != null) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge
                            .copy(fontWeight = FontWeight.Bold)
                    )
                }
                
                if (onHistoryClick != null && onClearClick != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onHistoryClick,
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .background(
                                    MaterialTheme.colorScheme.secondaryContainer,
                                    shape = MaterialTheme.shapes.small
                                )
                        ) {
                            Icon(
                                Icons.Filled.History,
                                contentDescription = "View Chat History",
                                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        
                        IconButton(
                            onClick = onClearClick,
                            modifier = Modifier.background(
                                MaterialTheme.colorScheme.errorContainer,
                                shape = MaterialTheme.shapes.small
                            )
                        ) {
                            Icon(
                                Icons.Filled.DeleteOutline, 
                                contentDescription = "Clear Chat History",
                                tint = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }
            content()
        }
    }
}