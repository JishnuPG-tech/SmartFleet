package com.truckconnect.core.design.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.truckconnect.core.design.R
import com.truckconnect.core.design.theme.TcDimens
import com.truckconnect.core.design.theme.TcShapes
import com.truckconnect.core.design.theme.TcThemeState

@Composable
fun TcProgressBar(
    progress: Float,
    modifier: Modifier = Modifier
) {
    val colors = TcThemeState.colors
    val shapes = TcThemeState.shapes
    val dimens = TcThemeState.dimens

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(dimens.ProgressBarHeight)
            .clip(shapes.ProgressBar)
            .background(colors.surface2)
            .border(dimens.CardBorder, colors.border, shapes.ProgressBar)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(fraction = progress.coerceIn(0f, 1f))
                .height(dimens.ProgressBarHeight)
                .background(colors.accent)
        )
    }
}

data class TcMapPinData(
    val lng: Double,
    val lat: Double,
    val kind: TcChipKind = TcChipKind.Accent,
    val id: String = "",
    val isSelected: Boolean = false,
    val routeOrigin: Pair<Double, Double>? = null, // lat to lng
    val routeDestination: Pair<Double, Double>? = null, // lat to lng
    val intermediateWaypoints: List<Pair<Double, Double>> = emptyList()
)

@Composable
fun TcMapSurface(
    modifier: Modifier = Modifier,
    pins: List<TcMapPinData> = emptyList(),
    selectedPinId: String? = null
) {
    val colors = TcThemeState.colors
    val typography = TcThemeState.typography
    val dimens = TcThemeState.dimens

    val coastPoints = listOf(
        74.9 to 12.75, 75.2 to 11.9, 75.55 to 11.4, 75.7 to 11.0,
        75.95 to 10.5, 76.1 to 10.1, 76.18 to 9.7, 76.22 to 9.4,
        76.4 to 9.0, 76.5 to 8.8, 76.8 to 8.4, 77.0 to 8.1,
        77.25 to 8.3, 77.15 to 8.9, 77.1 to 9.3, 77.25 to 9.8,
        77.1 to 10.2, 76.95 to 10.6, 76.85 to 10.95, 76.75 to 11.15,
        76.55 to 11.5, 76.25 to 11.85, 76.0 to 12.25, 75.65 to 12.45,
        75.3 to 12.7, 75.1 to 12.78
    )

    val cities = listOf(
        Triple("Kannur", 75.37, 11.87),
        Triple("Kozhikode", 75.78, 11.26),
        Triple("Thrissur", 76.21, 10.53),
        Triple("Kochi", 76.27, 9.93),
        Triple("Kollam", 76.60, 8.89),
        Triple("Thiruvananthapuram", 76.94, 8.52)
    )

    Box(
        modifier = modifier
            .clip(TcShapes.MapBox)
            .background(colors.mapSea)
            .border(dimens.CardBorder, colors.border, TcShapes.MapBox)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val scaleX = size.width / 290f
            val scaleY = size.height / 470f

            fun project(lng: Double, lat: Double): Offset {
                val x = ((lng - 74.55) * 92.0 + 8.0) * scaleX
                val y = ((12.95 - lat) * 92.0 + 8.0) * scaleY
                return Offset(x.toFloat(), y.toFloat())
            }

            // 1. Draw coast polygon
            val coastPath = Path()
            coastPoints.forEachIndexed { index, (lng, lat) ->
                val pt = project(lng, lat)
                if (index == 0) coastPath.moveTo(pt.x, pt.y)
                else coastPath.lineTo(pt.x, pt.y)
            }
            coastPath.close()

            drawPath(
                path = coastPath,
                color = colors.mapLand
            )
            drawPath(
                path = coastPath,
                color = colors.mapLine,
                style = Stroke(
                    width = 1.5f * scaleX,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )

            // 2. Draw cities
            cities.forEach { (_, lng, lat) ->
                val pt = project(lng, lat)
                drawCircle(
                    color = colors.surface,
                    radius = 2.5f * scaleX,
                    center = pt
                )
                drawCircle(
                    color = colors.muted,
                    radius = 2.5f * scaleX,
                    center = pt,
                    style = Stroke(width = 1f * scaleX)
                )
            }

            // 3. Draw pins
            pins.forEach { pin ->
                val pt = project(pin.lng, pin.lat)
                val isSel = pin.isSelected || (selectedPinId != null && pin.id == selectedPinId)

                val pinColor = when (pin.kind) {
                    TcChipKind.Ok -> colors.ok
                    TcChipKind.Warn -> colors.warn
                    TcChipKind.Crit -> colors.crit
                    TcChipKind.Info -> colors.info
                    TcChipKind.Accent -> colors.accent
                    TcChipKind.Neutral -> colors.neutral
                }

                if (isSel) {
                    drawCircle(
                        color = pinColor.copy(alpha = 0.2f),
                        radius = 15f * scaleX,
                        center = pt
                    )
                }

                drawCircle(
                    color = pinColor,
                    radius = 9f * scaleX,
                    center = pt
                )
                drawCircle(
                    color = colors.surface,
                    radius = 9f * scaleX,
                    center = pt,
                    style = Stroke(width = 2f * scaleX)
                )

                // Truck mini icon inside pin
                val truckPath = Path().apply {
                    val cx = pt.x
                    val cy = pt.y
                    moveTo(cx - 4f * scaleX, cy - 2.5f * scaleY)
                    lineTo(cx + 1f * scaleX, cy - 2.5f * scaleY)
                    lineTo(cx + 1f * scaleX, cy + 2.5f * scaleY)
                    lineTo(cx - 4f * scaleX, cy + 2.5f * scaleY)
                    close()

                    moveTo(cx + 1f * scaleX, cy - 1f * scaleY)
                    lineTo(cx + 3.5f * scaleX, cy - 1f * scaleY)
                    lineTo(cx + 5f * scaleX, cy + 0.5f * scaleY)
                    lineTo(cx + 5f * scaleX, cy + 3f * scaleY)
                    lineTo(cx + 1f * scaleX, cy + 3f * scaleY)
                    close()
                }

                drawPath(
                    path = truckPath,
                    color = Color.White,
                    style = Stroke(width = 1.1f * scaleX)
                )
            }
        }

        // Attribution
        Text(
            text = "© OpenStreetMap contributors",
            style = typography.xsmall,
            color = colors.muted,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 8.dp, bottom = 5.dp)
        )
    }
}

