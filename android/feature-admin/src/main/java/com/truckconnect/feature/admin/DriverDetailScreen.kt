package com.truckconnect.feature.admin

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.truckconnect.core.design.R
import com.truckconnect.core.design.component.TcAvatar
import com.truckconnect.core.design.component.TcCard
import com.truckconnect.core.design.component.TcChipKind
import com.truckconnect.core.design.component.TcDetailItem
import com.truckconnect.core.design.component.TcDetailList
import com.truckconnect.core.design.component.TcIconButton
import com.truckconnect.core.design.component.TcScreen
import com.truckconnect.core.design.component.TcStatus
import com.truckconnect.core.design.theme.TcThemeState

data class AdminDriverInfo(
    val name: String,
    val initials: String,
    val phone: String,
    val status: String,
    val statusKind: TcChipKind,
    val trips: String,
    val onTime: String,
    val rating: String,
    val vehicle: String,
    val licence: String,
    val registeredDevice: String,
    val appVersion: String,
    val lastTelemetry: String
)

object DriverDetailsRepository {
    fun getDriver(name: String): AdminDriverInfo {
        return when {
            name.contains("Rajan", ignoreCase = true) -> AdminDriverInfo(
                name = "Rajan Nair",
                initials = "RN",
                phone = "+91 98470 55501",
                status = "On trip",
                statusKind = TcChipKind.Accent,
                trips = "38",
                onTime = "91%",
                rating = "4.8",
                vehicle = "KL-08-BX-1571 · 12 t Tata Ultra",
                licence = "HGMV · exp 14 Mar 2029",
                registeredDevice = "Redmi Note 12 5G",
                appVersion = "v3.0.0 · Active",
                lastTelemetry = "1 min ago · Near Thrissur"
            )
            name.contains("Venugopal", ignoreCase = true) -> AdminDriverInfo(
                name = "Venugopal R",
                initials = "VR",
                phone = "+91 94471 22334",
                status = "Available",
                statusKind = TcChipKind.Ok,
                trips = "64",
                onTime = "96%",
                rating = "4.9",
                vehicle = "KL-02-HR-3797 · 16 t BharatBenz",
                licence = "HGMV · exp 22 Nov 2030",
                registeredDevice = "OnePlus Nord CE 3",
                appVersion = "v3.0.0 · Active",
                lastTelemetry = "5 min ago · At Munnar Depot"
            )
            name.contains("Shaji", ignoreCase = true) -> AdminDriverInfo(
                name = "Shaji Varghese",
                initials = "SV",
                phone = "+91 94472 33445",
                status = "On leave",
                statusKind = TcChipKind.Warn,
                trips = "52",
                onTime = "79%",
                rating = "4.5",
                vehicle = "KL-04-KP-4539 · 10 t Eicher",
                licence = "HGMV · exp 18 Jan 2027",
                registeredDevice = "Vivo Y200 5G",
                appVersion = "v3.0.0 · Standby",
                lastTelemetry = "Yesterday · Kochi Home Base"
            )
            name.contains("Haridas", ignoreCase = true) -> AdminDriverInfo(
                name = "Haridas M",
                initials = "HM",
                phone = "+91 94473 44556",
                status = "Offline",
                statusKind = TcChipKind.Neutral,
                trips = "41",
                onTime = "88%",
                rating = "4.7",
                vehicle = "KL-05-LN-4910 · 10 t Ashok Leyland",
                licence = "HGMV · exp 09 Sep 2028",
                registeredDevice = "Realme 11 Pro",
                appVersion = "v3.0.0 · Offline",
                lastTelemetry = "4 hours ago · Palakkad Terminal"
            )
            else -> AdminDriverInfo(
                name = "Suresh Kumar",
                initials = "SK",
                phone = "+91 94470 12891",
                status = "On trip",
                statusKind = TcChipKind.Accent,
                trips = "49",
                onTime = "82%",
                rating = "4.6",
                vehicle = "KL-07-AZ-1200 · 10 t Eicher Pro",
                licence = "HGMV · exp 14 Aug 2029",
                registeredDevice = "Samsung Galaxy A14",
                appVersion = "v3.0.0 · Active",
                lastTelemetry = "2 min ago · Near Kozhikode"
            )
        }
    }
}

@Composable
fun DriverDetailScreen(
    onBackClick: () -> Unit = {},
    driverName: String = "Suresh Kumar"
) {
    val context = LocalContext.current
    val colors = TcThemeState.colors
    val typography = TcThemeState.typography
    val dimens = TcThemeState.dimens

    val driver = remember(driverName) {
        DriverDetailsRepository.getDriver(driverName)
    }

    TcScreen(
        title = "Driver",
        onBackClick = onBackClick,
        isScrollable = true
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Profile Header
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                TcAvatar(text = driver.initials, isLarge = true)

                Text(
                    text = driver.name,
                    style = typography.btnLg,
                    color = colors.text
                )

                // Contact Row with WhatsApp & Call intents
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = driver.phone,
                        style = typography.body,
                        color = colors.muted
                    )

                    TcIconButton(
                        onClick = {
                            val cleanPhone = driver.phone.replace("[^0-9]".toRegex(), "")
                            val uri = Uri.parse("https://wa.me/$cleanPhone")
                            val intent = Intent(Intent.ACTION_VIEW, uri)
                            context.startActivity(intent)
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_whatsapp),
                            contentDescription = "WhatsApp",
                            modifier = Modifier.size(15.dp),
                            tint = colors.ok
                        )
                    }

                    TcIconButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${driver.phone}"))
                            context.startActivity(intent)
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_phone),
                            contentDescription = "Call",
                            modifier = Modifier.size(15.dp),
                            tint = colors.accent
                        )
                    }
                }

                TcStatus(
                    text = driver.status,
                    kind = driver.statusKind,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // 3-col KPI Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TcCard(modifier = Modifier.weight(1f)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Trips", style = typography.xsmall, color = colors.muted)
                        Text(text = driver.trips, style = typography.btnLg, color = colors.text)
                    }
                }

                TcCard(modifier = Modifier.weight(1f)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "On-time", style = typography.xsmall, color = colors.muted)
                        Text(text = driver.onTime, style = typography.btnLg, color = colors.text)
                    }
                }

                TcCard(modifier = Modifier.weight(1f)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Rating", style = typography.xsmall, color = colors.muted)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(text = driver.rating, style = typography.btnLg, color = colors.text)
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.ic_star),
                                contentDescription = "Rating star",
                                modifier = Modifier.size(14.dp),
                                tint = colors.warn
                            )
                        }
                    }
                }
            }

            // Details List
            Text(
                text = "Vehicle & Credentials",
                style = typography.label,
                color = colors.muted,
                modifier = Modifier.padding(top = 4.dp)
            )

            TcDetailList(
                items = listOf(
                    TcDetailItem(
                        label = "Vehicle",
                        value = driver.vehicle
                    ),
                    TcDetailItem(
                        label = "Licence",
                        value = driver.licence,
                        trailingContent = {
                            TcStatus(text = "Valid", kind = TcChipKind.Ok)
                        }
                    ),
                    TcDetailItem(
                        label = "Registered Device",
                        value = driver.registeredDevice
                    ),
                    TcDetailItem(
                        label = "App Version",
                        value = driver.appVersion
                    ),
                    TcDetailItem(
                        label = "Last Telemetry",
                        value = driver.lastTelemetry
                    )
                )
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
