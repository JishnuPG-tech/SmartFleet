package com.truckconnect.feature.driver

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import com.truckconnect.core.design.R
import com.truckconnect.core.design.component.TcButton
import com.truckconnect.core.design.component.TcButtonSize
import com.truckconnect.core.design.component.TcButtonVariant
import com.truckconnect.core.design.component.TcCard
import com.truckconnect.core.design.component.TcScreen
import com.truckconnect.core.design.component.TcTimeline
import com.truckconnect.core.design.component.TcTimelineStep
import com.truckconnect.core.design.theme.TcThemeState

@Composable
fun ActiveTripScreen(
    onBackClick: () -> Unit = {},
    onNavigateClick: () -> Unit = {},
    onPhoneClick: () -> Unit = {},
    onReachedDropClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val colors = TcThemeState.colors
    val dimens = TcThemeState.dimens
    val isMalayalam = TcThemeState.isMalayalam

    val steps = listOf(
        TcTimelineStep(
            title = if (isMalayalam) "കയറ്റുന്ന സ്ഥലത്ത് എത്തി" else "Reached pickup",
            subtitle = "7:50 am",
            isPending = false
        ),
        TcTimelineStep(
            title = if (isMalayalam) "ലോഡ് കയറ്റി" else "Loaded",
            subtitle = "8:45 am",
            isPending = false
        ),
        TcTimelineStep(
            title = if (isMalayalam) "യാത്രയിൽ" else "In transit",
            subtitle = if (isMalayalam) "ഇപ്പോൾ" else "Now",
            isPending = false
        ),
        TcTimelineStep(
            title = if (isMalayalam) "ഇറക്കുന്ന സ്ഥലത്ത് എത്തി" else "Reached drop",
            isPending = true
        ),
        TcTimelineStep(
            title = if (isMalayalam) "എത്തിച്ചു" else "Delivered",
            isPending = true
        )
    )

    TcScreen(
        title = "Kasaragod → Malappuram",
        onBackClick = onBackClick,
        isScrollable = true,
        pinnedBottomActions = {
            TcButton(
                onClick = onReachedDropClick,
                variant = TcButtonVariant.Primary,
                size = TcButtonSize.Xl,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_check),
                    contentDescription = null,
                    modifier = Modifier.size(dimens.Size26),
                    tint = colors.accentInk
                )
                Text(
                    text = if (isMalayalam) "ഇറക്കുന്ന സ്ഥലത്ത് എത്തി" else "Reached drop location",
                    modifier = Modifier.padding(start = dimens.Gap8)
                )
            }
        }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(dimens.Gap12)
        ) {
            // Milestone Timeline Card (OD-2 clean nodes, no glowing halos)
            TcCard {
                TcTimeline(
                    steps = steps,
                    isDriver = true
                )
            }

            // Action Row: Navigate & Call Dispatcher
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(dimens.Gap10)
            ) {
                TcButton(
                    onClick = {
                        try {
                            val gmmIntentUri = Uri.parse("google.navigation:q=11.07,76.07")
                            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
                                setPackage("com.google.android.apps.maps")
                            }
                            context.startActivity(mapIntent)
                        } catch (e: Exception) {
                            onNavigateClick()
                        }
                    },
                    variant = TcButtonVariant.Outline,
                    size = TcButtonSize.Lg,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_nav),
                        contentDescription = null,
                        modifier = Modifier.size(dimens.Size22),
                        tint = colors.text
                    )
                    Text(
                        text = if (isMalayalam) "മാപ്പിൽ കാണിക്കുക" else "Navigate",
                        modifier = Modifier.padding(start = dimens.Gap6)
                    )
                }

                TcButton(
                    onClick = {
                        try {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+919876543210"))
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            onPhoneClick()
                        }
                    },
                    variant = TcButtonVariant.Outline,
                    size = TcButtonSize.Lg,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_phone),
                        contentDescription = null,
                        modifier = Modifier.size(dimens.Size22),
                        tint = colors.text
                    )
                    Text(
                        text = if (isMalayalam) "വിളിക്കുക" else "Call dispatch",
                        modifier = Modifier.padding(start = dimens.Gap6)
                    )
                }
            }
        }
    }
}
