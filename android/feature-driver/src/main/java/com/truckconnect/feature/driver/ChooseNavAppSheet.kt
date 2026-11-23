package com.truckconnect.feature.driver

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.truckconnect.core.design.R
import com.truckconnect.core.design.component.TcAppBar
import com.truckconnect.core.design.component.TcCard
import com.truckconnect.core.design.component.TcChip
import com.truckconnect.core.design.component.TcChipKind
import com.truckconnect.core.design.component.TcIconBox
import com.truckconnect.core.design.component.TcScrim
import com.truckconnect.core.design.component.TcSheet
import com.truckconnect.core.design.theme.TcDimens
import com.truckconnect.core.design.theme.TcThemeState

@Composable
fun ChooseNavAppSheet(
    onDismiss: () -> Unit = {},
    onAppSelect: (String) -> Unit = {}
) {
    val colors = TcThemeState.colors
    val typography = TcThemeState.typography
    val dimens = TcThemeState.dimens
    val isMalayalam = TcThemeState.isMalayalam

    val apps = listOf(
        Triple("Google Maps", R.drawable.ic_map, false),
        Triple("Vela", R.drawable.ic_nav, true),
        Triple("OsmAnd", R.drawable.ic_globe, false)
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.bg)
        ) {
            TcAppBar(title = "Kasaragod → Malappuram")
        }

        TcScrim(onDismiss = onDismiss)

        TcSheet(modifier = Modifier.align(Alignment.BottomCenter)) {
            Text(
                text = if (isMalayalam) "വഴി കാണിക്കുക" else "Navigate",
                style = typography.h1,
                color = colors.text,
                modifier = Modifier.padding(bottom = dimens.Gap4)
            )

            Column(verticalArrangement = Arrangement.spacedBy(dimens.Gap10)) {
                apps.forEach { (name, iconRes, isDefault) ->
                    TcCard(
                        onClick = { onAppSelect(name) }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(dimens.Gap10)
                        ) {
                            TcIconBox(iconRes = iconRes)
                            Text(
                                text = name,
                                style = typography.btnLg,
                                color = colors.text,
                                modifier = Modifier.weight(1f)
                            )
                            if (isDefault) {
                                TcChip(
                                    text = "Default",
                                    kind = TcChipKind.Ok
                                )
                            }
                        }
                    }
                }
            }

            Text(
                text = "Opens your installed maps app with the destination",
                style = typography.note,
                color = colors.muted,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = dimens.Gap6)
            )
        }
    }
}
