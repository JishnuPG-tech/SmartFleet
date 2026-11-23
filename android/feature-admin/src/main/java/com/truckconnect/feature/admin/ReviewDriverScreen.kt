package com.truckconnect.feature.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.truckconnect.core.design.R
import com.truckconnect.core.design.component.TcAppBar
import com.truckconnect.core.design.component.TcAvatar
import com.truckconnect.core.design.component.TcButton
import com.truckconnect.core.design.component.TcButtonSize
import com.truckconnect.core.design.component.TcButtonVariant
import com.truckconnect.core.design.component.TcCard
import com.truckconnect.core.design.component.TcListRow
import com.truckconnect.core.design.component.TcPhotoBox
import com.truckconnect.core.design.theme.TcDimens
import com.truckconnect.core.design.theme.TcThemeState

@Composable
fun ReviewDriverScreen(
    onBackClick: () -> Unit = {},
    onRejectClick: () -> Unit = {},
    onApproveClick: () -> Unit = {}
) {
    val colors = TcThemeState.colors
    val typography = TcThemeState.typography
    val dimens = TcThemeState.dimens

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bg)
            .padding(bottom = dimens.ScreenPaddingBottom),
        verticalArrangement = Arrangement.spacedBy(dimens.Gap10)
    ) {
        TcAppBar(title = "Review driver", onBackClick = onBackClick)

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = dimens.ScreenPaddingHorizontal),
            verticalArrangement = Arrangement.spacedBy(dimens.Gap10)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dimens.Gap10)
            ) {
                TcAvatar(text = "Rajan Nair")
                Column {
                    Text(
                        text = "Rajan Nair",
                        style = typography.btnLg,
                        color = colors.text
                    )
                    Text(
                        text = "+91 98470 55501 · Submitted 9:12 pm",
                        style = typography.xsmall,
                        color = colors.muted
                    )
                }
            }

            // Details Card
            TcCard {
                Column {
                    TcListRow {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Licence no.", style = typography.small, color = colors.muted)
                            Text(text = "KL-0720110012345", style = typography.btnSm, color = colors.text)
                        }
                    }
                    TcListRow {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Type / expiry", style = typography.small, color = colors.muted)
                            Text(text = "HGMV · 14 Mar 2029", style = typography.btnSm, color = colors.text)
                        }
                    }
                    TcListRow {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Vehicle", style = typography.small, color = colors.muted)
                            Text(text = "Owner-operator · KL-08-BX-1571", style = typography.btnSm, color = colors.text)
                        }
                    }
                    TcListRow(isLast = true) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Emergency", style = typography.small, color = colors.muted)
                            Text(text = "Sreeja Nair (Spouse) · +91 94950 11223", style = typography.btnSm, color = colors.text)
                        }
                    }
                }
            }

            // Photos Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(dimens.Gap10)
            ) {
                TcPhotoBox(
                    label = "Front · verified",
                    isFilled = true,
                    modifier = Modifier.weight(1f)
                )
                TcPhotoBox(
                    label = "Back · verified",
                    isFilled = true,
                    modifier = Modifier.weight(1f)
                )
            }

            // DPDP Consent row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dimens.Gap8)
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_check),
                    contentDescription = null,
                    modifier = Modifier.size(dimens.Size16),
                    tint = colors.ok
                )
                Text(
                    text = "Consented to location & document processing under DPDP Act 2023 on 22 Nov 9:12 pm",
                    style = typography.xsmall,
                    color = colors.muted
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Action Row (Defect F1 fixed: weight(1f), spacedBy(dimens.Gap10))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(dimens.Gap10)
            ) {
                TcButton(
                    onClick = onRejectClick,
                    variant = TcButtonVariant.No,
                    size = TcButtonSize.Lg,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Reject")
                }

                TcButton(
                    onClick = onApproveClick,
                    variant = TcButtonVariant.Ok,
                    size = TcButtonSize.Lg,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Approve driver")
                }
            }
        }
    }
}
