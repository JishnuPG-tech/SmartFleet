package com.truckconnect.core.design.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.truckconnect.core.design.R
import com.truckconnect.core.design.theme.TcDimens
import com.truckconnect.core.design.theme.TcShapes
import com.truckconnect.core.design.theme.TcThemeState

/**
 * Standard enum for Admin bottom navigation tabs (fixes BUG-03).
 * Order: Home, Map, Shipments, Fleet, More
 */
enum class AdminTab(val title: String, val iconRes: Int) {
    HOME("Home", R.drawable.ic_home),
    MAP("Map", R.drawable.ic_map),
    SHIPMENTS("Shipments", R.drawable.ic_box),
    FLEET("Fleet", R.drawable.ic_truck),
    MORE("More", R.drawable.ic_more)
}

/**
 * Standard enum for Driver bottom navigation tabs.
 * Order: Home, History, Alerts, Profile
 */
enum class DriverTab(val defaultTitle: String, val iconRes: Int) {
    HOME("Home", R.drawable.ic_home),
    HISTORY("History", R.drawable.ic_clock),
    ALERTS("Alerts", R.drawable.ic_bell),
    PROFILE("Profile", R.drawable.ic_user)
}

data class TcNavItem(
    val iconRes: Int,
    val label: String
)

object TcNavItems {
    val AdminTabs = AdminTab.values().map { TcNavItem(it.iconRes, it.title) }

    fun driverTabs(
        homeLabel: String = "Home",
        historyLabel: String = "History",
        alertsLabel: String = "Alerts",
        profileLabel: String = "Profile"
    ) = listOf(
        TcNavItem(R.drawable.ic_home, homeLabel),
        TcNavItem(R.drawable.ic_clock, historyLabel),
        TcNavItem(R.drawable.ic_bell, alertsLabel),
        TcNavItem(R.drawable.ic_user, profileLabel)
    )
}

@Composable
fun TcBottomNav(
    tabs: List<TcNavItem>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    isDriver: Boolean = TcThemeState.isDriver
) {
    val colors = TcThemeState.colors
    val shapes = TcThemeState.shapes
    val typography = TcThemeState.typography
    val dimens = TcThemeState.dimens

    val navHeight = if (isDriver) dimens.BottomNavDriverHeight else dimens.BottomNavAdminHeight

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.surface)
    ) {
        // 1px top border
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(dimens.CardBorder)
                .background(colors.border)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(navHeight)
                .padding(bottom = dimens.CardBorder * 6),
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEachIndexed { index, tab ->
                val isSelected = index == selectedIndex

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .height(navHeight)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(bounded = false, radius = dimens.Size24, color = colors.accent),
                            onClick = { onTabSelected(index) }
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .size(
                                    width = dimens.BottomNavActivePillWidth,
                                    height = dimens.BottomNavActivePillHeight
                                )
                                .clip(shapes.NavActivePill)
                                .background(colors.accentSoft),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(tab.iconRes),
                                contentDescription = tab.label,
                                modifier = Modifier.size(dimens.IconSizeDefault),
                                tint = colors.accent
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .size(
                                    width = dimens.BottomNavActivePillWidth,
                                    height = dimens.BottomNavActivePillHeight
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(tab.iconRes),
                                contentDescription = tab.label,
                                modifier = Modifier.size(dimens.IconSizeDefault),
                                tint = colors.muted
                            )
                        }
                    }

                    Text(
                        text = tab.label,
                        style = typography.bottomNavLabel,
                        color = if (isSelected) colors.accent else colors.muted
                    )
                }
            }
        }
    }
}

@Composable
fun TcFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconRes: Int = R.drawable.ic_plus
) {
    val colors = TcThemeState.colors
    val shapes = TcThemeState.shapes
    val dimens = TcThemeState.dimens

    Box(
        modifier = modifier
            .size(dimens.FabSize)
            .shadow(elevation = dimens.CardBorder * 8, shape = shapes.Fab)
            .clip(shapes.Fab)
            .background(colors.accent)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = colors.accentInk),
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(iconRes),
            contentDescription = "FAB",
            modifier = Modifier.size(dimens.IconSizeDefault),
            tint = colors.accentInk
        )
    }
}

@Composable
fun TcSosButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = TcThemeState.colors
    val typography = TcThemeState.typography
    val dimens = TcThemeState.dimens

    Box(
        modifier = modifier
            .size(dimens.SosButtonSize)
            .shadow(elevation = dimens.CardBorder * 8, shape = CircleShape)
            .clip(CircleShape)
            .background(colors.crit)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = colors.accentInk),
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "SOS",
            style = typography.btnXl,
            color = colors.accentInk
        )
    }
}

@Composable
fun TcPulse(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val colors = TcThemeState.colors
    val dimens = TcThemeState.dimens

    Box(
        modifier = modifier.size(dimens.PulseContainerSize),
        contentAlignment = Alignment.Center
    ) {
        // Outer ring
        Box(
            modifier = Modifier
                .size(dimens.PulseContainerSize)
                .clip(CircleShape)
                .background(colors.accentSoft)
        )
        // Middle ring
        Box(
            modifier = Modifier
                .size(dimens.PulseContainerSize - dimens.CardBorder * 28)
                .clip(CircleShape)
                .background(colors.accentSoft)
        )
        // Inner circle
        Box(
            modifier = Modifier
                .size(dimens.PulseContainerSize - dimens.CardBorder * 56)
                .clip(CircleShape)
                .background(colors.accentInk),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}
