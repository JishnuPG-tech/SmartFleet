package com.truckconnect.feature.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.truckconnect.core.design.component.TcAppBar
import com.truckconnect.core.design.component.TcBarChart
import com.truckconnect.core.design.component.TcButton
import com.truckconnect.core.design.component.TcButtonVariant
import com.truckconnect.core.design.component.TcCard
import com.truckconnect.core.design.component.TcDonut
import com.truckconnect.core.design.component.TcIconButton
import com.truckconnect.core.design.component.TcKpiTile
import com.truckconnect.core.design.component.TcScaffold
import com.truckconnect.core.design.component.TcSegmented
import com.truckconnect.core.design.theme.TcDimens
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

    TcScaffold(
        topBar = {
            TcAppBar(
                title = "Reports",
                onBackClick = onBackClick,
                actions = {
                    TcIconButton(onClick = onDownloadClick) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_download),
                            contentDescription = "Download",
                            modifier = Modifier.size(dimens.IconSizeDefault),
                            tint = colors.text2
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = dimens.ScreenPaddingHorizontal),
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
                    horizontalArrangement = Arrangement.spacedBy(dimens.Gap16)
                ) {
                    TcDonut(
                        completedPercent = 0.96f,
                        cancelledPercent = 0.03f
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(dimens.Gap6)) {
                        Text(
                            text = "Completed vs cancelled",
                            style = typography.btnSm,
                            color = colors.text
                        )
                        Text(
                            text = "● 246 completed",
                            style = typography.small,
                            color = colors.ok
                        )
                        Text(
                            text = "● 8 cancelled",
                            style = typography.small,
                            color = colors.crit
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Action Row (Defect F1 fixed: weight(1f), spacedBy(dimens.Gap10))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(dimens.Gap10)
            ) {
                TcButton(
                    onClick = onExportPdfClick,
                    variant = TcButtonVariant.Outline,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Export PDF")
                }

                TcButton(
                    onClick = onExportExcelClick,
                    variant = TcButtonVariant.Outline,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Export Excel")
                }
            }
        }
    }
}
