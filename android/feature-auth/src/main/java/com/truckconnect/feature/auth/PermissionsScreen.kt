package com.truckconnect.feature.auth

import androidx.compose.foundation.background
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
import com.truckconnect.core.design.component.TcAppBar
import com.truckconnect.core.design.component.TcButton
import com.truckconnect.core.design.component.TcButtonSize
import com.truckconnect.core.design.component.TcButtonVariant
import com.truckconnect.core.design.component.TcCard
import com.truckconnect.core.design.component.TcChip
import com.truckconnect.core.design.component.TcChipKind
import com.truckconnect.core.design.component.TcIconBox
import com.truckconnect.core.design.component.TcScaffold
import com.truckconnect.core.design.theme.TcDimens
import com.truckconnect.core.design.theme.TcThemeState

@Composable
fun PermissionsScreen(
    onAllowLocationClick: () -> Unit = {},
    onAllowNotificationsClick: () -> Unit = {},
    onOpenBatteryGuideClick: () -> Unit = {},
    onNextClick: () -> Unit = {}
) {
    val colors = TcThemeState.colors
    val typography = TcThemeState.typography
    val dimens = TcThemeState.dimens
    val isMalayalam = TcThemeState.isMalayalam

    var locationGranted by remember { mutableStateOf(false) }
    var notificationGranted by remember { mutableStateOf(false) }

    TcScaffold(
        topBar = {
            TcAppBar(
                title = if (isMalayalam) "അനുമതികൾ" else "Permissions"
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(
                    start = dimens.ScreenPaddingHorizontal,
                    end = dimens.ScreenPaddingHorizontal,
                    top = dimens.Gap10,
                    bottom = dimens.ScreenPaddingBottom
                ),
            verticalArrangement = Arrangement.spacedBy(dimens.Gap12)
        ) {
            Text(
                text = if (isMalayalam) "ജോലികൾ ശരിയായി ലഭിക്കാൻ ഇവ അനുവദിക്കുക" else "Allow these so jobs and tracking work reliably",
                style = typography.small,
                color = colors.muted
            )

            // Location Card
            TcCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(dimens.Gap12)
                ) {
                    TcIconBox(iconRes = R.drawable.ic_map)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isMalayalam) "കൃത്യമായ ലൊക്കേഷൻ" else "Precise location",
                            style = typography.btnSm,
                            color = colors.text
                        )
                        Text(
                            text = if (isMalayalam) "ട്രിപ്പ് സമയത്ത് മാത്രം" else "While on trips only",
                            style = typography.xsmall,
                            color = colors.muted
                        )
                    }
                    if (locationGranted) {
                        TcChip(text = "Allowed", kind = TcChipKind.Ok)
                    } else {
                        TcButton(
                            text = if (isMalayalam) "അനുവദിക്കുക" else "Allow",
                            onClick = {
                                locationGranted = true
                                onAllowLocationClick()
                            },
                            size = TcButtonSize.Sm
                        )
                    }
                }
            }

            // Notification Card
            TcCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(dimens.Gap12)
                ) {
                    TcIconBox(iconRes = R.drawable.ic_bell)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isMalayalam) "അടിയന്തര അറിയിപ്പുകൾ" else "Urgent job alerts",
                            style = typography.btnSm,
                            color = colors.text
                        )
                        Text(
                            text = if (isMalayalam) "ഫുൾ-സ്ക്രീൻ ശബ്ദം" else "Sound even when locked",
                            style = typography.xsmall,
                            color = colors.muted
                        )
                    }
                    if (notificationGranted) {
                        TcChip(text = "Allowed", kind = TcChipKind.Ok)
                    } else {
                        TcButton(
                            text = if (isMalayalam) "അനുവദിക്കുക" else "Allow",
                            onClick = {
                                notificationGranted = true
                                onAllowNotificationsClick()
                            },
                            size = TcButtonSize.Sm
                        )
                    }
                }
            }

            // Battery Guide Card
            TcCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(dimens.Gap12)
                ) {
                    TcIconBox(iconRes = R.drawable.ic_shield)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isMalayalam) "ബാറ്ററി ഒപ്റ്റിമൈസേഷൻ" else "Battery optimization",
                            style = typography.btnSm,
                            color = colors.text
                        )
                        Text(
                            text = if (isMalayalam) "ട്രാക്കിംഗ് തടസ്സപ്പെടാതിരിക്കാൻ" else "Unrestricted background",
                            style = typography.xsmall,
                            color = colors.muted
                        )
                    }
                    TcButton(
                        text = if (isMalayalam) "വഴികാട്ടി" else "Guide",
                        onClick = onOpenBatteryGuideClick,
                        variant = TcButtonVariant.Outline,
                        size = TcButtonSize.Sm
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            TcButton(
                text = if (isMalayalam) "തുടങ്ങുക" else "Continue to App",
                onClick = onNextClick,
                size = TcButtonSize.Lg,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
