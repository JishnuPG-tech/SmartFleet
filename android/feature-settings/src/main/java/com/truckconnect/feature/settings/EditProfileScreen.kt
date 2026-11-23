package com.truckconnect.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.truckconnect.core.design.component.TcButton
import com.truckconnect.core.design.component.TcButtonSize
import com.truckconnect.core.design.component.TcButtonVariant
import com.truckconnect.core.design.component.TcCard
import com.truckconnect.core.design.component.TcField
import com.truckconnect.core.design.component.TcScreen
import com.truckconnect.core.design.theme.TcThemeState

@Composable
fun EditProfileScreen(
    onBackClick: () -> Unit = {},
    onSaved: () -> Unit = {}
) {
    val colors = TcThemeState.colors
    val typography = TcThemeState.typography
    val dimens = TcThemeState.dimens

    var name by remember { mutableStateOf("Anil Kumar") }
    var email by remember { mutableStateOf("anil@truckconnect.in") }
    var phone by remember { mutableStateOf("+91 98765 43210") }
    var isSavedMessageVisible by remember { mutableStateOf(false) }

    TcScreen(
        title = "Edit profile",
        onBackClick = onBackClick,
        imeAware = true,
        isScrollable = true,
        pinnedBottomActions = {
            TcButton(
                text = "Save changes",
                onClick = {
                    isSavedMessageVisible = true
                    onSaved()
                },
                size = TcButtonSize.Lg,
                modifier = Modifier.fillMaxWidth()
            )
        }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(dimens.Gap14)
        ) {
            TcCard {
                Column(verticalArrangement = Arrangement.spacedBy(dimens.Gap12)) {
                    Text(text = "Full name", style = typography.btnSm, color = colors.text)
                    TcField(
                        value = name,
                        onValueChange = { name = it },
                        placeholder = "Enter full name"
                    )

                    Text(text = "Email address", style = typography.btnSm, color = colors.text)
                    TcField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = "Enter work email"
                    )

                    Text(text = "Mobile phone", style = typography.btnSm, color = colors.text)
                    TcField(
                        value = phone,
                        onValueChange = { phone = it },
                        placeholder = "+91 ..."
                    )
                }
            }

            if (isSavedMessageVisible) {
                Text(
                    text = "Profile updated successfully!",
                    style = typography.btnSm,
                    color = colors.ok
                )
            }
        }
    }
}
