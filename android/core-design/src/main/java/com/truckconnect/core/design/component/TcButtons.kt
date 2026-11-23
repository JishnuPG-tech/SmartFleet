package com.truckconnect.core.design.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.truckconnect.core.design.theme.TcDimens
import com.truckconnect.core.design.theme.TcShapes
import com.truckconnect.core.design.theme.TcThemeState

enum class TcButtonVariant {
    Primary, Outline, Ok, No, Text
}

enum class TcButtonSize {
    Md, Lg, Xl, Sm, Text
}

@Composable
fun TcButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: TcButtonVariant = TcButtonVariant.Primary,
    size: TcButtonSize = TcButtonSize.Md,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    val colors = TcThemeState.colors
    val typography = TcThemeState.typography

    val height = when (size) {
        TcButtonSize.Md -> TcDimens.ButtonMdHeight
        TcButtonSize.Lg -> TcDimens.ButtonLgHeight
        TcButtonSize.Xl -> TcDimens.ButtonXlHeight
        TcButtonSize.Sm -> TcDimens.ButtonSmHeight
        TcButtonSize.Text -> TcDimens.ButtonTextHeight
    }

    val shape = when (size) {
        TcButtonSize.Md -> TcShapes.ButtonMd
        TcButtonSize.Lg -> TcShapes.ButtonLg
        TcButtonSize.Xl -> TcShapes.ButtonXl
        TcButtonSize.Sm -> TcShapes.ButtonSm
        TcButtonSize.Text -> TcShapes.ButtonMd
    }

    val textStyle = when (size) {
        TcButtonSize.Md -> typography.btn
        TcButtonSize.Lg -> typography.btnLg
        TcButtonSize.Xl -> typography.btnXl
        TcButtonSize.Sm -> typography.btnSm
        TcButtonSize.Text -> typography.btn
    }

    val containerColor = when (variant) {
        TcButtonVariant.Primary -> colors.accent
        TcButtonVariant.Outline -> colors.surface
        TcButtonVariant.Ok -> colors.ok
        TcButtonVariant.No -> colors.crit
        TcButtonVariant.Text -> Color.Transparent
    }

    val contentColor = when (variant) {
        TcButtonVariant.Primary -> colors.accentInk
        TcButtonVariant.Outline -> colors.text
        TcButtonVariant.Ok -> Color.White
        TcButtonVariant.No -> Color.White
        TcButtonVariant.Text -> colors.accent
    }

    val border = if (variant == TcButtonVariant.Outline) {
        BorderStroke(1.dp, colors.border2)
    } else null

    val padding = if (size == TcButtonSize.Sm) {
        PaddingValues(horizontal = TcDimens.ButtonSmPaddingHorizontal, vertical = 0.dp)
    } else {
        PaddingValues(horizontal = 16.dp, vertical = 0.dp)
    }

    if (variant == TcButtonVariant.Text) {
        TextButton(
            onClick = onClick,
            modifier = modifier.height(height),
            enabled = enabled,
            shape = shape,
            colors = ButtonDefaults.textButtonColors(
                contentColor = contentColor,
                disabledContentColor = colors.muted
            )
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(TcDimens.ButtonContentGap, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
                content = content
            )
        }
    } else {
        Button(
            onClick = onClick,
            modifier = modifier.height(height),
            enabled = enabled,
            shape = shape,
            border = border,
            contentPadding = padding,
            colors = ButtonDefaults.buttonColors(
                containerColor = containerColor,
                contentColor = contentColor,
                disabledContainerColor = colors.surface2,
                disabledContentColor = colors.muted
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp, pressedElevation = 0.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(TcDimens.ButtonContentGap, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
                content = content
            )
        }
    }
}

@Composable
fun TcButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: TcButtonVariant = TcButtonVariant.Primary,
    size: TcButtonSize = TcButtonSize.Md,
    enabled: Boolean = true
) {
    TcButton(
        onClick = onClick,
        modifier = modifier,
        variant = variant,
        size = size,
        enabled = enabled
    ) {
        val typography = TcThemeState.typography
        val textStyle = when (size) {
            TcButtonSize.Md -> typography.btn
            TcButtonSize.Lg -> typography.btnLg
            TcButtonSize.Xl -> typography.btnXl
            TcButtonSize.Sm -> typography.btnSm
            TcButtonSize.Text -> typography.btn
        }
        Text(text = text, style = textStyle)
    }
}
