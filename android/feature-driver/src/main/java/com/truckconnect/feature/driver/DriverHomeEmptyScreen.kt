package com.truckconnect.feature.driver

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import com.truckconnect.core.design.R
import com.truckconnect.core.design.component.TcAppBar
import com.truckconnect.core.design.component.TcBottomNav
import com.truckconnect.core.design.component.TcButton
import com.truckconnect.core.design.component.TcButtonSize
import com.truckconnect.core.design.component.TcButtonVariant
import com.truckconnect.core.design.component.TcCard
import com.truckconnect.core.design.component.TcChip
import com.truckconnect.core.design.component.TcChipKind
import com.truckconnect.core.design.component.TcIconButton
import com.truckconnect.core.design.component.TcNavItems
import com.truckconnect.core.design.component.TcScaffold
import com.truckconnect.core.design.theme.TcDimens
import com.truckconnect.core.design.theme.TcShapes
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

    TcScaffold(
        topBar = {
            TcAppBar(
                title = if (isMalayalam) "സുപ്രഭാതം, Suresh" else "Good morning, Suresh",
                actions = {
                    TcIconButton(onClick = onAlertsClick) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_bell),
                            contentDescription = "Alerts",
                            modifier = Modifier.size(dimens.IconSizeDefault),
                            tint = colors.text2
                        )
                    }
                }
            )
        },
        bottomBar = {
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
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = dimens.Padding24),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                dimens.Gap16,
                alignment = Alignment.CenterVertically
            )
        ) {
            // Truck Icon Box
            Box(
                modifier = Modifier
                    .size(dimens.Size110)
                    .clip(TcShapes.Radius36)
                    .background(colors.accentSoft),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_truck),
                    contentDescription = null,
                    modifier = Modifier.size(dimens.Size54),
                    tint = colors.accent
                )
            }

            Text(
                text = if (isMalayalam) "ഇപ്പോൾ ജോലി ഇല്ല" else "No job right now",
                style = typography.h1,
                color = colors.text,
                textAlign = TextAlign.Center
            )

            Text(
                text = if (isMalayalam)
                    "പുതിയ ജോലി വന്നാൽ ശബ്ദത്തോടെ അറിയിക്കും"
                else
                    "You will hear a loud alert when a new job arrives",
                style = typography.body,
                color = colors.muted,
                textAlign = TextAlign.Center
            )

            TcChip(
                text = if (isMalayalam) "ഓൺലൈൻ · ലഭ്യം" else "Online · Available",
                kind = TcChipKind.Ok
            )

            // Live incoming trip banner / test card
            TcCard(onClick = onJobAlertClick) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = if (isMalayalam) "പുതിയ ലോഡ് ലഭ്യമാണ്" else "New shipment assigned",
                            style = typography.btnSm,
                            color = colors.text
                        )
                        Text(
                            text = "Kasaragod → Malappuram · 8.5 t",
                            style = typography.xsmall,
                            color = colors.muted
                        )
                    }
                    TcButton(
                        text = if (isMalayalam) "കാണുക" else "View job",
                        onClick = onJobAlertClick,
                        variant = TcButtonVariant.Primary,
                        size = TcButtonSize.Sm
                    )
                }
            }
        }
    }
}
