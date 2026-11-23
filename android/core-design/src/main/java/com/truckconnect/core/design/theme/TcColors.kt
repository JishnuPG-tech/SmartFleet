package com.truckconnect.core.design.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

enum class TcTheme {
    Indigo, Emerald, Graphite, Sunset
}

enum class TcMode {
    Light, Dark, Auto
}

data class TcColors(
    val bg: Color,
    val surface: Color,
    val surface2: Color,
    val border: Color,
    val border2: Color,
    val text: Color,
    val text2: Color,
    val muted: Color,
    val ok: Color,
    val warn: Color,
    val crit: Color,
    val info: Color,
    val neutral: Color,
    val accent: Color,
    val accentSoft: Color,
    val accentInk: Color,
    val mapLand: Color,
    val mapSea: Color,
    val mapLine: Color,
    val isDark: Boolean
) {
    // Derived colours
    val okChipBg: Color get() = ok.copy(alpha = 0.12f)
    val warnChipBg: Color get() = warn.copy(alpha = 0.12f)
    val critChipBg: Color get() = crit.copy(alpha = 0.12f)
    val infoChipBg: Color get() = info.copy(alpha = 0.12f)
    val neutralChipBg: Color get() = neutral.copy(alpha = 0.12f)
    val accentChipBg: Color get() = accent.copy(alpha = 0.12f)

    val okIconBg: Color get() = ok.copy(alpha = 0.13f)
    val warnIconBg: Color get() = warn.copy(alpha = 0.13f)
    val critIconBg: Color get() = crit.copy(alpha = 0.13f)
    val infoIconBg: Color get() = info.copy(alpha = 0.13f)
    val neutralIconBg: Color get() = neutral.copy(alpha = 0.13f)
    val accentIconBg: Color get() = accent.copy(alpha = 0.13f)

    val scrim: Color get() = Color(0x730A0C10) // #0A0C10 @ 45% = 0.45 * 255 = 114.75 ~ 0x73
}

// Light base
val LightBg = Color(0xFFF4F5F8)
val LightSurface = Color(0xFFFFFFFF)
val LightSurface2 = Color(0xFFF6F7FA)
val LightBorder = Color(0xFFE7E9EE)
val LightBorder2 = Color(0xFFD7DBE3)
val LightText = Color(0xFF101319)
val LightText2 = Color(0xFF4A5160)
val LightMuted = Color(0xFF8A90A0)
val LightOk = Color(0xFF0B7D45)
val LightWarn = Color(0xFFA96B00)
val LightCrit = Color(0xFFC9342F)
val LightInfo = Color(0xFF2563EB)
val LightNeutral = Color(0xFF6B7280)
val LightAccentInk = Color(0xFFFFFFFF)
val LightMapLand = Color(0xFFEEF1F6)
val LightMapSea = Color(0xFFF8FAFD)
val LightMapLine = Color(0xFFCFD5E1)

// Light accents
val LightIndigoAccent = Color(0xFF4F46E5)
val LightIndigoSoft = Color(0xFFEEF0FF)

val LightEmeraldAccent = Color(0xFF047857)
val LightEmeraldSoft = Color(0xFFE6F6EF)

val LightGraphiteAccent = Color(0xFF27272A)
val LightGraphiteSoft = Color(0xFFF0F0F1)

val LightSunsetAccent = Color(0xFFD9480F)
val LightSunsetSoft = Color(0xFFFFF0E8)

// Swatches for theme picker
val ThemeSwatchIndigo: Color = LightIndigoAccent
val ThemeSwatchEmerald: Color = LightEmeraldAccent
val ThemeSwatchGraphite: Color = LightGraphiteAccent
val ThemeSwatchSunset: Color = LightSunsetAccent

// Dark base
val DarkBg = Color(0xFF0C0E12)
val DarkSurface = Color(0xFF14171D)
val DarkSurface2 = Color(0xFF1B1F26)
val DarkBorder = Color(0xFF262B34)
val DarkBorder2 = Color(0xFF343A46)
val DarkText = Color(0xFFECEEF2)
val DarkText2 = Color(0xFFB6BCC9)
val DarkMuted = Color(0xFF7F8696)
val DarkOk = Color(0xFF3CC47A)
val DarkWarn = Color(0xFFE0A63A)
val DarkCrit = Color(0xFFF0716C)
val DarkInfo = Color(0xFF6AA1FF)
val DarkNeutral = Color(0xFF9AA1B0)
val DarkAccentInk = Color(0xFF0C0E12)
val DarkMapLand = Color(0xFF1C212B)
val DarkMapSea = Color(0xFF12151B)
val DarkMapLine = Color(0xFF323948)

// Dark accents
val DarkIndigoAccent = Color(0xFF8B83FF)
val DarkIndigoSoft = Color(0xFF232245)

val DarkEmeraldAccent = Color(0xFF34D399)
val DarkEmeraldSoft = Color(0xFF0F2A22)

val DarkGraphiteAccent = Color(0xFFD4D4D8)
val DarkGraphiteSoft = Color(0xFF25262B)

val DarkSunsetAccent = Color(0xFFFB923C)
val DarkSunsetSoft = Color(0xFF33200F)

