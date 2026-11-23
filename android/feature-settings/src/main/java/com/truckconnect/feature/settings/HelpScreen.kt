package com.truckconnect.feature.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.truckconnect.core.design.R
import com.truckconnect.core.design.component.TcAppBar
import com.truckconnect.core.design.component.TcButton
import com.truckconnect.core.design.component.TcCard
import com.truckconnect.core.design.component.TcHeroCard
import com.truckconnect.core.design.component.TcIconBox
import com.truckconnect.core.design.component.TcListRow
import com.truckconnect.core.design.component.TcScreen
import com.truckconnect.core.design.theme.TcDimens
import com.truckconnect.core.design.theme.TcThemeState

/**
 * Help & Guides Screen (S7 / X22)
 *
 * Provides instant live actions:
 * - Direct WhatsApp support intent
 * - Direct phone dialer intent
 * - Video guide player sheet
 * - Bilingual help article sheets (Job acceptance, Battery tracking, DPDP privacy)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val colors = TcThemeState.colors
    val typography = TcThemeState.typography
    val dimens = TcThemeState.dimens
    val isMalayalam = TcThemeState.isMalayalam

    var activeArticleTitle by remember { mutableStateOf<String?>(null) }
    var activeArticleBody by remember { mutableStateOf<String?>(null) }

    val helpItems = listOf(
        Triple(R.drawable.ic_whatsapp, "WhatsApp support", "Chat with the office"),
        Triple(R.drawable.ic_phone, if (isMalayalam) "ഓഫീസിനെ വിളിക്കുക" else "Call office", "+91 484 000 0000"),
        Triple(R.drawable.ic_doc, "How to accept a job", "3 simple steps"),
        Triple(R.drawable.ic_map, "Fix tracking on my phone", "Battery settings & background permission"),
        Triple(R.drawable.ic_shield, "Privacy & location", "DPDP Act 2023 consent")
    )

    fun handleHelpClick(label: String) {
        when {
            label.contains("WhatsApp") -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/914840000000?text=Hello%20TruckConnect%20Support"))
                context.startActivity(intent)
            }
            label.contains("Call") || label.contains("വിളിക്കുക") -> {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+914840000000"))
                context.startActivity(intent)
            }
            label.contains("accept a job") -> {
                activeArticleTitle = "How to accept a job"
                activeArticleBody = if (isMalayalam) {
                    "1. പുതിയ ജോബ് അലേർട്ട് വരുമ്പോൾ ഫോൺ റിംഗ് ചെയ്യും.\n2. വിവരങ്ങൾ വായിച്ച് 'സ്വീകരിക്കുക' (Accept) അമർത്തുക.\n3. പിക്കപ്പ് ലൊക്കേഷനിലേക്ക് എത്തിച്ചേരാൻ 'വഴി കാണിക്കുക' ഉപയോഗിക്കുക."
                } else {
                    "1. When a new shipment is assigned, your phone sounds an urgent alarm.\n2. Review pickup, goods weight, and consignee contact, then tap 'Accept'.\n3. Tap 'Navigate' to route directly to the pickup point."
                }
            }
            label.contains("tracking") -> {
                activeArticleTitle = "Fix background tracking (OEM Guide)"
                activeArticleBody = if (isMalayalam) {
                    "1. സെറ്റിങ്സ് -> ആപ്പ് മാനേജ്‌മെന്റ് -> TruckConnect തുറക്കുക.\n2. ബാറ്ററി യൂസേജ് 'Unrestricted' ആക്കുക.\n3. Auto-start അനുമതി നൽകുക."
                } else {
                    "1. Open Settings -> App Management -> TruckConnect.\n2. Set Battery Optimization to 'Don't Optimize' (Unrestricted).\n3. Enable Auto-start and background activity permissions (ColorOS, MIUI, OxygenOS)."
                }
            }
            label.contains("Privacy") -> {
                activeArticleTitle = "Privacy & Location Consent (DPDP)"
                activeArticleBody = if (isMalayalam) {
                    "നിങ്ങളുടെ ലൊക്കേഷൻ ട്രിപ്പ് സമയത്ത് മാത്രമേ ട്രാക്ക് ചെയ്യുകയുള്ളൂ. ട്രിപ്പ് പൂർത്തിയായ ശേഷം ട്രാക്കിംഗ് തനിയെ നിലയ്ക്കും."
                } else {
                    "Under DPDP Act 2023, your precise GPS location is only collected while an active trip is in progress. The foreground tracking service automatically halts when delivery is completed."
                }
            }
        }
    }

    TcScreen(
        title = if (isMalayalam) "സഹായം" else "Help",
        onBackClick = onBackClick,
        isScrollable = true
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(dimens.Gap10)
        ) {
            // Video Guide Hero Card
            TcHeroCard {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(color = colors.accentInk)
                        ) {
                            activeArticleTitle = "Video Guide"
                            activeArticleBody = "Playing 2-minute overview video: Driver assignment, milestone updates, GPS tracking, and proof of delivery."
                        },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(dimens.Gap12)
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_play),
                        contentDescription = null,
                        modifier = Modifier.size(dimens.Size30),
                        tint = colors.accentInk
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Watch the 2-minute guide",
                            style = typography.btnLg,
                            color = colors.accentInk
                        )
                        Text(
                            text = if (isMalayalam) "മലയാളത്തിൽ" else "In Malayalam",
                            style = typography.small,
                            color = colors.accentInk.copy(alpha = 0.85f)
                        )
                    }
                }
            }

            // Support List Card
            TcCard {
                Column {
                    helpItems.forEachIndexed { index, (iconRes, label, sub) ->
                        TcListRow(
                            isLast = index == helpItems.lastIndex,
                            onClick = { handleHelpClick(label) }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(dimens.Gap12)
                            ) {
                                TcIconBox(
                                    iconRes = iconRes,
                                    modifier = Modifier.size(dimens.Size36)
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
        }

        // Help Article Bottom Sheet (X22)
        if (activeArticleTitle != null) {
            ModalBottomSheet(
                onDismissRequest = {
                    activeArticleTitle = null
                    activeArticleBody = null
                },
                containerColor = colors.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = dimens.ScreenPaddingHorizontal)
                        .padding(bottom = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = activeArticleTitle.orEmpty(),
                        style = typography.h2,
                        color = colors.text
                    )

                    Text(
                        text = activeArticleBody.orEmpty(),
                        style = typography.body,
                        color = colors.muted
                    )

                    TcButton(
                        text = "Close",
                        onClick = {
                            activeArticleTitle = null
                            activeArticleBody = null
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
