package com.truckconnect.feature.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import com.truckconnect.core.design.theme.TcDimens
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.accent)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = dimens.ScreenPaddingHorizontal,
                    end = dimens.ScreenPaddingHorizontal,
                    bottom = dimens.Padding14
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                dimens.Gap18,
                alignment = Alignment.CenterVertically
            )
        ) {
            Box(
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    logoTapCount++
                    if (logoTapCount >= 5) {
                        onOpenGallery()
                    }
                }
            ) {
                TcLogoTile()
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "TruckConnect",
                    style = typography.splashTitle,
                    color = colors.accentInk
                )
                Text(
                    text = if (isMalayalam) "ലോഡ് · യാത്ര · ഡെലിവറി" else "Dispatch · Track · Deliver",
                    style = typography.body,
                    color = colors.accentInk.copy(alpha = 0.8f),
                    modifier = Modifier.padding(top = dimens.Gap6)
                )
            }
        }

        Text(
            text = "v2.1.0 · Kerala",
            style = typography.xsmall,
            color = colors.accentInk.copy(alpha = 0.7f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = dimens.Padding34)
        )
    }
}
