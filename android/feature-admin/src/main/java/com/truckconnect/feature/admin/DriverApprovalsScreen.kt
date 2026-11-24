package com.truckconnect.feature.admin

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.truckconnect.core.design.component.TcAvatar
import com.truckconnect.core.design.component.TcButton
import com.truckconnect.core.design.component.TcButtonSize
import com.truckconnect.core.design.component.TcButtonVariant
import com.truckconnect.core.design.component.TcCard
import com.truckconnect.core.design.component.TcChip
import com.truckconnect.core.design.component.TcChipKind
import com.truckconnect.core.design.component.TcScreen
import com.truckconnect.core.design.theme.TcThemeState

object DriverApprovalsStore {
    val pendingList = mutableStateListOf(
        Triple("Rajan Nair", "Owner-operator · HGMV", "9:12 pm"),
        Triple("Anoop Das", "Company fleet · LMV", "7:40 pm")
    )

    fun approve(name: String) {
        pendingList.removeAll { it.first == name }
    }

    fun reject(name: String) {
        pendingList.removeAll { it.first == name }
    }
}

@Composable
fun DriverApprovalsScreen(
    onBackClick: () -> Unit = {},
    onReviewClick: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val colors = TcThemeState.colors
    val typography = TcThemeState.typography
    val dimens = TcThemeState.dimens

    val pendingRegistrations = DriverApprovalsStore.pendingList

    TcScreen(
        title = "Driver approvals",
        onBackClick = onBackClick,
        topBarActions = {
            if (pendingRegistrations.isNotEmpty()) {
                TcChip(text = "${pendingRegistrations.size} new", kind = TcChipKind.Warn)
            } else {
                TcChip(text = "0 pending", kind = TcChipKind.Ok)
            }
        },
        isScrollable = true
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(dimens.Gap10)
        ) {
            Text(
                text = if (pendingRegistrations.isNotEmpty())
                    "New self-registrations waiting for verification"
                else
                    "All pending applications have been processed.",
                style = typography.small,
                color = colors.muted
            )

            if (pendingRegistrations.isEmpty()) {
                TcCard {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "No Pending Approvals",
                            style = typography.btnLg,
                            color = colors.text
                        )
                        Text(
                            text = "All driver registration requests have been approved or reviewed.",
                            style = typography.small,
                            color = colors.muted
                        )
                    }
                }
            } else {
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
                                Text(
                                    text = "Submitted $time",
                                    style = typography.xsmall,
                                    color = colors.muted
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                TcButton(
                                    onClick = {
                                        DriverApprovalsStore.approve(name)
                                        Toast.makeText(context, "$name approved! Added to active fleet.", Toast.LENGTH_SHORT).show()
                                    },
                                    variant = TcButtonVariant.Outline,
                                    size = TcButtonSize.Sm,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(text = "Approve")
                                }

                                TcButton(
                                    onClick = { onReviewClick(name) },
                                    variant = TcButtonVariant.Primary,
                                    size = TcButtonSize.Sm,
                                    modifier = Modifier.weight(1f)
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
}
