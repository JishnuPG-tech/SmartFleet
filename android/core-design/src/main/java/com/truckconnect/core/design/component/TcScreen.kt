package com.truckconnect.core.design.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.truckconnect.core.design.R
import com.truckconnect.core.design.theme.TcThemeState

/**
 * Section 4: Universal Top Bar for TruckConnect
 * - Back button (left, 48 dp touch target)
 * - Title (1 line, ellipsized, text color)
 * - Up to 2 actions (right)
 * - Handles status bar & display cutout insets internally
 * - Scroll-aware divider
 */
@Composable
fun TcTopBar(
    title: String,
    modifier: Modifier = Modifier,
    onBackClick: (() -> Unit)? = null,
    actions: (@Composable RowScope.() -> Unit)? = null,
    showDivider: Boolean = false,
    backgroundColor: Color = TcThemeState.colors.bg
) {
    val colors = TcThemeState.colors
    val typography = TcThemeState.typography
    val dimens = TcThemeState.dimens

    val safeTopInsets = WindowInsets.statusBars.union(WindowInsets.displayCutout)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .windowInsetsPadding(safeTopInsets)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = dimens.ScreenPaddingHorizontal,
                    end = dimens.ScreenPaddingHorizontal,
                    top = 10.dp,
                    bottom = 10.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onBackClick != null) {
                TcIconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(48.dp)
                        .padding(end = 6.dp)
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_back),
                        contentDescription = "Back",
                        modifier = Modifier.size(dimens.IconSizeDefault),
                        tint = colors.text
                    )
                }
            }

            Text(
                text = title,
                style = typography.appBarTitle,
                color = colors.text,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = if (onBackClick != null) 6.dp else 0.dp)
            )

            if (actions != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    actions()
                }
            }
        }

        if (showDivider) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(colors.border)
            )
        }
    }
}

/**
 * Section 4: TcScreen — The universal root of every screen in TruckConnect.
 *
 * Rules:
 * - Centralized system bars (status bar + display cutout pill + 3-button navigation bar).
 * - Optional top bar or integrated title with back navigation.
 * - Pinned bottom actions sit safely ABOVE the navigation bar inset with 16 dp margin.
 * - Destructive actions use crit text on outline (or solid crit for final confirm).
 * - Never grey disabled-looking text for enabled buttons.
 */
@Composable
fun TcScreen(
    title: String? = null,
    onBackClick: (() -> Unit)? = null,
    topBarActions: (@Composable RowScope.() -> Unit)? = null,
    customTopBar: (@Composable () -> Unit)? = null,
    bottomNav: (@Composable () -> Unit)? = null,
    pinnedBottomActions: (@Composable () -> Unit)? = null,
    fab: (@Composable () -> Unit)? = null,
    overlays: (@Composable () -> Unit)? = null,
    backgroundColor: Color = TcThemeState.colors.bg,
    accentFullBleed: Boolean = false,
    imeAware: Boolean = false,
    isScrollable: Boolean = false,
    useDefaultPadding: Boolean = true,
    content: @Composable () -> Unit
) {
    val colors = TcThemeState.colors
    val dimens = TcThemeState.dimens
    val resolvedBg = if (accentFullBleed) colors.accent else backgroundColor

    val safeTopInsets = WindowInsets.statusBars.union(WindowInsets.displayCutout)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(resolvedBg)
            .then(if (imeAware) Modifier.imePadding() else Modifier)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Bar
            if (customTopBar != null) {
                customTopBar()
            } else if (title != null) {
                TcTopBar(
                    title = title,
                    onBackClick = onBackClick,
                    actions = topBarActions,
                    backgroundColor = resolvedBg
                )
            } else if (!accentFullBleed) {
                // If no top bar and not full bleed, still pad for safe status bar & cutout
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(safeTopInsets)
                )
            }

            // Main Content Area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                val paddingModifier = if (useDefaultPadding) {
                    Modifier.padding(horizontal = dimens.ScreenPaddingHorizontal, vertical = 8.dp)
                } else {
                    Modifier
                }

                if (isScrollable) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .then(paddingModifier)
                    ) {
                        content()
                        Spacer(modifier = Modifier.height(28.dp))
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .then(paddingModifier)
                    ) {
                        content()
                    }
                }

                // Overlays anchored inside content bounds (e.g. SOS button)
                if (overlays != null) {
                    overlays()
                }

                // FAB anchored inside content bounds
                if (fab != null) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(bottom = 16.dp, end = 8.dp)
                    ) {
                        fab()
                    }
                }
            }

            // Pinned Bottom Action Buttons (OD-6 / Section 4: 16 dp above navigation bar inset)
            if (pinnedBottomActions != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(resolvedBg)
                        .padding(horizontal = dimens.ScreenPaddingHorizontal)
                        .padding(bottom = 12.dp)
                        .navigationBarsPadding()
                ) {
                    pinnedBottomActions()
                }
            }

            // Bottom Navigation Bar (safely padded for 3-button / gesture navigation)
            if (bottomNav != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colors.surface)
                        .navigationBarsPadding()
                ) {
                    bottomNav()
                }
            }
        }
    }
}
