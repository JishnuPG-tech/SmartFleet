package com.truckconnect.core.design.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.truckconnect.core.design.theme.LocalSpacing
import com.truckconnect.core.design.theme.StatusDanger
import com.truckconnect.core.design.theme.StatusDangerBg
import com.truckconnect.core.design.theme.StatusInfo
import com.truckconnect.core.design.theme.StatusInfoBg
import com.truckconnect.core.design.theme.StatusSuccess
import com.truckconnect.core.design.theme.StatusSuccessBg
import com.truckconnect.core.design.theme.StatusWarning
import com.truckconnect.core.design.theme.StatusWarningBg

enum class StatusKind {
    SUCCESS,
    WARNING,
    DANGER,
    INFO,
    NEUTRAL
}

@Composable
fun StatusChip(
    text: String,
    kind: StatusKind,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current
    val (bgColor, textColor, dotColor) = when (kind) {
        StatusKind.SUCCESS -> Triple(StatusSuccessBg, StatusSuccess, StatusSuccess)
        StatusKind.WARNING -> Triple(StatusWarningBg, StatusWarning, StatusWarning)
        StatusKind.DANGER -> Triple(StatusDangerBg, StatusDanger, StatusDanger)
        StatusKind.INFO -> Triple(StatusInfoBg, StatusInfo, StatusInfo)
        StatusKind.NEUTRAL -> Triple(Color(0xFFF3F4F6), Color(0xFF4B5563), Color(0xFF9CA3AF))
    }

    Row(
        modifier = modifier
            .background(bgColor, RoundedCornerShape(spacing.radiusPill))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(dotColor, CircleShape)
        )
        Text(
            text = text,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 6.dp)
        )
    }
}
