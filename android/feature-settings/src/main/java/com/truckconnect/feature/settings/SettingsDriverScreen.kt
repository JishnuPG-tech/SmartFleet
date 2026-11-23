package com.truckconnect.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import com.truckconnect.core.design.component.TcIconBox
import com.truckconnect.core.design.component.TcListRow
import com.truckconnect.core.design.component.TcScaffold
import com.truckconnect.core.design.component.TcSegmented
import com.truckconnect.core.design.theme.TcDimens
import com.truckconnect.core.design.theme.TcThemeState

@Composable
fun SettingsDriverScreen(
    onBackClick: () -> Unit = {},
    onSettingClick: (String) -> Unit = {}
) {
    val colors = TcThemeState.colors
    val typography = TcThemeState.typography
    val dimens = TcThemeState.dimens
    val isMalayalam = TcThemeState.isMalayalam

    var textSizeIndex by remember { mutableIntStateOf(1) } // Large is selected
    val textSizeOptions = listOf("Normal", "Large")

    val settingsItems = listOf(
        Triple(R.drawable.ic_palette, "Theme", ""),
        Triple(R.drawable.ic_globe, if (isMalayalam) "ഭാഷ" else "Language", if (isMalayalam) "മലയാളം" else "English"),
        Triple(R.drawable.ic_vol, "Voice read-out", "On"),
        Triple(R.drawable.ic_bell, "Notifications", ""),
        Triple(R.drawable.ic_help, if (isMalayalam) "സഹായം" else "Help", "")
    )

    TcScaffold(
        topBar = {
            TcAppBar(
                title = if (isMalayalam) "ക്രമീകരണങ്ങൾ" else "Settings",
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
            // Driver settings list with larger spacing
            TcCard {
                Column {
                    settingsItems.forEachIndexed { index, (iconRes, label, sub) ->
                        TcListRow(
                            isLast = index == settingsItems.lastIndex,
                            onClick = { onSettingClick(label) }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(dimens.Gap12)
                            ) {
                                TcIconBox(iconRes = iconRes)
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = label,
                                        style = typography.btnLg,
                                        color = colors.text
                                    )
                                    if (sub.isNotEmpty()) {
                                        Text(
                                            text = sub,
                                            style = typography.small,
                                            color = colors.muted
                                        )
                                    }
                                }
                                Icon(
                                    imageVector = ImageVector.vectorResource(R.drawable.ic_chev),
                                    contentDescription = null,
                                    modifier = Modifier.size(dimens.Size18),
                                    tint = colors.text2
                                )
                            }
                        }
                    }
                }
            }

            // Text size Card
            TcCard {
                Column(verticalArrangement = Arrangement.spacedBy(dimens.Gap8)) {
                    Text(
                        text = "Text size",
                        style = typography.btnLg,
                        color = colors.text
                    )
                    TcSegmented(
                        options = textSizeOptions,
                        selectedIndex = textSizeIndex,
                        onSelect = { textSizeIndex = it }
                    )
                }
            }
        }
    }
}
