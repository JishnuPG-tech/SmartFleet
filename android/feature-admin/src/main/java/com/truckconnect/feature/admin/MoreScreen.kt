package com.truckconnect.feature.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.truckconnect.core.design.R
import com.truckconnect.core.design.component.AdminTab
import com.truckconnect.core.design.component.TcAppBar
import com.truckconnect.core.design.component.TcAvatar
import com.truckconnect.core.design.component.TcBottomNav
import com.truckconnect.core.design.component.TcCard
import com.truckconnect.core.design.component.TcChip
import com.truckconnect.core.design.component.TcChipKind
import com.truckconnect.core.design.component.TcIconBox
import com.truckconnect.core.design.component.TcListRow
import com.truckconnect.core.design.component.TcNavItems
import com.truckconnect.core.design.component.TcScreen
import com.truckconnect.core.design.theme.TcDimens
import com.truckconnect.core.design.theme.TcThemeState

/**
 * Admin More Screen (M14 - More Tab)
 *
 * Fixes:
 * - BUG-01 / BUG-02: Hosted in TcScaffold for full system bars and cutout insets.
 * - BUG-04: Bottom navigation wired to AdminTab.MORE.ordinal (4) with active click handlers.
 */
@Composable
fun MoreScreen(
    onProfileClick: () -> Unit = {},
    onMenuItemClick: (String) -> Unit = {},
    onSignOutClick: () -> Unit = {},
    onTabSelected: (Int) -> Unit = {}
) {
    val colors = TcThemeState.colors
    val typography = TcThemeState.typography
    val dimens = TcThemeState.dimens

    val menuItems = listOf(
        Triple(R.drawable.ic_chart, "Reports", ""),
        Triple(R.drawable.ic_truck, "Vehicles", ""),
        Triple(R.drawable.ic_users, "Driver approvals", "2"),
        Triple(R.drawable.ic_download, "App versions", ""),
        Triple(R.drawable.ic_list, "Audit log", ""),
        Triple(R.drawable.ic_cog, "Settings", "")
    )

    TcScreen(
        title = "Profile",
        bottomNav = {
            TcBottomNav(
                tabs = TcNavItems.AdminTabs,
                selectedIndex = AdminTab.PROFILE.ordinal,
                onTabSelected = onTabSelected,
                isDriver = false
            )
        },
        isScrollable = true
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(dimens.Gap12)
        ) {
            // User Profile Row
            TcCard(onClick = onProfileClick) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(dimens.Gap10)
                ) {
                    TcAvatar(text = "Jishnu P G")
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Jishnu P G",
                            style = typography.btnSm,
                            color = colors.text
                        )
                        Text(
                            text = "jishnupg2005@gmail.com · Super Admin",
                            style = typography.xsmall,
                            color = colors.muted
                        )
                    }
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_chev),
                        contentDescription = null,
                        modifier = Modifier.size(dimens.Size18),
                        tint = colors.text2
                    )
                }
            }

            // Menu items Card
            TcCard {
                Column {
                    menuItems.forEachIndexed { index, (iconRes, label, badge) ->
                        TcListRow(
                            isLast = index == menuItems.lastIndex,
                            onClick = { onMenuItemClick(label) }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(dimens.Gap12)
                            ) {
                                TcIconBox(
                                    iconRes = iconRes,
                                    modifier = Modifier.size(dimens.Size34)
                                )
                                Text(
                                    text = label,
                                    style = typography.btnSm,
                                    color = colors.text,
                                    modifier = Modifier.weight(1f)
                                )
                                if (badge.isNotEmpty()) {
                                    TcChip(text = badge, kind = TcChipKind.Warn)
                                }
                                Icon(
                                    imageVector = ImageVector.vectorResource(R.drawable.ic_chev),
                                    contentDescription = null,
                                    modifier = Modifier.size(dimens.Size16),
                                    tint = colors.text2
                                )
                            }
                        }
                    }
                }
            }

            // Sign out Card
            TcCard(onClick = onSignOutClick) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(dimens.Gap10)
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_logout),
                        contentDescription = null,
                        modifier = Modifier.size(dimens.Size20),
                        tint = colors.crit
                    )
                    Text(
                        text = "Sign out",
                        style = typography.btnSm,
                        color = colors.crit
                    )
                }
            }
        }
    }
}
