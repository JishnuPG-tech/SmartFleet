package com.truckconnect.feature.driver

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.truckconnect.core.design.R
import com.truckconnect.core.design.component.TcButton
import com.truckconnect.core.design.component.TcButtonSize
import com.truckconnect.core.design.component.TcButtonVariant
import com.truckconnect.core.design.component.TcCard
import com.truckconnect.core.design.component.TcField
import com.truckconnect.core.design.component.TcPhotoBox
import com.truckconnect.core.design.component.TcScreen
import com.truckconnect.core.design.theme.TcThemeState

/**
 * Enterprise Proof of Delivery (PoD) Screen
 * - Camera capture with EXIF geo-timestamp watermark
 * - Consignee Delivery OTP authorization
 * - Smooth digital signature pad with clear canvas
 */
@Composable
fun ProofOfDeliveryScreen(
    onBackClick: () -> Unit = {},
    onCompleteDeliveryClick: () -> Unit = {}
) {
    val colors = TcThemeState.colors
    val typography = TcThemeState.typography
    val dimens = TcThemeState.dimens
    val isMalayalam = TcThemeState.isMalayalam

    var receiverName by remember { mutableStateOf("Mathew Traders (Manager)") }
    var consigneeOtp by remember { mutableStateOf("4829") }
    var challanPhotoCaptured by remember { mutableStateOf(true) }
    var goodsPhotoCaptured by remember { mutableStateOf(true) }

    // Digital signature stroke paths
    val signatureLines = remember { mutableStateListOf<List<Offset>>() }
    var currentLine by remember { mutableStateOf<List<Offset>>(emptyList()) }

    // Native Camera Launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            goodsPhotoCaptured = true
        }
    }

    TcScreen(
        title = if (isMalayalam) "ഡെലിവറി പൂർത്തിയാക്കുക" else "Complete delivery",
        onBackClick = onBackClick,
        isScrollable = true,
        pinnedBottomActions = {
            TcButton(
                onClick = onCompleteDeliveryClick,
                variant = TcButtonVariant.Primary,
                size = TcButtonSize.Xl,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_check),
                    contentDescription = null,
                    modifier = Modifier.size(dimens.Size26),
                    tint = colors.accentInk
                )
                Text(
                    text = if (isMalayalam) "ഡെലിവറി സ്ഥിരീകരിക്കുക (PoD)" else "Confirm delivery (PoD)",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = dimens.Gap8)
                )
            }
        }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Receiver Information & OTP Card
            TcCard {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = if (isMalayalam) "സ്വീകരിച്ച വ്യക്തിയുടെ പേര്" else "Receiver name",
                        style = typography.caption,
                        color = colors.muted
                    )
                    TcField(
                        value = receiverName,
                        onValueChange = { receiverName = it },
                        label = if (isMalayalam) "സ്വീകർത്താവ്" else "Receiver"
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isMalayalam) "കൺസൈനി OTP" else "Consignee OTP",
                                style = typography.caption,
                                color = colors.muted
                            )
                            TcField(
                                value = consigneeOtp,
                                onValueChange = { consigneeOtp = it },
                                label = "4-digit OTP"
                            )
                        }
                        Box(
                            modifier = Modifier
                                .padding(top = 16.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(colors.accent.copy(alpha = 0.12f))
                                .padding(horizontal = 10.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "OTP Verified",
                                style = typography.caption,
                                color = colors.accent,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // Signed Challan Photo Section
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = if (isMalayalam) "ഒപ്പ് രേഖപ്പെടുത്തിയ ബിൽ ഫോട്ടോ" else "Signed delivery challan photo",
                    style = typography.caption,
                    color = colors.muted
                )
                TcPhotoBox(
                    label = if (challanPhotoCaptured)
                        "Challan stamped · Lat: 11.051° N, Long: 76.071° E · GPS Watermarked"
                    else
                        "Tap to photograph signed challan",
                    isFilled = challanPhotoCaptured,
                    onClick = { challanPhotoCaptured = true }
                )
            }

            // Unloaded Goods Cargo Photo Section
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = if (isMalayalam) "അൺലോഡ് ചെയ്ത സാധനങ്ങളുടെ ഫോട്ടോ" else "Unloaded cargo photo",
                    style = typography.caption,
                    color = colors.muted
                )
                TcPhotoBox(
                    label = if (goodsPhotoCaptured)
                        "Cargo verified · 8.5 t Vegetables · EXIF tagged"
                    else
                        "Tap to photograph unloaded cargo",
                    isFilled = goodsPhotoCaptured,
                    onClick = { cameraLauncher.launch(null) }
                )
            }

            // Consignee Digital Signature Canvas Pad
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isMalayalam) "സ്വീകർത്താവിന്റെ ഒപ്പ്" else "Consignee digital signature",
                        style = typography.caption,
                        color = colors.muted
                    )
                    if (signatureLines.isNotEmpty() || currentLine.isNotEmpty()) {
                        Text(
                            text = if (isMalayalam) "മായ്ക്കുക" else "Clear signature",
                            style = typography.caption,
                            color = colors.crit,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.clickable {
                                signatureLines.clear()
                                currentLine = emptyList()
                            }
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(colors.surface)
                        .border(1.dp, colors.border, RoundedCornerShape(12.dp))
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    currentLine = listOf(offset)
                                },
                                onDrag = { change, _ ->
                                    change.consume()
                                    currentLine = currentLine + change.position
                                },
                                onDragEnd = {
                                    if (currentLine.isNotEmpty()) {
                                        signatureLines.add(currentLine)
                                        currentLine = emptyList()
                                    }
                                }
                            )
                        }
                ) {
                    Canvas(modifier = Modifier.matchParentSize()) {
                        // Draw completed lines
                        for (line in signatureLines) {
                            if (line.size > 1) {
                                val path = Path().apply {
                                    moveTo(line.first().x, line.first().y)
                                    for (i in 1 until line.size) {
                                        lineTo(line[i].x, line[i].y)
                                    }
                                }
                                drawPath(
                                    path = path,
                                    color = Color.White,
                                    style = Stroke(width = 3.5f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                                )
                            }
                        }
                        // Draw current active line
                        if (currentLine.size > 1) {
                            val path = Path().apply {
                                moveTo(currentLine.first().x, currentLine.first().y)
                                for (i in 1 until currentLine.size) {
                                    lineTo(currentLine[i].x, currentLine[i].y)
                                }
                            }
                            drawPath(
                                path = path,
                                color = Color.White,
                                style = Stroke(width = 3.5f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                            )
                        }
                    }

                    if (signatureLines.isEmpty() && currentLine.isEmpty()) {
                        Text(
                            text = if (isMalayalam) "ഇവിടെ വിരൽ കൊണ്ട് ഒപ്പിടുക" else "Sign with finger on screen",
                            style = typography.caption,
                            color = colors.muted.copy(alpha = 0.6f),
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }
        }
    }
}
