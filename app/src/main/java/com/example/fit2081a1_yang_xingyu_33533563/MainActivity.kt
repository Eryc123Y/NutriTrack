package com.example.fit2081a1_yang_xingyu_33533563

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.fit2081a1_yang_xingyu_33533563.navigation.AppNavigation
import com.example.fit2081a1_yang_xingyu_33533563.ui.theme.FIT2081A1Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FIT2081A1Theme {
                AppNavigation()
            }
        }
    }
}



