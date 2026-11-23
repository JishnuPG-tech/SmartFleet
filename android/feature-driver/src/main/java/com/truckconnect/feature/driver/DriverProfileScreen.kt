package com.truckconnect.feature.driver

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.truckconnect.core.design.R
import com.truckconnect.core.design.component.TcAppBar
import com.truckconnect.core.design.component.TcAvatar
import com.truckconnect.core.design.component.TcBottomNav
import com.truckconnect.core.design.component.TcButton
import com.truckconnect.core.design.component.TcButtonSize
import com.truckconnect.core.design.component.TcButtonVariant
import com.truckconnect.core.design.component.TcCard
import com.truckconnect.core.design.component.TcChip
import com.truckconnect.core.design.component.TcChipKind
import com.truckconnect.core.design.component.TcIconButton
import com.truckconnect.core.design.component.TcListRow
import com.truckconnect.core.design.component.TcNavItems
import com.truckconnect.core.design.theme.TcDimens
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bg)
    ) {
        TcAppBar(
            title = if (isMalayalam) "പ്രൊഫൈൽ" else "Profile",
            actions = {
                TcIconButton(onClick = onSettingsClick) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_cog),
                        contentDescription = "Settings",
                        modifier = Modifier.size(dimens.IconSizeDefault),
                        tint = colors.text2
                    )
                }
            }
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = dimens.ScreenPaddingHorizontal),
            verticalArrangement = Arrangement.spacedBy(dimens.Gap14)
        ) {
            // Profile Header
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(dimens.Gap6)
            ) {
                TcAvatar(text = "SK", isLarge = true)
                Text(
                    text = "Suresh Kumar",
                    style = typography.btnXl,
                    color = colors.text
                )
                TcChip(text = "Approved", kind = TcChipKind.Ok)
            }

            // Info Card
            TcCard {
                Column {
                    TcListRow {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Vehicle", style = typography.small, color = colors.muted)
                            Text(text = "KL-07-AZ-1200", style = typography.btnSm, color = colors.text)
                        }
                    }

                    TcListRow {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Licence", style = typography.small, color = colors.muted)
                            Text(text = "20 Jun 2027", style = typography.btnSm, color = colors.text)
                        }
                    }

                    TcListRow(isLast = true) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isMalayalam) "ഭാഷ" else "Language",
                                style = typography.small,
                                color = colors.muted
                            )
                            Text(text = "മലയാളം", style = typography.btnSm, color = colors.text)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            TcButton(
                onClick = onLogoutClick,
                variant = TcButtonVariant.Outline,
                size = TcButtonSize.Lg,
                modifier = Modifier.padding(bottom = dimens.Padding14)
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_logout),
                    contentDescription = null,
                    modifier = Modifier.size(dimens.Size22),
                    tint = colors.crit
                )
                Text(
                    text = if (isMalayalam) "ലോഗ് ഔട്ട്" else "Log out",
                    color = colors.crit,
                    modifier = Modifier.padding(start = dimens.Gap8)
                )
            }
        }

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
    }
}
