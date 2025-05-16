package com.example.fit2081a1_yang_xingyu_33533563

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import com.example.fit2081a1_yang_xingyu_33533563.navigation.AppNavigation
import com.example.fit2081a1_yang_xingyu_33533563.ui.theme.FIT2081A1Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Enable edge-to-edge display
        enableEdgeToEdge()
        
        // Optimize for animations by enabling hardware acceleration
        window.setFlags(
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
        )
        
        // Set render mode to improve animation performance
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        // Use hardware-accelerated rendering
        window.decorView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        
        setContent {
            val app = application as NutriTrackApp
            FIT2081A1Theme {
                // Init ViewModelFactory
                val viewModelProviderFactory = app.viewModelProviderFactory
                AppNavigation(viewModelProviderFactory)
            }
        }
    }
}



