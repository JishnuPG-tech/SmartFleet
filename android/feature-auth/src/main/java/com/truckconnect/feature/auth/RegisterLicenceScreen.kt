package com.truckconnect.feature.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import com.truckconnect.core.design.component.TcButton
import com.truckconnect.core.design.component.TcButtonSize
import com.truckconnect.core.design.component.TcButtonVariant
import com.truckconnect.core.design.component.TcField
import com.truckconnect.core.design.component.TcPhotoBox
import com.truckconnect.core.design.component.TcScreen
import com.truckconnect.core.design.component.TcSegmented
import com.truckconnect.core.design.component.TcStepsBar
import com.truckconnect.core.design.component.TcToggle
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
    var dpdpConsent by remember { mutableStateOf(true) }

    val vehicleOptions = listOf(
        if (isMalayalam) "കമ്പനി വാഹനം" else "Company fleet",
        if (isMalayalam) "സ്വന്തം വാഹനം" else "Owner-operator"
    )

    TcScreen(
        title = if (isMalayalam) "ലൈസൻസ് വിവരങ്ങൾ" else "Licence Details",
        onBackClick = onBackClick,
        imeAware = true,
        isScrollable = true,
        pinnedBottomActions = {
            TcButton(
                onClick = onSubmitClick,
                variant = TcButtonVariant.Primary,
                size = TcButtonSize.Lg,
                enabled = dpdpConsent,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = if (isMalayalam) "അംഗീകാരത്തിനായി അയയ്ക്കുക" else "Submit for verification")
            }
        }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(dimens.Gap10)
        ) {
            TcStepsBar(totalSteps = 3, currentStep = 2)

            Text(
                text = if (isMalayalam) "3 / 3 · ലൈസൻസും വാഹനവും" else "3 / 3 · Licence & Vehicle",
                style = typography.small,
                color = colors.muted
            )

            Text(
                text = if (isMalayalam) "രേഖകൾ സമർപ്പിക്കുക" else "Driver Documents",
                style = typography.h1,
                color = colors.text
            )

            TcField(
                value = licenceNumber,
                onValueChange = { licenceNumber = it },
                label = if (isMalayalam) "ലൈസൻസ് നമ്പർ" else "Licence number"
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(dimens.Gap10)
            ) {
                TcField(
                    value = licenceType,
                    onValueChange = { licenceType = it },
                    label = if (isMalayalam) "തരം" else "Class",
                    modifier = Modifier.weight(1f)
                )

                TcField(
                    value = licenceExpiry,
                    onValueChange = { licenceExpiry = it },
                    label = if (isMalayalam) "കാലാവധി" else "Expiry date",
                    modifier = Modifier.weight(1f)
                )
            }

            Text(
                text = if (isMalayalam) "വാഹന തരം" else "Vehicle category",
                style = typography.small,
                color = colors.muted
            )

            TcSegmented(
                options = vehicleOptions,
                selectedIndex = vehicleIndex,
                onSelect = { vehicleIndex = it }
            )

            Text(
                text = if (isMalayalam) "ലൈസൻസ് ഫോട്ടോ (മുന്നിൽ)" else "Upload licence photo (Front)",
                style = typography.small,
                color = colors.muted
            )

            TcPhotoBox(
                label = if (isMalayalam) "ഫോട്ടോ എടുക്കുക അല്ലെങ്കിൽ അപ്‌ലോഡ് ചെയ്യുക" else "Tap to capture or upload photo",
                isFilled = true
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dimens.Gap10)
            ) {
                TcToggle(
                    checked = dpdpConsent,
                    onCheckedChange = { dpdpConsent = it }
                )
                Text(
                    text = if (isMalayalam)
                        "DPDP ആക്റ്റ് 2023 പ്രകാരം വിവരങ്ങൾ പരിശോധിക്കുന്നതിന് സമ്മതിക്കുന്നു"
                    else
                        "I consent to document verification under DPDP Act 2023",
                    style = typography.xsmall,
                    color = colors.text,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
