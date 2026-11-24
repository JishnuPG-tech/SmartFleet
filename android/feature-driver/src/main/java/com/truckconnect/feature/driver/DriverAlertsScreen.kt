package com.truckconnect.feature.driver

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.truckconnect.core.design.R
import com.truckconnect.core.design.component.TcBottomNav
import com.truckconnect.core.design.component.TcCard
import com.truckconnect.core.design.component.TcChip
import com.truckconnect.core.design.component.TcChipKind
import com.truckconnect.core.design.component.TcIconBox
import com.truckconnect.core.design.component.TcListRow
import com.truckconnect.core.design.component.TcNavItems
import com.truckconnect.core.design.component.TcScreen
import com.truckconnect.core.design.theme.TcThemeState

data class DriverAlertItem(
    val iconRes: Int,
    val title: String,
    val subtitle: String,
    val kind: TcChipKind,
    val isUnread: Boolean = false
)

@Composable
fun DriverAlertsScreen(
    onTabSelected: (Int) -> Unit = {}
) {
    val colors = TcThemeState.colors
    val typography = TcThemeState.typography
    val dimens = TcThemeState.dimens
    val isMalayalam = TcThemeState.isMalayalam

    val todayAlerts = listOf(
        DriverAlertItem(
            iconRes = R.drawable.ic_truck,
            title = if (isMalayalam) "പുതിയ ലോഡ് · Kasaragod" else "New shipment · Kasaragod",
            subtitle = "6:00 am · TC-2026-000131",
            kind = TcChipKind.Accent,
            isUnread = true
        ),
        DriverAlertItem(
            iconRes = R.drawable.ic_check,
            title = if (isMalayalam) "എത്തിച്ചു · TC-2026-000125" else "Delivered · TC-2026-000125",
            subtitle = "Yesterday, 4:15 pm",
            kind = TcChipKind.Ok,
            isUnread = false
        )
    )

    val earlierAlerts = listOf(
        DriverAlertItem(
            iconRes = R.drawable.ic_warn,
            title = if (isMalayalam) "ലൈസൻസ് കാലാവധി 5 ദിവസത്തിനകം" else "Licence expires in 5 days",
            subtitle = "2 days ago · Renewal required",
            kind = TcChipKind.Warn,
            isUnread = false
        ),
        DriverAlertItem(
            iconRes = R.drawable.ic_download,
            title = if (isMalayalam) "പുതിയ ആപ്പ് പതിപ്പ് v2.1.0" else "New app version v2.1.0",
            subtitle = "3 days ago · Tap to install",
            kind = TcChipKind.Info,
            isUnread = false
        )
    )

    TcScreen(
        title = if (isMalayalam) "അറിയിപ്പുകൾ" else "Alerts",
        bottomNav = {
            TcBottomNav(
                tabs = TcNavItems.driverTabs(
                    homeLabel = if (isMalayalam) "ഹോം" else "Home",
                    historyLabel = if (isMalayalam) "ചരിത്രം" else "History",
                    profileLabel = if (isMalayalam) "പ്രൊഫൈൽ" else "Profile",
                    settingsLabel = if (isMalayalam) "ക്രമീകരണങ്ങൾ" else "Settings"
                ),
                selectedIndex = 2,
                onTabSelected = onTabSelected,
                isDriver = true
            )
        },
        isScrollable = true
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(dimens.Gap12)
        ) {
            // Today Section
            Text(
                text = if (isMalayalam) "ഇന്ന്" else "Today",
                style = typography.caption,
                color = colors.muted
            )

            TcCard {
                Column {
                    todayAlerts.forEachIndexed { index, alert ->
                        TcListRow(
                            isLast = index == todayAlerts.lastIndex
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(dimens.Gap12)
                            ) {
                                if (alert.isUnread) {
                                    Box(
                                        modifier = Modifier
                                            .width(3.dp)
                                            .height(28.dp)
                                            .clip(RoundedCornerShape(2.dp))
                                            .background(colors.accent)
                                    )
                                }

                                TcIconBox(
                                    iconRes = alert.iconRes,
                                    kind = alert.kind,
                                    modifier = Modifier.size(34.dp)
                                )

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = alert.title,
                                        style = if (alert.isUnread) typography.btnSm else typography.body,
                                        color = colors.text
                                    )
                                    Text(
                                        text = alert.subtitle,
                                        style = typography.xsmall,
                                        color = colors.muted
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Earlier Section
            Text(
                text = if (isMalayalam) "മുമ്പ്" else "Earlier",
                style = typography.caption,
                color = colors.muted,
                modifier = Modifier.padding(top = 4.dp)
            )

            TcCard {
                Column {
                    earlierAlerts.forEachIndexed { index, alert ->
                        TcListRow(
                            isLast = index == earlierAlerts.lastIndex
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(dimens.Gap12)
                            ) {
                                TcIconBox(
                                    iconRes = alert.iconRes,
                                    kind = alert.kind,
                                    modifier = Modifier.size(34.dp)
                                )

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = alert.title,
                                        style = typography.body,
                                        color = colors.text
                                    )
                                    Text(
                                        text = alert.subtitle,
                                        style = typography.xsmall,
                                        color = colors.muted
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