fun tcColors(theme: TcTheme, isDark: Boolean): TcColors {
    return if (isDark) {
        when (theme) {
            TcTheme.Indigo -> TcColors(
                bg = DarkBg, surface = DarkSurface, surface2 = DarkSurface2,
                border = DarkBorder, border2 = DarkBorder2, text = DarkText,
                text2 = DarkText2, muted = DarkMuted, ok = DarkOk,
                warn = DarkWarn, crit = DarkCrit, info = DarkInfo,
                neutral = DarkNeutral, accent = DarkIndigoAccent,
                accentSoft = DarkIndigoSoft, accentInk = DarkAccentInk,
                mapLand = DarkMapLand, mapSea = DarkMapSea, mapLine = DarkMapLine,
                isDark = true
            )
            TcTheme.Emerald -> TcColors(
                bg = DarkBg, surface = DarkSurface, surface2 = DarkSurface2,
                border = DarkBorder, border2 = DarkBorder2, text = DarkText,
                text2 = DarkText2, muted = DarkMuted, ok = DarkOk,
                warn = DarkWarn, crit = DarkCrit, info = DarkInfo,
                neutral = DarkNeutral, accent = DarkEmeraldAccent,
                accentSoft = DarkEmeraldSoft, accentInk = DarkAccentInk,
                mapLand = DarkMapLand, mapSea = DarkMapSea, mapLine = DarkMapLine,
                isDark = true
            )
            TcTheme.Graphite -> TcColors(
                bg = DarkBg, surface = DarkSurface, surface2 = DarkSurface2,
                border = DarkBorder, border2 = DarkBorder2, text = DarkText,
                text2 = DarkText2, muted = DarkMuted, ok = DarkOk,
                warn = DarkWarn, crit = DarkCrit, info = DarkInfo,
                neutral = DarkNeutral, accent = DarkGraphiteAccent,
                accentSoft = DarkGraphiteSoft, accentInk = DarkAccentInk,
                mapLand = DarkMapLand, mapSea = DarkMapSea, mapLine = DarkMapLine,
                isDark = true
            )
            TcTheme.Sunset -> TcColors(
                bg = DarkBg, surface = DarkSurface, surface2 = DarkSurface2,
                border = DarkBorder, border2 = DarkBorder2, text = DarkText,
                text2 = DarkText2, muted = DarkMuted, ok = DarkOk,
                warn = DarkWarn, crit = DarkCrit, info = DarkInfo,
                neutral = DarkNeutral, accent = DarkSunsetAccent,
                accentSoft = DarkSunsetSoft, accentInk = DarkAccentInk,
                mapLand = DarkMapLand, mapSea = DarkMapSea, mapLine = DarkMapLine,
                isDark = true
            )
        }
    } else {
        when (theme) {
            TcTheme.Indigo -> TcColors(
                bg = LightBg, surface = LightSurface, surface2 = LightSurface2,
                border = LightBorder, border2 = LightBorder2, text = LightText,
                text2 = LightText2, muted = LightMuted, ok = LightOk,
                warn = LightWarn, crit = LightCrit, info = LightInfo,
                neutral = LightNeutral, accent = LightIndigoAccent,
                accentSoft = LightIndigoSoft, accentInk = LightAccentInk,
                mapLand = LightMapLand, mapSea = LightMapSea, mapLine = LightMapLine,
                isDark = false
            )
            TcTheme.Emerald -> TcColors(
                bg = LightBg, surface = LightSurface, surface2 = LightSurface2,
                border = LightBorder, border2 = LightBorder2, text = LightText,
                text2 = LightText2, muted = LightMuted, ok = LightOk,
                warn = LightWarn, crit = LightCrit, info = LightInfo,
                neutral = LightNeutral, accent = LightEmeraldAccent,
                accentSoft = LightEmeraldSoft, accentInk = LightAccentInk,
                mapLand = LightMapLand, mapSea = LightMapSea, mapLine = LightMapLine,
                isDark = false
            )
            TcTheme.Graphite -> TcColors(
                bg = LightBg, surface = LightSurface, surface2 = LightSurface2,
                border = LightBorder, border2 = LightBorder2, text = LightText,
                text2 = LightText2, muted = LightMuted, ok = LightOk,
                warn = LightWarn, crit = LightCrit, info = LightInfo,
                neutral = LightNeutral, accent = LightGraphiteAccent,
                accentSoft = LightGraphiteSoft, accentInk = LightAccentInk,
                mapLand = LightMapLand, mapSea = LightMapSea, mapLine = LightMapLine,
                isDark = false
            )
            TcTheme.Sunset -> TcColors(
                bg = LightBg, surface = LightSurface, surface2 = LightSurface2,
                border = LightBorder, border2 = LightBorder2, text = LightText,
                text2 = LightText2, muted = LightMuted, ok = LightOk,
                warn = LightWarn, crit = LightCrit, info = LightInfo,
                neutral = LightNeutral, accent = LightSunsetAccent,
                accentSoft = LightSunsetSoft, accentInk = LightAccentInk,
                mapLand = LightMapLand, mapSea = LightMapSea, mapLine = LightMapLine,
                isDark = false
            )
        }
    }
}

val LocalTcColors = staticCompositionLocalOf {
    tcColors(TcTheme.Indigo, false)
}
