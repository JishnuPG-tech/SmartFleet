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
import com.truckconnect.core.design.component.TcAvatar
import com.truckconnect.core.design.component.TcButton
import com.truckconnect.core.design.component.TcButtonVariant
import com.truckconnect.core.design.component.TcCard
import com.truckconnect.core.design.component.TcChip
import com.truckconnect.core.design.component.TcChipKind
import com.truckconnect.core.design.component.TcIconButton
import com.truckconnect.core.design.component.TcListRow
import com.truckconnect.core.design.theme.TcDimens
import com.truckconnect.core.design.theme.TcThemeState

@Composable
fun DriverDetailScreen(
    onBackClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onResetPinClick: () -> Unit = {},
    onRevokeDeviceClick: () -> Unit = {}
) {
    val colors = TcThemeState.colors
    val typography = TcThemeState.typography
    val dimens = TcThemeState.dimens

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bg)
            .padding(bottom = dimens.ScreenPaddingBottom),
        verticalArrangement = Arrangement.spacedBy(dimens.Gap10)
    ) {
        TcAppBar(
            title = "Driver",
            onBackClick = onBackClick,
            actions = {
                TcIconButton(onClick = onEditClick) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_edit),
                        contentDescription = "Edit",
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
            // Profile Header
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(dimens.Gap6)
            ) {
                TcAvatar(text = "SK", isLarge = true)
                Text(
                    text = "Suresh Kumar",
                    style = typography.btnLg,
                    color = colors.text
                )
                Text(
                    text = "+91 94470 00000",
                    style = typography.small,
                    color = colors.muted
                )
                TcChip(text = "On trip", kind = TcChipKind.Accent)
            }

            // 3-col KPI Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(dimens.Gap8)
            ) {
                TcCard(modifier = Modifier.weight(1f)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Trips", style = typography.xsmall, color = colors.muted)
                        Text(text = "49", style = typography.btnLg, color = colors.text)
                    }
                }
                TcCard(modifier = Modifier.weight(1f)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "On-time", style = typography.xsmall, color = colors.muted)
                        Text(text = "82%", style = typography.btnLg, color = colors.text)
                    }
                }
                TcCard(modifier = Modifier.weight(1f)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Rating", style = typography.xsmall, color = colors.muted)
                        Text(text = "4.6★", style = typography.btnLg, color = colors.text)
                    }
                }
            }

            // Details Card
            TcCard {
                Column {
                    TcListRow {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Vehicle", style = typography.small, color = colors.muted)
                            Text(text = "KL-07-AZ-1200 · 10 t", style = typography.btnSm, color = colors.text)
                        }
                    }
                    TcListRow {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Licence", style = typography.small, color = colors.muted)
                            Text(text = "HGMV · exp 2029", style = typography.btnSm, color = colors.text)
                        }
                    }
                    TcListRow(isLast = true) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Device", style = typography.small, color = colors.muted)
                            Text(text = "Samsung A14 · Android 14", style = typography.btnSm, color = colors.text)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Action Row (Defect F1 fixed: weight(1f), spacedBy(dimens.Gap10))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(dimens.Gap10)
            ) {
                TcButton(
                    onClick = onResetPinClick,
                    variant = TcButtonVariant.Outline,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Reset PIN")
                }

                TcButton(
                    onClick = onRevokeDeviceClick,
                    variant = TcButtonVariant.Outline,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Revoke device")
                }
            }
        }
    }
}
