package com.truckconnect.feature.driver

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.truckconnect.core.design.component.TcAppBar
import com.truckconnect.core.design.component.TcButton
import com.truckconnect.core.design.component.TcButtonSize
import com.truckconnect.core.design.component.TcButtonVariant
import com.truckconnect.core.design.component.TcCard
import com.truckconnect.core.design.component.TcRadio
import com.truckconnect.core.design.component.TcScrim
import com.truckconnect.core.design.component.TcSheet
import com.truckconnect.core.design.theme.TcDimens
import com.truckconnect.core.design.theme.TcThemeState

@Composable
fun RejectReasonSheet(
    onDismiss: () -> Unit = {},
    onRejectConfirm: () -> Unit = {},
    onLaterClick: () -> Unit = {}
) {
    val colors = TcThemeState.colors
    val typography = TcThemeState.typography
    val dimens = TcThemeState.dimens
    val isMalayalam = TcThemeState.isMalayalam

    var selectedIndex by remember { mutableIntStateOf(0) }

    val reasons = listOf(
        if (isMalayalam) "വാഹനത്തിന് പ്രശ്നം" else "Vehicle problem",
        if (isMalayalam) "ഞാൻ അവധിയിലാണ്" else "On leave",
        if (isMalayalam) "ദൂരം കൂടുതലാണ്" else "Too far"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // Dim background over background screen
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.bg)
        ) {
            TcAppBar(title = if (isMalayalam) "പുതിയ ജോലി" else "New job")
        }

        TcScrim(onDismiss = onDismiss)

        // Bottom Sheet
        TcSheet(modifier = Modifier.align(Alignment.BottomCenter)) {
            Text(
                text = if (isMalayalam) "കാരണം" else "Reason",
                style = typography.h1,
                color = colors.text,
                modifier = Modifier.padding(bottom = dimens.Gap4)
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(dimens.Gap10)
            ) {
                reasons.forEachIndexed { index, reason ->
                    val isSelected = index == selectedIndex
                    TcCard(
                        onClick = { selectedIndex = index }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(dimens.Gap10)
                        ) {
                            TcRadio(
                                selected = isSelected,
                                onClick = { selectedIndex = index }
                            )
                            Text(
                                text = reason,
                                style = typography.btnLg,
                                color = colors.text
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier.padding(top = dimens.Gap6),
                verticalArrangement = Arrangement.spacedBy(dimens.Gap10)
            ) {
                TcButton(
                    onClick = onRejectConfirm,
                    variant = TcButtonVariant.No,
                    size = TcButtonSize.Xl
                ) {
                    Text(text = if (isMalayalam) "നിരസിക്കുക" else "Reject")
                }

                TcButton(
                    onClick = onLaterClick,
                    variant = TcButtonVariant.Outline,
                    size = TcButtonSize.Lg
                ) {
                    Text(text = if (isMalayalam) "പിന്നീട്" else "Later")
                }
            }
        }
    }
}
