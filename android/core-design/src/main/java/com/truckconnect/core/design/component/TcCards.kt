package com.truckconnect.core.design.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.truckconnect.core.design.R
import com.truckconnect.core.design.theme.TcDimens
import com.truckconnect.core.design.theme.TcThemeState

@Composable
fun TcCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val colors = TcThemeState.colors
    val shapes = TcThemeState.shapes
    val dimens = TcThemeState.dimens

    val clickableModifier = if (onClick != null) {
        Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = ripple(color = colors.accent),
            onClick = onClick
        )
    } else Modifier

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shapes.Card)
            .background(colors.surface)
            .border(BorderStroke(dimens.CardBorder, colors.border), shapes.Card)
            .then(clickableModifier)
            .padding(dimens.CardPadding),
        content = content
    )
}

@Composable
fun TcHeroCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val colors = TcThemeState.colors
    val shapes = TcThemeState.shapes
    val dimens = TcThemeState.dimens

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shapes.Hero)
            .background(colors.accent)
            .padding(dimens.CardPadding + dimens.CardBorder * 2),
        content = content
    )
}

@Composable
fun TcKpiTile(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    sub: String? = null,
    subColor: Color? = null,
    onClick: (() -> Unit)? = null
) {
    val colors = TcThemeState.colors
    val shapes = TcThemeState.shapes
    val typography = TcThemeState.typography
    val dimens = TcThemeState.dimens

    val clickableModifier = if (onClick != null) {
        Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = ripple(color = colors.accent),
            onClick = onClick
        )
    } else Modifier

    Column(
        modifier = modifier
            .clip(shapes.KpiCard)
            .background(colors.surface)
            .border(BorderStroke(dimens.CardBorder, colors.border), shapes.KpiCard)
            .then(clickableModifier)
            .padding(dimens.KpiCardPadding)
    ) {
        Text(
            text = label,
            style = typography.caption,
            color = colors.muted
        )
        Text(
            text = value,
            style = typography.kpiValue,
            color = colors.text,
            modifier = Modifier.padding(top = dimens.CardBorder * 4)
        )
        if (sub != null) {
            Text(
                text = sub,
                style = typography.xsmall,
                color = subColor ?: colors.muted,
                modifier = Modifier.padding(top = dimens.CardBorder * 2)
            )
        }
    }
}

/**
 * 4dp progress bar matching .bar0 in mockup specification (BUG-09).
 */
@Composable
fun TcProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    fillColor: Color = TcThemeState.colors.accent,
    trackColor: Color = TcThemeState.colors.border
) {
    val dimens = TcThemeState.dimens
    val shapes = TcThemeState.shapes

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(dimens.CardBorder * 4)
            .clip(shapes.Chip)
            .background(trackColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(fraction = progress.coerceIn(0f, 1f))
                .height(dimens.CardBorder * 4)
                .clip(shapes.Chip)
                .background(fillColor)
        )
    }
}

@Composable
fun TcPhotoBox(
    label: String,
    modifier: Modifier = Modifier,
    subLabel: String? = null,
    isFilled: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val colors = TcThemeState.colors
    val shapes = TcThemeState.shapes
    val typography = TcThemeState.typography
    val dimens = TcThemeState.dimens

    val clickableModifier = if (onClick != null) {
        Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = ripple(color = colors.accent),
            onClick = onClick
        )
    } else Modifier

    val boxModifier = if (isFilled) {
        modifier
            .fillMaxWidth()
            .height(dimens.PhotoBoxHeight)
            .clip(shapes.PhotoBox)
            .background(
                Brush.linearGradient(
                    listOf(colors.accentSoft, colors.surface2)
                )
            )
            .then(clickableModifier)
    } else {
        modifier
            .fillMaxWidth()
            .height(dimens.PhotoBoxHeight)
            .clip(shapes.PhotoBox)
            .background(colors.surface)
            .border(BorderStroke(dimens.PhotoBoxBorderWidth, colors.border2), shapes.PhotoBox)
            .then(clickableModifier)
    }

    Column(
        modifier = boxModifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(if (isFilled) R.drawable.ic_check else R.drawable.ic_camera),
            contentDescription = null,
            modifier = Modifier.size(dimens.IconSizeDefault),
            tint = if (isFilled) colors.accent else colors.muted
        )
        Text(
            text = label,
            style = typography.caption,
            color = if (isFilled) colors.accent else colors.muted,
            modifier = Modifier.padding(top = dimens.CardBorder * 4)
        )
        if (subLabel != null) {
            Text(
                text = subLabel,
                style = typography.xsmall,
                color = colors.muted
            )
        }
    }
}

@Composable
fun TcListRow(
    modifier: Modifier = Modifier,
    isLast: Boolean = false,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val colors = TcThemeState.colors
    val dimens = TcThemeState.dimens
    val isDriver = TcThemeState.isDriver

    val verticalPadding = if (isDriver) dimens.ListRowPaddingVerticalDriver else dimens.ListRowPaddingVerticalAdmin

    val clickableModifier = if (onClick != null) {
        Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = ripple(color = colors.accent),
            onClick = onClick
        )
    } else Modifier

    Box(
        modifier = modifier
            .fillMaxWidth()
            .then(clickableModifier)
            .padding(vertical = verticalPadding)
    ) {
        content()
    }
}
