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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign

import androidx.compose.ui.unit.sp
import com.truckconnect.core.design.R
import com.truckconnect.core.design.component.TcButton
import com.truckconnect.core.design.component.TcButtonSize
import com.truckconnect.core.design.component.TcButtonVariant
import com.truckconnect.core.design.component.TcPulse
import com.truckconnect.core.design.theme.TcDimens
import com.truckconnect.core.design.theme.TcShapes
import com.truckconnect.core.design.theme.TcThemeState

@Composable
fun JobAlertScreen(
    onSpeakJobClick: () -> Unit = {},
    onRejectClick: () -> Unit = {},
    onAcceptClick: () -> Unit = {}
) {
    val colors = TcThemeState.colors
    val typography = TcThemeState.typography
    val dimens = TcThemeState.dimens
    val isMalayalam = TcThemeState.isMalayalam

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.accent)
            .padding(
                top = dimens.Padding18,
                start = dimens.Padding18,
                end = dimens.Padding18,
                bottom = dimens.Padding22
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        TcPulse {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_bell),
                contentDescription = null,
                modifier = Modifier.size(dimens.Size32),
                tint = colors.accent
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = (if (isMalayalam) "പുതിയ ജോലി" else "New job").uppercase(),
                style = typography.btnSm,
                color = colors.accentInk.copy(alpha = 0.85f)
            )
            Text(
                text = "Kasaragod\n→ Malappuram",
                style = typography.jobAlertDestination,
                color = colors.accentInk,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = dimens.Gap8)
            )
        }

        // Info pill
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(TcShapes.Hero)
                .background(colors.accentInk.copy(alpha = 0.14f))
                .padding(dimens.Padding14),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = if (isMalayalam) "സാധനങ്ങൾ" else "Goods",
                    style = typography.small,
                    color = colors.accentInk.copy(alpha = 0.8f)
                )
                Text(
                    text = "Vegetables",
                    style = typography.btnSm,
                    color = colors.accentInk
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = if (isMalayalam) "ഭാരം" else "Weight",
                    style = typography.small,
                    color = colors.accentInk.copy(alpha = 0.8f)
                )
                Text(
                    text = "8.5 t",
                    style = typography.btnSm,
                    color = colors.accentInk
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = if (isMalayalam) "ഇന്ന്" else "Today",
                    style = typography.small,
                    color = colors.accentInk.copy(alpha = 0.8f)
                )
                Text(
                    text = "6:00 am",
                    style = typography.btnSm,
                    color = colors.accentInk
                )
            }
        }

        // Speak Job button
        TcButton(
            onClick = onSpeakJobClick,
            variant = TcButtonVariant.Primary,
            size = TcButtonSize.Lg,
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.accentInk.copy(alpha = 0.18f), TcShapes.ButtonLg)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_vol),
                contentDescription = null,
                modifier = Modifier.size(dimens.Size22),
                tint = colors.accentInk
            )
            Text(
                text = if (isMalayalam) "ജോലി കേൾക്കുക" else "Speak job",
                color = colors.accentInk,
                modifier = Modifier.padding(start = dimens.Gap8)
            )
        }

        // Defect F1 fix: 2-button row with weight(1f) and spacedBy(dimens.Gap10)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(dimens.Gap10)
        ) {
            TcButton(
                onClick = onRejectClick,
                variant = TcButtonVariant.No,
                size = TcButtonSize.Xl,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_x),
                    contentDescription = null,
                    modifier = Modifier.size(dimens.Size26),
                    tint = Color.White
                )
                Text(
                    text = if (isMalayalam) "നിരസിക്കുക" else "Reject",
                    modifier = Modifier.padding(start = dimens.Gap6)
                )
            }

            TcButton(
                onClick = onAcceptClick,
                variant = TcButtonVariant.Ok,
                size = TcButtonSize.Xl,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_check),
                    contentDescription = null,
                    modifier = Modifier.size(dimens.Size26),
                    tint = Color.White
                )
                Text(
                    text = if (isMalayalam) "സ്വീകരിക്കുക" else "Accept",
                    modifier = Modifier.padding(start = dimens.Gap6)
                )
            }
        }
    }
}
