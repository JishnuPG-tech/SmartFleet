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

import com.truckconnect.core.design.R
import com.truckconnect.core.design.component.TcButton
import com.truckconnect.core.design.component.TcButtonSize
import com.truckconnect.core.design.component.TcButtonVariant
import com.truckconnect.core.design.component.TcCard
import com.truckconnect.core.design.theme.TcDimens
import com.truckconnect.core.design.theme.TcThemeState

@Composable
fun DeliveredSuccessScreen(
    onBackToHomeClick: () -> Unit = {}
) {
    val colors = TcThemeState.colors
    val typography = TcThemeState.typography
    val dimens = TcThemeState.dimens
    val isMalayalam = TcThemeState.isMalayalam

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bg)
            .padding(
                top = dimens.ScreenPaddingTop,
                start = dimens.Padding24,
                end = dimens.Padding24,
                bottom = dimens.ScreenPaddingBottom
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            dimens.Gap18,
            alignment = Alignment.CenterVertically
        )
    ) {
        // Ok Check Icon Box (110x110, radius 36)
        Box(
            modifier = Modifier
                .size(dimens.Size110)
                .clip(RoundedCornerShape(dimens.Size36))
                .background(colors.okIconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_check),
                contentDescription = null,
                modifier = Modifier.size(dimens.Size58),
                tint = colors.ok
            )
        }

        Text(
            text = if (isMalayalam) "എത്തിച്ചു" else "Delivered",
            style = typography.h1,
            color = colors.text,
            textAlign = TextAlign.Center
        )

        TcCard {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(dimens.Gap8)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TC-2026-000131",
                        style = typography.small,
                        color = colors.muted
                    )
                    Text(
                        text = "2:36 pm",
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
                        text = if (isMalayalam) "സ്വീകരിച്ചയാളുടെ പേര്" else "Receiver name",
                        style = typography.small,
                        color = colors.muted
                    )
                    Text(
                        text = "Mathew Traders",
                        style = typography.btnSm,
                        color = colors.text
                    )
                }
            }
        }

        TcButton(
            onClick = onBackToHomeClick,
            variant = TcButtonVariant.Primary,
            size = TcButtonSize.Xl
        ) {
            Text(text = if (isMalayalam) "ഹോമിലേക്ക് മടങ്ങുക" else "Back to home")
        }
    }
}
