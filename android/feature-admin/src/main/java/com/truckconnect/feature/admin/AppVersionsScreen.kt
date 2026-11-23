package com.truckconnect.feature.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.truckconnect.core.design.component.TcAppBar
import com.truckconnect.core.design.component.TcAvatar
import com.truckconnect.core.design.component.TcButton
import com.truckconnect.core.design.component.TcButtonVariant
import com.truckconnect.core.design.component.TcCard
import com.truckconnect.core.design.component.TcChip
import com.truckconnect.core.design.component.TcChipKind
import com.truckconnect.core.design.component.TcProgressBar
import com.truckconnect.core.design.theme.TcDimens
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

    val driversVersions = listOf(
        listOf("Suresh Kumar", "v2.1.0", "Latest", TcChipKind.Ok),
        listOf("Mohanan P", "v2.0.3", "Outdated", TcChipKind.Warn),
        listOf("Abdul Rahman", "v2.0.3", "Outdated", TcChipKind.Warn),
        listOf("Gopalakrishnan", "v2.0.1", "Below minimum", TcChipKind.Crit)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bg)
            .padding(bottom = dimens.ScreenPaddingBottom),
        verticalArrangement = Arrangement.spacedBy(dimens.Gap10)
    ) {
        TcAppBar(title = "App versions", onBackClick = onBackClick)

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = dimens.ScreenPaddingHorizontal),
            verticalArrangement = Arrangement.spacedBy(dimens.Gap10)
        ) {
            // Rollout Header Card
            TcCard {
                Column(verticalArrangement = Arrangement.spacedBy(dimens.Gap8)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "v2.1.0 · latest",
                            style = typography.btnSm,
                            color = colors.text
                        )
                        TcChip(text = "Rollout 100%", kind = TcChipKind.Ok)
                    }

                    TcProgressBar(progress = 0.75f)

                    Text(
                        text = "9 of 12 drivers updated · minimum supported v2.0.3",
                        style = typography.xsmall,
                        color = colors.muted
                    )
                }
            }

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

            Spacer(modifier = Modifier.weight(1f))

            // Action Row (Defect F1 fixed: weight(1f), spacedBy(dimens.Gap10))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(dimens.Gap10)
            ) {
                TcButton(
                    onClick = onHaltRolloutClick,
                    variant = TcButtonVariant.Outline,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Halt rollout")
                }

                TcButton(
                    onClick = onForceUpdateClick,
                    variant = TcButtonVariant.Primary,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Force update")
                }
            }
        }
    }
}
