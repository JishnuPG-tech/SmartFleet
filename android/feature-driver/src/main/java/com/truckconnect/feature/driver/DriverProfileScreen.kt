package com.truckconnect.feature.driver

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.truckconnect.core.design.R
import com.truckconnect.core.design.component.TcAvatar
import com.truckconnect.core.design.component.TcBottomNav
import com.truckconnect.core.design.component.TcButton
import com.truckconnect.core.design.component.TcButtonSize
import com.truckconnect.core.design.component.TcButtonVariant
import com.truckconnect.core.design.component.TcCard
import com.truckconnect.core.design.component.TcConfirmDialog
import com.truckconnect.core.design.component.TcDetailItem
import com.truckconnect.core.design.component.TcDetailList
import com.truckconnect.core.design.component.TcIconButton
import com.truckconnect.core.design.component.TcNavItems
import com.truckconnect.core.design.component.TcScreen
import com.truckconnect.core.design.theme.TcThemeState

@Composable
fun DriverProfileScreen(
    onSettingsClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onTabSelected: (Int) -> Unit = {}
) {
    val colors = TcThemeState.colors
    val typography = TcThemeState.typography
    val dimens = TcThemeState.dimens
    val isMalayalam = TcThemeState.isMalayalam

    var showLogoutDialog by remember { mutableStateOf(false) }

    TcScreen(
        title = if (isMalayalam) "പ്രൊഫൈൽ" else "Driver profile",
        topBarActions = {
            TcIconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_cog),
                    contentDescription = "Settings",
                    modifier = Modifier.size(dimens.IconSizeDefault),
                    tint = colors.text2
                )
            }
        },
        bottomNav = {
            TcBottomNav(
                tabs = TcNavItems.driverTabs(
                    homeLabel = if (isMalayalam) "ഹോം" else "Home",
                    historyLabel = if (isMalayalam) "ചരിത്രം" else "History",
                    alertsLabel = if (isMalayalam) "അറിയിപ്പുകൾ" else "Alerts",
                    profileLabel = if (isMalayalam) "പ്രൊഫൈൽ" else "Profile"
                ),
                selectedIndex = 3,
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
            // Profile Card with "SK" initials
            TcCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(dimens.Gap12)
                ) {
                    TcAvatar(text = "Suresh Kumar", isLarge = true)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Suresh Kumar",
                            style = typography.h2,
                            color = colors.text
                        )
                        Text(
                            text = "+91 94470 12891",
                            style = typography.body,
                            color = colors.muted
                        )
                        Text(
                            text = if (isMalayalam) "ഡ്രൈവർ ഐഡി: DRV-0012" else "Driver ID: DRV-0012",
                            style = typography.xsmall,
                            color = colors.muted
                        )
                    }
                }
            }

            // Driver Details List using OD-8 TcDetailList
            Text(
                text = if (isMalayalam) "രേഖകളും വാഹനവും" else "Assigned vehicle & licence",
                style = typography.caption,
                color = colors.muted,
                modifier = Modifier.padding(top = 4.dp)
            )

            TcCard {
                TcDetailList(
                    items = listOf(
                        TcDetailItem(
                            label = if (isMalayalam) "വാഹന നമ്പർ" else "Assigned vehicle",
                            value = "KL-07-AZ-1200 · 8.5 t"
                        ),
                        TcDetailItem(
                            label = if (isMalayalam) "ഡ്രൈവിംഗ് ലൈസൻസ്" else "Driving licence",
                            value = "KL-0720190012345 (Heavy Commercial)"
                        ),
                        TcDetailItem(
                            label = if (isMalayalam) "ലൈസൻസ് കാലാവധി" else "Licence validity",
                            value = "Valid until 14 Aug 2028"
                        ),
                        TcDetailItem(
                            label = if (isMalayalam) "ഭാഷാ മുൻഗണന" else "Preferred language",
                            value = if (isMalayalam) "മലയാളം (English)" else "English (മലയാളം)"
                        ),
                        TcDetailItem(
                            label = if (isMalayalam) "ആപ്പ് പതിപ്പ്" else "App version",
                            value = "v2.1.0 (Build 42)"
                        )
                    )
                )
            }

            // Full-width destructive log out button with confirmation dialog
            TcButton(
                onClick = { showLogoutDialog = true },
                variant = TcButtonVariant.CritOutline,
                size = TcButtonSize.Lg,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_logout),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = colors.crit
                )
                Text(
                    text = if (isMalayalam) "ലോഗ് ഔട്ട് ചെയ്യുക" else "Log out",
                    style = typography.btnSm,
                    color = colors.crit,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }

    if (showLogoutDialog) {
        TcConfirmDialog(
            title = if (isMalayalam) "ലോഗ് ഔട്ട് ചെയ്യണോ?" else "Log out of TruckConnect?",
            message = if (isMalayalam)
                "ലോഗ് ഔട്ട് ചെയ്താൽ പുതിയ ജോലികളുടെ അറിയിപ്പുകൾ ലഭിക്കില്ല."
            else
                "You will not receive new shipment alerts while logged out.",
            confirmLabel = if (isMalayalam) "ലോഗ് ഔട്ട്" else "Log out",
            isDestructive = true,
            onConfirm = {
                showLogoutDialog = false
                onLogoutClick()
            },
            onDismiss = { showLogoutDialog = false }
        )
    }
}
