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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import com.truckconnect.core.design.R
import com.truckconnect.core.design.component.TcButton
import com.truckconnect.core.design.component.TcButtonSize
import com.truckconnect.core.design.component.TcButtonVariant
import com.truckconnect.core.design.component.TcCard
import com.truckconnect.core.design.component.TcChip
import com.truckconnect.core.design.component.TcChipKind
import com.truckconnect.core.design.component.TcScreen
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

    TcScreen(
        pinnedBottomActions = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TcButton(
                    onClick = {
                        try {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+919876543210"))
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            onCallOfficeClick()
                        }
                    },
                    variant = TcButtonVariant.Outline,
                    size = TcButtonSize.Lg,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_phone),
                        contentDescription = null,
                        modifier = Modifier.size(dimens.Size22),
                        tint = colors.text
                    )
                    Text(
                        text = if (isMalayalam) "ഓഫീസിലേക്ക് വിളിക്കുക" else "Call central dispatch",
                        modifier = Modifier.padding(start = dimens.Gap8)
                    )
                }

                TcButton(
                    onClick = onContinueClick,
                    variant = TcButtonVariant.Primary,
                    size = TcButtonSize.Lg,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = if (isMalayalam) "അംഗീകരിച്ചു — തുടരുക" else "Simulate approved — Continue")
                }
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = dimens.Padding24),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(colors.warnIconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_clock),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = colors.warn
                )
            }

            Text(
                text = if (isMalayalam) "അംഗീകാരത്തിനായി കാത്തിരിക്കുന്നു" else "Verification in progress",
                style = typography.h1,
                color = colors.text,
                textAlign = TextAlign.Center
            )

            Text(
                text = if (isMalayalam)
                    "നിങ്ങളുടെ രേഖകൾ ഫ്ലീറ്റ് മാനേജർ പരിശോധിച്ചു കൊണ്ടിരിക്കുന്നു. സാധാരണയായി 2 മണിക്കൂറിനകം പൂർത്തിയാകും."
                else
                    "Your documents have been submitted to fleet management. Verification usually completes within 2 hours.",
                style = typography.body,
                color = colors.muted,
                textAlign = TextAlign.Center
            )

            TcChip(
                text = if (isMalayalam) "പരിശോധനയിലാണ്" else "Under Review",
                kind = TcChipKind.Warn
            )

            TcCard {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isMalayalam) "അപേക്ഷ ഐഡി" else "Application ID",
                            style = typography.xsmall,
                            color = colors.muted
                        )
                        Text(
                            text = "APP-2026-9912",
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
                            text = if (isMalayalam) "സമർപ്പിച്ചത്" else "Submitted",
                            style = typography.xsmall,
                            color = colors.muted
                        )
                        Text(
                            text = "Today, 10:14 am",
                            style = typography.xsmall,
                            color = colors.text
                        )
                    }
                }
            }
        }
    }
}
