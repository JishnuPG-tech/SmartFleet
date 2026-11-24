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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.truckconnect.core.design.R
import com.truckconnect.core.design.component.AdminTab
import com.truckconnect.core.design.component.TcAppBar
import com.truckconnect.core.design.component.TcAvatar
import com.truckconnect.core.design.component.TcBottomNav
import com.truckconnect.core.design.component.TcButton
import com.truckconnect.core.design.component.TcCard
import com.truckconnect.core.design.component.TcChip
import com.truckconnect.core.design.component.TcChipKind
import com.truckconnect.core.design.component.TcField
import com.truckconnect.core.design.component.TcIconButton
import com.truckconnect.core.design.component.TcNavItems
import com.truckconnect.core.design.component.TcScreen
import com.truckconnect.core.design.component.TcSegmented
import com.truckconnect.core.design.theme.TcDimens
import com.truckconnect.core.design.theme.TcThemeState

data class DriverItem(
    val name: String,
    val plate: String,
    val status: String,
    val kind: TcChipKind,
    val phone: String = "+91 98765 00000"
)

/**
 * Admin Drivers Screen (M7 - Fleet Tab / X3)
 * Full interactive driver roster with search, filter, and Add Driver modal sheet.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriversScreen(
    onDriverClick: (String) -> Unit = {},
    onTabSelected: (Int) -> Unit = {}
) {
    val colors = TcThemeState.colors
    val typography = TcThemeState.typography
    val dimens = TcThemeState.dimens

    var searchQuery by remember { mutableStateOf("") }
    var filterIndex by remember { mutableIntStateOf(0) }
    val filterOptions = listOf("All", "Free", "On trip", "Leave")

    val driversList = remember {
        mutableStateListOf(
            DriverItem("Suresh Kumar", "KL-07-AZ-1200", "On trip", TcChipKind.Accent),
            DriverItem("Rajan Nair", "KL-08-BX-1571", "On trip", TcChipKind.Accent),
            DriverItem("Venugopal R", "KL-02-HR-3797", "Available", TcChipKind.Info),
            DriverItem("Shaji Varghese", "KL-04-KP-4539", "On leave", TcChipKind.Warn),
            DriverItem("Haridas M", "KL-05-LN-4910", "Offline", TcChipKind.Neutral)
        )
    }

    var showAddDriverSheet by remember { mutableStateOf(false) }
    var newDriverName by remember { mutableStateOf("") }
    var newDriverPhone by remember { mutableStateOf("") }
    var newDriverVehicle by remember { mutableStateOf("KL-07-AZ-1200") }

    val filteredList = driversList.filter { driver ->
        val matchesSearch = searchQuery.isBlank() || driver.name.contains(searchQuery, ignoreCase = true) || driver.plate.contains(searchQuery, ignoreCase = true)
        val matchesFilter = when (filterIndex) {
            1 -> driver.status == "Available"
            2 -> driver.status == "On trip"
            3 -> driver.status == "On leave"
            else -> true
        }
        matchesSearch && matchesFilter
    }

    TcScreen(
        title = "Drivers",
        topBarActions = {
            TcIconButton(onClick = { showAddDriverSheet = true }) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_plus),
                    contentDescription = "Add",
                    modifier = Modifier.size(dimens.IconSizeDefault),
                    tint = colors.text2
                )
            }
        },
        bottomNav = {
            TcBottomNav(
                tabs = TcNavItems.AdminTabs,
                selectedIndex = AdminTab.DRIVERS.ordinal,
                onTabSelected = onTabSelected,
                isDriver = false
            )
        },
        isScrollable = true
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(dimens.Gap10)
        ) {
            TcField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = "Search name or phone",
                leadingIcon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_search),
                        contentDescription = null,
                        modifier = Modifier.size(dimens.Size18),
                        tint = colors.muted
                    )
                }
            )

            TcSegmented(
                options = filterOptions,
                selectedIndex = filterIndex,
                onSelect = { filterIndex = it }
            )

            filteredList.forEach { driver ->
                TcCard(onClick = { onDriverClick(driver.name) }) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(dimens.Gap10)
                        ) {
                            TcAvatar(text = driver.name)
                            Column {
                                Text(
                                    text = driver.name,
                                    style = typography.btnSm,
                                    color = colors.text
                                )
                                Text(
                                    text = driver.plate,
                                    style = typography.xsmall,
                                    color = colors.muted
                                )
                            }
                        }
                        TcChip(text = driver.status, kind = driver.kind)
                    }
                }
            }
        }

        // Add Driver Modal Sheet (X3)
        if (showAddDriverSheet) {
            ModalBottomSheet(
                onDismissRequest = { showAddDriverSheet = false },
                containerColor = colors.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = dimens.ScreenPaddingHorizontal)
                        .padding(bottom = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Add new driver",
                        style = typography.h2,
                        color = colors.text
                    )

                    TcField(
                        value = newDriverName,
                        onValueChange = { newDriverName = it },
                        placeholder = "Driver full name"
                    )

                    TcField(
                        value = newDriverPhone,
                        onValueChange = { newDriverPhone = it },
                        placeholder = "Mobile phone (+91 ...)"
                    )

                    TcField(
                        value = newDriverVehicle,
                        onValueChange = { newDriverVehicle = it },
                        placeholder = "Assigned vehicle reg number"
                    )

                    TcButton(
                        text = "Register & issue PIN",
                        onClick = {
                            if (newDriverName.isNotBlank()) {
                                driversList.add(
                                    0,
                                    DriverItem(
                                        name = newDriverName,
                                        plate = newDriverVehicle,
                                        status = "Available",
                                        kind = TcChipKind.Info,
                                        phone = newDriverPhone
                                    )
                                )
                                newDriverName = ""
                                newDriverPhone = ""
                                showAddDriverSheet = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
