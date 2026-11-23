package com.truckconnect.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.truckconnect.core.design.component.TcCard
import com.truckconnect.core.design.component.TcListRow
import com.truckconnect.core.design.component.TcScreen
import com.truckconnect.core.design.component.TcToggle
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
            "Job alerts (loud sound)" to true,
            "Cancellation & reassignment" to true,
            "Repeat push if not opened" to true,
            "Delay and incident alerts" to true,
            "Update reminders" to true,
            "Daily operational summary" to false
        )
    }

    TcScreen(
        title = "Notifications",
        onBackClick = onBackClick,
        isScrollable = true
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(dimens.Gap12)
        ) {
            Text(
                text = "Alert Preferences",
                style = typography.caption,
                color = colors.muted
            )

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
                                    style = typography.body,
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
        }
    }
}
