package com.truckconnect.feature.driver

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.truckconnect.core.design.R
import com.truckconnect.core.design.component.TcBottomNav
import com.truckconnect.core.design.component.TcButton
import com.truckconnect.core.design.component.TcButtonSize
import com.truckconnect.core.design.component.TcButtonVariant
import com.truckconnect.core.design.component.TcCard
import com.truckconnect.core.design.component.TcChip
import com.truckconnect.core.design.component.TcChipKind
import com.truckconnect.core.design.component.TcIconButton
import com.truckconnect.core.design.component.TcNavItems
import com.truckconnect.core.design.component.TcScreen
import com.truckconnect.core.design.theme.TcThemeState

@Composable
fun DriverHomeEmptyScreen(
    onAlertsClick: () -> Unit = {},
    onJobAlertClick: () -> Unit = {},
    onTabSelected: (Int) -> Unit = {}
) {
    val colors = TcThemeState.colors
    val typography = TcThemeState.typography
    val dimens = TcThemeState.dimens
    val isMalayalam = TcThemeState.isMalayalam

    var isOnline by remember { mutableStateOf(true) }
    var hasActiveJob by remember { mutableStateOf(true) }

    TcScreen(
        title = if (isMalayalam) "സുപ്രഭാതം, Suresh" else "Good morning, Suresh",
        bottomNav = {
            TcBottomNav(
                tabs = TcNavItems.driverTabs(
                    homeLabel = if (isMalayalam) "ഹോം" else "Home",
                    historyLabel = if (isMalayalam) "ചരിത്രം" else "History",
                    alertsLabel = if (isMalayalam) "അറിയിപ്പുകൾ" else "Alerts",
                    profileLabel = if (isMalayalam) "പ്രൊഫൈൽ" else "Profile"
                ),
                selectedIndex = 0,
                onTabSelected = onTabSelected,
                isDriver = true
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Status bar row: Online toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isMalayalam) "ഡ്യൂട്ടി സ്റ്റാറ്റസ്" else "Duty Status",
                    style = typography.caption,
                    color = colors.muted
                )
                TcChip(
                    text = if (isOnline) {
                        if (isMalayalam) "ഓൺലൈൻ · ലഭ്യമാണ്" else "Online · Available"
                    } else {
                        if (isMalayalam) "ഓഫ്‌ലൈൻ" else "Offline"
                    },
                    kind = if (isOnline) TcChipKind.Ok else TcChipKind.Neutral,
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { isOnline = !isOnline }
                    )
                )
            }

            // Main body: If job assigned -> show assigned job card; if no job -> show clean empty state (OD-3)
            if (hasActiveJob) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = if (isMalayalam) "പുതിയ അസൈൻമെന്റ്" else "New shipment assigned",
                        style = typography.h2,
                        color = colors.text
                    )

                    TcCard(onClick = onJobAlertClick) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "TC-2026-000131",
                                    style = typography.btnSm,
                                    color = colors.text
                                )
                                TcChip(text = "Action required", kind = TcChipKind.Warn)
                            }

                            Text(
                                text = "Kasaragod → Malappuram",
                                style = typography.h2,
                                color = colors.text
                            )

                            Text(
                                text = "Vegetables · 8.5 t · 165 km · Reporting by 7:30 am",
                                style = typography.small,
                                color = colors.muted
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                TcButton(
                                    text = if (isMalayalam) "വിശദാംശങ്ങൾ കാണുക" else "View job details",
                                    onClick = onJobAlertClick,
                                    variant = TcButtonVariant.Primary,
                                    size = TcButtonSize.Lg,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            } else {
                // Clean empty state (OD-3)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .clip(RoundedCornerShape(28.dp))
                            .background(colors.accentSoft),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_truck),
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = colors.accent
                        )
                    }

                    Text(
                        text = if (isMalayalam) "ഇപ്പോൾ പുതിയ ജോലി ഇല്ല" else "No job right now",
                        style = typography.h1,
                        color = colors.text,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = if (isMalayalam)
                            "പുതിയ ജോലി വരുമ്പോൾ ശബ്ദത്തോടെ അറിയിപ്പ് ലഭിക്കും"
                        else
                            "You will receive an instant loud alert when a new shipment is assigned.",
                        style = typography.body,
                        color = colors.muted,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Bottom hint
            Text(
                text = if (isMalayalam) "സഹായത്തിന് ഡിസ്പാച്ചറുമായി ബന്ധപ്പെടുക" else "Need assistance? Call central dispatch",
                style = typography.note,
                color = colors.muted,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
        }
    }
}
