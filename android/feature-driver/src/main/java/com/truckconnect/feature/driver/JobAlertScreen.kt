package com.truckconnect.feature.driver

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.truckconnect.core.design.R
import com.truckconnect.core.design.component.TcButton
import com.truckconnect.core.design.component.TcButtonSize
import com.truckconnect.core.design.component.TcButtonVariant
import com.truckconnect.core.design.component.TcScreen
import com.truckconnect.core.design.theme.TcThemeState
import kotlinx.coroutines.delay

@Composable
fun JobAlertScreen(
    onSpeakJobClick: () -> Unit = {},
    onRejectClick: () -> Unit = {},
    onAcceptClick: () -> Unit = {},
    onLaterClick: () -> Unit = onRejectClick
) {
    val colors = TcThemeState.colors
    val typography = TcThemeState.typography
    val dimens = TcThemeState.dimens
    val isMalayalam = TcThemeState.isMalayalam

    var secondsRemaining by remember { mutableIntStateOf(581) } // ~9:41 min

    LaunchedEffect(Unit) {
        while (secondsRemaining > 0) {
            delay(1000L)
            secondsRemaining--
        }
    }

    val minutes = secondsRemaining / 60
    val seconds = secondsRemaining % 60
    val timerText = String.format("%02d:%02d", minutes, seconds)

    TcScreen(
        accentFullBleed = true,
        pinnedBottomActions = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Reject / Accept Actions Row
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

                // Decide Later text button
                Text(
                    text = if (isMalayalam) "പിന്നീട് തീരുമാനിക്കുക" else "Decide later",
                    style = typography.caption,
                    color = colors.accentInk.copy(alpha = 0.85f),
                    modifier = Modifier
                        .clickable(onClick = onLaterClick)
                        .padding(vertical = 4.dp)
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimens.ScreenPaddingHorizontal),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
        ) {
            // Pulse Rings (translucent outer rings per OD-5)
            Box(
                modifier = Modifier.size(130.dp),
                contentAlignment = Alignment.Center
            ) {
                // Outer ring 10%
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .clip(CircleShape)
                        .background(colors.accentInk.copy(alpha = 0.10f))
                )
                // Mid ring 16%
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .background(colors.accentInk.copy(alpha = 0.16f))
                )
                // Center disc
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(colors.surface),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_bell),
                        contentDescription = null,
                        modifier = Modifier.size(dimens.Size32),
                        tint = colors.accent
                    )
                }
            }

            // Route Destination
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = (if (isMalayalam) "പുതിയ ജോലി" else "New shipment assigned").uppercase(),
                    style = typography.btnSm,
                    color = colors.accentInk.copy(alpha = 0.90f)
                )
                Text(
                    text = "Kasaragod\n→ Malappuram",
                    style = typography.jobAlertDestination,
                    color = colors.accentInk,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = dimens.Gap8)
                )
            }

            // Info Card
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(colors.accentInk.copy(alpha = 0.14f))
                    .padding(14.dp),
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
                        text = if (isMalayalam) "പച്ചക്കറികൾ" else "Vegetables",
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
                        text = if (isMalayalam) "ദൂരം" else "Distance",
                        style = typography.small,
                        color = colors.accentInk.copy(alpha = 0.8f)
                    )
                    Text(
                        text = "165 km",
                        style = typography.btnSm,
                        color = colors.accentInk
                    )
                }
            }

            // Speak Job Button (Clear 1.5dp boundary)
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .border(1.5.dp, colors.accentInk.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                    .clickable(onClick = onSpeakJobClick)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_nav),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = colors.accentInk
                )
                Text(
                    text = if (isMalayalam) "ജോലി വായിക്കുക" else "Speak job details",
                    style = typography.btnSm,
                    color = colors.accentInk
                )
            }

            // Response Timer
            Text(
                text = if (isMalayalam) "പ്രതികരിക്കുക: $timerText നുള്ളിൽ" else "Respond within $timerText",
                style = typography.caption,
                color = colors.accentInk.copy(alpha = 0.85f)
            )
        }
    }
}
