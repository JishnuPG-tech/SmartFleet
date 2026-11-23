package com.truckconnect.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.truckconnect.core.design.component.TcAvatar
import com.truckconnect.core.design.component.TcButton
import com.truckconnect.core.design.component.TcButtonVariant
import com.truckconnect.core.design.component.TcCard
import com.truckconnect.core.design.theme.TcDimens
import com.truckconnect.core.design.theme.TcShapes
import com.truckconnect.core.design.theme.TcThemeState

@Composable
fun InstallPermissionScreen(
    onOpenSettingsClick: () -> Unit = {}
) {
    val colors = TcThemeState.colors
    val typography = TcThemeState.typography
    val dimens = TcThemeState.dimens

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bg)
            .padding(
                top = dimens.ScreenPaddingTop,
                start = dimens.Padding22,
                end = dimens.Padding22,
                bottom = dimens.ScreenPaddingBottom
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            dimens.Gap16,
            alignment = Alignment.CenterVertically
        )
    ) {
        // Shield Icon (84x84, radius 28)
        Box(
            modifier = Modifier
                .size(dimens.Size84)
                .clip(TcShapes.Radius28)
                .background(colors.warnIconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_shield),
                contentDescription = null,
                modifier = Modifier.size(dimens.Size42),
                tint = colors.warn
            )
        }

        Text(
            text = "Allow installs from TruckConnect",
            style = typography.h1,
            color = colors.text,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Android asks once for “Install unknown apps”. Turn it on for TruckConnect, then come back. We check the file’s signature before installing.",
            style = typography.body,
            color = colors.muted,
            textAlign = TextAlign.Center
        )

        TcCard {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(dimens.Gap8)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(dimens.Gap10)
                ) {
                    TcAvatar(
                        text = "1",
                        modifier = Modifier.size(dimens.Size28)
                    )
                    Text(
                        text = "Tap Open settings",
                        style = typography.small,
                        color = colors.text
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(dimens.Gap10)
                ) {
                    TcAvatar(
                        text = "2",
                        modifier = Modifier.size(dimens.Size28)
                    )
                    Text(
                        text = "Switch on Allow from this source",
                        style = typography.small,
                        color = colors.text
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(dimens.Gap10)
                ) {
                    TcAvatar(
                        text = "3",
                        modifier = Modifier.size(dimens.Size28)
                    )
                    Text(
                        text = "Press back and tap Install",
                        style = typography.small,
                        color = colors.text
                    )
                }
            }
        }

        TcButton(
            onClick = onOpenSettingsClick,
            variant = TcButtonVariant.Primary
        ) {
            Text(text = "Open settings")
        }
    }
}
