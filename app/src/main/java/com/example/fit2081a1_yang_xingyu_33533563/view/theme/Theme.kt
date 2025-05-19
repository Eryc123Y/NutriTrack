package com.example.fit2081a1_yang_xingyu_33533563.view.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = DarkLeaf,
    secondary = DarkSoftBlue,
    tertiary = DarkMint,
    background = DarkBackgroundBlack,
    surface = DarkSurfaceBlack,
    onPrimary = DarkOnPrimaryWhite,
    onSecondary = DarkOnPrimaryWhite,
    onTertiary = DarkOnPrimaryWhite,
    onBackground = DarkOnSurfaceWhite,
    onSurface = DarkOnSurfaceWhite,
    error = Error,
    onError = DarkOnPrimaryWhite
)

private val LightColorScheme = lightColorScheme(
    primary = Leaf,
    secondary = SoftBlue,
    tertiary = Mint,
    background = LightBackgroundWhite,
    surface = LightSurfaceWhite,
    onPrimary = LightOnPrimaryBlack,
    onSecondary = LightOnPrimaryBlack,
    onTertiary = LightOnPrimaryBlack,
    onBackground = LightOnSurfaceBlack,
    onSurface = LightOnSurfaceBlack,
    error = Error,
    onError = LightOnPrimaryBlack

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun FIT2081A1Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}