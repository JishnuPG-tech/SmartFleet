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
import com.truckconnect.core.design.component.TcCard
import com.truckconnect.core.design.component.TcScreen
import com.truckconnect.core.design.component.TcSegmented
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

    var selectedThemeIndex by remember { mutableIntStateOf(1) } // Emerald default

    val themes = listOf(
        Triple("Indigo", com.truckconnect.core.design.theme.ThemeSwatchIndigo, "indigo"),
        Triple("Emerald", com.truckconnect.core.design.theme.ThemeSwatchEmerald, "emerald"),
        Triple("Graphite", com.truckconnect.core.design.theme.ThemeSwatchGraphite, "graphite"),
        Triple("Sunset", com.truckconnect.core.design.theme.ThemeSwatchSunset, "sunset")
    )

    TcScreen(
        title = "Theme",
        onBackClick = onBackClick,
        isScrollable = true
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(dimens.Gap14)
        ) {
            // Mode Card
            TcCard {
                Column(verticalArrangement = Arrangement.spacedBy(dimens.Gap8)) {
                    Text(text = "Appearance", style = typography.btnSm, color = colors.text)
                    TcSegmented(
                        options = modeOptions,
                        selectedIndex = modeIndex,
                        onSelect = {
                            modeIndex = it
                            onModeSelect(modeOptions[it])
                        }
                    )
                }
            }

            // Accent Palette Card
            TcCard {
                Column(verticalArrangement = Arrangement.spacedBy(dimens.Gap10)) {
                    Text(text = "Primary accent color", style = typography.btnSm, color = colors.text)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(dimens.Gap8)
                    ) {
                        themes.forEachIndexed { index, (name, swatchColor, key) ->
                            val isSelected = index == selectedThemeIndex

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(dimens.Radius12))
                                    .background(if (isSelected) colors.accentSoft else colors.surface)
                                    .border(
                                        BorderStroke(
                                            width = if (isSelected) dimens.CardBorder * 2 else dimens.CardBorder,
                                            color = if (isSelected) colors.accent else colors.border
                                        ),
                                        RoundedCornerShape(dimens.Radius12)
                                    )
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null,
                                        onClick = {
                                            selectedThemeIndex = index
                                            onThemeSelect(key)
                                        }
                                    )
                                    .padding(dimens.Padding8),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(dimens.Gap6)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(dimens.Size28)
                                        .clip(RoundedCornerShape(dimens.Radius8))
                                        .background(swatchColor),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isSelected) {
                                        Icon(
                                            imageVector = ImageVector.vectorResource(R.drawable.ic_check),
                                            contentDescription = null,
                                            modifier = Modifier.size(dimens.Size16),
                                            tint = Color.White
                                        )
                                    }
                                }
                                Text(
                                    text = name,
                                    style = typography.caption,
                                    color = if (isSelected) colors.accent else colors.text
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
