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
import com.truckconnect.core.design.component.TcButton
import com.truckconnect.core.design.component.TcButtonSize
import com.truckconnect.core.design.component.TcButtonVariant
import com.truckconnect.core.design.component.TcCard
import com.truckconnect.core.design.component.TcChip
import com.truckconnect.core.design.component.TcChipKind
import com.truckconnect.core.design.component.TcIconBox
import com.truckconnect.core.design.theme.TcDimens
import com.truckconnect.core.design.theme.TcThemeState

@Composable
fun JobDetailsScreen(
    onBackClick: () -> Unit = {},
    onNavigateClick: () -> Unit = {},
    onCallOfficeClick: () -> Unit = {},
    onStartTripClick: () -> Unit = {}
) {
    val colors = TcThemeState.colors
    val typography = TcThemeState.typography
    val dimens = TcThemeState.dimens
    val isMalayalam = TcThemeState.isMalayalam

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
                TcChip(text = "Accepted", kind = TcChipKind.Info)
            }
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = dimens.ScreenPaddingHorizontal),
            verticalArrangement = Arrangement.spacedBy(dimens.Gap10)
        ) {
            // Pickup and Drop Card
            TcCard {
                Column(verticalArrangement = Arrangement.spacedBy(dimens.Gap10)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(dimens.Gap10)
                    ) {
                        TcIconBox(iconRes = R.drawable.ic_map, kind = TcChipKind.Ok)
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isMalayalam) "കയറ്റുന്ന സ്ഥലം" else "Pickup",
                                style = typography.small,
                                color = colors.muted
                            )
                            Text(
                                text = "Kasaragod Market",
                                style = typography.btnLg,
                                color = colors.text
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(dimens.Gap10)
                    ) {
                        TcIconBox(iconRes = R.drawable.ic_map, kind = TcChipKind.Crit)
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isMalayalam) "ഇറക്കുന്ന സ്ഥലം" else "Drop",
                                style = typography.small,
                                color = colors.muted
                            )
                            Text(
                                text = "Malappuram Mandi",
                                style = typography.btnLg,
                                color = colors.text
                            )
                        }
                    }
                }
            }

            // Goods and Weight Card
            TcCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isMalayalam) "സാധനങ്ങൾ" else "Goods",
                            style = typography.small,
                            color = colors.muted
                        )
                        Text(
                            text = "Vegetables",
                            style = typography.btnLg,
                            color = colors.text
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isMalayalam) "ഭാരം" else "Weight",
                            style = typography.small,
                            color = colors.muted
                        )
                        Text(
                            text = "8.5 t",
                            style = typography.btnLg,
                            color = colors.text
                        )
                    }
                }
            }

            // Defect F1 fix: 2 buttons in a row with weight(1f) and spacedBy(dimens.Gap10)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(dimens.Gap10)
            ) {
                TcButton(
                    onClick = onNavigateClick,
                    variant = TcButtonVariant.Outline,
                    size = TcButtonSize.Lg,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_nav),
                        contentDescription = null,
                        modifier = Modifier.size(dimens.Size22),
                        tint = colors.text
                    )
                    Text(
                        text = if (isMalayalam) "വഴി കാണിക്കുക" else "Navigate",
                        modifier = Modifier.padding(start = dimens.Gap6)
                    )
                }

                TcButton(
                    onClick = onCallOfficeClick,
                    variant = TcButtonVariant.Outline,
                    size = TcButtonSize.Lg,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_phone),
                        contentDescription = null,
                        modifier = Modifier.size(dimens.Size22),
                        tint = colors.text
                    )
                    Text(
                        text = if (isMalayalam) "ഓഫീസിനെ വിളിക്കുക" else "Call office",
                        modifier = Modifier.padding(start = dimens.Gap6)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            TcButton(
                onClick = onStartTripClick,
                variant = TcButtonVariant.Primary,
                size = TcButtonSize.Xl
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_play),
                    contentDescription = null,
                    modifier = Modifier.size(dimens.Size24),
                    tint = colors.accentInk
                )
                Text(
                    text = if (isMalayalam) "യാത്ര തുടങ്ങുക" else "Start trip",
                    modifier = Modifier.padding(start = dimens.Gap8)
                )
            }
        }
    }
}
