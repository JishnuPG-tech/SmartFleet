package com.truckconnect.core.design.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import com.truckconnect.core.design.R
import com.truckconnect.core.design.theme.TcDimens
import com.truckconnect.core.design.theme.TcShapes
import com.truckconnect.core.design.theme.TcThemeState

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

enum class TcChipKind {
    Ok, Warn, Crit, Info, Accent, Neutral
}

/**
 * OD-1 / Section 4: Rebuilt TcStatus (Chip)
 * - 16 dp line icon + label in status colour
 * - 12 % tint background (never generic pill with colored dot)
 * - Height 28 dp, radius 10 dp
 */
@Composable
fun TcChip(
    text: String,
    kind: TcChipKind,
    modifier: Modifier = Modifier,
    iconOverride: Int? = null
) {
    TcStatus(
        text = text,
        kind = kind,
        modifier = modifier,
        iconOverride = iconOverride
    )
}

@Composable
fun TcStatus(
    text: String,
    kind: TcChipKind,
    modifier: Modifier = Modifier,
    iconOverride: Int? = null
) {
    val colors = TcThemeState.colors
    val typography = TcThemeState.typography

    val chipColor = when (kind) {
        TcChipKind.Ok -> colors.ok
        TcChipKind.Warn -> colors.warn
        TcChipKind.Crit -> colors.crit
        TcChipKind.Info -> colors.info
        TcChipKind.Accent -> colors.accent
        TcChipKind.Neutral -> colors.neutral
    }

    // 12% tint background per Antigravity v3 spec (OD-1)
    val bgColor = chipColor.copy(alpha = 0.12f)

    val lower = text.lowercase()
    val iconRes = iconOverride ?: when {
        lower.contains("created") -> R.drawable.ic_doc
        lower.contains("cancel") -> R.drawable.ic_x
        lower.contains("offline") -> R.drawable.ic_battery
        lower.contains("assign") -> R.drawable.ic_user
        lower.contains("avail") -> R.drawable.ic_check
        lower.contains("transit") || lower.contains("load") || lower.contains("trip") || lower.contains("reached") -> R.drawable.ic_truck
        lower.contains("deliver") || lower.contains("approv") || lower.contains("latest") || lower.contains("online") -> R.drawable.ic_check
        lower.contains("delay") || lower.contains("stale") || lower.contains("expir") -> R.drawable.ic_clock
        lower.contains("outdate") -> R.drawable.ic_warn
        lower.contains("reject") || lower.contains("fail") || lower.contains("below") -> R.drawable.ic_x
        lower.contains("sos") -> R.drawable.ic_sos
        kind == TcChipKind.Ok -> R.drawable.ic_check
        kind == TcChipKind.Warn -> R.drawable.ic_warn
        kind == TcChipKind.Crit -> R.drawable.ic_x
        kind == TcChipKind.Info -> R.drawable.ic_user
        kind == TcChipKind.Accent -> R.drawable.ic_truck
        else -> R.drawable.ic_check
    }

    Row(
        modifier = modifier
            .height(28.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(iconRes),
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = chipColor
        )
        Text(
            text = text,
            style = typography.chip,
            color = chipColor,
            modifier = Modifier.padding(start = 5.dp)
        )
    }
}

@Composable
fun TcAvatar(
    text: String,
    modifier: Modifier = Modifier,
    isLarge: Boolean = false
) {
    val colors = TcThemeState.colors
    val typography = TcThemeState.typography
    val dimens = TcThemeState.dimens

    val size = if (isLarge) dimens.AvatarLargeSize else dimens.AvatarSize
    val textStyle = if (isLarge) typography.avatarLarge else typography.avatar
    val displayInitials = initials(text)

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(colors.accentSoft),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = displayInitials,
            style = textStyle,
            color = colors.accent,
            maxLines = 1,
            softWrap = false,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun TcIconBox(
    iconRes: Int,
    modifier: Modifier = Modifier,
    kind: TcChipKind = TcChipKind.Accent,
    contentDescription: String? = null
) {
    val colors = TcThemeState.colors
    val shapes = TcThemeState.shapes
    val dimens = TcThemeState.dimens

    val (iconColor, bgColor) = when (kind) {
        TcChipKind.Ok -> colors.ok to colors.okIconBg
        TcChipKind.Warn -> colors.warn to colors.warnIconBg
        TcChipKind.Crit -> colors.crit to colors.critIconBg
        TcChipKind.Info -> colors.info to colors.infoIconBg
        TcChipKind.Accent -> colors.accent to colors.accentSoft
        TcChipKind.Neutral -> colors.neutral to colors.neutralChipBg
    }

    Box(
        modifier = modifier
            .size(dimens.IconBoxSize)
            .clip(shapes.IconBox)
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(iconRes),
            contentDescription = contentDescription,
            modifier = Modifier.size(dimens.IconSizeDefault),
            tint = iconColor
        )
    }
}
