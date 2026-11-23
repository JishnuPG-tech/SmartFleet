package com.truckconnect.feature.driver

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.truckconnect.core.design.R
import com.truckconnect.core.design.component.TcAppBar
import com.truckconnect.core.design.component.TcBottomNav
import com.truckconnect.core.design.component.TcCard
import com.truckconnect.core.design.component.TcChipKind
import com.truckconnect.core.design.component.TcIconBox
import com.truckconnect.core.design.component.TcListRow
import com.truckconnect.core.design.component.TcNavItems
import com.truckconnect.core.design.theme.TcDimens
import com.truckconnect.core.design.theme.TcThemeState

@Composable
fun DriverAlertsScreen(
    onTabSelected: (Int) -> Unit = {}
) {
    val colors = TcThemeState.colors
    val typography = TcThemeState.typography
    val dimens = TcThemeState.dimens
    val isMalayalam = TcThemeState.isMalayalam

    val alerts = listOf(
        listOf(R.drawable.ic_truck, "New job · Kasaragod", "6:00 am", TcChipKind.Accent),
        listOf(R.drawable.ic_check, "Delivered · TC-…125", "Yesterday", TcChipKind.Ok),
        listOf(R.drawable.ic_warn, "Licence expires in 5 days", "2 days ago", TcChipKind.Warn),
        listOf(R.drawable.ic_download, "New app version", "3 days ago", TcChipKind.Accent)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bg)
    ) {
        TcAppBar(title = if (isMalayalam) "അറിയിപ്പുകൾ" else "Alerts")

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = dimens.ScreenPaddingHorizontal)
        ) {
            TcCard {
                Column {
                    alerts.forEachIndexed { index, alert ->
                        val iconRes = alert[0] as Int
                        val title = alert[1] as String
                        val time = alert[2] as String
                        val kind = alert[3] as TcChipKind

                        TcListRow(isLast = index == alerts.lastIndex) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(dimens.Gap12)
                            ) {
                                TcIconBox(iconRes = iconRes, kind = kind)
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = title,
                                        style = typography.btnSm,
                                        color = colors.text
                                    )
                                    Text(
                                        text = time,
                                        style = typography.small,
                                        color = colors.muted
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        TcBottomNav(
            tabs = TcNavItems.driverTabs(
                homeLabel = if (isMalayalam) "ഹോം" else "Home",
                historyLabel = if (isMalayalam) "ചരിത്രം" else "History",
                alertsLabel = if (isMalayalam) "അറിയിപ്പുകൾ" else "Alerts",
                profileLabel = if (isMalayalam) "പ്രൊഫൈൽ" else "Profile"
            ),
            selectedIndex = 2,
            onTabSelected = onTabSelected,
            isDriver = true
        )
    }
}
