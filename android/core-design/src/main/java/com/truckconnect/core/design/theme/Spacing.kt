package com.truckconnect.core.design.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class Spacing(
    val extraSmall: Dp = 4.dp,
    val small: Dp = 8.dp,
    val medium: Dp = 12.dp,
    val default: Dp = 16.dp,
    val large: Dp = 20.dp,
    val extraLarge: Dp = 24.dp,
    val huge: Dp = 32.dp,
    
    // Accessibility Touch Targets
    val minTouchAdmin: Dp = 48.dp,
    val minTouchDriver: Dp = 56.dp,
    
    // Corner Radius
    val radiusSmall: Dp = 8.dp,
    val radiusMedium: Dp = 14.dp,
    val radiusLarge: Dp = 20.dp,
    val radiusCard: Dp = 24.dp,
    val radiusPill: Dp = 100.dp
)

val LocalSpacing = staticCompositionLocalOf { Spacing() }
