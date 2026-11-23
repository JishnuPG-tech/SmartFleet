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
import com.truckconnect.core.design.component.TcButton
import com.truckconnect.core.design.component.TcButtonSize
import com.truckconnect.core.design.component.TcButtonVariant
import com.truckconnect.core.design.component.TcCard
import com.truckconnect.core.design.theme.TcDimens
import com.truckconnect.core.design.theme.TcShapes
import com.truckconnect.core.design.theme.TcThemeState

@Composable
fun ForcedUpdateScreen(
    onUpdateClick: () -> Unit = {}
) {
    val colors = TcThemeState.colors
    val typography = TcThemeState.typography
    val dimens = TcThemeState.dimens
    val isMalayalam = TcThemeState.isMalayalam

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.surface)
            .padding(
                top = dimens.ScreenPaddingTop,
                start = dimens.Padding24,
                end = dimens.Padding24,
                bottom = dimens.ScreenPaddingBottom
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            dimens.Gap16,
            alignment = Alignment.CenterVertically
        )
    ) {
        // Warning Icon (92x92, radius 30)
        Box(
            modifier = Modifier
                .size(dimens.Size92)
                .clip(TcShapes.Radius30)
                .background(colors.critIconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_warn),
                contentDescription = null,
                modifier = Modifier.size(dimens.Size44),
                tint = colors.crit
            )
        }

        Text(
            text = if (isMalayalam) "അപ്ഡേറ്റ് ആവശ്യമാണ്" else "Update required",
            style = typography.h1,
            color = colors.text,
            textAlign = TextAlign.Center
        )

        Text(
            text = if (isMalayalam)
                "തുടരാൻ ഏറ്റവും പുതിയ പതിപ്പ് ഇൻസ്റ്റാൾ ചെയ്യുക"
            else
                "This version is no longer supported. Install the latest to continue.",
            style = typography.body,
            color = colors.muted,
            textAlign = TextAlign.Center
        )

        TcCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Installed",
                    style = typography.small,
                    color = colors.muted
                )
                Text(
                    text = "v2.0.1",
                    style = typography.btnSm,
                    color = colors.text
                )
                Text(
                    text = "→",
                    style = typography.small,
                    color = colors.muted
                )
                Text(
                    text = "v2.1.1",
                    style = typography.btnSm,
                    color = colors.accent
                )
            }
        }

        TcButton(
            onClick = onUpdateClick,
            variant = TcButtonVariant.Primary,
            size = TcButtonSize.Xl
        ) {
            Text(text = if (isMalayalam) "അപ്ഡേറ്റ്" else "Update")
        }

        Text(
            text = "Never shown during an active trip",
            style = typography.note,
            color = colors.muted,
            textAlign = TextAlign.Center
        )
    }
}
