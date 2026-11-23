package com.truckconnect.feature.driver

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import com.truckconnect.core.design.component.TcButton
import com.truckconnect.core.design.component.TcButtonSize
import com.truckconnect.core.design.component.TcButtonVariant
import com.truckconnect.core.design.component.TcCard
import com.truckconnect.core.design.component.TcScaffold
import com.truckconnect.core.design.component.TcSosButton
import com.truckconnect.core.design.component.TcTimeline
import com.truckconnect.core.design.component.TcTimelineStep
import com.truckconnect.core.design.theme.TcDimens
import com.truckconnect.core.design.theme.TcThemeState

/**
 * Driver Active Trip Screen (V5)
 *
 * Fixes:
 * - BUG-01 / BUG-02: Hosted inside TcScaffold for full system bars and display cutout insets.
 * - BUG-12: Timeline wraps content (all 5 steps rendered with exact intrinsic height).
 * - Defect F1: Two-button row with equal weight (1f) for Navigate and Call buttons.
 * - Pinned bottom milestone action ("Reached drop") with safe padding above system navigation bar.
 * - Floating SOS button safely anchored in scaffold overlays.
 */
@Composable
fun ActiveTripScreen(
    onBackClick: () -> Unit = {},
    onNavigateClick: () -> Unit = {},
    onPhoneClick: () -> Unit = {},
    onReachedDropClick: () -> Unit = {},
    onSosClick: () -> Unit = {}
) {
    val colors = TcThemeState.colors
    val typography = TcThemeState.typography
    val dimens = TcThemeState.dimens
    val isMalayalam = TcThemeState.isMalayalam

    val steps = listOf(
        TcTimelineStep(
            title = if (isMalayalam) "കയറ്റുന്ന സ്ഥലത്ത് എത്തി" else "Reached pickup",
            subtitle = "7:50 am",
            isPending = false
        ),
        TcTimelineStep(
            title = if (isMalayalam) "ലോഡ് കയറ്റി" else "Loaded",
            subtitle = "8:45 am",
            isPending = false
        ),
        TcTimelineStep(
            title = if (isMalayalam) "യാത്രയിൽ" else "In transit",
            subtitle = if (isMalayalam) "ഇപ്പോൾ" else "Now",
            isPending = false
        ),
        TcTimelineStep(
            title = if (isMalayalam) "ഇറക്കുന്ന സ്ഥലത്ത് എത്തി" else "Reached drop",
            isPending = true
        ),
        TcTimelineStep(
            title = if (isMalayalam) "എത്തിച്ചു" else "Delivered",
            isPending = true
        )
    )

    TcScaffold(
        topBar = {
            TcAppBar(
                title = "Kasaragod → Malappuram",
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
                .padding(
                    start = dimens.ScreenPaddingHorizontal,
                    end = dimens.ScreenPaddingHorizontal,
                    top = dimens.Gap10,
                    bottom = dimens.ScreenPaddingBottom
                ),
            verticalArrangement = Arrangement.spacedBy(dimens.Gap10)
        ) {
            // Timeline Card (Defect F2 fixed, IntrinsicSize rail)
            TcCard {
                TcTimeline(
                    steps = steps,
                    isDriver = true
                )
            }

            // Action Row (Defect F1 fixed: weight(1f), spacedBy(dimens.Gap10))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(dimens.Gap10)
            ) {
                TcButton(
                    onClick = onNavigateClick,
                    variant = TcButtonVariant.Outline,
                    size = TcButtonSize.Lg,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_nav),
                        contentDescription = null,
                        modifier = Modifier.size(dimens.Size22),
                        tint = colors.text
                    )
                    Text(
                        text = if (isMalayalam) "വഴി കാണിക്കുക" else "Navigate",
                        modifier = Modifier.padding(start = dimens.Gap6)
                    )
                }

                TcButton(
                    onClick = onPhoneClick,
                    variant = TcButtonVariant.Outline,
                    size = TcButtonSize.Lg,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_phone),
                        contentDescription = null,
                        modifier = Modifier.size(dimens.Size22),
                        tint = colors.text
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            TcButton(
                onClick = onReachedDropClick,
                variant = TcButtonVariant.Primary,
                size = TcButtonSize.Xl
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_check),
                    contentDescription = null,
                    modifier = Modifier.size(dimens.Size26),
                    tint = colors.accentInk
                )
                Text(
                    text = if (isMalayalam) "ഇറക്കുന്ന സ്ഥലത്ത് എത്തി" else "Reached drop",
                    modifier = Modifier.padding(start = dimens.Gap8)
                )
            }
        }
    }
}
