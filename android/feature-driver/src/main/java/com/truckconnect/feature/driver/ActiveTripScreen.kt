package com.truckconnect.feature.driver

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.truckconnect.core.design.R
import com.truckconnect.core.design.component.TcButton
import com.truckconnect.core.design.component.TcButtonSize
import com.truckconnect.core.design.component.TcButtonVariant
import com.truckconnect.core.design.component.TcCard
import com.truckconnect.core.design.component.TcScreen
import com.truckconnect.core.design.component.TcTimeline
import com.truckconnect.core.design.component.TcTimelineStep
import com.truckconnect.core.design.theme.TcThemeState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Enterprise Active Trip Screen:
 * - Dynamic 4-milestone timeline:
 *   1. Reached pickup
 *   2. Loaded & Started driving (turns green when Start Trip is clicked)
 *   3. In transit (automatically turns green after start)
 *   4. Arrived at destination (turns green when driver clicks Arrived)
 *   5. Delivery Completed (turns green after OTP verification)
 * - Directly starts Android Foreground LocationTrackingService on launch to send 100% accurate live GPS telemetry to admin.
 */
@Composable
fun ActiveTripScreen(
    onBackClick: () -> Unit = {},
    onNavigateClick: () -> Unit = {},
    onPhoneClick: () -> Unit = {},
    onReachedDropClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val colors = TcThemeState.colors
    val typography = TcThemeState.typography
    val dimens = TcThemeState.dimens
    val isMalayalam = TcThemeState.isMalayalam

    // Milestone states:
    // 0: Loaded & Started driving
    // 1: In transit
    // 2: Reached drop location
    var tripStage by remember { mutableIntStateOf(1) } // Default: In transit (since started from JobDetails)

    LaunchedEffect(Unit) {
        // Safe start Foreground Location Service if permission is present
        try {
            val hasFine = androidx.core.content.ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
            val hasCoarse = androidx.core.content.ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED

            if (hasFine || hasCoarse) {
                val serviceIntent = Intent().apply {
                    setClassName(context.packageName, "com.truckconnect.app.service.LocationTrackingService")
                    action = "START_TRACKING"
                    putExtra("EXTRA_SHIPMENT_ID", "TC-2026-000131")
                }
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    context.startForegroundService(serviceIntent)
                } else {
                    context.startService(serviceIntent)
                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    val steps = listOf(
        TcTimelineStep(
            title = if (isMalayalam) "കയറ്റുന്ന സ്ഥലത്ത് എത്തി" else "Reached pickup",
            subtitle = "7:50 am",
            isPending = false
        ),
        TcTimelineStep(
            title = if (isMalayalam) "ലോഡ് കയറ്റി · യാത്ര തുടങ്ങി" else "Loaded & Started driving",
            subtitle = "8:45 am",
            isPending = false
        ),
        TcTimelineStep(
            title = if (isMalayalam) "യാത്രയിൽ (ലൈവ് ജിപിഎസ് സജീവം)" else "In transit (Live GPS active)",
            subtitle = if (tripStage >= 1) (if (isMalayalam) "ഇപ്പോൾ" else "Now") else null,
            isPending = tripStage < 1
        ),
        TcTimelineStep(
            title = if (isMalayalam) "ഇറക്കുന്ന സ്ഥലത്ത് എത്തി" else "Arrived at destination",
            subtitle = if (tripStage >= 2) "Just now" else null,
            isPending = tripStage < 2
        ),
        TcTimelineStep(
            title = if (isMalayalam) "എത്തിച്ചു · OTP സ്ഥിരീകരണം" else "Delivered · OTP verification",
            isPending = true
        )
    )

    TcScreen(
        title = "Palakkad → Kozhikode",
        onBackClick = onBackClick,
        isScrollable = true,
        pinnedBottomActions = {
            if (tripStage < 2) {
                TcButton(
                    onClick = {
                        tripStage = 2
                        Toast.makeText(
                            context,
                            if (isMalayalam) "ലൊക്കേഷനിൽ എത്തിച്ചേർന്നു എന്ന് അടയാളപ്പെടുത്തി" else "Marked: Arrived at destination",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
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
                        text = if (isMalayalam) "സ്ഥലത്ത് എത്തിച്ചേർന്നു" else "Arrived at destination",
                        modifier = Modifier.padding(start = dimens.Gap8)
                    )
                }
            } else {
                TcButton(
                    onClick = onReachedDropClick,
                    variant = TcButtonVariant.Primary,
                    size = TcButtonSize.Xl,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_shield),
                        contentDescription = null,
                        modifier = Modifier.size(dimens.Size26),
                        tint = colors.accentInk
                    )
                    Text(
                        text = if (isMalayalam) "OTP ചോദിച്ചു വാങ്ങുക & ഡെലിവർ ചെയ്യുക" else "Enter OTP & Complete Delivery",
                        modifier = Modifier.padding(start = dimens.Gap8)
                    )
                }
            }
        }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(dimens.Gap12)
        ) {
            // Milestone Timeline Card
            TcCard {
                TcTimeline(
                    steps = steps,
                    isDriver = true
                )
            }

            // Live Telemetry GPS Notice
            TcCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_globe),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = colors.ok
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isMalayalam) "ലൈവ് ജിപിഎസ് ട്രാക്കിംഗ് സജീവമാണ്" else "Live GPS Location Sharing Active",
                            style = typography.btnSm,
                            color = colors.text
                        )
                        Text(
                            text = if (isMalayalam)
                                "അഡ്മിൻ നിങ്ങളുടെ തത്സമയ ലൊക്കേഷൻ കൃത്യമായി നിരീക്ഷിക്കുന്നുണ്ട്"
                            else
                                "Your real-time coordinates are being sent continuously to fleet admin.",
                            style = typography.xsmall,
                            color = colors.muted
                        )
                    }
                }
            }

            // Action Row: Google Maps Nav & Call Dispatcher
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(dimens.Gap10)
            ) {
                TcButton(
                    onClick = {
                        try {
                            val gmmIntentUri = Uri.parse("google.navigation:q=11.2588,75.7804")
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
                        text = if (isMalayalam) "മാപ്പിൽ കാണിക്കുക" else "Google Maps",
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
