package com.truckconnect.feature.admin

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.truckconnect.core.design.R
import com.truckconnect.core.design.component.TcAppBar
import com.truckconnect.core.design.component.TcButton
import com.truckconnect.core.design.component.TcButtonSize
import com.truckconnect.core.design.component.TcButtonVariant
import com.truckconnect.core.design.component.TcCard
import com.truckconnect.core.design.component.TcChip
import com.truckconnect.core.design.component.TcChipKind
import com.truckconnect.core.design.component.TcIconButton
import com.truckconnect.core.design.component.TcRadio
import com.truckconnect.core.design.component.TcScrim
import com.truckconnect.core.design.component.TcSheet
import com.truckconnect.core.design.theme.TcDimens
import com.truckconnect.core.design.theme.TcThemeState

@Composable
fun AssignDriverSheet(
    onDismiss: () -> Unit = {},
    onAssignConfirm: (String) -> Unit = {}
) {
    val colors = TcThemeState.colors
    val typography = TcThemeState.typography
    val dimens = TcThemeState.dimens

    var selectedIndex by remember { mutableIntStateOf(0) }

    val drivers = listOf(
        listOf("Venugopal R", "Available · KL-02-HR-3797 · 10 t", "Recommended", TcChipKind.Ok),
        listOf("Gopalakrishnan", "Available · KL-13-JQ-4168 · 1.5 t", "Too small", TcChipKind.Warn),
        listOf("Suresh Kumar", "On trip until 2:40 pm", "On trip", TcChipKind.Warn)
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.bg)
        ) {
            TcAppBar(title = "New shipment")
        }

        TcScrim(onDismiss = onDismiss)

        TcSheet(modifier = Modifier.align(Alignment.BottomCenter)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Assign driver",
                    style = typography.btnLg,
                    color = colors.text
                )
                TcIconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_x),
                        contentDescription = "Close",
                        modifier = Modifier.size(dimens.Size16),
                        tint = colors.text2
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(dimens.Gap10)) {
                drivers.forEachIndexed { index, item ->
                    val name = item[0] as String
                    val details = item[1] as String
                    val chipText = item[2] as String
                    val chipKind = item[3] as TcChipKind
                    val isSelected = index == selectedIndex

                    TcCard(onClick = { selectedIndex = index }) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(dimens.Gap10)
                        ) {
                            TcRadio(
                                selected = isSelected,
                                onClick = { selectedIndex = index }
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = name,
                                        style = typography.btnSm,
                                        color = colors.text
                                    )
                                    TcChip(text = chipText, kind = chipKind)
                                }
                                Text(
                                    text = details,
                                    style = typography.xsmall,
                                    color = colors.muted
                                )
                            }
                        }
                    }
                }
            }

            TcButton(
                onClick = { onAssignConfirm(drivers[selectedIndex][0] as String) },
                variant = TcButtonVariant.Primary,
                size = TcButtonSize.Lg,
                modifier = Modifier.padding(top = dimens.Gap6)
            ) {
                Text(text = "Assign ${drivers[selectedIndex][0]}")
            }
        }
    }
}
