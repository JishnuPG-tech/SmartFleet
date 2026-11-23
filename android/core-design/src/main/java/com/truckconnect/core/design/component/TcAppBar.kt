package com.truckconnect.core.design.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.truckconnect.core.design.R
import com.truckconnect.core.design.theme.TcDimens
import com.truckconnect.core.design.theme.TcShapes
import com.truckconnect.core.design.theme.TcThemeState

@Composable
fun TcIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    hasPip: Boolean = false,
    content: @Composable () -> Unit
) {
    val colors = TcThemeState.colors
    val shapes = TcThemeState.shapes
    val dimens = TcThemeState.dimens

    Box(
        modifier = modifier
            .size(dimens.IconButtonSize)
            .clip(shapes.IconButton)
            .background(colors.surface)
            .border(BorderStroke(dimens.CardBorder, colors.border), shapes.IconButton)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = colors.accent),
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        content()

        if (hasPip) {
            Box(
                modifier = Modifier
                    .size(dimens.NotificationPipSize)
                    .align(Alignment.TopEnd)
                    .offset(x = -dimens.CardBorder * 9, y = dimens.CardBorder * 8)
                    .clip(CircleShape)
                    .background(colors.crit)
                    .border(dimens.NotificationPipRing, colors.surface, CircleShape)
            )
        }
    }
}

@Composable
fun TcAppBar(
    title: String,
    modifier: Modifier = Modifier,
    onBackClick: (() -> Unit)? = null,
    navigationIcon: (@Composable () -> Unit)? = if (onBackClick != null) {
        {
            TcIconButton(onClick = onBackClick) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_back),
                    contentDescription = "Back",
                    modifier = Modifier.size(TcDimens.IconSizeDefault),
                    tint = TcThemeState.colors.text2
                )
            }
        }
    } else null,
    actions: (@Composable RowScope.() -> Unit)? = null
) {
    val colors = TcThemeState.colors
    val typography = TcThemeState.typography
    val dimens = TcThemeState.dimens

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = dimens.AppBarPaddingHorizontal,
                end = dimens.AppBarPaddingHorizontal,
                top = dimens.AppBarPaddingTop,
                bottom = dimens.AppBarPaddingBottom
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimens.AppBarGap)
    ) {
        if (navigationIcon != null) {
            navigationIcon()
        }

        Text(
            text = title,
            style = typography.appBarTitle,
            color = colors.text,
            modifier = Modifier.weight(1f)
        )

        if (actions != null) {
            actions()
        }
    }
}
