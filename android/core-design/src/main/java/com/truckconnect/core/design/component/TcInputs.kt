package com.truckconnect.core.design.component

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.truckconnect.core.design.R
import com.truckconnect.core.design.theme.TcDimens
import com.truckconnect.core.design.theme.TcShapes
import com.truckconnect.core.design.theme.TcThemeState

@Composable
fun TcField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    label: String? = null,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    readOnly: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true
) {
    val colors = TcThemeState.colors
    val shapes = TcThemeState.shapes
    val typography = TcThemeState.typography
    val dimens = TcThemeState.dimens
    val isDriver = TcThemeState.isDriver

    val height = if (isDriver) dimens.InputDriverHeight else dimens.InputAdminHeight
    val shape = if (isDriver) shapes.InputDriver else shapes.InputAdmin
    val textStyle = typography.inputText

    Column(modifier = modifier.fillMaxWidth()) {
        if (label != null) {
            Text(
                text = label,
                style = typography.label,
                color = colors.text2,
                modifier = Modifier.padding(bottom = dimens.CardBorder * 6)
            )
        }

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .clip(shape)
                .background(colors.surface)
                .border(BorderStroke(dimens.CardBorder, colors.border2), shape)
                .padding(horizontal = dimens.InputPaddingHorizontal),
            readOnly = readOnly,
            textStyle = textStyle.copy(color = colors.text),
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = singleLine,
            cursorBrush = SolidColor(colors.accent),
            decorationBox = { innerTextField ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(dimens.InputGap)
                ) {
                    if (leadingIcon != null) {
                        leadingIcon()
                    }

                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                        if (value.isEmpty() && placeholder.isNotEmpty()) {
                            Text(
                                text = placeholder,
                                style = textStyle,
                                color = colors.muted
                            )
                        }
                        innerTextField()
                    }

                    if (trailingIcon != null) {
                        trailingIcon()
                    }
                }
            }
        )
    }
}

@Composable
fun TcToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = TcThemeState.colors
    val shapes = TcThemeState.shapes
    val dimens = TcThemeState.dimens

    val knobOffset by animateDpAsState(
        targetValue = if (checked) 21.dp else 3.dp,
        label = "knobOffset"
    )

    Box(
        modifier = modifier
            .size(width = dimens.ToggleWidth, height = dimens.ToggleHeight)
            .clip(shapes.Toggle)
            .background(if (checked) colors.accent else colors.border2)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { onCheckedChange(!checked) }
            )
    ) {
        Box(
            modifier = Modifier
                .offset(x = knobOffset, y = dimens.ToggleKnobInset)
                .size(dimens.ToggleKnobSize)
                .clip(CircleShape)
                .background(Color.White)
        )
    }
}

@Composable
fun TcRadio(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = TcThemeState.colors
    val dimens = TcThemeState.dimens

    Box(
        modifier = modifier
            .size(dimens.RadioSize)
            .clip(CircleShape)
            .border(
                width = dimens.CardBorder * 2,
                color = if (selected) colors.accent else colors.border2,
                shape = CircleShape
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            Box(
                modifier = Modifier
                    .size(dimens.RadioDotSize)
                    .clip(CircleShape)
                    .background(colors.accent)
            )
        }
    }
}

@Composable
fun TcSegmented(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = TcThemeState.colors
    val shapes = TcThemeState.shapes
    val typography = TcThemeState.typography
    val dimens = TcThemeState.dimens

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shapes.Segmented)
            .background(colors.surface2)
            .border(BorderStroke(dimens.CardBorder, colors.border), shapes.Segmented)
            .padding(dimens.SegmentedPadding),
        horizontalArrangement = Arrangement.spacedBy(dimens.SegmentedGap)
    ) {
        options.forEachIndexed { index, option ->
            val isSelected = index == selectedIndex
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(shapes.SegmentedItem)
                    .background(if (isSelected) colors.surface else Color.Transparent)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { onSelect(index) }
                    )
                    .padding(
                        vertical = dimens.SegmentedItemPaddingVertical * 2,
                        horizontal = dimens.SegmentedItemPaddingHorizontal / 2
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = option,
                    style = typography.segmented,
                    color = if (isSelected) colors.text else colors.text2
                )
            }
        }
    }
}

@Composable
fun TcStepsBar(
    totalSteps: Int,
    currentStep: Int,
    modifier: Modifier = Modifier
) {
    val colors = TcThemeState.colors
    val shapes = TcThemeState.shapes
    val dimens = TcThemeState.dimens

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(dimens.StepsBarGap)
    ) {
        for (i in 0 until totalSteps) {
            val isActive = i <= currentStep
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(dimens.StepsBarHeight)
                    .clip(shapes.StepsBar)
                    .background(if (isActive) colors.accent else colors.border2)
            )
        }
    }
}

@Composable
fun TcPinDots(
    totalDots: Int = 4,
    enteredCount: Int,
    modifier: Modifier = Modifier
) {
    val colors = TcThemeState.colors
    val dimens = TcThemeState.dimens

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(dimens.PinDotGap),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until totalDots) {
            val isFilled = i < enteredCount
            Box(
                modifier = Modifier
                    .size(dimens.PinDotSize)
                    .clip(CircleShape)
                    .background(if (isFilled) colors.accent else Color.Transparent)
                    .border(
                        width = dimens.CardBorder * 2,
                        color = if (isFilled) colors.accent else colors.border2,
                        shape = CircleShape
                    )
            )
        }
    }
}

@Composable
fun TcKeypad(
    onKeyClick: (String) -> Unit,
    onBackspace: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = TcThemeState.colors
    val shapes = TcThemeState.shapes
    val typography = TcThemeState.typography
    val dimens = TcThemeState.dimens

    val keys = listOf(
        "1", "2", "3",
        "4", "5", "6",
        "7", "8", "9",
        "", "0", "DEL"
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(dimens.KeypadGap)
    ) {
        for (row in 0 until 4) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(dimens.KeypadGap)
            ) {
                for (col in 0 until 3) {
                    val key = keys[row * 3 + col]
                    if (key.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(dimens.KeypadKeyHeight)
                        )
                    } else if (key == "DEL") {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(dimens.KeypadKeyHeight)
                                .clip(shapes.KeypadKey)
                                .background(colors.surface)
                                .border(BorderStroke(dimens.CardBorder, colors.border), shapes.KeypadKey)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = ripple(color = colors.accent),
                                    onClick = onBackspace
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.ic_x),
                                contentDescription = "Delete",
                                modifier = Modifier.size(dimens.IconSizeDefault),
                                tint = colors.text
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(dimens.KeypadKeyHeight)
                                .clip(shapes.KeypadKey)
                                .background(colors.surface)
                                .border(BorderStroke(dimens.CardBorder, colors.border), shapes.KeypadKey)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = ripple(color = colors.accent),
                                    onClick = { onKeyClick(key) }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = key,
                                style = typography.keypadKey,
                                color = colors.text
                            )
                        }
                    }
                }
            }
        }
    }
}
