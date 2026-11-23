package com.truckconnect.feature.driver

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.truckconnect.core.design.component.TcChip
import com.truckconnect.core.design.component.TcChipKind
import com.truckconnect.core.design.component.TcField
import com.truckconnect.core.design.component.TcPhotoBox
import com.truckconnect.core.design.theme.TcDimens
import com.truckconnect.core.design.theme.TcThemeState

@Composable
fun ProofOfDeliveryScreen(
    onBackClick: () -> Unit = {},
    onCompleteDeliveryClick: () -> Unit = {}
) {
    val colors = TcThemeState.colors
    val typography = TcThemeState.typography
    val dimens = TcThemeState.dimens
    val isMalayalam = TcThemeState.isMalayalam

    var receiverName by remember { mutableStateOf("Mathew Traders") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bg)
            .padding(bottom = dimens.ScreenPaddingBottom),
        verticalArrangement = Arrangement.spacedBy(dimens.Gap12)
    ) {
        TcAppBar(
            title = if (isMalayalam) "ഡെലിവറി പൂർത്തിയാക്കുക" else "Complete delivery",
            onBackClick = onBackClick
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = dimens.ScreenPaddingHorizontal),
            verticalArrangement = Arrangement.spacedBy(dimens.Gap12)
        ) {
            Column {
                Text(
                    text = if (isMalayalam) "സാധനങ്ങളുടെ ഫോട്ടോ" else "Photo of goods",
                    style = typography.label,
                    color = colors.text2,
                    modifier = Modifier.padding(bottom = dimens.Gap6)
                )
                TcPhotoBox(
                    label = "Photo taken · 280 KB",
                    isFilled = true,
                    modifier = Modifier.height(dimens.PhotoBoxLgHeight)
                )
            }

            TcField(
                value = receiverName,
                onValueChange = { receiverName = it },
                label = if (isMalayalam) "സ്വീകരിച്ചയാളുടെ പേര്" else "Receiver name"
            )

            TcCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(dimens.Gap12)
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_key),
                        contentDescription = null,
                        modifier = Modifier.size(dimens.Size24),
                        tint = colors.text2
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "OTP (optional)",
                            style = typography.btnSm,
                            color = colors.text
                        )
                        Text(
                            text = "Customer OTP · 4 digits",
                            style = typography.small,
                            color = colors.muted
                        )
                    }
                    TcChip(
                        text = "Skip",
                        kind = TcChipKind.Neutral
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            TcButton(
                onClick = onCompleteDeliveryClick,
                variant = TcButtonVariant.Ok,
                size = TcButtonSize.Xl
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_check),
                    contentDescription = null,
                    modifier = Modifier.size(dimens.Size26),
                    tint = colors.accentInk
                )
                Text(
                    text = if (isMalayalam) "ഡെലിവറി പൂർത്തിയാക്കുക" else "Complete delivery",
                    modifier = Modifier.padding(start = dimens.Gap8)
                )
            }
        }
    }
}
