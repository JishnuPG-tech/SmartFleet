package com.truckconnect.core.design.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.truckconnect.core.design.theme.LocalSpacing
import com.truckconnect.core.design.theme.StatusDanger

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isDriverMode: Boolean = false
) {
    val spacing = LocalSpacing.current
    val minHeight = if (isDriverMode) spacing.minTouchDriver else spacing.minTouchAdmin

    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = minHeight),
        enabled = enabled,
        shape = RoundedCornerShape(spacing.radiusMedium),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Text(
            text = text,
            fontSize = if (isDriverMode) 18.sp else 15.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isDriverMode: Boolean = false
) {
    val spacing = LocalSpacing.current
    val minHeight = if (isDriverMode) spacing.minTouchDriver else spacing.minTouchAdmin

    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = minHeight),
        enabled = enabled,
        shape = RoundedCornerShape(spacing.radiusMedium),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Text(
            text = text,
            fontSize = if (isDriverMode) 18.sp else 15.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun DangerButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isDriverMode: Boolean = false
) {
    val spacing = LocalSpacing.current
    val minHeight = if (isDriverMode) spacing.minTouchDriver else spacing.minTouchAdmin

    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = minHeight),
        enabled = enabled,
        shape = RoundedCornerShape(spacing.radiusMedium),
        colors = ButtonDefaults.buttonColors(
            containerColor = StatusDanger,
            contentColor = Color.White
        )
    ) {
        Text(
            text = text,
            fontSize = if (isDriverMode) 18.sp else 15.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
