package com.truckconnect.core.design.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.truckconnect.core.design.theme.TcThemeState

data class TcDetailItem(
    val label: String,
    val value: String,
    val isDestructive: Boolean = false,
    val isStacked: Boolean = false,
    val trailingContent: (@Composable () -> Unit)? = null,
    val onClick: (() -> Unit)? = null
)

/**
 * OD-8 / Section 3.1: TcDetailList
 * Responsive two-column or stacked layout with strict weight constraints
 * to eliminate all text collisions and overlaps.
 */
@Composable
fun TcDetailList(
    items: List<TcDetailItem>,
    modifier: Modifier = Modifier
) {
    val colors = TcThemeState.colors
    val typography = TcThemeState.typography

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colors.surface)
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        items.forEachIndexed { index, item ->
            val isLast = index == items.lastIndex

            val rowModifier = if (item.onClick != null) {
                Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(color = colors.accent),
                        onClick = item.onClick
                    )
            } else {
                Modifier.fillMaxWidth()
            }

            if (item.isStacked || (item.label.length > 20 && item.value.length > 25)) {
                Column(
                    modifier = rowModifier.padding(vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = item.label,
                        style = typography.caption,
                        color = colors.muted
                    )
                    if (item.trailingContent != null) {
                        item.trailingContent()
                    } else {
                        Text(
                            text = item.value,
                            style = typography.btnSm,
                            color = if (item.isDestructive) colors.crit else colors.text
                        )
                    }
                }
            } else {
                Row(
                    modifier = rowModifier.padding(vertical = 12.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = item.label,
                        style = typography.caption,
                        color = colors.muted,
                        modifier = Modifier.weight(0.38f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (item.trailingContent != null) {
                        Box(
                            modifier = Modifier.weight(0.62f),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            item.trailingContent()
                        }
                    } else {
                        Text(
                            text = item.value,
                            style = typography.btnSm,
                            color = if (item.isDestructive) colors.crit else colors.text,
                            modifier = Modifier.weight(0.62f),
                            textAlign = TextAlign.End,
                            softWrap = true
                        )
                    }
                }
            }

            // 1 dp divider between rows
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(colors.border)
                )
            }
        }
    }
}