@Composable
fun TcBarChart(
    values: List<Float> = listOf(49f, 44f, 42f, 40f, 36f, 34f),
    modifier: Modifier = Modifier
) {
    val colors = TcThemeState.colors

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
    ) {
        val scaleX = size.width / 270f
        val scaleY = size.height / 100f

        values.forEachIndexed { i, v ->
            val barWidth = 30f * scaleX
            val barHeight = v * 1.8f * scaleY
            val x = (6f + i * 44f) * scaleX
            val y = size.height - barHeight
            val opacity = (1f - i * 0.1f).coerceIn(0.2f, 1f)

            drawRoundRect(
                color = colors.accent.copy(alpha = opacity),
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(5f * scaleX, 5f * scaleY)
            )
        }
    }
}

@Composable
fun TcDonut(
    completedPercent: Float = 0.96f,
    cancelledPercent: Float = 0.03f,
    modifier: Modifier = Modifier
) {
    val colors = TcThemeState.colors

    Canvas(modifier = modifier.size(84.dp)) {
        val strokeWidth = 12f
        val radius = (size.minDimension - strokeWidth) / 2f
        val center = Offset(size.width / 2f, size.height / 2f)

        // Track
        drawCircle(
            color = colors.border2,
            radius = radius,
            center = center,
            style = Stroke(width = strokeWidth)
        )

        // Completed arc
        drawArc(
            color = colors.ok,
            startAngle = -90f,
            sweepAngle = completedPercent * 360f,
            useCenter = false,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2),
            style = Stroke(width = strokeWidth)
        )

        // Cancelled arc
        drawArc(
            color = colors.crit,
            startAngle = -90f + completedPercent * 360f,
            sweepAngle = cancelledPercent * 360f,
            useCenter = false,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2),
            style = Stroke(width = strokeWidth)
        )
    }
}

@Composable
fun TcLogoTile(
    modifier: Modifier = Modifier
) {
    val colors = TcThemeState.colors
    val shapes = TcThemeState.shapes

    Box(
        modifier = modifier
            .size(84.dp)
            .clip(shapes.LogoTile)
            .background(Color(0x29FFFFFF)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.ic_truck),
            contentDescription = "TruckConnect Logo",
            modifier = Modifier.size(44.dp),
            tint = colors.accentInk
        )
    }
}
