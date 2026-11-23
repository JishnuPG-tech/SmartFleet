package com.truckconnect.feature.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.truckconnect.core.design.component.TcAvatar
import com.truckconnect.core.design.component.TcButton
import com.truckconnect.core.design.component.TcButtonVariant
import com.truckconnect.core.design.component.TcCard
import com.truckconnect.core.design.component.TcChip
import com.truckconnect.core.design.component.TcChipKind
import com.truckconnect.core.design.component.TcConfirmDialog
import com.truckconnect.core.design.component.TcProgressBar
import com.truckconnect.core.design.component.TcScreen
import com.truckconnect.core.design.theme.TcThemeState

@Composable
fun AppVersionsScreen(
    onBackClick: () -> Unit = {},
    onHaltRolloutClick: () -> Unit = {},
    onForceUpdateClick: () -> Unit = {}
) {
    val colors = TcThemeState.colors
    val typography = TcThemeState.typography
    val dimens = TcThemeState.dimens

    var showForceDialog by remember { mutableStateOf(false) }
    var showHaltDialog by remember { mutableStateOf(false) }

    val driversVersions = listOf(
        listOf("Suresh Kumar", "v2.1.0", "Latest", TcChipKind.Ok),
        listOf("Mohanan P", "v2.0.3", "Outdated", TcChipKind.Warn),
        listOf("Abdul Rahman", "v2.0.3", "Outdated", TcChipKind.Warn),
        listOf("Gopalakrishnan", "v2.0.1", "Below minimum", TcChipKind.Crit)
    )

    TcScreen(
        title = "App versions",
        onBackClick = onBackClick,
        isScrollable = true,
        pinnedBottomActions = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(dimens.Gap10)
            ) {
                TcButton(
                    onClick = { showHaltDialog = true },
                    variant = TcButtonVariant.Outline,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Halt rollout")
                }

                TcButton(
                    onClick = { showForceDialog = true },
                    variant = TcButtonVariant.Crit,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Force update")
                }
            }
        }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(dimens.Gap10)
        ) {
            // Rollout Header Card with clear labels
            TcCard {
                Column(verticalArrangement = Arrangement.spacedBy(dimens.Gap8)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "v2.1.0 · Current release",
                                style = typography.btnSm,
                                color = colors.text
                            )
                            Text(
                                text = "Target: All active drivers",
                                style = typography.xsmall,
                                color = colors.muted
                            )
                        }
                        TcChip(text = "Active rollout", kind = TcChipKind.Ok)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Fleet adoption",
                            style = typography.caption,
                            color = colors.text2
                        )
                        Text(
                            text = "75% (9 of 12 drivers)",
                            style = typography.caption,
                            color = colors.text
                        )
                    }

                    TcProgressBar(progress = 0.75f)

                    Text(
                        text = "Minimum supported version: v2.0.3 · 1 driver below minimum",
                        style = typography.xsmall,
                        color = colors.muted
                    )
                }
            }

            Text(
                text = "Driver breakdown",
                style = typography.h2,
                color = colors.text,
                modifier = Modifier.padding(top = 6.dp)
            )

            // Driver versions list
            driversVersions.forEach { item ->
                val name = item[0] as String
                val version = item[1] as String
                val status = item[2] as String
                val kind = item[3] as TcChipKind

                TcCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(dimens.Gap10)
                    ) {
                        TcAvatar(text = name)
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = name,
                                style = typography.btnSm,
                                color = colors.text
                            )
                            Text(
                                text = version,
                                style = typography.xsmall,
                                color = colors.muted
                            )
                        }
                        TcChip(text = status, kind = kind)
                    }
                }
            }
        }
    }

    if (showForceDialog) {
        TcConfirmDialog(
            title = "Force update to v2.1.0?",
            message = "This will require all 12 drivers to update before they can start their next trip. Drivers currently on an active trip can complete delivery first.",
            confirmLabel = "Force update",
            isDestructive = true,
            onConfirm = {
                showForceDialog = false
                onForceUpdateClick()
            },
            onDismiss = { showForceDialog = false }
        )
    }

    if (showHaltDialog) {
        TcConfirmDialog(
            title = "Halt v2.1.0 rollout?",
            message = "New drivers will stop receiving prompts to update. Drivers already on v2.1.0 will continue using it.",
            confirmLabel = "Halt rollout",
            isDestructive = true,
            onConfirm = {
                showHaltDialog = false
                onHaltRolloutClick()
            },
            onDismiss = { showHaltDialog = false }
        )
    }
}
