package com.truckconnect.feature.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.truckconnect.core.design.R
import com.truckconnect.core.design.component.AdminTab
import com.truckconnect.core.design.component.TcAppBar
import com.truckconnect.core.design.component.TcBottomNav
import com.truckconnect.core.design.component.TcCard
import com.truckconnect.core.design.component.TcChip
import com.truckconnect.core.design.component.TcChipKind
import com.truckconnect.core.design.component.TcFab
import com.truckconnect.core.design.component.TcField
import com.truckconnect.core.design.component.TcIconButton
import com.truckconnect.core.design.component.TcNavItems
import com.truckconnect.core.design.component.TcScaffold
import com.truckconnect.core.design.component.TcSegmented
import com.truckconnect.core.design.theme.TcDimens
import com.truckconnect.core.design.theme.TcThemeState

/**
 * Admin Shipments Screen (M3 / X1)
 *
 * Full functional features:
 * - Search toggle & filtering (X1)
 * - Status segmented filtering (All, Active, Pending, Done)
 * - 5-shipment fixture with full timestamps
 * - FAB creating new shipment wizard
 */
@Composable
fun ShipmentsScreen(
    onShipmentClick: (String) -> Unit = {},
    onFabClick: () -> Unit = {},
    onTabSelected: (Int) -> Unit = {}
) {
    val colors = TcThemeState.colors
    val typography = TcThemeState.typography
    val dimens = TcThemeState.dimens

    var isSearchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var filterIndex by remember { mutableIntStateOf(0) }
    val filterOptions = listOf("All", "Active", "Pending", "Done")

    val shipments = listOf(
        listOf("TC-2026-000138", "Idukki → Kasaragod", "Assigned", TcChipKind.Info, "Venugopal R · 22 Nov, 6:00 am"),
        listOf("TC-2026-000141", "Malappuram → Wayanad", "Rejected", TcChipKind.Crit, "Unassigned · 22 Nov, 6:00 am"),
        listOf("TC-2026-000131", "Kasaragod → Malappuram", "In transit", TcChipKind.Accent, "Suresh K · 22 Nov, 6:00 am"),
        listOf("TC-2026-000133", "Thrissur → Kannur", "Loaded", TcChipKind.Accent, "Babu Thomas · 22 Nov, 6:00 am"),
        listOf("TC-2026-000125", "Kochi → Palakkad", "Delivered", TcChipKind.Ok, "Haridas M · 22 Nov, 6:00 am")
    )

    val filteredList = shipments.filter { item ->
        val id = item[0] as String
        val route = item[1] as String
        val status = item[2] as String
        val driver = item[4] as String

        val matchesSearch = searchQuery.isBlank() ||
                id.contains(searchQuery, ignoreCase = true) ||
                route.contains(searchQuery, ignoreCase = true) ||
                driver.contains(searchQuery, ignoreCase = true)

        val matchesFilter = when (filterIndex) {
            1 -> status == "In transit" || status == "Loaded" || status == "Assigned"
            2 -> status == "Rejected" || status == "Assigned"
            3 -> status == "Delivered"
            else -> true
        }

        matchesSearch && matchesFilter
    }

    TcScaffold(
        topBar = {
            TcAppBar(
                title = "Shipments",
                actions = {
                    TcIconButton(onClick = { isSearchActive = !isSearchActive }) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_search),
                            contentDescription = "Search",
                            modifier = Modifier.size(dimens.IconSizeDefault),
                            tint = if (isSearchActive) colors.accent else colors.text2
                        )
                    }
                }
            )
        },
        bottomBar = {
            TcBottomNav(
                tabs = TcNavItems.AdminTabs,
                selectedIndex = AdminTab.SHIPMENTS.ordinal,
                onTabSelected = onTabSelected,
                isDriver = false
            )
        },
        fab = {
            TcFab(
                onClick = onFabClick,
                modifier = Modifier.padding(
                    end = dimens.ScreenPaddingHorizontal,
                    bottom = dimens.Gap14
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(
                    start = dimens.ScreenPaddingHorizontal,
                    end = dimens.ScreenPaddingHorizontal,
                    top = dimens.Gap10,
                    bottom = dimens.Gap10
                ),
            verticalArrangement = Arrangement.spacedBy(dimens.Gap10)
        ) {
            if (isSearchActive) {
                TcField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = "Search shipment ID, route, or driver",
                    leadingIcon = {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_search),
                            contentDescription = null,
                            modifier = Modifier.size(dimens.Size18),
                            tint = colors.muted
                        )
                    }
                )
            }

            TcSegmented(
                options = filterOptions,
                selectedIndex = filterIndex,
                onSelect = { filterIndex = it }
            )

            filteredList.forEach { item ->
                val id = item[0] as String
                val route = item[1] as String
                val status = item[2] as String
                val kind = item[3] as TcChipKind
                val subtitle = item[4] as String

                TcCard(onClick = { onShipmentClick(id) }) {
                    Column(verticalArrangement = Arrangement.spacedBy(dimens.Gap4)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = id,
                                style = typography.btnSm,
                                color = colors.text
                            )
                            TcChip(text = status, kind = kind)
                        }
                        Text(
                            text = route,
                            style = typography.small,
                            color = colors.text
                        )
                        Text(
                            text = subtitle,
                            style = typography.xsmall,
                            color = colors.muted
                        )
                    }
                }
            }
        }
    }
}
