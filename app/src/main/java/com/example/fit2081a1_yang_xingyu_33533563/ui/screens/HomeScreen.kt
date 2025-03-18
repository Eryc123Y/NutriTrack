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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fit2081a1_yang_xingyu_33533563.R
import com.example.fit2081a1_yang_xingyu_33533563.ui.components.BottomNavigationBar
import com.example.fit2081a1_yang_xingyu_33533563.util.getGradientColorsForScore


@Preview(showSystemUi = true)
@Composable
fun HomeScreen(
    userId: String = "Eric",
    onNavigate: (String) -> Unit = {}
) {
    Scaffold(
        topBar = {
//            TopNavigationBar(
//                title = "Home",
//                showBackButton = false,
//                onBackButtonClick = onBackClick
//            )
        },
        bottomBar = {
            BottomNavigationBar(
                currentRoute = "home",
                onNavigate = onNavigate
            )
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                GreetingSection(userId = userId)
                QuestionnaireStatusSection(onNavigate = onNavigate)
                NutritionMixImage()
                MyScoreDisplay()
                FoodQualityScoreInfo()
            }
        }
    }
}
/**
 * Composable function for the greeting section
 */
@Composable
fun GreetingSection(userId: String = "Eric") {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = "Hello,",
            color = Color.Gray,
            style = TextStyle(fontSize = 16.sp)
        )
        // User greeting
        val gradientColors = listOf(Color.Cyan, Color.Blue)
        Text(
            text = userId,
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
fun QuestionnaireStatusSection(onNavigate: (String) -> Unit) {
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
            onClick = { onNavigate("questionnaire") },
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
fun MyScoreDisplay() {
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

        TextButton(onClick = {}) {
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
    var score = 90
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
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "What is the Food Quality Score?",
            style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = "Your Food Quality Score provides an indication of the overall quality of your diet. " +
                    "It is calculated based on the types of foods you eat, and the frequency and quantity of " +
                    "consumption. The score is out of 100, with higher scores indicating a healthier diet. " +
                    "The score is updated each time you complete the food questionnaire.",
            textAlign = TextAlign.Justify,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "This personalized measurement considers various food groups including " +
            "vegetables, fruits, whole grains, and proteins to give you practical insights" +
            "for making healthier food choices.",
            textAlign = TextAlign.Justify
        )
    }
}