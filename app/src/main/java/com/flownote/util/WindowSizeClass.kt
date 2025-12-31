package com.flownote.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Window size classes for responsive layouts
 * Based on Material Design 3 guidelines
 */
enum class WindowSizeClass {
    COMPACT,  // < 600dp (phones)
    MEDIUM,   // 600dp - 840dp (small tablets, large phones)
    EXPANDED  // > 840dp (tablets)
}

/**
 * Get current window size class based on screen width
 */
@Composable
fun rememberWindowSizeClass(): WindowSizeClass {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    
    return when {
        screenWidth < 600.dp -> WindowSizeClass.COMPACT
        screenWidth < 840.dp -> WindowSizeClass.MEDIUM
        else -> WindowSizeClass.EXPANDED
    }
}

/**
 * Get adaptive padding based on window size
 */
fun WindowSizeClass.getHorizontalPadding(): Dp {
    return when (this) {
        WindowSizeClass.COMPACT -> 16.dp
        WindowSizeClass.MEDIUM -> 24.dp
        WindowSizeClass.EXPANDED -> 32.dp
    }
}

/**
 * Get max content width for centered layouts
 */
fun WindowSizeClass.getMaxContentWidth(): Dp {
    return when (this) {
        WindowSizeClass.COMPACT -> Dp.Unspecified // Full width
        WindowSizeClass.MEDIUM -> 720.dp
        WindowSizeClass.EXPANDED -> 840.dp
    }
}

/**
 * Get adaptive font size scale factor
 */
fun WindowSizeClass.getFontScale(): Float {
    return when (this) {
        WindowSizeClass.COMPACT -> 1.0f    // Normal size for phones
        WindowSizeClass.MEDIUM -> 1.05f    // Slightly larger for small tablets
        WindowSizeClass.EXPANDED -> 1.1f   // Larger for tablets
    }
}

/**
 * Scale a text size based on window size class
 */
fun WindowSizeClass.scaledSp(baseSp: Int): androidx.compose.ui.unit.TextUnit {
    return (baseSp * getFontScale()).sp
}

/**
 * Get adaptive spacing multiplier
 */
fun WindowSizeClass.getSpacingScale(): Float {
    return when (this) {
        WindowSizeClass.COMPACT -> 1.0f
        WindowSizeClass.MEDIUM -> 1.15f
        WindowSizeClass.EXPANDED -> 1.25f
    }
}
