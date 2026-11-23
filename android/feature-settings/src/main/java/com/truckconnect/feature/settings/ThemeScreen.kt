package com.truckconnect.feature.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.truckconnect.core.design.R
import com.truckconnect.core.design.component.TcAppBar
import com.truckconnect.core.design.component.TcCard
import com.truckconnect.core.design.component.TcScaffold
import com.truckconnect.core.design.component.TcSegmented
import com.truckconnect.core.design.theme.TcDimens
import com.truckconnect.core.design.theme.TcShapes
import com.truckconnect.core.design.theme.TcThemeState

@Composable
fun ThemeScreen(
    onBackClick: () -> Unit = {},
    onThemeSelect: (String) -> Unit = {},
    onModeSelect: (String) -> Unit = {}
) {
    val colors = TcThemeState.colors
    val typography = TcThemeState.typography
    val dimens = TcThemeState.dimens

    var modeIndex by remember { mutableIntStateOf(2) } // Auto is selected
    val modeOptions = listOf("Light", "Dark", "Auto")

    var selectedThemeIndex by remember { mutableIntStateOf(0) } // Indigo selected

    val themes = listOf(
        Triple("Indigo", com.truckconnect.core.design.theme.ThemeSwatchIndigo, "indigo"),
        Triple("Emerald", com.truckconnect.core.design.theme.ThemeSwatchEmerald, "emerald"),
        Triple("Graphite", com.truckconnect.core.design.theme.ThemeSwatchGraphite, "graphite"),
        Triple("Sunset", com.truckconnect.core.design.theme.ThemeSwatchSunset, "sunset")
    )

    TcScaffold(
        topBar = {
            TcAppBar(title = "Theme", onBackClick = onBackClick)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = dimens.ScreenPaddingHorizontal),
            verticalArrangement = Arrangement.spacedBy(dimens.Gap12)
        ) {
            TcSegmented(
                options = modeOptions,
                selectedIndex = modeIndex,
                onSelect = {
                    modeIndex = it
                    onModeSelect(modeOptions[it])
                }
            )

            Text(
                text = "Pick a colour theme. All meet WCAG AA contrast.",
                style = typography.small,
                color = colors.muted
            )

            // 2x2 Grid of Theme Swatches
            for (row in 0 until 2) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(dimens.Gap10)
                ) {
                    for (col in 0 until 2) {
                        val index = row * 2 + col
                        val (name, color, key) = themes[index]
                        val isSelected = index == selectedThemeIndex

                        TcCard(
                            modifier = Modifier
                                .weight(1f)
                                .border(
                                    border = if (isSelected) BorderStroke(dimens.CardBorder * 2, colors.accent) else BorderStroke(dimens.CardBorder, colors.border),
                                    shape = TcShapes.Card
                                ),
                            onClick = {
                                selectedThemeIndex = index
                                onThemeSelect(key)
                            }
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(dimens.Gap8)) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(dimens.KeypadKeyHeight)
                                        .clip(TcShapes.InputAdmin)
                                        .background(color),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isSelected) {
                                        Icon(
                                            imageVector = ImageVector.vectorResource(R.drawable.ic_check),
                                            contentDescription = "Selected",
                                            modifier = Modifier.size(dimens.Size24),
                                            tint = Color.White
                                        )
                                    }
                                }
                                Text(
                                    text = name,
                                    style = typography.btnSm,
                                    color = colors.text
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
