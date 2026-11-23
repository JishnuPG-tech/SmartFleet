package com.truckconnect.feature.driver

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.truckconnect.core.design.R
import com.truckconnect.core.design.component.TcButton
import com.truckconnect.core.design.component.TcButtonSize
import com.truckconnect.core.design.component.TcButtonVariant
import com.truckconnect.core.design.component.TcCard
import com.truckconnect.core.design.component.TcScreen
import com.truckconnect.core.design.theme.TcThemeState

@Composable
fun DeliveredSuccessScreen(
    onBackToHomeClick: () -> Unit = {}
) {
    val colors = TcThemeState.colors
    val typography = TcThemeState.typography
    val dimens = TcThemeState.dimens
    val isMalayalam = TcThemeState.isMalayalam

    TcScreen(
        pinnedBottomActions = {
            TcButton(
                onClick = onBackToHomeClick,
                variant = TcButtonVariant.Primary,
                size = TcButtonSize.Lg,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = if (isMalayalam) "ഹോമിലേക്ക് മടങ്ങുക" else "Back to home")
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = dimens.Padding24),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                dimens.Gap18,
                alignment = Alignment.CenterVertically
            )
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(colors.okIconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_check),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = colors.ok
                )
            }

            Text(
                text = if (isMalayalam) "ഡെലിവറി പൂർത്തിയായി!" else "Delivery completed!",
                style = typography.h1,
                color = colors.text,
                textAlign = TextAlign.Center
            )

            Text(
                text = if (isMalayalam)
                    "TC-2026-000131 സുരക്ഷിതമായി എത്തിച്ചു. PoD രേഖകൾ ഫ്ലീറ്റ് സിസ്റ്റത്തിലേക്ക് അപ്‌ലോഡ് ചെയ്തു."
                else
                    "Shipment TC-2026-000131 delivered safely. Proof of delivery uploaded to fleet console.",
                style = typography.body,
                color = colors.muted,
                textAlign = TextAlign.Center
            )

            TcCard {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isMalayalam) "റൂട്ട്" else "Route",
                            style = typography.xsmall,
                            color = colors.muted
                        )
                        Text(
                            text = "Kasaragod → Malappuram",
                            style = typography.btnSm,
                            color = colors.text
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isMalayalam) "സമയം" else "Completion time",
                            style = typography.xsmall,
                            color = colors.muted
                        )
                        Text(
                            text = "Today, 2:36 pm",
                            style = typography.xsmall,
                            color = colors.text
                        )
                    }
                }
            }
        }
    }
}
