package com.truckconnect.feature.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.truckconnect.core.design.R
import com.truckconnect.core.design.component.TcButton
import com.truckconnect.core.design.component.TcButtonVariant
import com.truckconnect.core.design.component.TcCard
import com.truckconnect.core.design.component.TcChipKind
import com.truckconnect.core.design.component.TcIconBox
import com.truckconnect.core.design.component.TcScaffold
import com.truckconnect.core.design.theme.TcDimens
import com.truckconnect.core.design.theme.TcThemeState

@Composable
fun RoleSelectScreen(
    onAdminSelected: () -> Unit = {},
    onDriverSelected: () -> Unit = {}
) {
    val colors = TcThemeState.colors
    val typography = TcThemeState.typography
    val dimens = TcThemeState.dimens

    TcScaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(
                    start = dimens.ScreenPaddingHorizontal,
                    end = dimens.ScreenPaddingHorizontal,
                    top = dimens.Padding20,
                    bottom = dimens.ScreenPaddingBottom
                ),
            verticalArrangement = Arrangement.spacedBy(dimens.Gap14)
        ) {
        Column {
            Text(
                text = "Welcome",
                style = typography.h1,
                color = colors.text
            )
            Text(
                text = "Choose how you want to continue",
                style = typography.body,
                color = colors.muted,
                modifier = Modifier.padding(top = dimens.Gap6)
            )
        }

        // Admin Card
        TcCard {
            Column(
                verticalArrangement = Arrangement.spacedBy(dimens.Gap12)
            ) {
                TcIconBox(iconRes = R.drawable.ic_chart)
                Column {
                    Text(
                        text = "Admin / Dispatcher",
                        style = typography.btnLg,
                        color = colors.text
                    )
                    Text(
                        text = "Assign jobs, track vehicles, view reports",
                        style = typography.small,
                        color = colors.muted,
                        modifier = Modifier.padding(top = dimens.Gap3)
                    )
                }
                TcButton(
                    onClick = onAdminSelected,
                    variant = TcButtonVariant.Primary
                ) {
                    Text(text = "Continue as Admin")
                }
            }
        }

        // Driver Card
        TcCard {
            Column(
                verticalArrangement = Arrangement.spacedBy(dimens.Gap12)
            ) {
                TcIconBox(iconRes = R.drawable.ic_truck, kind = TcChipKind.Ok)
                Column {
                    Text(
                        text = "Driver",
                        style = typography.btnLg,
                        color = colors.text
                    )
                    Text(
                        text = "Receive jobs, update trips, share proof",
                        style = typography.small,
                        color = colors.muted,
                        modifier = Modifier.padding(top = dimens.Gap3)
                    )
                }
                TcButton(
                    onClick = onDriverSelected,
                    variant = TcButtonVariant.Outline
                ) {
                    Text(text = "Continue as Driver")
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "By continuing you accept the Terms and Privacy Policy (DPDP Act 2023)",
            style = typography.note,
            color = colors.muted,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
}
