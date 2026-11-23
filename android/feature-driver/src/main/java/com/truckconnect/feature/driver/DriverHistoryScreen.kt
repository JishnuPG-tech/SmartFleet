package com.truckconnect.feature.driver

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.text.style.TextAlign
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
import com.truckconnect.core.design.component.TcSegmented
import com.truckconnect.core.design.theme.TcThemeState

@Composable
fun DriverHistoryScreen(
    onTripClick: (String) -> Unit = {},
    onTabSelected: (Int) -> Unit = {}
) {
    val colors = TcThemeState.colors
    val typography = TcThemeState.typography
    val dimens = TcThemeState.dimens
    val isMalayalam = TcThemeState.isMalayalam

    var rangeIndex by remember { mutableIntStateOf(0) }
    val rangeOptions = listOf(
        if (isMalayalam) "ഈ ആഴ്ച" else "This week",
        if (isMalayalam) "ഈ മാസം" else "This month",
        if (isMalayalam) "എല്ലാം" else "All"
    )

    val historyTrips = listOf(
        Triple("TC-2026-000131", "Kasaragod → Malappuram", "Today · 2:36 pm"),
        Triple("TC-2026-000125", "Kochi → Kozhikode", "21 Nov · 5:12 pm"),
        Triple("TC-2026-000118", "Thrissur → Kannur", "19 Nov · 11:30 am"),
        Triple("TC-2026-000110", "Palakkad → Idukki", "18 Nov · 4:45 pm")
    )

    TcScreen(
        title = if (isMalayalam) "ചരിത്രം" else "Trip history",
        bottomNav = {
            TcBottomNav(
                tabs = TcNavItems.driverTabs(
                    homeLabel = if (isMalayalam) "ഹോം" else "Home",
                    historyLabel = if (isMalayalam) "ചരിത്രം" else "History",
                    alertsLabel = if (isMalayalam) "അറിയിപ്പുകൾ" else "Alerts",
                    profileLabel = if (isMalayalam) "പ്രൊഫൈൽ" else "Profile"
                ),
                selectedIndex = 1,
                onTabSelected = onTabSelected,
                isDriver = true
            )
        },
        isScrollable = true
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(dimens.Gap10)
        ) {
            // Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(dimens.Gap10)
            ) {
                TcCard(modifier = Modifier.weight(1f)) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isMalayalam) "ആകെ യാത്രകൾ" else "Total trips",
                            style = typography.small,
                            color = colors.muted,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "49",
                            style = typography.kpiValue,
                            color = colors.text,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                TcCard(modifier = Modifier.weight(1f)) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isMalayalam) "കൃത്യസമയം" else "On time",
                            style = typography.small,
                            color = colors.muted,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "94%",
                            style = typography.kpiValue,
                            color = colors.ok,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Time Range Filter
            TcSegmented(
                options = rangeOptions,
                selectedIndex = rangeIndex,
                onSelect = { rangeIndex = it }
            )

            Text(
                text = if (isMalayalam) "പൂർത്തിയാക്കിയ യാത്രകൾ" else "Completed trips",
                style = typography.h2,
                color = colors.text,
                modifier = Modifier.padding(top = 4.dp)
            )

            // Trips List Card with Chevron on every row
            TcCard {
                Column {
                    historyTrips.forEachIndexed { index, (tripId, route, time) ->
                        TcListRow(
                            isLast = index == historyTrips.lastIndex,
                            onClick = { onTripClick(tripId) }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(dimens.Gap12)
                            ) {
                                TcIconBox(
                                    iconRes = R.drawable.ic_check,
                                    kind = TcChipKind.Ok,
                                    modifier = Modifier.size(34.dp)
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = route,
                                        style = typography.btnSm,
                                        color = colors.text
                                    )
                                    Text(
                                        text = "$tripId · $time",
                                        style = typography.xsmall,
                                        color = colors.muted
                                    )
                                }
                                TcChip(
                                    text = if (isMalayalam) "എത്തിച്ചു" else "Delivered",
                                    kind = TcChipKind.Ok
                                )
                                Icon(
                                    imageVector = ImageVector.vectorResource(R.drawable.ic_chev),
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = colors.text2
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
