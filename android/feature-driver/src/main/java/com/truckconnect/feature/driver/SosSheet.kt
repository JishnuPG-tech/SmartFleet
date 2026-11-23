package com.truckconnect.feature.driver

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import com.truckconnect.core.design.component.TcAppBar
import com.truckconnect.core.design.component.TcButton
import com.truckconnect.core.design.component.TcButtonSize
import com.truckconnect.core.design.component.TcButtonVariant
import com.truckconnect.core.design.component.TcScrim
import com.truckconnect.core.design.component.TcSheet
import com.truckconnect.core.design.theme.TcDimens
import com.truckconnect.core.design.theme.TcThemeState

@Composable
fun SosSheet(
    onDismiss: () -> Unit = {},
    onHoldToSend: () -> Unit = {},
    onCancel: () -> Unit = {}
) {
    val colors = TcThemeState.colors
    val typography = TcThemeState.typography
    val dimens = TcThemeState.dimens
    val isMalayalam = TcThemeState.isMalayalam

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.bg)
        ) {
            TcAppBar(title = "SOS")
        }

        TcScrim(onDismiss = onDismiss)

        TcSheet(modifier = Modifier.align(Alignment.BottomCenter)) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(dimens.Gap10)
            ) {
                // SOS Circle Icon (84x84, circle)
                Box(
                    modifier = Modifier
                        .size(dimens.Size84)
                        .clip(CircleShape)
                        .background(colors.critIconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_sos),
                        contentDescription = null,
                        modifier = Modifier.size(dimens.Size44),
                        tint = colors.crit
                    )
                }

                Text(
                    text = if (isMalayalam) "അടിയന്തര സഹായം അയയ്ക്കുക?" else "Send SOS?",
                    style = typography.h1,
                    color = colors.text,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = if (isMalayalam)
                        "നിങ്ങളുടെ സ്ഥലം ഓഫീസിലേക്ക് അയയ്ക്കും"
                    else
                        "Your live location will be sent to the office",
                    style = typography.small,
                    color = colors.muted,
                    textAlign = TextAlign.Center
                )
            }

            Column(
                modifier = Modifier.padding(top = dimens.Gap4),
                verticalArrangement = Arrangement.spacedBy(dimens.Gap10)
            ) {
                TcButton(
                    onClick = onHoldToSend,
                    variant = TcButtonVariant.No,
                    size = TcButtonSize.Xl
                ) {
                    Text(text = if (isMalayalam) "അയയ്ക്കാൻ അമർത്തിപ്പിടിക്കുക" else "Hold to send")
                }

                TcButton(
                    onClick = onCancel,
                    variant = TcButtonVariant.Outline,
                    size = TcButtonSize.Lg
                ) {
                    Text(text = if (isMalayalam) "വേണ്ട" else "Cancel")
                }
            }
        }
    }
}
