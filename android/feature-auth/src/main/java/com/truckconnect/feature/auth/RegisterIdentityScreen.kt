package com.truckconnect.feature.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import com.truckconnect.core.design.component.TcAppBar
import com.truckconnect.core.design.component.TcButton
import com.truckconnect.core.design.component.TcButtonSize
import com.truckconnect.core.design.component.TcButtonVariant
import com.truckconnect.core.design.component.TcField
import com.truckconnect.core.design.component.TcSegmented
import com.truckconnect.core.design.component.TcStepsBar
import com.truckconnect.core.design.theme.TcDimens
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

    val relationOptions = listOf("Spouse", "Parent", "Child", "Other")

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
        verticalArrangement = Arrangement.spacedBy(dimens.Gap12)
    ) {
        TcAppBar(
            title = if (isMalayalam) "ഡ്രൈവറായി രജിസ്റ്റർ ചെയ്യുക" else "Register as Driver",
            onBackClick = onBackClick
        )

        TcStepsBar(totalSteps = 3, currentStep = 1)

        Text(
            text = if (isMalayalam) "2 / 3 · തിരിച്ചറിയലും അടിയന്തര കോണ്ടാക്ടും" else "2 / 3 · Identity & emergency contact",
            style = typography.small,
            color = colors.muted
        )

        TcField(
            value = fullName,
            onValueChange = { fullName = it },
            label = if (isMalayalam) "പൂർണ്ണ നാമം" else "Full name"
        )

        TcField(
            value = emergencyName,
            onValueChange = { emergencyName = it },
            label = "Emergency contact name"
        )

        TcField(
            value = emergencyPhone,
            onValueChange = { emergencyPhone = it },
            label = "Emergency contact phone",
            leadingIcon = {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_phone),
                    contentDescription = null,
                    modifier = Modifier.size(dimens.Size18),
                    tint = colors.text2
                )
            }
        )

        Column {
            Text(
                text = "Relation",
                style = typography.label,
                color = colors.text2,
                modifier = Modifier.padding(bottom = dimens.Gap6)
            )
            TcSegmented(
                options = relationOptions,
                selectedIndex = relationIndex,
                onSelect = { relationIndex = it }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        TcButton(
            onClick = onNextClick,
            variant = TcButtonVariant.Primary,
            size = TcButtonSize.Lg
        ) {
            Text(text = if (isMalayalam) "അടുത്തത്" else "Next")
        }
    }
}
