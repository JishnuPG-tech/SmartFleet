package com.truckconnect.feature.admin

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.truckconnect.core.design.R
import com.truckconnect.core.design.component.TcAvatar
import com.truckconnect.core.design.component.TcButton
import com.truckconnect.core.design.component.TcButtonSize
import com.truckconnect.core.design.component.TcButtonVariant
import com.truckconnect.core.design.component.TcCard
import com.truckconnect.core.design.component.TcChipKind
import com.truckconnect.core.design.component.TcConfirmDialog
import com.truckconnect.core.design.component.TcDetailItem
import com.truckconnect.core.design.component.TcDetailList
import com.truckconnect.core.design.component.TcIconButton
import com.truckconnect.core.design.component.TcScreen
import com.truckconnect.core.design.component.TcStatus
import com.truckconnect.core.design.theme.TcThemeState

/**
 * E07: Admin Driver Detail Screen
 *
 * Full production features per Antigravity v3:
 * - Rebuilt on TcScreen with safe status bar, pill cutout, and 3-button nav insets.
 * - Back button with 48 dp target + Edit button in top bar.
 * - Avatar correctly shows SK initials.
 * - Real formatted Kerala mobile number (+91 94470 12891) with Call & WhatsApp buttons.
 * - OD-1 Status chip with icon, 12% tint background (no decorative dot).
 * - OD-4 Vector ic_star for Rating (no text glyphs).
 * - OD-8 TcDetailList with 1 dp dividers.
 * - Pinned Reset Password and Revoke device actions with TcConfirmDialogs.
 */
@Composable
fun DriverDetailScreen(
    onBackClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onResetPasswordClick: () -> Unit = {},
    onRevokeDeviceClick: () -> Unit = {},
    driverName: String = "Suresh Kumar",
    driverPhone: String = "+91 94470 12891"
) {
    val context = LocalContext.current
    val colors = TcThemeState.colors
    val typography = TcThemeState.typography
    val dimens = TcThemeState.dimens

    var showRevokeDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }
    var passwordResetNotice by remember { mutableStateOf<String?>(null) }

    TcScreen(
        title = "Driver",
        onBackClick = onBackClick,
        isScrollable = true,
        topBarActions = {
            TcIconButton(onClick = onEditClick) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_edit),
                    contentDescription = "Edit profile",
                    modifier = Modifier.size(dimens.IconSizeDefault),
                    tint = colors.text
                )
            }
        },
        pinnedBottomActions = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TcButton(
                    onClick = { showResetDialog = true },
                    variant = TcButtonVariant.Outline,
                    size = TcButtonSize.Lg,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Reset password")
                }

                TcButton(
                    onClick = { showRevokeDialog = true },
                    variant = TcButtonVariant.CritOutline,
                    size = TcButtonSize.Lg,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Revoke device")
                }
            }
        }
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Profile Header
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                TcAvatar(text = "SK", isLarge = true)

                Text(
                    text = driverName,
                    style = typography.btnLg,
                    color = colors.text
                )

                // Contact Row with WhatsApp & Call intents
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = driverPhone,
                        style = typography.body,
                        color = colors.muted
                    )

                    TcIconButton(
                        onClick = {
                            val uri = Uri.parse("https://wa.me/919447012891")
                            val intent = Intent(Intent.ACTION_VIEW, uri)
                            context.startActivity(intent)
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_whatsapp),
                            contentDescription = "WhatsApp",
                            modifier = Modifier.size(15.dp),
                            tint = colors.ok
                        )
                    }

                    TcIconButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+919447012891"))
                            context.startActivity(intent)
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_phone),
                            contentDescription = "Call",
                            modifier = Modifier.size(15.dp),
                            tint = colors.accent
                        )
                    }
                }

                // OD-1 Status Chip (No dot)
                TcStatus(
                    text = "On trip",
                    kind = TcChipKind.Accent,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // 3-col KPI Grid with OD-4 Vector Star
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TcCard(modifier = Modifier.weight(1f)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Trips", style = typography.xsmall, color = colors.muted)
                        Text(text = "49", style = typography.btnLg, color = colors.text)
                    }
                }

                TcCard(modifier = Modifier.weight(1f)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "On-time", style = typography.xsmall, color = colors.muted)
                        Text(text = "82%", style = typography.btnLg, color = colors.text)
                    }
                }

                TcCard(modifier = Modifier.weight(1f)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Rating", style = typography.xsmall, color = colors.muted)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(text = "4.6", style = typography.btnLg, color = colors.text)
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.ic_star),
                                contentDescription = "Rating star",
                                modifier = Modifier.size(14.dp),
                                tint = colors.warn
                            )
                        }
                    }
                }
            }

            // OD-8 Details List with 1 dp dividers
            Text(
                text = "Vehicle & Credentials",
                style = typography.label,
                color = colors.muted,
                modifier = Modifier.padding(top = 4.dp)
            )

            TcDetailList(
                items = listOf(
                    TcDetailItem(
                        label = "Vehicle",
                        value = "KL-07-AZ-1200 · 10 t"
                    ),
                    TcDetailItem(
                        label = "Licence",
                        value = "HGMV · exp 2029",
                        trailingContent = {
                            TcStatus(text = "Valid", kind = TcChipKind.Ok)
                        }
                    ),
                    TcDetailItem(
                        label = "Registered Device",
                        value = "Samsung Galaxy A14"
                    ),
                    TcDetailItem(
                        label = "App Version",
                        value = "v2.1.0 · Active"
                    ),
                    TcDetailItem(
                        label = "Last Telemetry",
                        value = "2 min ago · Near Kozhikode"
                    )
                )
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Revoke Device Confirmation Dialog
        TcConfirmDialog(
            isOpen = showRevokeDialog,
            onDismiss = { showRevokeDialog = false },
            onConfirm = {
                showRevokeDialog = false
                onRevokeDeviceClick()
            },
            title = "Revoke Device Session?",
            message = "This will immediately sign out Suresh Kumar from Samsung Galaxy A14. The driver must re-authenticate with their credentials before accepting any further jobs.",
            confirmLabel = "Revoke Session",
            dismissLabel = "Cancel",
            isDestructive = true
        )

        // Reset Password Dialog
        TcConfirmDialog(
            isOpen = showResetDialog,
            onDismiss = { showResetDialog = false },
            onConfirm = {
                showResetDialog = false
                onResetPasswordClick()
                passwordResetNotice = "Temporary password generated: TC-PASS-9821"
            },
            title = "Reset Driver Password?",
            message = "Generate a new temporary password for Suresh Kumar? The driver will be required to change it on their next login.",
            confirmLabel = "Reset Password",
            dismissLabel = "Cancel",
            isDestructive = false
        )
    }
}
