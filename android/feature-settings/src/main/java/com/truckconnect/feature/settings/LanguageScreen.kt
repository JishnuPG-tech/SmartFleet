package com.truckconnect.feature.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.truckconnect.core.design.component.TcCard
import com.truckconnect.core.design.component.TcRadio
import com.truckconnect.core.design.component.TcScreen
import com.truckconnect.core.design.theme.TcThemeState

@Composable
fun LanguageScreen(
    onBackClick: () -> Unit = {},
    onLanguageSelect: (String) -> Unit = {}
) {
    val colors = TcThemeState.colors
    val typography = TcThemeState.typography
    val dimens = TcThemeState.dimens
    val isMalayalam = TcThemeState.isMalayalam

    var selectedIndex by remember { mutableIntStateOf(if (isMalayalam) 0 else 1) }

    val languages = listOf(
        Pair("മലയാളം", "Malayalam (Native)"),
        Pair("English", "English (International)")
    )

    TcScreen(
        title = if (isMalayalam) "ഭാഷ" else "Language",
        onBackClick = onBackClick,
        isScrollable = true
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(dimens.Gap12)
        ) {
            Text(
                text = if (isMalayalam) "ആപ്പ് ഭാഷ തിരഞ്ഞെടുക്കുക" else "Choose preferred app language",
                style = typography.caption,
                color = colors.muted
            )

            TcCard {
                Column {
                    languages.forEachIndexed { index, (primary, secondary) ->
                        val isSelected = index == selectedIndex
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = ripple(color = colors.accent),
                                    onClick = {
                                        selectedIndex = index
                                        onLanguageSelect(primary)
                                    }
                                )
                                .padding(vertical = dimens.Padding12),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = primary,
                                    style = typography.btnSm,
                                    color = if (isSelected) colors.accent else colors.text
                                )
                                Text(
                                    text = secondary,
                                    style = typography.xsmall,
                                    color = colors.muted
                                )
                            }
                            TcRadio(selected = isSelected, onClick = {
                                selectedIndex = index
                                onLanguageSelect(primary)
                            })
                        }
                    }
                }
            }
        }
    }
}
