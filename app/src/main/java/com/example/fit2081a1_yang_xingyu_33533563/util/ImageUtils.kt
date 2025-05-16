package com.example.fit2081a1_yang_xingyu_33533563.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.example.fit2081a1_yang_xingyu_33533563.R // Assuming R class is in this package
import android.util.Log

@Composable
fun getFoodImagePainter(foodDefId: String): Painter {
    val context = LocalContext.current
    val resourceName = "foodimg_$foodDefId"
    // It's generally safer to check for 0 return value from getIdentifier
    val resourceId = context.resources.getIdentifier(resourceName, "drawable", context.packageName)

    return if (resourceId != 0) {
        painterResource(id = resourceId)
    } else {
        // Fallback to a default placeholder image if the specific one isn't found.
        // Replace with your actual placeholder drawable.
        painterResource(id = R.drawable.ic_launcher_background) // Example placeholder
    }
}

@Composable
fun getPersonaImagePainter(personaId: String): Painter {
    val context = LocalContext.current
    val resourceName = "persona_$personaId"
    Log.d("ImageDebug", "Attempting to load image. Persona ID: $personaId, Resource Name: $resourceName, Package: ${context.packageName}")

    val resourceId = context.resources.getIdentifier(resourceName, "drawable", context.packageName)
    Log.d("ImageDebug", "Resource ID found: $resourceId (0 means not found)")

    return if (resourceId != 0) {
        painterResource(id = resourceId)
    } else {
        Log.e("ImageDebug", "Fallback image used for persona_$personaId")
        // Fallback to a default placeholder image
        painterResource(id = R.drawable.ic_launcher_foreground) // Example placeholder, change as needed
    }
} 