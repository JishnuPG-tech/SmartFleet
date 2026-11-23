package com.truckconnect.feature.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.truckconnect.core.design.component.TcLogoTile
import com.truckconnect.core.design.component.TcScreen
import com.truckconnect.core.design.theme.TcThemeState
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashComplete: () -> Unit = {},
    onOpenGallery: () -> Unit = {}
) {
    val colors = TcThemeState.colors
    val typography = TcThemeState.typography
    val dimens = TcThemeState.dimens
    val isMalayalam = TcThemeState.isMalayalam

    var logoTapCount by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        delay(1500)
        if (logoTapCount < 5) {
            onSplashComplete()
        }
    }

    TcScreen(
        accentFullBleed = true,
        pinnedBottomActions = {
            Text(
                text = "v2.1.0 · Build 42 · Powered by SmartFleet Engine",
                style = typography.note,
                color = colors.accentInk.copy(alpha = 0.75f),
                modifier = Modifier.padding(bottom = dimens.Padding4)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = dimens.ScreenPaddingHorizontal),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                dimens.Gap18,
                alignment = Alignment.CenterVertically
            )
        ) {
            TcLogoTile(
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {
                        logoTapCount++
                        if (logoTapCount >= 5) {
                            onOpenGallery()
                        }
                    }
                )
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(dimens.Gap6)
            ) {
                Text(
                    text = "TruckConnect",
                    style = typography.splashTitle,
                    color = colors.accentInk
                )
                Text(
                    text = if (isMalayalam)
                        "കേരളത്തിന്റെ പ്രീമിയം ഫ്ലീറ്റ് നെറ്റ്വർക്ക്"
                    else
                        "Enterprise Logistics & Fleet Operations",
                    style = typography.body,
                    color = colors.accentInk.copy(alpha = 0.90f)
                )
            }
        }
    }
}
