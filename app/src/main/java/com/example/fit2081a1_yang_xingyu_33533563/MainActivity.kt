package com.example.fit2081a1_yang_xingyu_33533563

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.example.fit2081a1_yang_xingyu_33533563.navigation.AppNavigation
import com.example.fit2081a1_yang_xingyu_33533563.view.theme.FIT2081A1Theme

/**
 * Main entry point for the NutriTrack Android application.
 * 
 * This activity sets up the application's main screen, handles splash screen display,
 * configures window settings for optimal performance, and initializes the Compose UI.
 */
class MainActivity : ComponentActivity() {
    /**
     * Called when the activity is first created.
     * Sets up splash screen, window optimizations, and initializes the Compose UI.
     * 
     * @param savedInstanceState Bundle containing the activity's previously saved state
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        // Install and configure splash screen
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        
        // Keep the splash screen visible until content is ready
        var isContentReady = false
        splashScreen.setKeepOnScreenCondition { !isContentReady }
        
        // Enable edge-to-edge display for immersive experience
        enableEdgeToEdge()
        
        // Optimize for animations by enabling hardware acceleration
        window.setFlags(
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
        )
        
        // Set render mode to improve animation performance
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        // Use hardware-accelerated rendering for better performance
        window.decorView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        
        // Set up the Compose UI
        setContent {
            // Get application instance for accessing dependencies
            val app = application as NutriTrackApp
            
            // State for dark mode toggle
            var isDarkMode by remember { mutableStateOf(false) }
            
            // Apply theme and set up navigation
            FIT2081A1Theme(darkTheme = isDarkMode) {
                AppNavigation(
                    viewModelProviderFactory = app.viewModelProviderFactory,
                    isDarkMode = isDarkMode,
                    onToggleDarkMode =  { newValue -> isDarkMode = newValue }
                )
            }
            
            // Signal that the content is ready to be displayed
            isContentReady = true
        }
    }
}



