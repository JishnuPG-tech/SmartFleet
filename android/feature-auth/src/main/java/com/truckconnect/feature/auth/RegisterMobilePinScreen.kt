package com.truckconnect.feature.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.truckconnect.core.design.R
import com.truckconnect.core.design.component.TcButton
import com.truckconnect.core.design.component.TcButtonSize
import com.truckconnect.core.design.component.TcButtonVariant
import com.truckconnect.core.design.component.TcField
import com.truckconnect.core.design.component.TcScreen
import com.truckconnect.core.design.component.TcStepsBar
import com.truckconnect.core.design.theme.TcThemeState

@Composable
fun RegisterMobilePinScreen(
    onBackClick: () -> Unit = {},
    onNextClick: () -> Unit = {}
) {
    val colors = TcThemeState.colors
    val typography = TcThemeState.typography
    val dimens = TcThemeState.dimens
    val isMalayalam = TcThemeState.isMalayalam

    var phone by remember { mutableStateOf("+91 98470 55501") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    TcScreen(
        title = if (isMalayalam) "ഡ്രൈവറായി രജിസ്റ്റർ ചെയ്യുക" else "Register as Driver",
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
            verticalArrangement = Arrangement.spacedBy(dimens.Gap14)
        ) {
            TcStepsBar(totalSteps = 3, currentStep = 0)

            Text(
                text = if (isMalayalam) "1 / 3 · മൊബൈലും പാസ്‌വേഡും" else "1 / 3 · Mobile & Password",
                style = typography.small,
                color = colors.muted
            )

            Text(
                text = if (isMalayalam) "ലോഗിൻ വിവരങ്ങൾ നൽകുക" else "Enter login details",
                style = typography.h1,
                color = colors.text
            )

            TcField(
                value = phone,
                onValueChange = { phone = it },
                label = if (isMalayalam) "മൊബൈൽ നമ്പർ" else "Mobile phone number",
                leadingIcon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_phone),
                        contentDescription = null,
                        modifier = Modifier.size(dimens.Size18),
                        tint = colors.text2
                    )
                }
            )

            TcField(
                value = password,
                onValueChange = { password = it },
                label = if (isMalayalam) "പാസ്‌വേഡ് സൃഷ്ടിക്കുക" else "Create password",
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                leadingIcon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_lock),
                        contentDescription = null,
                        modifier = Modifier.size(dimens.Size18),
                        tint = colors.text2
                    )
                },
                trailingIcon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_shield),
                        contentDescription = if (isPasswordVisible) "Hide password" else "Show password",
                        modifier = Modifier
                            .size(dimens.Size18)
                            .clickable { isPasswordVisible = !isPasswordVisible },
                        tint = if (isPasswordVisible) colors.accent else colors.muted
                    )
                }
            )

            TcField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = if (isMalayalam) "പാസ്‌വേഡ് വീണ്ടും നൽകുക" else "Confirm password",
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                leadingIcon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_lock),
                        contentDescription = null,
                        modifier = Modifier.size(dimens.Size18),
                        tint = colors.text2
                    )
                }
            )

            Text(
                text = if (isMalayalam)
                    "കുറഞ്ഞത് 8 അക്ഷരങ്ങളും സംഖ്യകളും അടങ്ങിയിരിക്കണം"
                else
                    "Must be at least 8 characters with a mix of letters and numbers",
                style = typography.xsmall,
                color = colors.muted
            )
        }
    }
}
