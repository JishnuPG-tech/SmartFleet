package com.truckconnect.feature.admin

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import com.truckconnect.core.design.component.TcField
import com.truckconnect.core.design.component.TcStepsBar
import com.truckconnect.core.design.theme.TcDimens
import com.truckconnect.core.design.theme.TcThemeState

@Composable
fun NewShipmentScreen(
    onBackClick: () -> Unit = {},
    onNextAssignClick: () -> Unit = {}
) {
    val colors = TcThemeState.colors
    val typography = TcThemeState.typography
    val dimens = TcThemeState.dimens

    var pickup by remember { mutableStateOf("Kasaragod") }
    var drop by remember { mutableStateOf("Malappuram") }
    var goods by remember { mutableStateOf("Vegetables") }
    var weight by remember { mutableStateOf("8.5") }
    var consignee by remember { mutableStateOf("Fresh Basket · +91 94470 33221") }
    var pickupTime by remember { mutableStateOf("22 Nov, 6:00 am") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bg)
            .padding(
                top = dimens.Padding2,
                start = dimens.ScreenPaddingHorizontal,
                end = dimens.ScreenPaddingHorizontal,
                bottom = dimens.ScreenPaddingBottom
            ),
        verticalArrangement = Arrangement.spacedBy(dimens.Gap10)
    ) {
        TcAppBar(title = "New shipment", onBackClick = onBackClick)

        TcStepsBar(totalSteps = 3, currentStep = 1)

        Text(
            text = "2 / 3 · Goods & schedule",
            style = typography.small,
            color = colors.muted
        )

        TcField(
            value = pickup,
            onValueChange = { pickup = it },
            label = "Pickup",
            leadingIcon = {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_map),
                    contentDescription = null,
                    modifier = Modifier.size(dimens.Size18),
                    tint = colors.text2
                )
            }
        )

        TcField(
            value = drop,
            onValueChange = { drop = it },
            label = "Drop",
            leadingIcon = {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_map),
                    contentDescription = null,
                    modifier = Modifier.size(dimens.Size18),
                    tint = colors.text2
                )
            }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(dimens.Gap10)
        ) {
            TcField(
                value = goods,
                onValueChange = { goods = it },
                label = "Goods",
                modifier = Modifier.weight(1f)
            )
            TcField(
                value = weight,
                onValueChange = { weight = it },
                label = "Weight (t)",
                modifier = Modifier.weight(1f)
            )
        }

        TcField(
            value = consignee,
            onValueChange = { consignee = it },
            label = "Consignee",
            leadingIcon = {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_user),
                    contentDescription = null,
                    modifier = Modifier.size(dimens.Size18),
                    tint = colors.text2
                )
            }
        )

        TcField(
            value = pickupTime,
            onValueChange = { pickupTime = it },
            label = "Pickup time",
            leadingIcon = {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_cal),
                    contentDescription = null,
                    modifier = Modifier.size(dimens.Size18),
                    tint = colors.text2
                )
            }
        )

        // Capacity alert banner
        TcCard(
            modifier = Modifier.background(colors.warnChipBg, TcThemeState.shapes.Card)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dimens.Gap10)
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_warn),
                    contentDescription = null,
                    modifier = Modifier.size(dimens.Size20),
                    tint = colors.warn
                )
                Text(
                    text = "Exceeds 5 t mini-truck capacity. Heavy vehicle required.",
                    style = typography.xsmall,
                    color = colors.warn
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        TcButton(
            onClick = onNextAssignClick,
            variant = TcButtonVariant.Primary,
            size = TcButtonSize.Lg
        ) {
            Text(text = "Next: assign driver")
        }
    }
}
