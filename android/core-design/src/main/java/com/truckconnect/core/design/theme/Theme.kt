package com.truckconnect.core.design.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

val LocalIsDriver = staticCompositionLocalOf { false }
val LocalIsMalayalam = staticCompositionLocalOf { false }

object TcThemeState {
    val colors: TcColors
        @Composable
        @ReadOnlyComposable
        get() = LocalTcColors.current

    val typography: TcTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalTcTypography.current

    val dimens: TcDimens
        get() = TcDimens

    val shapes: TcShapes
        get() = TcShapes

    val isDriver: Boolean
        @Composable
        @ReadOnlyComposable
        get() = LocalIsDriver.current

    val isMalayalam: Boolean
        @Composable
        @ReadOnlyComposable
        get() = LocalIsMalayalam.current
}

@Composable
fun TruckConnectTheme(
    theme: TcTheme = TcTheme.Indigo,
    mode: TcMode = TcMode.Auto,
    isDriver: Boolean = false,
    isMalayalam: Boolean = false,
    content: @Composable () -> Unit
) {
    val isDark = when (mode) {
        TcMode.Light -> false
        TcMode.Dark -> true
        TcMode.Auto -> isSystemInDarkTheme()
    }

    val colors = tcColors(theme, isDark)
    val typography = tcTypography(isDriver, isMalayalam)

    CompositionLocalProvider(
        LocalTcColors provides colors,
        LocalTcTypography provides typography,
        LocalIsDriver provides isDriver,
        LocalIsMalayalam provides isMalayalam
    ) {
        content()
    }
}
