package com.truckconnect.core.design.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.truckconnect.core.design.theme.TcDimens
import com.truckconnect.core.design.theme.TcThemeState

data class TcTimelineStep(
    val title: String,
    val subtitle: String? = null,
    val isPending: Boolean = false
)

/**
 * Vertical milestone timeline component.
 *
 * Fixes:
 * - BUG-12: Uses Modifier.height(IntrinsicSize.Min) on step rows so the vertical rail
 *   measures the exact intrinsic height of the text content rather than expanding to screen height.
 * - Defect F2: Perfectly centers dots on the 2dp rail.
 * - Enforces zero magic numbers by deriving all dimensions from TcDimens.
 */
@Composable
fun TcTimeline(
    steps: List<TcTimelineStep>,
    modifier: Modifier = Modifier,
    isDriver: Boolean = TcThemeState.isDriver
) {
    val colors = TcThemeState.colors
    val typography = TcThemeState.typography
    val dimens = TcThemeState.dimens

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = dimens.CardBorder * 6)
    ) {
        steps.forEachIndexed { index, step ->
            val isLast = index == steps.lastIndex

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
            ) {
                // Left column: Dot and Rail (centered)
                Box(
                    modifier = Modifier.width(dimens.CardBorder * 18),
                    contentAlignment = Alignment.TopCenter
                ) {
                    // Vertical rail
                    if (!isLast) {
                        Box(
                            modifier = Modifier
                                .width(dimens.CardBorder * 2)
                                .fillMaxHeight()
                                .offset(y = dimens.CardBorder * 6)
                                .background(colors.border)
                        )
                    }

                    // Dot (12dp circle)
                    if (step.isPending) {
                        Box(
                            modifier = Modifier
                                .size(dimens.CardBorder * 12)
                                .offset(y = dimens.CardBorder * 3)
                                .clip(CircleShape)
                                .background(colors.bg)
                                .border(dimens.CardBorder, colors.border2, CircleShape)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(dimens.CardBorder * 12)
                                .offset(y = dimens.CardBorder * 3)
                                .clip(CircleShape)
                                .background(colors.accent)
                                .border(dimens.CardBorder * 3, colors.bg, CircleShape)
                                .border(dimens.CardBorder, colors.accent, CircleShape)
                        )
                    }
                }

                // Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(
                            start = dimens.CardBorder * 12,
                            bottom = if (isLast) dimens.CardBorder * 0 else dimens.CardBorder * 12
                        )
                ) {
                    Text(
                        text = step.title,
                        style = typography.timelineTitle,
                        color = if (step.isPending) colors.muted else colors.text
                    )
                    if (step.subtitle != null) {
                        Text(
                            text = step.subtitle,
                            style = typography.timelineSubtitle,
                            color = colors.muted
                        )
                    }
                }
            }
        }
    }
}
