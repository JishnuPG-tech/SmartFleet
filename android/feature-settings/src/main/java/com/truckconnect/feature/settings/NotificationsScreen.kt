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
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.truckconnect.core.design.R
import com.truckconnect.core.design.component.TcAppBar
import com.truckconnect.core.design.component.TcCard
import com.truckconnect.core.design.component.TcListRow
import com.truckconnect.core.design.component.TcScaffold
import com.truckconnect.core.design.component.TcToggle
import com.truckconnect.core.design.theme.TcDimens
import com.truckconnect.core.design.theme.TcThemeState

@Composable
fun NotificationsScreen(
    onBackClick: () -> Unit = {}
) {
    val colors = TcThemeState.colors
    val typography = TcThemeState.typography
    val dimens = TcThemeState.dimens

    val notificationSettings = remember {
        mutableStateMapOf(
            "Job alerts (loud)" to true,
            "Cancellation & reassignment" to true,
            "Repeat push if not opened" to true,
            "Delay and SOS alerts" to true,
            "Update reminders" to false,
            "Daily summary" to false
        )
    }

    TcScaffold(
        topBar = {
            TcAppBar(title = "Notifications", onBackClick = onBackClick)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = dimens.ScreenPaddingHorizontal),
            verticalArrangement = Arrangement.spacedBy(dimens.Gap12)
        ) {
            // Toggles Card
            TcCard {
                Column {
                    notificationSettings.keys.toList().forEachIndexed { index, title ->
                        val isChecked = notificationSettings[title] ?: false
                        TcListRow(isLast = index == notificationSettings.size - 1) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = title,
                                    style = typography.btnSm,
                                    color = colors.text,
                                    modifier = Modifier.weight(1f)
                                )
                                TcToggle(
                                    checked = isChecked,
                                    onCheckedChange = { notificationSettings[title] = it }
                                )
                            }
                        }
                    }
                }
            }

            // Info Card
            TcCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(dimens.Gap10)
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_bell),
                        contentDescription = null,
                        modifier = Modifier.size(dimens.Size20),
                        tint = colors.text2
                    )
                    Text(
                        text = "Channels: Job alerts · Updates · General",
                        style = typography.small,
                        color = colors.muted
                    )
                }
            }
        }
    }
}
