package com.truckconnect.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.truckconnect.core.design.R
import com.truckconnect.core.design.component.TcAppBar
import com.truckconnect.core.design.component.TcCard
import com.truckconnect.core.design.component.TcRadio
import com.truckconnect.core.design.component.TcScaffold
import com.truckconnect.core.design.theme.TcDimens
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
        Pair("മലയാളം", "Malayalam"),
        Pair("English", "English")
    )

    TcScaffold(
        topBar = {
            TcAppBar(
                title = if (isMalayalam) "ഭാഷ" else "Language",
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = dimens.ScreenPaddingHorizontal),
            verticalArrangement = Arrangement.spacedBy(dimens.Gap10)
        ) {
            Text(
                text = "Driver screens default to Malayalam",
                style = typography.small,
                color = colors.muted
            )

            languages.forEachIndexed { index, (nativeName, englishName) ->
                val isSelected = index == selectedIndex
                TcCard(
                    onClick = {
                        selectedIndex = index
                        onLanguageSelect(if (index == 0) "ml" else "en")
                    }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(dimens.Gap10)
                    ) {
                        TcRadio(
                            selected = isSelected,
                            onClick = {
                                selectedIndex = index
                                onLanguageSelect(if (index == 0) "ml" else "en")
                            }
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = nativeName,
                                style = typography.btnLg,
                                color = colors.text
                            )
                            Text(
                                text = englishName,
                                style = typography.small,
                                color = colors.muted
                            )
                        }
                    }
                }
            }

            TcCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(dimens.Gap10)
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_globe),
                        contentDescription = null,
                        modifier = Modifier.size(dimens.Size20),
                        tint = colors.text2
                    )
                    Text(
                        text = "Switch any time. No restart needed.",
                        style = typography.small,
                        color = colors.muted
                    )
                }
            }
        }
    }
}
