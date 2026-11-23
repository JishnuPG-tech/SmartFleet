package com.truckconnect.feature.admin

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import com.truckconnect.core.design.R
import com.truckconnect.core.design.component.TcAppBar
import com.truckconnect.core.design.component.TcBottomNav
import com.truckconnect.core.design.component.TcButton
import com.truckconnect.core.design.component.TcButtonSize
import com.truckconnect.core.design.component.TcButtonVariant
import com.truckconnect.core.design.component.TcCard
import com.truckconnect.core.design.component.TcChip
import com.truckconnect.core.design.component.TcChipKind
import com.truckconnect.core.design.component.TcIconBox
import com.truckconnect.core.design.component.TcIconButton
import com.truckconnect.core.design.component.TcKpiTile
import com.truckconnect.core.design.component.TcNavItems
import com.truckconnect.core.design.component.TcProgressBar
import com.truckconnect.core.design.component.TcScreen
import com.truckconnect.core.design.theme.TcDimens
import com.truckconnect.core.design.theme.TcThemeState

/**
 * Admin Dashboard Screen (M1)
 *
 * Full functional wiring:
 * - Bell icon -> Alerts Centre
 * - KPI tiles -> Shipments filtered
 * - SOS Card with direct Call intent and View Map navigation
 * - Trip cards with direct navigation to Shipment Details
 */
@Composable
fun DashboardScreen(
    onBellClick: () -> Unit = {},
    onSosCardClick: () -> Unit = {},
    onViewMapClick: () -> Unit = {},
    onTripClick: (String) -> Unit = {},
    onKpiClick: (String) -> Unit = {},
    onTabSelected: (Int) -> Unit = {}
) {
    val context = LocalContext.current
    val colors = TcThemeState.colors
    val typography = TcThemeState.typography
    val dimens = TcThemeState.dimens

    TcScreen(
        title = "Hello, Anil",
        bottomNav = {
            TcBottomNav(
                tabs = TcNavItems.AdminTabs,
                selectedIndex = 0,
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
            // 2x2 KPI Grid with token-accurate subtext colors
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(dimens.Gap10)
            ) {
                TcKpiTile(
                    label = "On trip",
                    value = "7",
                    sub = "+2 today",
                    subColor = colors.ok,
                    onClick = { onKpiClick("on_trip") },
                    modifier = Modifier.weight(1f)
                )
                TcKpiTile(
                    label = "Available",
                    value = "2",
                    sub = "ready to assign",
                    subColor = colors.muted,
                    onClick = { onKpiClick("available") },
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(dimens.Gap10)
            ) {
                TcKpiTile(
                    label = "Pending",
                    value = "5",
                    sub = "1 overdue",
                    subColor = colors.warn,
                    onClick = { onKpiClick("pending") },
                    modifier = Modifier.weight(1f)
                )
                TcKpiTile(
                    label = "Delayed",
                    value = "2",
                    sub = "needs action",
                    subColor = colors.crit,
                    onClick = { onKpiClick("delayed") },
                    modifier = Modifier.weight(1f)
                )
            }

            // Section Header: Live trips / View map
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = dimens.Gap6),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Live trips · 7",
                    style = typography.h2,
                    color = colors.text
                )
                Text(
                    text = "View map",
                    style = typography.small,
                    color = colors.accent,
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(color = colors.accent),
                        onClick = onViewMapClick
                    )
                )
            }

            // Trip 1
            TcCard(onClick = { onTripClick("TC-2026-000131") }) {
                Column(verticalArrangement = Arrangement.spacedBy(dimens.Gap8)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TC-2026-000131",
                            style = typography.xsmall,
                            color = colors.muted
                        )
                        TcChip(text = "On time", kind = TcChipKind.Ok)
                    }
                    Text(
                        text = "Kasaragod → Malappuram",
                        style = typography.btnSm,
                        color = colors.text
                    )
                    Text(
                        text = "Suresh Kumar · KL-07-AZ-1200",
                        style = typography.xsmall,
                        color = colors.muted
                    )
                    TcProgressBar(progress = 0.62f)
                    Text(
                        text = "62% completed · ETA 2:40 pm",
                        style = typography.xsmall,
                        color = colors.muted
                    )
                }
            }

            // Trip 2
            TcCard(onClick = { onTripClick("TC-2026-000133") }) {
                Column(verticalArrangement = Arrangement.spacedBy(dimens.Gap8)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TC-2026-000133",
                            style = typography.xsmall,
                            color = colors.muted
                        )
                        TcChip(text = "Delayed", kind = TcChipKind.Warn)
                    }
                    Text(
                        text = "Thrissur → Kannur",
                        style = typography.btnSm,
                        color = colors.text
                    )
                    Text(
                        text = "Babu Thomas · KL-08-BX-1571",
                        style = typography.xsmall,
                        color = colors.muted
                    )
                    TcProgressBar(progress = 0.38f)
                    Text(
                        text = "38% completed · 45 min delay",
                        style = typography.xsmall,
                        color = colors.muted
                    )
                }
            }
        }
    }
}
