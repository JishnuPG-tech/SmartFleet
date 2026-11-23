package com.truckconnect.core.design.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.truckconnect.core.design.theme.TcThemeState

/**
 * Universal Scaffold for all TruckConnect screens.
 *
 * Enforces Antigravity v2 rules:
 * - Automatically handles status bars + display cutout insets on the top bar.
 * - Automatically adds navigation bars insets beneath the bottom navigation bar and bottom sheets.
 * - Safely anchors FAB and SOS overlays above the bottom navigation bar and inside safe margins.
 * - Supports full-bleed accent backgrounds (Splash, Job Alert) while keeping content safe.
 * - Supports keyboard (IME) awareness for form screens.
 */
@Composable
fun TcScaffold(
    modifier: Modifier = Modifier,
    topBar: (@Composable () -> Unit)? = null,
    bottomBar: (@Composable () -> Unit)? = null,
    fab: (@Composable () -> Unit)? = null,
    overlays: (@Composable () -> Unit)? = null,
    backgroundColor: Color = TcThemeState.colors.bg,
    accentFullBleed: Boolean = false,
    imeAware: Boolean = false,
    content: @Composable (PaddingValues) -> Unit
) {
    val colors = TcThemeState.colors
    val resolvedBg = if (accentFullBleed) colors.accent else backgroundColor

    val safeTopInsets = WindowInsets.statusBars.union(WindowInsets.displayCutout)
    val topPaddingDp = safeTopInsets.asPaddingValues().calculateTopPadding()
    val navBarsPaddingDp = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(resolvedBg)
            .then(if (imeAware) Modifier.imePadding() else Modifier)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top App Bar with safe insets (status bar + display cutout)
            if (topBar != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(
                            if (!accentFullBleed) Modifier.background(colors.surface)
                            else Modifier.background(colors.accent)
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .windowInsetsPadding(safeTopInsets)
                    ) {
                        topBar()
                    }
                }
            }

            // Main Content Area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                content(
                    PaddingValues(
                        top = if (topBar == null && !accentFullBleed) topPaddingDp else TcThemeState.dimens.CardBorder * 0,
                        bottom = if (bottomBar == null && !accentFullBleed) navBarsPaddingDp else TcThemeState.dimens.CardBorder * 0
                    )
                )

                // Overlays anchored inside content bounds (e.g. SOS button)
                if (overlays != null) {
                    overlays()
                }

                // FAB anchored inside content bounds
                if (fab != null) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                    ) {
                        fab()
                    }
                }
            }

            // Bottom Bar with safe navigation bar insets
            if (bottomBar != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colors.surface)
                        .navigationBarsPadding()
                ) {
                    bottomBar()
                }
            }
        }
    }
}
