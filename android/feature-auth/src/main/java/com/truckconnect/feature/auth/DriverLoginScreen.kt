package com.truckconnect.feature.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.truckconnect.core.design.R
import com.truckconnect.core.design.component.TcButton
import com.truckconnect.core.design.component.TcButtonSize
import com.truckconnect.core.design.component.TcButtonVariant
import com.truckconnect.core.design.component.TcChip
import com.truckconnect.core.design.component.TcChipKind
import com.truckconnect.core.design.component.TcField
import com.truckconnect.core.design.component.TcIconBox
import com.truckconnect.core.design.component.TcScaffold
import com.truckconnect.core.design.theme.TcDimens
import com.truckconnect.core.design.theme.TcThemeState

/**
 * Driver Login Screen (D1)
 *
 * Updated per Owner Directive (v2):
 * - Authenticates using Mobile Number and Password (no PIN code).
 * - Hosted in TcScaffold for edge-to-edge system insets and IME awareness.
 */
@Composable
fun DriverLoginScreen(
    onLoginClick: () -> Unit = {},
    onRegisterClick: () -> Unit = {},
    onLanguageToggle: () -> Unit = {}
) {
    val colors = TcThemeState.colors
    val typography = TcThemeState.typography
    val dimens = TcThemeState.dimens
    val isMalayalam = TcThemeState.isMalayalam

    var phone by remember { mutableStateOf("+91 94470 12345") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    TcScaffold(
        imeAware = true
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
                .padding(
                    start = dimens.ScreenPaddingHorizontal,
                    end = dimens.ScreenPaddingHorizontal,
                    top = dimens.Padding12,
                    bottom = dimens.ScreenPaddingBottom
                ),
            verticalArrangement = Arrangement.spacedBy(dimens.Gap14)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TcIconBox(
                    iconRes = R.drawable.ic_truck,
                    kind = TcChipKind.Ok
                )
                TcChip(
                    text = if (isMalayalam) "മലയാളം (English)" else "English (മലയാളം)",
                    kind = TcChipKind.Neutral,
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onLanguageToggle
                    )
                )
            }

            Text(
                text = if (isMalayalam) "ഡ്രൈവർ ലോഗിൻ" else "Driver login",
                style = typography.h1,
                color = colors.text
            )

            Text(
                text = if (isMalayalam) "തുടരുന്നതിന് നിങ്ങളുടെ മൊബൈൽ നമ്പറും പാസ്‌വേഡും നൽകുക" 
                       else "Enter your mobile number and password to continue",
                style = typography.body,
                color = colors.muted
            )

            TcField(
                value = phone,
                onValueChange = { phone = it },
                label = if (isMalayalam) "മൊബൈൽ നമ്പർ" else "Mobile number",
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
                label = if (isMalayalam) "പാസ്‌വേഡ്" else "Password",
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

            Spacer(modifier = Modifier.weight(1f))

            TcButton(
                onClick = onLoginClick,
                variant = TcButtonVariant.Primary,
                size = TcButtonSize.Lg
            ) {
                Text(text = if (isMalayalam) "ലോഗിൻ" else "Login")
            }

            TcButton(
                onClick = onRegisterClick,
                variant = TcButtonVariant.Text,
                size = TcButtonSize.Text
            ) {
                Text(text = if (isMalayalam) "ഡ്രൈവറായി രജിസ്റ്റർ ചെയ്യുക" else "Register as Driver")
            }
        }
    }
}
