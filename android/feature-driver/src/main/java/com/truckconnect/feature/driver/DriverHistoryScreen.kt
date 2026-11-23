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
import androidx.compose.ui.text.style.TextAlign
import com.truckconnect.core.design.R
import com.truckconnect.core.design.component.TcAppBar
import com.truckconnect.core.design.component.TcBottomNav
import com.truckconnect.core.design.component.TcCard
import com.truckconnect.core.design.component.TcChipKind
import com.truckconnect.core.design.component.TcIconBox
import com.truckconnect.core.design.component.TcNavItems
import com.truckconnect.core.design.theme.TcDimens
import com.truckconnect.core.design.theme.TcThemeState

@Composable
fun DriverHistoryScreen(
    onTabSelected: (Int) -> Unit = {}
) {
    val colors = TcThemeState.colors
    val typography = TcThemeState.typography
    val dimens = TcThemeState.dimens
    val isMalayalam = TcThemeState.isMalayalam

    val historyTrips = listOf(
        "Kasaragod → Malappuram" to "Today · 2:36 pm",
        "Kochi → Kozhikode" to "21 Nov",
        "Thrissur → Kannur" to "19 Nov",
        "Palakkad → Idukki" to "18 Nov"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bg)
    ) {
        TcAppBar(title = if (isMalayalam) "ചരിത്രം" else "History")

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = dimens.ScreenPaddingHorizontal),
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
                            text = if (isMalayalam) "യാത്രകൾ" else "Trips",
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
                            text = "82%",
                            style = typography.kpiValue,
                            color = colors.text,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Trips List
            historyTrips.forEach { (route, date) ->
                TcCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(dimens.Gap10)
                    ) {
                        TcIconBox(iconRes = R.drawable.ic_check, kind = TcChipKind.Ok)
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = route,
                                style = typography.btnLg,
                                color = colors.text
                            )
                            Text(
                                text = date,
                                style = typography.small,
                                color = colors.muted
                            )
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
            selectedIndex = 1,
            onTabSelected = onTabSelected,
            isDriver = true
        )
    }
}
