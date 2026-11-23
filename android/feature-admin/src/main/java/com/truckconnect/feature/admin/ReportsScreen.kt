package com.truckconnect.feature.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.truckconnect.core.design.R
import com.truckconnect.core.design.component.TcBarChart
import com.truckconnect.core.design.component.TcButton
import com.truckconnect.core.design.component.TcButtonVariant
import com.truckconnect.core.design.component.TcCard
import com.truckconnect.core.design.component.TcDonut
import com.truckconnect.core.design.component.TcIconButton
import com.truckconnect.core.design.component.TcKpiTile
import com.truckconnect.core.design.component.TcScreen
import com.truckconnect.core.design.component.TcSegmented
import com.truckconnect.core.design.theme.TcThemeState

@Composable
fun ReportsScreen(
    onBackClick: () -> Unit = {},
    onDownloadClick: () -> Unit = {},
    onExportPdfClick: () -> Unit = {},
    onExportExcelClick: () -> Unit = {}
) {
    val colors = TcThemeState.colors
    val typography = TcThemeState.typography
    val dimens = TcThemeState.dimens

    var periodIndex by remember { mutableIntStateOf(1) } // "Month" is on in mockup
    val periodOptions = listOf("Week", "Month", "Quarter")

    TcScreen(
        title = "Reports",
        onBackClick = onBackClick,
        topBarActions = {
            TcIconButton(onClick = onDownloadClick) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_download),
                    contentDescription = "Download",
                    modifier = Modifier.size(dimens.IconSizeDefault),
                    tint = colors.text2
                )
            }
        },
        isScrollable = true
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(dimens.Gap10)
        ) {
            TcSegmented(
                options = periodOptions,
                selectedIndex = periodIndex,
                onSelect = { periodIndex = it }
            )

            // KPI Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(dimens.Gap10)
            ) {
                TcKpiTile(
                    label = "On-time",
                    value = "94%",
                    sub = "+2 pt",
                    modifier = Modifier.weight(1f)
                )
                TcKpiTile(
                    label = "Accept time",
                    value = "4.2 m",
                    sub = "target 5",
                    modifier = Modifier.weight(1f)
                )
            }

            // Bar Chart Card
            TcCard {
                Column(verticalArrangement = Arrangement.spacedBy(dimens.Gap8)) {
                    Text(
                        text = "Trips per driver",
                        style = typography.btnSm,
                        color = colors.text
                    )
                    TcBarChart(values = listOf(49f, 44f, 42f, 40f, 36f, 34f))
                }
            }

            // Donut Chart Card
            TcCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(dimens.Gap4)) {
                        Text(
                            text = "Fleet status split",
                            style = typography.btnSm,
                            color = colors.text
                        )
                        Text(
                            text = "Moving · Idle · Off",
                            style = typography.xsmall,
                            color = colors.muted
                        )
                    }
                    TcDonut(progress = 0.72f)
                }
            }

            // Action Row: Export PDF / Export Excel
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(dimens.Gap10)
            ) {
                TcButton(
                    onClick = onExportPdfClick,
                    variant = TcButtonVariant.Outline,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_doc),
                        contentDescription = null,
                        modifier = Modifier.size(dimens.IconSizeDefault),
                        tint = colors.text
                    )
                    Text(
                        text = "Export PDF",
                        modifier = Modifier.padding(start = dimens.Gap6)
                    )
                }

                TcButton(
                    onClick = onExportExcelClick,
                    variant = TcButtonVariant.Outline,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_download),
                        contentDescription = null,
                        modifier = Modifier.size(dimens.IconSizeDefault),
                        tint = colors.text
                    )
                    Text(
                        text = "Export Excel",
                        modifier = Modifier.padding(start = dimens.Gap6)
                    )
                }
            }
        }
    }
}
