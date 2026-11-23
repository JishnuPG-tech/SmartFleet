package com.truckconnect.feature.auth

import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import com.truckconnect.core.design.R
import com.truckconnect.core.design.component.TcButton
import com.truckconnect.core.design.component.TcButtonSize
import com.truckconnect.core.design.component.TcButtonVariant
import com.truckconnect.core.design.component.TcCard
import com.truckconnect.core.design.component.TcChip
import com.truckconnect.core.design.component.TcChipKind
import com.truckconnect.core.design.component.TcScaffold
import com.truckconnect.core.design.theme.TcDimens
import com.truckconnect.core.design.theme.TcShapes
import com.truckconnect.core.design.theme.TcThemeState

@Composable
fun PendingApprovalScreen(
    onCallOfficeClick: () -> Unit = {},
    onContinueClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val colors = TcThemeState.colors
    val typography = TcThemeState.typography
    val dimens = TcThemeState.dimens
    val isMalayalam = TcThemeState.isMalayalam

    TcScaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(
                    start = dimens.Padding24,
                    end = dimens.Padding24,
                    top = dimens.Gap20,
                    bottom = dimens.ScreenPaddingBottom
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                dimens.Gap16,
                alignment = Alignment.CenterVertically
            )
        ) {
            // Warning Icon
            Box(
                modifier = Modifier
                    .size(dimens.Size84)
                    .clip(TcShapes.Radius28)
                    .background(colors.warnIconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_clock),
                    contentDescription = null,
                    modifier = Modifier.size(dimens.Size40),
                    tint = colors.warn
                )
            }

            Text(
                text = if (isMalayalam) "അംഗീകാരത്തിനായി കാത്തിരിക്കുന്നു" else "Waiting for approval",
                style = typography.h1,
                color = colors.text,
                textAlign = TextAlign.Center
            )

            Text(
                text = if (isMalayalam)
                    "നിങ്ങളുടെ വിവരങ്ങൾ ഓഫീസ് പരിശോധിക്കുന്നു. അംഗീകരിച്ചാൽ അറിയിക്കും."
                else
                    "The office is verifying your details. You will get an alert once approved.",
                style = typography.body,
                color = colors.muted,
                textAlign = TextAlign.Center
            )

            TcCard {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(dimens.Gap10)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isMalayalam) "അപേക്ഷാ നമ്പർ" else "Application",
                            style = typography.small,
                            color = colors.muted
                        )
                        Text(
                            text = "APP-2026-0881",
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
                            text = if (isMalayalam) "സ്റ്റാറ്റസ്" else "Status",
                            style = typography.small,
                            color = colors.muted
                        )
                        TcChip(
                            text = if (isMalayalam) "പരിശോധനയിൽ" else "In review",
                            kind = TcChipKind.Warn
                        )
                    }
                }
            }

            TcButton(
                onClick = {
                    val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+914840000000"))
                    context.startActivity(dialIntent)
                    onCallOfficeClick()
                },
                variant = TcButtonVariant.Outline,
                size = TcButtonSize.Lg,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_phone),
                    contentDescription = null,
                    modifier = Modifier.size(dimens.Size20),
                    tint = colors.text
                )
                Text(
                    text = if (isMalayalam) "ഓഫീസിനെ വിളിക്കുക" else "Call office",
                    modifier = Modifier.padding(start = dimens.Gap8)
                )
            }

            TcButton(
                text = if (isMalayalam) "അംഗീകാരം സ്ഥിരീകരിക്കുക" else "Continue when approved",
                onClick = onContinueClick,
                variant = TcButtonVariant.Primary,
                size = TcButtonSize.Md,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
