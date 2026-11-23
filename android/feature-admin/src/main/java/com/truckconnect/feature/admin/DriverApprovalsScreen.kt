package com.truckconnect.feature.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.truckconnect.core.design.component.TcAvatar
import com.truckconnect.core.design.component.TcButton
import com.truckconnect.core.design.component.TcButtonSize
import com.truckconnect.core.design.component.TcButtonVariant
import com.truckconnect.core.design.component.TcCard
import com.truckconnect.core.design.component.TcChip
import com.truckconnect.core.design.component.TcChipKind
import com.truckconnect.core.design.component.TcScreen
import com.truckconnect.core.design.theme.TcThemeState

@Composable
fun DriverApprovalsScreen(
    onBackClick: () -> Unit = {},
    onReviewClick: (String) -> Unit = {}
) {
    val colors = TcThemeState.colors
    val typography = TcThemeState.typography
    val dimens = TcThemeState.dimens

    val pendingRegistrations = listOf(
        Triple("Rajan Nair", "Owner-operator · HGMV", "9:12 pm"),
        Triple("Anoop Das", "Company fleet · LMV", "7:40 pm")
    )

    TcScreen(
        title = "Driver approvals",
        onBackClick = onBackClick,
        topBarActions = {
            TcChip(text = "2 new", kind = TcChipKind.Warn)
        },
        isScrollable = true
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(dimens.Gap10)
        ) {
            Text(
                text = "New self-registrations waiting for verification",
                style = typography.small,
                color = colors.muted
            )

            pendingRegistrations.forEach { (name, details, time) ->
                TcCard {
                    Column(verticalArrangement = Arrangement.spacedBy(dimens.Gap10)) {
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
                                    text = details,
                                    style = typography.xsmall,
                                    color = colors.muted
                                )
                            }
                            Text(
                                text = time,
                                style = typography.xsmall,
                                color = colors.muted
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TcChip(text = "PENDING_VERIFICATION", kind = TcChipKind.Warn)
                            Spacer(modifier = Modifier.weight(1f))
                            TcButton(
                                onClick = { onReviewClick(name) },
                                variant = TcButtonVariant.Primary,
                                size = TcButtonSize.Sm
                            ) {
                                Text(text = "Review")
                            }
                        }
                    }
                }
            }
        }
    }
}
