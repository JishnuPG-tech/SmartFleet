package com.truckconnect.core.design.theme

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TcColorsTest {

    @Test
    fun testLightIndigoColors() {
        val colors = tcColors(TcTheme.Indigo, isDark = false)
        assertFalse(colors.isDark)
        assertEquals(Color(0xFFF4F5F8), colors.bg)
        assertEquals(Color(0xFFFFFFFF), colors.surface)
        assertEquals(Color(0xFFF6F7FA), colors.surface2)
        assertEquals(Color(0xFFE7E9EE), colors.border)
        assertEquals(Color(0xFFD7DBE3), colors.border2)
        assertEquals(Color(0xFF101319), colors.text)
        assertEquals(Color(0xFF4A5160), colors.text2)
        assertEquals(Color(0xFF8A90A0), colors.muted)
        assertEquals(Color(0xFF0B7D45), colors.ok)
        assertEquals(Color(0xFFA96B00), colors.warn)
        assertEquals(Color(0xFFC9342F), colors.crit)
        assertEquals(Color(0xFF2563EB), colors.info)
        assertEquals(Color(0xFF6B7280), colors.neutral)
        assertEquals(Color(0xFF4F46E5), colors.accent)
        assertEquals(Color(0xFFEEF0FF), colors.accentSoft)
        assertEquals(Color(0xFFFFFFFF), colors.accentInk)
        assertEquals(Color(0xFFEEF1F6), colors.mapLand)
        assertEquals(Color(0xFFF8FAFD), colors.mapSea)
        assertEquals(Color(0xFFCFD5E1), colors.mapLine)
    }

    @Test
    fun testDarkIndigoColors() {
        val colors = tcColors(TcTheme.Indigo, isDark = true)
        assertTrue(colors.isDark)
        assertEquals(Color(0xFF0C0E12), colors.bg)
        assertEquals(Color(0xFF14171D), colors.surface)
        assertEquals(Color(0xFF1B1F26), colors.surface2)
        assertEquals(Color(0xFF262B34), colors.border)
        assertEquals(Color(0xFF343A46), colors.border2)
        assertEquals(Color(0xFFECEEF2), colors.text)
        assertEquals(Color(0xFFB6BCC9), colors.text2)
        assertEquals(Color(0xFF7F8696), colors.muted)
        assertEquals(Color(0xFF3CC47A), colors.ok)
        assertEquals(Color(0xFFE0A63A), colors.warn)
        assertEquals(Color(0xFFF0716C), colors.crit)
        assertEquals(Color(0xFF6AA1FF), colors.info)
        assertEquals(Color(0xFF9AA1B0), colors.neutral)
        assertEquals(Color(0xFF8B83FF), colors.accent)
        assertEquals(Color(0xFF232245), colors.accentSoft)
        assertEquals(Color(0xFF0C0E12), colors.accentInk)
        assertEquals(Color(0xFF1C212B), colors.mapLand)
        assertEquals(Color(0xFF12151B), colors.mapSea)
        assertEquals(Color(0xFF323948), colors.mapLine)
    }

    @Test
    fun testEmeraldAccents() {
        val light = tcColors(TcTheme.Emerald, isDark = false)
        assertEquals(Color(0xFF047857), light.accent)
        assertEquals(Color(0xFFE6F6EF), light.accentSoft)

        val dark = tcColors(TcTheme.Emerald, isDark = true)
        assertEquals(Color(0xFF34D399), dark.accent)
        assertEquals(Color(0xFF0F2A22), dark.accentSoft)
    }

    @Test
    fun testGraphiteAccents() {
        val light = tcColors(TcTheme.Graphite, isDark = false)
        assertEquals(Color(0xFF27272A), light.accent)
        assertEquals(Color(0xFFF0F0F1), light.accentSoft)

        val dark = tcColors(TcTheme.Graphite, isDark = true)
        assertEquals(Color(0xFFD4D4D8), dark.accent)
        assertEquals(Color(0xFF25262B), dark.accentSoft)
    }

    @Test
    fun testSunsetAccents() {
        val light = tcColors(TcTheme.Sunset, isDark = false)
        assertEquals(Color(0xFFD9480F), light.accent)
        assertEquals(Color(0xFFFFF0E8), light.accentSoft)

        val dark = tcColors(TcTheme.Sunset, isDark = true)
        assertEquals(Color(0xFFFB923C), dark.accent)
        assertEquals(Color(0xFF33200F), dark.accentSoft)
    }

    @Test
    fun testDerivedAlphaTokens() {
        val light = tcColors(TcTheme.Indigo, isDark = false)
        assertEquals(0.12f, light.okChipBg.alpha, 0.01f)
        assertEquals(0.13f, light.okIconBg.alpha, 0.01f)
        assertEquals(0.45f, light.scrim.alpha, 0.01f)
    }
}
