package com.truckconnect.feature.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.truckconnect.core.design.component.TcStepsBar
import com.truckconnect.core.design.theme.TcThemeState

/**
 * Step 2 of Driver Registration: Full Name & Identity
 * Stripped of all emergency contacts & family relations per enterprise specification.
 */
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
                enabled = fullName.isNotBlank(),
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
                text = if (isMalayalam) "2 / 3 · വ്യക്തിഗത വിവരങ്ങൾ" else "2 / 3 · Personal Details",
                style = typography.small,
                color = colors.muted
            )

            Text(
                text = if (isMalayalam) "നിങ്ങളുടെ പൂർണ്ണമായ പേര് നൽകുക" else "Driver Identification",
                style = typography.h1,
                color = colors.text
            )

            TcField(
                value = fullName,
                onValueChange = { fullName = it },
                label = if (isMalayalam) "പൂർണ്ണമായ പേര് (ലൈസൻസിലുള്ളതുപോലെ)" else "Full legal name (as per licence)",
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
                text = if (isMalayalam)
                    "അംഗീകൃത ലൈസൻസിൽ രേഖപ്പെടുത്തിയിട്ടുള്ള പേര് കൃത്യമായി നൽകുക. ഇത് ഉടമയും അഡ്മിനും പരിശോധിക്കുന്നതാണ്."
                else
                    "Enter your name exactly as it appears on your commercial driving licence. This will be verified during admin onboarding.",
                style = typography.xsmall,
                color = colors.muted
            )
        }
    }
}
