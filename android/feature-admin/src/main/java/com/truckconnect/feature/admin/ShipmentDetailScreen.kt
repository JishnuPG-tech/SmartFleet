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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.truckconnect.core.design.R
import com.truckconnect.core.design.component.TcAppBar
import com.truckconnect.core.design.component.TcButton
import com.truckconnect.core.design.component.TcButtonVariant
import com.truckconnect.core.design.component.TcCard
import com.truckconnect.core.design.component.TcChip
import com.truckconnect.core.design.component.TcChipKind
import com.truckconnect.core.design.component.TcIconButton
import com.truckconnect.core.design.component.TcTimeline
import com.truckconnect.core.design.component.TcTimelineStep
import com.truckconnect.core.design.theme.TcDimens
import com.truckconnect.core.design.theme.TcThemeState

@Composable
fun ShipmentDetailScreen(
    onBackClick: () -> Unit = {},
    onMoreClick: () -> Unit = {},
    onReassignClick: () -> Unit = {},
    onCancelShipmentClick: () -> Unit = {}
) {
    val colors = TcThemeState.colors
    val typography = TcThemeState.typography
    val dimens = TcThemeState.dimens

    val steps = listOf(
        TcTimelineStep(title = "Created", subtitle = "6:00 am · Anil Kumar", isPending = false),
        TcTimelineStep(title = "Driver assigned", subtitle = "6:05 am · Suresh Kumar", isPending = false),
        TcTimelineStep(title = "Driver accepted", subtitle = "6:10 am", isPending = false),
        TcTimelineStep(title = "Reached pickup", subtitle = "7:50 am · Kasaragod Market", isPending = false),
        TcTimelineStep(title = "Loaded", subtitle = "8:45 am · 8.5 t vegetables", isPending = false),
        TcTimelineStep(title = "In transit", subtitle = "Now · speed 48 km/h", isPending = false),
        TcTimelineStep(title = "Reached drop", isPending = true),
        TcTimelineStep(title = "Delivered", isPending = true)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bg)
            .padding(bottom = dimens.ScreenPaddingBottom),
        verticalArrangement = Arrangement.spacedBy(dimens.Gap10)
    ) {
        TcAppBar(
            title = "TC-2026-000131",
            onBackClick = onBackClick,
            actions = {
                TcIconButton(onClick = onMoreClick) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_more),
                        contentDescription = "More",
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
            verticalArrangement = Arrangement.spacedBy(dimens.Gap10)
        ) {
            // Header Card
            TcCard {
                Column(verticalArrangement = Arrangement.spacedBy(dimens.Gap6)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TcChip(text = "In transit", kind = TcChipKind.Accent)
                        Text(
                            text = "8.5 t · Vegetables",
                            style = typography.xsmall,
                            color = colors.muted
                        )
                    }
                    Text(
                        text = "Kasaragod → Malappuram",
                        style = typography.btnLg,
                        color = colors.text
                    )
                    Text(
                        text = "Suresh Kumar · KL-07-AZ-1200",
                        style = typography.xsmall,
                        color = colors.muted
                    )
                }
            }

            Text(
                text = "Timeline",
                style = typography.btnSm,
                color = colors.text,
                modifier = Modifier.padding(top = dimens.Gap4)
            )

            // Timeline (Defect F2 fixed)
            TcTimeline(
                steps = steps,
                isDriver = false
            )

            Spacer(modifier = Modifier.weight(1f))

            // Action Row (Defect F1 fixed: weight(1f), spacedBy(dimens.Gap10))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(dimens.Gap10)
            ) {
                TcButton(
                    onClick = onReassignClick,
                    variant = TcButtonVariant.Outline,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Reassign")
                }

                TcButton(
                    onClick = onCancelShipmentClick,
                    variant = TcButtonVariant.Outline,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Cancel shipment")
                }
            }
        }
    }
}
