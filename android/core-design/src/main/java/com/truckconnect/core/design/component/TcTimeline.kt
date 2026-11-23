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

import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.truckconnect.core.design.R

data class TcTimelineStep(
    val title: String,
    val subtitle: String? = null,
    val isPending: Boolean = false,
    val isCurrent: Boolean = false
)

/**
 * OD-2 / Section 3: Professional Milestone Timeline
 * - Done = solid accent circle 20 dp with check icon
 * - Current = accent circle 20 dp with truck glyph
 * - Pending = 20 dp hollow circle, 1.5 dp border2
 * - Rail 2 dp border, centered on nodes
 * - No glow / halo rings
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

    // Automatically determine current step if not explicitly set
    val firstPendingIndex = steps.indexOfFirst { it.isPending }
    val currentIndex = if (firstPendingIndex > 0) firstPendingIndex - 1 else if (firstPendingIndex == 0) 0 else steps.lastIndex

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 4.dp)
    ) {
        steps.forEachIndexed { index, step ->
            val isLast = index == steps.lastIndex
            val isCurrentNode = step.isCurrent || (index == currentIndex && !step.isPending)
            val isDoneNode = !step.isPending && !isCurrentNode

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
            ) {
                // Left column: Node & Rail (centered)
                Box(
                    modifier = Modifier.width(24.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    // Vertical rail (2dp border centered on nodes)
                    if (!isLast) {
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .fillMaxHeight()
                                .offset(y = 10.dp)
                                .background(colors.border)
                        )
                    }

                    // Node (20 dp)
                    when {
                        step.isPending -> {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(colors.bg)
                                    .border(1.5.dp, colors.border2, CircleShape)
                            )
                        }
                        isCurrentNode -> {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(colors.accent),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = ImageVector.vectorResource(R.drawable.ic_truck),
                                    contentDescription = null,
                                    modifier = Modifier.size(12.dp),
                                    tint = colors.accentInk
                                )
                            }
                        }
                        else -> { // Done
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(colors.accent),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = ImageVector.vectorResource(R.drawable.ic_check),
                                    contentDescription = null,
                                    modifier = Modifier.size(12.dp),
                                    tint = colors.accentInk
                                )
                            }
                        }
                    }
                }

                // Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(
                            start = 12.dp,
                            bottom = if (isLast) 0.dp else 16.dp
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
