package com.truckconnect.feature.driver

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.truckconnect.core.design.R
import com.truckconnect.core.design.component.TcButton
import com.truckconnect.core.design.component.TcButtonSize
import com.truckconnect.core.design.component.TcButtonVariant
import com.truckconnect.core.design.component.TcCard
import com.truckconnect.core.design.component.TcField
import com.truckconnect.core.design.component.TcPhotoBox
import com.truckconnect.core.design.component.TcScreen
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

    TcScreen(
        title = if (isMalayalam) "ഡെലിവറി പൂർത്തിയാക്കുക" else "Complete delivery",
        onBackClick = onBackClick,
        isScrollable = true,
        pinnedBottomActions = {
            TcButton(
                onClick = onCompleteDeliveryClick,
                variant = TcButtonVariant.Primary,
                size = TcButtonSize.Xl,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_check),
                    contentDescription = null,
                    modifier = Modifier.size(dimens.Size26),
                    tint = colors.accentInk
                )
                Text(
                    text = if (isMalayalam) "ഡെലിവറി സ്ഥിരീകരിക്കുക" else "Confirm delivery",
                    modifier = Modifier.padding(start = dimens.Gap8)
                )
            }
        }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(dimens.Gap12)
        ) {
            Text(
                text = if (isMalayalam) "ഡെലിവറി തെളിവ് (PoD)" else "Proof of delivery (PoD)",
                style = typography.caption,
                color = colors.muted
            )

            TcCard {
                Column(verticalArrangement = Arrangement.spacedBy(dimens.Gap10)) {
                    Text(
                        text = if (isMalayalam) "സ്വീകരിച്ച വ്യക്തിയുടെ പേര്" else "Receiver name",
                        style = typography.caption,
                        color = colors.muted
                    )
                    TcField(
                        value = receiverName,
                        onValueChange = { receiverName = it },
                        label = if (isMalayalam) "സ്വീകർത്താവ്" else "Receiver"
                    )
                }
            }

            Text(
                text = if (isMalayalam) "ഒപ്പ് രേഖപ്പെടുത്തിയ ബിൽ ഫോട്ടോ" else "Upload signed delivery challan",
                style = typography.caption,
                color = colors.muted
            )

            TcPhotoBox(
                label = if (isMalayalam) "ബിൽ ഫോട്ടോ എടുക്കുക" else "Tap to photograph signed challan",
                isFilled = true
            )

            Text(
                text = if (isMalayalam) "അൺലോഡ് ചെയ്ത സാധനങ്ങളുടെ ഫോട്ടോ" else "Goods unloaded photo",
                style = typography.caption,
                color = colors.muted
            )

            TcPhotoBox(
                label = if (isMalayalam) "സാധനങ്ങളുടെ ഫോട്ടോ എടുക്കുക" else "Tap to photograph unloaded cargo",
                isFilled = false
            )
        }
    }
}
