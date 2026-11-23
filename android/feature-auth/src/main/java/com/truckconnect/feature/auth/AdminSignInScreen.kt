package com.truckconnect.feature.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import com.truckconnect.core.design.R
import com.truckconnect.core.design.component.TcButton
import com.truckconnect.core.design.component.TcButtonVariant
import com.truckconnect.core.design.component.TcCard
import com.truckconnect.core.design.component.TcField
import com.truckconnect.core.design.component.TcIconBox
import com.truckconnect.core.design.component.TcScaffold
import com.truckconnect.core.design.component.TcToggle
import com.truckconnect.core.design.theme.TcDimens
import com.truckconnect.core.design.theme.TcThemeState

@Composable
fun AdminSignInScreen(
    onSignInClick: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {}
) {
    val colors = TcThemeState.colors
    val typography = TcThemeState.typography
    val dimens = TcThemeState.dimens

    var email by remember { mutableStateOf("anil@truckconnect.in") }
    var password by remember { mutableStateOf("••••••••••") }
    var keepSignedIn by remember { mutableStateOf(true) }

    TcScaffold(imeAware = true) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(
                    top = dimens.Padding12,
                    start = dimens.ScreenPaddingHorizontal,
                    end = dimens.ScreenPaddingHorizontal,
                    bottom = dimens.ScreenPaddingBottom
                ),
            verticalArrangement = Arrangement.spacedBy(dimens.Gap14)
        ) {
        TcIconBox(iconRes = R.drawable.ic_chart)

        Column {
            Text(
                text = "Admin sign in",
                style = typography.h1,
                color = colors.text
            )
            Text(
                text = "Use your work email and password",
                style = typography.body,
                color = colors.muted,
                modifier = Modifier.padding(top = dimens.Gap6)
            )
        }

        TcField(
            value = email,
            onValueChange = { email = it },
            label = "Email",
            leadingIcon = {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_mail),
                    contentDescription = null,
                    modifier = Modifier.size(dimens.Size18),
                    tint = colors.text2
                )
            }
        )

        TcField(
            value = password,
            onValueChange = { password = it },
            label = "Password",
            leadingIcon = {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_lock),
                    contentDescription = null,
                    modifier = Modifier.size(dimens.Size18),
                    tint = colors.text2
                )
            }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimens.Gap10)
        ) {
            TcToggle(
                checked = keepSignedIn,
                onCheckedChange = { keepSignedIn = it }
            )
            Text(
                text = "Keep me signed in",
                style = typography.small,
                color = colors.text
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Forgot?",
                style = typography.btnSm,
                color = colors.accent,
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onForgotPasswordClick
                )
            )
        }

        TcButton(
            onClick = onSignInClick,
            variant = TcButtonVariant.Primary
        ) {
            Text(text = "Sign in")
        }

        TcCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dimens.Gap12)
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_shield),
                    contentDescription = null,
                    modifier = Modifier.size(dimens.Size22),
                    tint = colors.text2
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "2-step verification",
                        style = typography.btnSm,
                        color = colors.text
                    )
                    Text(
                        text = "Enter the 6-digit code if enabled",
                        style = typography.xsmall,
                        color = colors.muted
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "Biometric or PIN app lock can be enabled in Security",
            style = typography.note,
            color = colors.muted,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
}
