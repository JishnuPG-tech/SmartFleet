package com.truckconnect.feature.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.truckconnect.core.design.component.TcAppBar
import com.truckconnect.core.design.component.TcButton
import com.truckconnect.core.design.component.TcButtonSize
import com.truckconnect.core.design.component.TcButtonVariant
import com.truckconnect.core.design.component.TcField
import com.truckconnect.core.design.component.TcPhotoBox
import com.truckconnect.core.design.component.TcSegmented
import com.truckconnect.core.design.component.TcStepsBar
import com.truckconnect.core.design.component.TcToggle
import com.truckconnect.core.design.theme.TcDimens
import com.truckconnect.core.design.theme.TcThemeState

@Composable
fun RegisterLicenceScreen(
    onBackClick: () -> Unit = {},
    onSubmitClick: () -> Unit = {}
) {
    val colors = TcThemeState.colors
    val typography = TcThemeState.typography
    val dimens = TcThemeState.dimens
    val isMalayalam = TcThemeState.isMalayalam

    var licenceNumber by remember { mutableStateOf("KL-0720110012345") }
    var licenceType by remember { mutableStateOf("HGMV") }
    var licenceExpiry by remember { mutableStateOf("14 Mar 2029") }
    var vehicleIndex by remember { mutableIntStateOf(0) }
    var dpdpConsent by remember { mutableStateOf(true) } // F3: checked in reference screenshot

    val vehicleOptions = listOf("Company fleet", "Owner-operator")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bg)
            .padding(
                top = dimens.Padding2,
                start = dimens.ScreenPaddingHorizontal,
                end = dimens.ScreenPaddingHorizontal,
                bottom = dimens.ScreenPaddingBottom
            ),
        verticalArrangement = Arrangement.spacedBy(dimens.Gap10)
    ) {
        TcAppBar(
            title = if (isMalayalam) "ഡ്രൈവറായി രജിസ്റ്റർ ചെയ്യുക" else "Register as Driver",
            onBackClick = onBackClick
        )

        TcStepsBar(totalSteps = 3, currentStep = 2)

        Text(
            text = if (isMalayalam) "3 / 3 · ലൈസൻസ് വിവരങ്ങൾ" else "3 / 3 · Driving licence",
            style = typography.small,
            color = colors.muted
        )

        TcField(
            value = licenceNumber,
            onValueChange = { licenceNumber = it },
            label = "Licence number"
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(dimens.Gap10)
        ) {
            TcField(
                value = licenceType,
                onValueChange = { licenceType = it },
                label = "Type",
                modifier = Modifier.weight(1f)
            )
            TcField(
                value = licenceExpiry,
                onValueChange = { licenceExpiry = it },
                label = "Expiry",
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(dimens.Gap10)
        ) {
            TcPhotoBox(
                label = "Front captured",
                isFilled = true,
                modifier = Modifier.weight(1f)
            )
            TcPhotoBox(
                label = "Back · tap to capture",
                isFilled = false,
                modifier = Modifier.weight(1f)
            )
        }

        Column {
            Text(
                text = "Vehicle",
                style = typography.label,
                color = colors.text2,
                modifier = Modifier.padding(bottom = dimens.Gap6)
            )
            TcSegmented(
                options = vehicleOptions,
                selectedIndex = vehicleIndex,
                onSelect = { vehicleIndex = it }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(dimens.Gap10)
        ) {
            TcToggle(
                checked = dpdpConsent,
                onCheckedChange = { dpdpConsent = it },
                modifier = Modifier.padding(top = dimens.Padding2)
            )
            Text(
                text = "I consent to location tracking during active trips and processing of my documents (DPDP Act 2023).",
                style = typography.xsmall,
                color = colors.muted,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        TcButton(
            onClick = onSubmitClick,
            variant = TcButtonVariant.Primary,
            size = TcButtonSize.Lg
        ) {
            Text(text = "Submit for approval")
        }
    }
}
