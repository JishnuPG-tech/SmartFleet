package com.truckconnect.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.truckconnect.core.design.R
import com.truckconnect.core.design.component.TcAppBar
import com.truckconnect.core.design.component.TcButton
import com.truckconnect.core.design.component.TcButtonSize
import com.truckconnect.core.design.component.TcButtonVariant
import com.truckconnect.core.design.component.TcCard
import com.truckconnect.core.design.component.TcIconBox
import com.truckconnect.core.design.component.TcProgressBar
import com.truckconnect.core.design.component.TcScrim
import com.truckconnect.core.design.component.TcSheet
import com.truckconnect.core.design.theme.TcDimens
import com.truckconnect.core.design.theme.TcThemeState

@Composable
fun UpdateSheet(
    onDismiss: () -> Unit = {},
    onLaterClick: () -> Unit = {},
    onUpdateClick: () -> Unit = {}
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
            TcAppBar(title = if (isMalayalam) "ഹോം" else "Home")
        }

        TcScrim(onDismiss = onDismiss)

        TcSheet(modifier = Modifier.align(Alignment.BottomCenter)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dimens.Gap10)
            ) {
                TcIconBox(iconRes = R.drawable.ic_download)
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isMalayalam) "പുതിയ പതിപ്പ് ലഭ്യമാണ്" else "New version available",
                        style = typography.btnLg,
                        color = colors.text
                    )
                    Text(
                        text = "v2.1.1 · 18 MB",
                        style = typography.small,
                        color = colors.muted
                    )
                }
            }

            // Release Notes Card
            TcCard {
                Text(
                    text = if (isMalayalam)
                        "• വേഗത്തിലുള്ള ജോലി അറിയിപ്പ്\n• ചെറിയ തകരാറുകൾ പരിഹരിച്ചു"
                    else
                        "• Faster job alerts\n• Small fixes",
                    style = typography.small,
                    color = colors.text
                )
            }

            TcProgressBar(progress = 0.64f)

            Text(
                text = "Downloading… 64%",
                style = typography.xsmall,
                color = colors.muted,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            // Action Row (Defect F1 fixed: weight(1f), spacedBy(dimens.Gap10))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(dimens.Gap10)
            ) {
                TcButton(
                    onClick = onLaterClick,
                    variant = TcButtonVariant.Outline,
                    size = TcButtonSize.Lg,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = if (isMalayalam) "പിന്നീട്" else "Later")
                }

                TcButton(
                    onClick = onUpdateClick,
                    variant = TcButtonVariant.Primary,
                    size = TcButtonSize.Lg,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = if (isMalayalam) "അപ്ഡേറ്റ്" else "Update")
                }
            }
        }
    }
}
