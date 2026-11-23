package com.truckconnect.feature.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.truckconnect.core.design.component.TcButton
import com.truckconnect.core.design.component.TcCard
import com.truckconnect.core.design.component.TcChip
import com.truckconnect.core.design.component.TcChipKind
import com.truckconnect.core.design.component.TcField
import com.truckconnect.core.design.component.TcIconButton
import com.truckconnect.core.design.component.TcScreen
import com.truckconnect.core.design.component.TcSegmented
import com.truckconnect.core.design.theme.TcThemeState

data class VehicleItem(
    val plate: String,
    val type: String,
    val status: String,
    val kind: TcChipKind,
    val expiry: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehiclesScreen(
    onBackClick: () -> Unit = {},
    onVehicleClick: (String) -> Unit = {}
) {
    val colors = TcThemeState.colors
    val typography = TcThemeState.typography
    val dimens = TcThemeState.dimens

    var filterIndex by remember { mutableIntStateOf(0) }
    val filterOptions = listOf("All", "Available", "On trip", "Service")

    val vehiclesList = remember {
        mutableStateListOf(
            VehicleItem("KL-07-AZ-1200", "Lorry · 10 t", "On trip", TcChipKind.Accent, "Insurance in 300 d"),
            VehicleItem("KL-08-BX-1571", "Mini truck · 3 t", "On trip", TcChipKind.Accent, "PUC in 45 d"),
            VehicleItem("KL-02-HR-3797", "Lorry · 10 t", "Available", TcChipKind.Info, "Permit in 25 d"),
            VehicleItem("KL-13-JQ-4168", "Pickup · 1.5 t", "Service", TcChipKind.Warn, "Fitness due")
        )
    }

    var showAddVehicleSheet by remember { mutableStateOf(false) }
    var newPlate by remember { mutableStateOf("") }
    var newType by remember { mutableStateOf("Lorry · 10 t") }

    val filteredList = when (filterIndex) {
        1 -> vehiclesList.filter { it.status == "Available" }
        2 -> vehiclesList.filter { it.status == "On trip" }
        3 -> vehiclesList.filter { it.status == "Service" }
        else -> vehiclesList
    }

    TcScreen(
        title = "Vehicles",
        onBackClick = onBackClick,
        topBarActions = {
            TcIconButton(onClick = { showAddVehicleSheet = true }) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_plus),
                    contentDescription = "Add",
                    modifier = Modifier.size(dimens.IconSizeDefault),
                    tint = colors.text2
                )
            }
        },
        isScrollable = true
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(dimens.Gap10)
        ) {
            TcSegmented(
                options = filterOptions,
                selectedIndex = filterIndex,
                onSelect = { filterIndex = it }
            )

            filteredList.forEach { item ->
                TcCard(onClick = { onVehicleClick(item.plate) }) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(dimens.Gap4)) {
                            Text(
                                text = item.plate,
                                style = typography.btnSm,
                                color = colors.text
                            )
                            Text(
                                text = item.type,
                                style = typography.small,
                                color = colors.muted
                            )
                            Text(
                                text = item.expiry,
                                style = typography.xsmall,
                                color = colors.muted
                            )
                        }
                        TcChip(text = item.status, kind = item.kind)
                    }
                }
            }
        }
    }

    if (showAddVehicleSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAddVehicleSheet = false },
            containerColor = colors.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimens.ScreenPaddingHorizontal)
                    .padding(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(dimens.Gap12)
            ) {
                Text(text = "Add new vehicle", style = typography.h2, color = colors.text)
                TcField(
                    value = newPlate,
                    onValueChange = { newPlate = it },
                    label = "Registration plate (e.g. KL-07-AZ-9999)"
                )
                TcField(
                    value = newType,
                    onValueChange = { newType = it },
                    label = "Vehicle category & payload"
                )
                TcButton(
                    onClick = {
                        if (newPlate.isNotBlank()) {
                            vehiclesList.add(
                                VehicleItem(
                                    plate = newPlate.uppercase(),
                                    type = newType,
                                    status = "Available",
                                    kind = TcChipKind.Info,
                                    expiry = "Fitness active"
                                )
                            )
                            newPlate = ""
                            showAddVehicleSheet = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Add vehicle")
                }
            }
        }
    }
}
