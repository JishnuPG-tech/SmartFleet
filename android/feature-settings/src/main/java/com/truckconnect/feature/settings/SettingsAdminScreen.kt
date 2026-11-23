package com.truckconnect.feature.settings

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import com.truckconnect.core.design.R
import com.truckconnect.core.design.component.TcAppBar
import com.truckconnect.core.design.component.TcAvatar
import com.truckconnect.core.design.component.TcCard
import com.truckconnect.core.design.component.TcIconBox
import com.truckconnect.core.design.component.TcListRow
import com.truckconnect.core.design.component.TcScreen
import com.truckconnect.core.design.theme.TcDimens
import com.truckconnect.core.design.theme.TcThemeState

/**
 * Admin Settings Screen (S1)
 *
 * Fixes:
 * - BUG-01 / BUG-02: Wrapped in TcScaffold for system bars and cutout insets.
 * - BUG-07: TcAvatar displays "AK" initials via initials() helper.
 * - BUG-08: Uses verified absolute VectorDrawable icons (ic_palette, ic_bell, ic_lock, ic_help).
 * - BUG-06: Every row triggers onSettingClick(label).
 * - BUG-15: Footnote text is centered at the bottom of the screen.
 */
@Composable
fun SettingsAdminScreen(
    onBackClick: () -> Unit = {},
    onProfileEditClick: () -> Unit = {},
    onSettingClick: (String) -> Unit = {}
) {
    val colors = TcThemeState.colors
    val typography = TcThemeState.typography
    val dimens = TcThemeState.dimens

    val settingsItems = listOf(
        Triple(R.drawable.ic_user, "Profile", "Name, phone, email"),
        Triple(R.drawable.ic_palette, "Theme", "Emerald · Auto"),
        Triple(R.drawable.ic_globe, "Language", "English"),
        Triple(R.drawable.ic_bell, "Notifications", "Alerts & sounds"),
        Triple(R.drawable.ic_lock, "Security", "App lock, sessions"),
        Triple(R.drawable.ic_help, "Help", "Guides & support"),
        Triple(R.drawable.ic_tag, "App version", "v2.1.0")
    )

    TcScreen(
        title = "Settings",
        onBackClick = onBackClick,
        isScrollable = true
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(dimens.Gap12)
        ) {
            // Profile Card (BUG-07 initials verification)
            TcCard(onClick = onProfileEditClick) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(dimens.Gap10)
                ) {
                    TcAvatar(text = "Anil Kumar")
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Anil Kumar",
                            style = typography.btnSm,
                            color = colors.text
                        )
                        Text(
                            text = "anil@truckconnect.in",
                            style = typography.xsmall,
                            color = colors.muted
                        )
                    }
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_edit),
                        contentDescription = "Edit",
                        modifier = Modifier.size(dimens.Size18),
                        tint = colors.text2
                    )
                }
            }

            // Settings List Card (BUG-06 wire click handlers)
            TcCard {
                Column {
                    settingsItems.forEachIndexed { index, (iconRes, label, sub) ->
                        TcListRow(
                            isLast = index == settingsItems.lastIndex,
                            onClick = { onSettingClick(label) }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(dimens.Gap12)
                            ) {
                                TcIconBox(
                                    iconRes = iconRes,
                                    modifier = Modifier.size(dimens.Size34)
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = label,
                                        style = typography.btnSm,
                                        color = colors.text
                                    )
                                    Text(
                                        text = sub,
                                        style = typography.xsmall,
                                        color = colors.muted
                                    )
                                }
                                Icon(
                                    imageVector = ImageVector.vectorResource(R.drawable.ic_chev),
                                    contentDescription = null,
                                    modifier = Modifier.size(dimens.Size16),
                                    tint = colors.text2
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Footnote (BUG-15 centered at bottom)
            Text(
                text = "Theme choice syncs to your profile",
                style = typography.note,
                color = colors.muted,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
