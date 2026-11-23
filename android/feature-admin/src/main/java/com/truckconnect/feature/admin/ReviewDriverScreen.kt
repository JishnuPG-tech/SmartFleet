package com.truckconnect.feature.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import com.truckconnect.core.design.component.TcAvatar
import com.truckconnect.core.design.component.TcButton
import com.truckconnect.core.design.component.TcButtonSize
import com.truckconnect.core.design.component.TcButtonVariant
import com.truckconnect.core.design.component.TcCard
import com.truckconnect.core.design.component.TcDetailItem
import com.truckconnect.core.design.component.TcDetailList
import com.truckconnect.core.design.component.TcPhotoBox
import com.truckconnect.core.design.component.TcScreen
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

    TcScreen(
        title = "Review driver",
        onBackClick = onBackClick,
        isScrollable = true,
        pinnedBottomActions = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(dimens.Gap10)
            ) {
                TcButton(
                    onClick = onRejectClick,
                    variant = TcButtonVariant.CritOutline,
                    size = TcButtonSize.Lg,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Reject")
                }

                TcButton(
                    onClick = onApproveClick,
                    variant = TcButtonVariant.Primary,
                    size = TcButtonSize.Lg,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Approve driver")
                }
            }
        }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(dimens.Gap10)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dimens.Gap10)
            ) {
                TcAvatar(text = "Rajan Nair", isLarge = true)
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

            // Details Card using OD-8 TcDetailList
            TcCard {
                TcDetailList(
                    items = listOf(
                        TcDetailItem("Licence no.", "KL-0720110012345"),
                        TcDetailItem("Class / expiry", "HGMV · 14 Mar 2029"),
                        TcDetailItem("Vehicle", "Owner-operator · KL-08-BX-1571"),
                        TcDetailItem("Emergency contact", "Sreeja Nair (Spouse) · +91 94950 11223")
                    )
                )
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
        }
    }
}
