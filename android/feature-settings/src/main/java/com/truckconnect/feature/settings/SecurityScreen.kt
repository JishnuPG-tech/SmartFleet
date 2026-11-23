package com.truckconnect.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.truckconnect.core.design.R
import com.truckconnect.core.design.component.TcAppBar
import com.truckconnect.core.design.component.TcButton
import com.truckconnect.core.design.component.TcButtonSize
import com.truckconnect.core.design.component.TcButtonVariant
import com.truckconnect.core.design.component.TcCard
import com.truckconnect.core.design.component.TcChip
import com.truckconnect.core.design.component.TcChipKind
import com.truckconnect.core.design.component.TcField
import com.truckconnect.core.design.component.TcIconBox
import com.truckconnect.core.design.component.TcListRow
import com.truckconnect.core.design.component.TcScreen
import com.truckconnect.core.design.component.TcToggle
import com.truckconnect.core.design.theme.TcDimens
import com.truckconnect.core.design.theme.TcThemeState

data class DeviceSession(
    val id: String,
    val name: String,
    val location: String,
    val isCurrent: Boolean
)

/**
 * Security & Device Management Screen (S6 / X14 / X16)
 *
 * Full functional controls:
 * - App lock toggle (PIN / Biometric)
 * - 2-step verification toggle
 * - Change Password modal sheet (X14)
 * - Active signed-in device sessions list with Revoke action
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityScreen(
    onBackClick: () -> Unit = {}
) {
    val colors = TcThemeState.colors
    val typography = TcThemeState.typography
    val dimens = TcThemeState.dimens

    var appLockEnabled by remember { mutableStateOf(true) }
    var twoStepEnabled by remember { mutableStateOf(true) }
    var showChangePasswordSheet by remember { mutableStateOf(false) }

    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordChangeSuccess by remember { mutableStateOf(false) }

    val devices = remember {
        mutableStateListOf(
            DeviceSession("1", "OnePlus 12 · Android 14", "Kochi · Active now", true),
            DeviceSession("2", "Chrome Mac · macOS 15", "Bengaluru · 2h ago", false),
            DeviceSession("3", "Redmi Note 12 · Android 13", "Thrissur · 3d ago", false)
        )
    }

    TcScreen(
        title = "Security",
        onBackClick = onBackClick,
        isScrollable = true
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(dimens.Gap12)
        ) {
            // Options Card
            TcCard {
                Column {
                    TcListRow {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(dimens.Gap12)
                        ) {
                            TcIconBox(
                                iconRes = R.drawable.ic_lock,
                                modifier = Modifier.size(dimens.Size34)
                            )
                            Text(
                                text = "App lock (PIN / biometric)",
                                style = typography.btnSm,
                                color = colors.text,
                                modifier = Modifier.weight(1f)
                            )
                            TcToggle(
                                checked = appLockEnabled,
                                onCheckedChange = { appLockEnabled = it }
                            )
                        }
                    }

                    TcListRow {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(dimens.Gap12)
                        ) {
                            TcIconBox(
                                iconRes = R.drawable.ic_shield,
                                modifier = Modifier.size(dimens.Size34)
                            )
                            Text(
                                text = "2-step verification",
                                style = typography.btnSm,
                                color = colors.text,
                                modifier = Modifier.weight(1f)
                            )
                            TcToggle(
                                checked = twoStepEnabled,
                                onCheckedChange = { twoStepEnabled = it }
                            )
                        }
                    }

                    TcListRow(
                        isLast = true,
                        onClick = { showChangePasswordSheet = true }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(dimens.Gap12)
                        ) {
                            TcIconBox(
                                iconRes = R.drawable.ic_key,
                                modifier = Modifier.size(dimens.Size34)
                            )
                            Text(
                                text = "Change password",
                                style = typography.btnSm,
                                color = colors.text,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.ic_chev),
                                contentDescription = null,
                                modifier = Modifier.size(dimens.Size16),
                                tint = colors.text2
                            )
                        }
                    }
                }
            }

            Text(
                text = "Signed-in devices (${devices.size})",
                style = typography.btnSm,
                color = colors.text
            )

            // Devices List Card
            TcCard {
                Column {
                    devices.forEachIndexed { index, device ->
                        TcListRow(isLast = index == devices.lastIndex) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(dimens.Gap12)
                            ) {
                                TcIconBox(
                                    iconRes = R.drawable.ic_truck,
                                    modifier = Modifier.size(dimens.Size34)
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = device.name,
                                        style = typography.btnSm,
                                        color = colors.text
                                    )
                                    Text(
                                        text = device.location,
                                        style = typography.xsmall,
                                        color = colors.muted
                                    )
                                }
                                if (device.isCurrent) {
                                    TcChip(text = "This device", kind = TcChipKind.Ok)
                                } else {
                                    TcButton(
                                        text = "Revoke",
                                        onClick = { devices.remove(device) },
                                        variant = TcButtonVariant.Outline,
                                        size = TcButtonSize.Sm
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Change Password Modal Sheet (X14)
        if (showChangePasswordSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showChangePasswordSheet = false
                    passwordChangeSuccess = false
                },
                containerColor = colors.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = dimens.ScreenPaddingHorizontal)
                        .padding(bottom = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Change account password",
                        style = typography.h2,
                        color = colors.text
                    )

                    TcField(
                        value = oldPassword,
                        onValueChange = { oldPassword = it },
                        placeholder = "Current password"
                    )

                    TcField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        placeholder = "New password (min 8 chars)"
                    )

                    TcField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        placeholder = "Confirm new password"
                    )

                    if (passwordChangeSuccess) {
                        Text(
                            text = "Password updated successfully!",
                            style = typography.btnSm,
                            color = colors.ok
                        )
                    }

                    TcButton(
                        text = "Update password",
                        onClick = {
                            if (newPassword.isNotEmpty() && newPassword == confirmPassword) {
                                passwordChangeSuccess = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
