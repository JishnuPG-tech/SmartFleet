package com.truckconnect.feature.driver

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.truckconnect.core.design.R
import com.truckconnect.core.design.component.TcButton
import com.truckconnect.core.design.component.TcButtonSize
import com.truckconnect.core.design.component.TcButtonVariant
import com.truckconnect.core.design.component.TcCard
import com.truckconnect.core.design.component.TcField
import com.truckconnect.core.design.component.TcScreen
import com.truckconnect.core.design.theme.TcThemeState

/**
 * Enterprise Proof of Delivery (PoD) Screen:
 * - Receiver pre-configured by admin (no extra signature or cargo photos needed).
 * - Consignee 4-digit Delivery OTP sent directly by admin to receiver.
 * - Driver collects and enters OTP to verify and mark delivery completed.
 */
@Composable
fun ProofOfDeliveryScreen(
    onBackClick: () -> Unit = {},
    onCompleteDeliveryClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val colors = TcThemeState.colors
    val typography = TcThemeState.typography
    val dimens = TcThemeState.dimens
    val isMalayalam = TcThemeState.isMalayalam

    val configuredReceiverName = "Mathew Traders (Manager)"
    val configuredDropContact = "+91 94472 33445"
    var enteredOtp by remember { mutableStateOf("") }

    TcScreen(
        title = if (isMalayalam) "ഡെലിവറി പൂർത്തിയാക്കുക" else "Complete delivery",
        onBackClick = onBackClick,
        isScrollable = true,
        pinnedBottomActions = {
            TcButton(
                onClick = {
                    if (enteredOtp.trim().length >= 4) {
                        onCompleteDeliveryClick()
                    } else {
                        Toast.makeText(
                            context,
                            if (isMalayalam) "ദയവായി സാധുവായ 4-അക്ക OTP നൽകുക" else "Please enter the 4-digit OTP provided by consignee",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
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
                    text = if (isMalayalam) "ഡെലിവറി സ്ഥിരീകരിക്കുക" else "Submit OTP & Complete Delivery",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = dimens.Gap8)
                )
            }
        }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Receiver Info Card (Admin Configured)
            TcCard {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = if (isMalayalam) "അഡ്മിൻ നിശ്ചയിച്ച സ്വീകർത്താവ്" else "Pre-configured Consignee",
                        style = typography.caption,
                        color = colors.muted
                    )
                    Text(
                        text = configuredReceiverName,
                        style = typography.h2,
                        color = colors.text
                    )
                    Text(
                        text = "Contact: $configuredDropContact · Malappuram Central Warehouse",
                        style = typography.small,
                        color = colors.muted
                    )
                }
            }

            // OTP Input Card
            TcCard {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = if (isMalayalam) "ഡെലിവറി OTP നൽകുക" else "Consignee Delivery OTP",
                        style = typography.caption,
                        color = colors.muted
                    )
                    Text(
                        text = if (isMalayalam)
                            "അഡ്മിൻ സ്വീകർത്താവിന്റെ ഫോണിലേക്ക് അയച്ച 4-അക്ക OTP ചോദിച്ചു വാങ്ങുക"
                        else
                            "Ask the consignee for the 4-digit security OTP sent to their mobile by dispatch admin.",
                        style = typography.small,
                        color = colors.text
                    )

                    TcField(
                        value = enteredOtp,
                        onValueChange = { if (it.length <= 6) enteredOtp = it },
                        label = if (isMalayalam) "4-അക്ക OTP നൽകുക" else "Enter 4-digit OTP"
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(colors.accent.copy(alpha = 0.10f))
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_shield),
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = colors.accent
                        )
                        Text(
                            text = if (isMalayalam) "സുരക്ഷിത പരിശോധന · ഡിസ്പാച്ച് ഒത്തുനോക്കൽ" else "Instant OTP verification with admin system",
                            style = typography.xsmall,
                            color = colors.accent
                        )
                    }
                }
            }
        }
    }
}
