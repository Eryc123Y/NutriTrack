package com.example.fit2081a1_yang_xingyu_33533563.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview

/**
 * The common bottom navigation bar for all main user interface
 */

/**
 * Data class for navigation items to facilitate create navigation bar
 */
data class NavigationItem(
    val itemTitle: String, // Title of item
    val itemIcon: ImageVector, // Icon of item
    val itemScreenRoute: String  // Route of item, used to navigate to corresponding screen
)

@Preview(showBackground = true)
@Composable
fun BottomNavigationBar(
    currentRoute: String = "home",
    onNavigate: (String) -> Unit = {}
) {
    // List of items
    val navigationItems = listOf(
        NavigationItem(
            itemTitle = "Home",
            itemIcon = Icons.Outlined.Home,
            itemScreenRoute = "home"
        ),
        NavigationItem(
            itemTitle = "Insights",
            itemIcon = Icons.Outlined.Info,
            itemScreenRoute = "insights"
        ),
        NavigationItem(
            itemTitle = "NutriCoach",
            itemIcon = Icons.Rounded.CheckCircle,
            itemScreenRoute = "nutricoach"
        ),
        NavigationItem(
            itemTitle = "Settings",
            itemIcon = Icons.Rounded.Settings,
            itemScreenRoute = "settings"
        )
    )
    NavigationBar (modifier = Modifier.fillMaxWidth()){
        navigationItems.forEachIndexed {
                index, item ->
            NavigationBarItem(
                icon = { Icon(item.itemIcon, contentDescription = item.itemTitle) },
                label = { Text(item.itemTitle) },
                selected = currentRoute == item.itemScreenRoute,
                onClick = { onNavigate(item.itemScreenRoute) }
            )
        }
    }
}

/**
 * The common top navigation bar for all main user interface
 * @param title The title of the screen
 * @param showBackButton Whether to show the back button
 * @param onBackButtonClick The action to perform when the back button is clicked, this is to be
 * added under specific screen, when a function navigate to another screen is needed
 */
@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun TopNavigationBar(
    title: String = "Title",
    showBackButton: Boolean = true,
    onBackButtonClick: () -> Unit = {}
) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            if (showBackButton) {
                IconButton(onClick = onBackButtonClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        },
    )
}
