package com.truckconnect.feature.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.truckconnect.core.design.component.TcAppBar
import com.truckconnect.core.design.component.TcCard
import com.truckconnect.core.design.component.TcChip
import com.truckconnect.core.design.component.TcChipKind
import com.truckconnect.core.design.component.TcScaffold
import com.truckconnect.core.design.component.TcSegmented
import com.truckconnect.core.design.theme.TcDimens
import com.truckconnect.core.design.theme.TcThemeState

/**
 * Audit Log Screen (X9)
 * Chronological log of administrative actions, status changes, and milestones.
 */
@Composable
fun AuditLogScreen(
    onBackClick: () -> Unit = {}
) {
    val colors = TcThemeState.colors
    val typography = TcThemeState.typography
    val dimens = TcThemeState.dimens

    var filterIndex by remember { mutableIntStateOf(0) }
    val filterOptions = listOf("All", "Drivers", "Shipments", "System")

    val allLogs = listOf(
        Triple("Anil Kumar · Approved driver Babu Thomas", "KL-08-BX-1571 · Verified DL & Aadhaar", "22 Nov, 10:30 am"),
        Triple("Anil Kumar · Reassigned shipment TC-2026-000138", "Idukki → Kasaragod · Venugopal R", "22 Nov, 9:15 am"),
        Triple("Suresh Kumar · Milestone: Reached pickup", "GPS ✓ Lat 10.85, Lng 76.05 · On time", "22 Nov, 7:50 am"),
        Triple("System · In-app Release v2.1.0 rolled out", "35% fleet devices staged · SHA-256 verified", "21 Nov, 6:00 pm"),
        Triple("Rajan Nair · Sent SOS emergency alert", "Near Thrissur · Engine breakdown", "21 Nov, 2:14 pm")
    )

    TcScaffold(
        topBar = {
            TcAppBar(title = "Audit log", onBackClick = onBackClick)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = dimens.ScreenPaddingHorizontal),
            verticalArrangement = Arrangement.spacedBy(dimens.Gap12)
        ) {
            TcSegmented(
                options = filterOptions,
                selectedIndex = filterIndex,
                onSelect = { filterIndex = it }
            )

            allLogs.forEach { (title, subtitle, time) ->
                TcCard {
                    Column(verticalArrangement = Arrangement.spacedBy(dimens.Gap4)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = title,
                                style = typography.btnSm,
                                color = colors.text,
                                modifier = Modifier.weight(1f)
                            )
                            TcChip(text = "Logged", kind = TcChipKind.Info)
                        }
                        Text(
                            text = subtitle,
                            style = typography.small,
                            color = colors.muted
                        )
                        Text(
                            text = time,
                            style = typography.xsmall,
                            color = colors.muted
                        )
                    }
                }
            }
        }
    }
}
