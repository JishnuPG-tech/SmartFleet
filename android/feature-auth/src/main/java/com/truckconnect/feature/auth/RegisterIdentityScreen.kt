package com.truckconnect.feature.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.truckconnect.core.design.R
import com.truckconnect.core.design.component.TcButton
import com.truckconnect.core.design.component.TcButtonSize
import com.truckconnect.core.design.component.TcButtonVariant
import com.truckconnect.core.design.component.TcField
import com.truckconnect.core.design.component.TcScreen
import com.truckconnect.core.design.component.TcSegmented
import com.truckconnect.core.design.component.TcStepsBar
import com.truckconnect.core.design.theme.TcThemeState

@Composable
fun RegisterIdentityScreen(
    onBackClick: () -> Unit = {},
    onNextClick: () -> Unit = {}
) {
    val colors = TcThemeState.colors
    val typography = TcThemeState.typography
    val dimens = TcThemeState.dimens
    val isMalayalam = TcThemeState.isMalayalam

    var fullName by remember { mutableStateOf("Rajan Nair") }
    var emergencyName by remember { mutableStateOf("Sreeja Nair") }
    var emergencyPhone by remember { mutableStateOf("+91 94950 11223") }
    var relationIndex by remember { mutableIntStateOf(0) }

    val relationOptions = listOf(
        if (isMalayalam) "ഭാര്യ/ഭർത്താവ്" else "Spouse",
        if (isMalayalam) "മാതാപിതാക്കൾ" else "Parent",
        if (isMalayalam) "മക്കൾ" else "Child",
        if (isMalayalam) "മറ്റുള്ളവർ" else "Other"
    )

    TcScreen(
        title = if (isMalayalam) "വ്യക്തിഗത വിവരങ്ങൾ" else "Personal Details",
        onBackClick = onBackClick,
        imeAware = true,
        isScrollable = true,
        pinnedBottomActions = {
            TcButton(
                onClick = onNextClick,
                variant = TcButtonVariant.Primary,
                size = TcButtonSize.Lg,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = if (isMalayalam) "തുടരുക" else "Continue")
            }
        }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(dimens.Gap12)
        ) {
            TcStepsBar(totalSteps = 3, currentStep = 1)

            Text(
                text = if (isMalayalam) "2 / 3 · വ്യക്തിഗത വിവരങ്ങൾ" else "2 / 3 · Personal details",
                style = typography.small,
                color = colors.muted
            )

            Text(
                text = if (isMalayalam) "നിങ്ങളുടെ പൂർണ്ണമായ വിവരങ്ങൾ" else "Personal & Emergency Info",
                style = typography.h1,
                color = colors.text
            )

            TcField(
                value = fullName,
                onValueChange = { fullName = it },
                label = if (isMalayalam) "പൂർണ്ണമായ പേര്" else "Full legal name",
                leadingIcon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_user),
                        contentDescription = null,
                        modifier = Modifier.size(dimens.Size18),
                        tint = colors.text2
                    )
                }
            )

            Text(
                text = if (isMalayalam) "അടിയന്തര സമ്പർക്കം" else "Emergency contact",
                style = typography.small,
                color = colors.muted
            )

            TcField(
                value = emergencyName,
                onValueChange = { emergencyName = it },
                label = if (isMalayalam) "സമ്പർക്ക വ്യക്തിയുടെ പേര്" else "Contact person's name",
                leadingIcon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_user),
                        contentDescription = null,
                        modifier = Modifier.size(dimens.Size18),
                        tint = colors.text2
                    )
                }
            )

            TcSegmented(
                options = relationOptions,
                selectedIndex = relationIndex,
                onSelect = { relationIndex = it }
            )

            TcField(
                value = emergencyPhone,
                onValueChange = { emergencyPhone = it },
                label = if (isMalayalam) "അടിയന്തര ഫോൺ നമ്പർ" else "Emergency phone number",
                leadingIcon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_phone),
                        contentDescription = null,
                        modifier = Modifier.size(dimens.Size18),
                        tint = colors.text2
                    )
                }
            )
        }
    }
}
