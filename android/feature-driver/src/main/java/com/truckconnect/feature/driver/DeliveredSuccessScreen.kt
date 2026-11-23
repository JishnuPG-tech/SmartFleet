package com.truckconnect.feature.driver

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.truckconnect.core.design.R
import com.truckconnect.core.design.component.TcButton
import com.truckconnect.core.design.component.TcButtonSize
import com.truckconnect.core.design.component.TcButtonVariant
import com.truckconnect.core.design.component.TcCard
import com.truckconnect.core.design.component.TcChip
import com.truckconnect.core.design.component.TcChipKind
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
                Text(
                    text = if (isMalayalam) "ഹോമിലേക്ക് മടങ്ങുക" else "Back to home",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = dimens.Padding24),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                16.dp,
                alignment = Alignment.CenterVertically
            )
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(colors.accent.copy(alpha = 0.15f))
                    .border(2.dp, colors.accent, RoundedCornerShape(28.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_check),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = colors.accent
                )
            }

            Text(
                text = if (isMalayalam) "ഡെലിവറി പൂർത്തിയായി!" else "Delivery completed!",
                style = typography.h1,
                color = colors.text,
                textAlign = TextAlign.Center
            )

            // Cloud Sync Status Badge
            TcChip(
                text = if (isMalayalam) "ക്ലൗഡിലേക്ക് അപ്‌ലോഡ് ചെയ്തു · സമന്വയിപ്പിച്ചു" else "Synced to cloud · PoD recorded",
                kind = TcChipKind.Ok
            )

            // Trip Summary & Earnings Receipt Card
            TcCard {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isMalayalam) "ഷിപ്പ്മെന്റ് ഐഡി" else "Shipment ID",
                            style = typography.caption,
                            color = colors.muted
                        )
                        Text(
                            text = "TC-2026-000131",
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
                            text = if (isMalayalam) "റൂട്ട്" else "Route",
                            style = typography.caption,
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
                            text = if (isMalayalam) "സാധനങ്ങൾ / ഭാരം" else "Goods / Weight",
                            style = typography.caption,
                            color = colors.muted
                        )
                        Text(
                            text = "Vegetables · 8.5 t",
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
                            text = if (isMalayalam) "ഡ്രൈവർ ബത്ത / ഇന്ധനം" else "Trip allowance / Fuel",
                            style = typography.caption,
                            color = colors.muted
                        )
                        Text(
                            text = "₹4,850 (Credited)",
                            style = typography.btnSm,
                            fontWeight = FontWeight.Bold,
                            color = colors.accent
                        )
                    }
                }
            }
        }
    }
}
