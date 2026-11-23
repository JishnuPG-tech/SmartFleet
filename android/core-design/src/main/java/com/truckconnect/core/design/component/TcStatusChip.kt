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
import com.truckconnect.core.design.theme.TcDimens
import com.truckconnect.core.design.theme.TcShapes
import com.truckconnect.core.design.theme.TcThemeState

enum class TcChipKind {
    Ok, Warn, Crit, Info, Accent, Neutral
}

@Composable
fun TcChip(
    text: String,
    kind: TcChipKind,
    modifier: Modifier = Modifier
) {
    val colors = TcThemeState.colors
    val shapes = TcThemeState.shapes
    val typography = TcThemeState.typography
    val dimens = TcThemeState.dimens
    val isDriver = TcThemeState.isDriver

    val (chipColor, bgColor) = when (kind) {
        TcChipKind.Ok -> colors.ok to colors.okChipBg
        TcChipKind.Warn -> colors.warn to colors.warnChipBg
        TcChipKind.Crit -> colors.crit to colors.critChipBg
        TcChipKind.Info -> colors.info to colors.infoChipBg
        TcChipKind.Accent -> colors.accent to colors.accentSoft
        TcChipKind.Neutral -> colors.neutral to colors.neutralChipBg
    }

    val padH = if (isDriver) dimens.ChipPaddingHorizontalDriver else dimens.ChipPaddingHorizontalAdmin
    val padV = if (isDriver) dimens.ChipPaddingVerticalDriver else dimens.ChipPaddingVerticalAdmin
    val textStyle = typography.chip

    Row(
        modifier = modifier
            .clip(shapes.Chip)
            .background(bgColor)
            .padding(
                start = padH - dimens.CardBorder * 2,
                end = padH,
                top = padV,
                bottom = padV
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(dimens.ChipDotSize)
                .clip(CircleShape)
                .background(chipColor)
        )
        Text(
            text = text,
            style = textStyle,
            color = chipColor,
            modifier = Modifier.padding(start = dimens.ChipGap)
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
