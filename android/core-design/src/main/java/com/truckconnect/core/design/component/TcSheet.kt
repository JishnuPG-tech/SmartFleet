package com.truckconnect.core.design.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.truckconnect.core.design.theme.TcDimens
import com.truckconnect.core.design.theme.TcShapes
import com.truckconnect.core.design.theme.TcThemeState

@Composable
fun TcScrim(
    modifier: Modifier = Modifier,
    onDismiss: (() -> Unit)? = null
) {
    val colors = TcThemeState.colors
    val clickableModifier = if (onDismiss != null) {
        Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onDismiss
        )
    } else Modifier

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.scrim)
            .then(clickableModifier)
    )
}

@Composable
fun TcSheet(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val colors = TcThemeState.colors
    val shapes = TcThemeState.shapes
    val dimens = TcThemeState.dimens

    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 16.dp, shape = shapes.BottomSheet)
            .clip(shapes.BottomSheet)
            .background(colors.surface)
            .padding(
                top = dimens.BottomSheetPaddingTop,
                start = dimens.BottomSheetPaddingHorizontal,
                end = dimens.BottomSheetPaddingHorizontal,
                bottom = dimens.BottomSheetPaddingBottom
            )
    ) {
        // Drag handle
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = dimens.BottomSheetHandleRadius * 2)
                .size(width = dimens.BottomSheetHandleWidth, height = dimens.BottomSheetHandleHeight)
                .clip(shapes.BottomSheetHandle)
                .background(colors.border2)
        )

        content()
    }
}
