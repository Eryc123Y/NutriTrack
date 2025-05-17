package com.example.fit2081a1_yang_xingyu_33533563.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fit2081a1_yang_xingyu_33533563.R
import com.example.fit2081a1_yang_xingyu_33533563.navigation.Screen
import com.example.fit2081a1_yang_xingyu_33533563.ui.components.BottomNavigationBar
import com.example.fit2081a1_yang_xingyu_33533563.util.SharedPreferencesManager
import com.example.fit2081a1_yang_xingyu_33533563.util.getGradientColorsForScore
import com.example.fit2081a1_yang_xingyu_33533563.data.viewmodel.ProfileViewModel
import androidx.compose.runtime.collectAsState
import com.example.fit2081a1_yang_xingyu_33533563.data.viewmodel.QuestionnaireViewModel
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    profileViewModel: ProfileViewModel,
    questionnaireViewModel: QuestionnaireViewModel,

    onNavigate: (String) -> Unit = {}
) {

    val context = LocalContext.current
    val prefManager = remember { SharedPreferencesManager.getInstance(context) }
    val currentUserIdFromPrefs = remember { prefManager.getCurrentUser() }

    // listen to the current user id from shared preferences and initialise the view model
    LaunchedEffect(currentUserIdFromPrefs) {
        currentUserIdFromPrefs?.let { userId ->
            profileViewModel.setUserId(userId)
        }
    }

    val scrollState = rememberScrollState()
    val score = profileViewModel.userTotalScore.collectAsState().value

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                currentRoute = Screen.Home.route,
                onNavigate = onNavigate
            )
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier.fillMaxSize() .padding(top = 8.dp),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                GreetingSection(profileViewModel.getUserName().toString())
                QuestionnaireStatusSection(onNavigate, questionnaireViewModel)
                NutritionMixImage()
                MyScoreDisplay(score?.toInt() ?: 0, onNavigate = onNavigate)
                FoodQualityScoreInfo()
            }
        }
    }
}

/**
 * Composable function for the greeting section
 */
@Composable
fun GreetingSection(userName: String) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = "Hello,",
            color = Color.Gray,
            style = TextStyle(fontSize = 16.sp)
        )
        // User greeting
        val gradientColors = listOf(Color.Cyan, Color.Blue)
        Text(
            text = userName,
            style = TextStyle(
                fontSize = 32.sp,
                brush = Brush.linearGradient(colors = gradientColors)
            ),
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Composable function for the questionnaire status section
 */
@Composable
fun QuestionnaireStatusSection(
    onNavigate: (String) -> Unit,
    questionnaireViewModel: QuestionnaireViewModel
    ) {
    val scope = androidx.compose.runtime.rememberCoroutineScope()
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),// Padding vertically to separate from greeting and other elements
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Text Column
        Text(
            text = "You've filled in your food questionnaire, but you can " +
                    "change details here.",
            textAlign = TextAlign.Justify,
            modifier = Modifier.weight(1f)
        )

        // Edit Button
        Button(
            onClick = {
                scope.launch {
                    // First reset the completed state
                    questionnaireViewModel.resetCompleted()
                    // Small delay before navigation to ensure the state is updated
                    kotlinx.coroutines.delay(200)
                    // Then navigate to questionnaire
                    onNavigate(Screen.Questionnaire.route)
                }
            },
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.padding(start = 8.dp)
        ) {
            Text(
                text = "Edit",
                fontSize = 16.sp
            )
            Icon(
                imageVector = Icons.Outlined.Edit,
                contentDescription = "Edit",
                modifier = Modifier.padding(start = 4.dp).size(18.dp)
            )
        }
    }
}

@Composable
fun NutritionMixImage() {
    Row (
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ){
        Image(
            painter = painterResource(id = R.drawable.nutrition),
            contentDescription = "Nutrition Mix",
            modifier = Modifier.size(200.dp)
        )
    }
}

@Composable
fun MyScoreDisplay(score: Int, onNavigate: (String) -> Unit) {
    // First row with title and "See all scores" button
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        Text(
            text = "My Score",
            style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        TextButton(onClick = { onNavigate(Screen.Insights.route) }) {
            Text(
                text = "See all scores",
                fontSize = 16.sp
            )
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                contentDescription = "See all scores",
                tint = Color.Gray,
                modifier = Modifier.size(20.dp)
            )
        }
    }

    // Second row with trend indicator + text on left, score on right
    var currentColours: List<Color> = getGradientColorsForScore(score)
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        // Left side with indicator and text grouped together
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .background(Color.LightGray, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = "Score Trend",
                    tint = currentColours[0],
                    modifier = Modifier.size(20.dp)
                )
            }

            Text(
                text = "Your Food Quality Score:",
                fontSize = 16.sp,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        // Right side with score

        Text(
            text = "$score/100",
            style = TextStyle(
                brush = Brush.linearGradient(
                    colors = currentColours
                ),
            ),
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(end = 16.dp)
        )
    }

    HorizontalDivider(
        modifier = Modifier.padding(8.dp)
    )
}

@Composable
fun FoodQualityScoreInfo() {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Text(
            text = "What is the Food Quality Score?",
            style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Your Food Quality Score provides an indication of the overall quality of your diet. " +
                    "It is calculated based on the types of foods you eat, and the frequency and quantity of " +
                    "consumption. The score is out of 100, with higher scores indicating a healthier diet. " +
                    "The score is updated each time you complete the food questionnaire.",
            textAlign = TextAlign.Justify,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "This personalized measurement considers various food groups including " +
            "vegetables, fruits, whole grains, and proteins to give you practical insights" +
            "for making healthier food choices.",
            textAlign = TextAlign.Justify
        )
    }
}