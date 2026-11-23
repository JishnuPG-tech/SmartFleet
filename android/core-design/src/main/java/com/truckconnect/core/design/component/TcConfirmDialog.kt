package com.truckconnect.core.design.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.truckconnect.core.design.R
import com.truckconnect.core.design.theme.TcThemeState

/**
 * Section 4: Universal Confirmation Dialog for high-impact/destructive actions.
 * Used for:
 * - Cancel shipment
 * - Revoke device
 * - Force update (shows affected drivers count)
 * - Halt rollout
 * - Log out
 * - Reset PIN
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TcConfirmDialog(
    isOpen: Boolean = true,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    title: String,
    message: String,
    confirmLabel: String = "Confirm",
    dismissLabel: String = "Cancel",
    isDestructive: Boolean = true,
    affectedCount: Int? = null
) {
    if (!isOpen) return

    val colors = TcThemeState.colors
    val typography = TcThemeState.typography
    val dimens = TcThemeState.dimens

    BasicAlertDialog(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(colors.surface)
                .border(dimens.CardBorder, colors.border, RoundedCornerShape(20.dp))
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (isDestructive) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(colors.crit.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_warn),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = colors.crit
                        )
                    }
                }
                Text(
                    text = title,
                    style = typography.h2,
                    color = colors.text
                )
            }

            Text(
                text = message,
                style = typography.body,
                color = colors.muted
            )

            if (affectedCount != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(colors.warn.copy(alpha = 0.12f))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Affects $affectedCount active drivers",
                        style = typography.btnSm,
                        color = colors.warn
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TcButton(
                    onClick = onDismiss,
                    variant = TcButtonVariant.Outline,
                    size = TcButtonSize.Lg,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = dismissLabel)
                }

                TcButton(
                    onClick = {
                        onConfirm()
                        onDismiss()
                    },
                    variant = if (isDestructive) TcButtonVariant.Crit else TcButtonVariant.Primary,
                    size = TcButtonSize.Lg,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = confirmLabel)
                }
            }
        }
    }
}
