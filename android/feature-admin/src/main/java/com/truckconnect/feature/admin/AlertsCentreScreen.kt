package com.truckconnect.feature.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.truckconnect.core.design.R
import com.truckconnect.core.design.component.TcBottomNav
import com.truckconnect.core.design.component.TcButton
import com.truckconnect.core.design.component.TcButtonSize
import com.truckconnect.core.design.component.TcButtonVariant
import com.truckconnect.core.design.component.TcCard
import com.truckconnect.core.design.component.TcChip
import com.truckconnect.core.design.component.TcChipKind
import com.truckconnect.core.design.component.TcIconBox
import com.truckconnect.core.design.component.TcNavItems
import com.truckconnect.core.design.component.TcScreen
import com.truckconnect.core.design.component.TcSegmented
import com.truckconnect.core.design.theme.TcThemeState

@Composable
fun AlertsCentreScreen(
    onAlertActionClick: (String) -> Unit = {},
    onTabSelected: (Int) -> Unit = {}
) {
    val colors = TcThemeState.colors
    val typography = TcThemeState.typography
    val dimens = TcThemeState.dimens

    var filterIndex by remember { mutableIntStateOf(0) }
    val filterOptions = listOf("All", "SOS", "Delays", "Docs")

    val alerts = listOf(
        listOf(R.drawable.ic_sos, TcChipKind.Crit, "SOS from Rajan Nair", "Near Thrissur · 4 min ago", "Call"),
        listOf(R.drawable.ic_x, TcChipKind.Crit, "Job rejected", "TC-2026-000141 · “Vehicle issue”", "Reassign"),
        listOf(R.drawable.ic_clock, TcChipKind.Warn, "42 min late", "TC-2026-000136 · Sasidharan K", "View"),
        listOf(R.drawable.ic_warn, TcChipKind.Warn, "No response · 12 min", "TC-2026-000138 · Venugopal R", "Reassign"),
        listOf(R.drawable.ic_doc, TcChipKind.Warn, "Licence expires in 5 days", "Sasidharan K", "Open")
    )

    TcScreen(
        title = "Alerts",
        topBarActions = {
            TcChip(text = "4 open", kind = TcChipKind.Crit)
        },
        bottomNav = {
            TcBottomNav(
                tabs = TcNavItems.AdminTabs,
                selectedIndex = 4,
                onTabSelected = onTabSelected,
                isDriver = false
            )
        },
        isScrollable = true
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(dimens.Gap10)
        ) {
            TcSegmented(
                options = filterOptions,
                selectedIndex = filterIndex,
                onSelect = { filterIndex = it }
            )

            alerts.forEach { item ->
                val iconRes = item[0] as Int
                val kind = item[1] as TcChipKind
                val title = item[2] as String
                val subtitle = item[3] as String
                val actionLabel = item[4] as String

                TcCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(dimens.Gap10)
                    ) {
                        TcIconBox(iconRes = iconRes, kind = kind)
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = title,
                                style = typography.btnSm,
                                color = colors.text
                            )
                            Text(
                                text = subtitle,
                                style = typography.xsmall,
                                color = colors.muted
                            )
                        }
                        TcButton(
                            onClick = { onAlertActionClick(title) },
                            variant = TcButtonVariant.Outline,
                            size = TcButtonSize.Sm
                        ) {
                            Text(text = actionLabel)
                        }
                    }
                }
            }
        }
    }
}
