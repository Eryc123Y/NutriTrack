package com.example.fit2081a1_yang_xingyu_33533563.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.fit2081a1_yang_xingyu_33533563.navigation.Screen

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
    currentRoute: String = Screen.Home.route,
    onNavigate: (String) -> Unit = {}
) {
    // List of items
    val navigationItems = listOf(
        NavigationItem(
            itemTitle = "Home",
            itemIcon = Icons.Outlined.Home,
            itemScreenRoute = Screen.Home.route
        ),
        NavigationItem(
            itemTitle = "Insights",
            itemIcon = Icons.Outlined.Info,
            itemScreenRoute = Screen.Insights.route
        ),
        NavigationItem(
            itemTitle = "NutriCoach",
            itemIcon = Icons.Outlined.CheckCircle,
            itemScreenRoute = Screen.NutriCoach.route
        ),
        NavigationItem(
            itemTitle = "Settings",
            itemIcon = Icons.Outlined.Settings,
            itemScreenRoute = Screen.Settings.route
        )
    )
    
    NavigationBar(
        modifier = Modifier.fillMaxWidth(),
        tonalElevation = 8.dp
    ) {
        navigationItems.forEachIndexed { index, item ->
            val selected = currentRoute == item.itemScreenRoute
            
            // Animate icon size when selected
            val iconSize by animateDpAsState(
                targetValue = if (selected) 28.dp else 24.dp,
                animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing), 
                label = "icon size"
            )
            
            // Animate colors
            val iconColor by animateColorAsState(
                targetValue = if (selected) 
                    MaterialTheme.colorScheme.primary 
                else 
                    MaterialTheme.colorScheme.onSurfaceVariant,
                animationSpec = tween(300),
                label = "icon color"
            )
            
            val textColor by animateColorAsState(
                targetValue = if (selected) 
                    MaterialTheme.colorScheme.primary 
                else 
                    MaterialTheme.colorScheme.onSurfaceVariant,
                animationSpec = tween(300),
                label = "text color"
            )
            
            NavigationBarItem(
                icon = {
                    Box(contentAlignment = Alignment.Center) {
                        // Add indicator dot when selected
                        if (selected) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .padding(top = 2.dp)
                                    .size(5.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary)
                            )
                        }
                        
                        Icon(
                            imageVector = item.itemIcon,
                            contentDescription = item.itemTitle,
                            modifier = Modifier.size(iconSize),
                            tint = iconColor
                        )
                    }
                },
                label = { 
                    Text(
                        text = item.itemTitle,
                        color = textColor
                    ) 
                },
                selected = selected,
                onClick = { onNavigate(item.itemScreenRoute) },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
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
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        state = rememberTopAppBarState(),
        flingAnimationSpec = rememberSplineBasedDecay(),
        snapAnimationSpec = tween(300, easing = LinearOutSlowInEasing)
    )
    
    TopAppBar(
        title = { 
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            ) 
        },
        navigationIcon = {
            if (showBackButton) {
                IconButton(
                    onClick = onBackButtonClick,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .clip(CircleShape)
                        .size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
            scrolledContainerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            navigationIconContentColor = MaterialTheme.colorScheme.primary
        ),
        scrollBehavior = scrollBehavior
    )
}
