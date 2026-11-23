package com.truckconnect.core.design.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.sp
import com.truckconnect.core.design.theme.BorderLight
import com.truckconnect.core.design.theme.LocalSpacing
import com.truckconnect.core.design.theme.TextMuted
import com.truckconnect.core.design.theme.TextPrimary

@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    isDriverMode: Boolean = false,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    val spacing = LocalSpacing.current
    val fontSize = if (isDriverMode) 18.sp else 15.sp

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        label = { Text(label, fontSize = if (isDriverMode) 16.sp else 13.sp) },
        placeholder = { Text(placeholder, color = TextMuted) },
        textStyle = TextStyle(fontSize = fontSize, color = TextPrimary),
        singleLine = true,
        isError = isError,
        supportingText = if (errorMessage != null) {
            { Text(errorMessage, color = MaterialTheme.colorScheme.error) }
        } else null,
        shape = RoundedCornerShape(spacing.radiusMedium),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = BorderLight,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface
        ),
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation
    )
}
