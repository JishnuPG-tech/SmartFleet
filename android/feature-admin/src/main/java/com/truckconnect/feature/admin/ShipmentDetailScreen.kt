package com.truckconnect.feature.admin

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.truckconnect.core.design.R
import com.truckconnect.core.design.component.TcButton
import com.truckconnect.core.design.component.TcButtonSize
import com.truckconnect.core.design.component.TcButtonVariant
import com.truckconnect.core.design.component.TcCard
import com.truckconnect.core.design.component.TcChipKind
import com.truckconnect.core.design.component.TcConfirmDialog
import com.truckconnect.core.design.component.TcDetailItem
import com.truckconnect.core.design.component.TcDetailList
import com.truckconnect.core.design.component.TcIconButton
import com.truckconnect.core.design.component.TcScreen
import com.truckconnect.core.design.component.TcStatus
import com.truckconnect.core.design.component.TcTimeline
import com.truckconnect.core.design.component.TcTimelineStep
import com.truckconnect.core.design.theme.TcThemeState

data class AdminShipmentInfo(
    val id: String,
    val route: String,
    val status: String,
    val statusKind: TcChipKind,
    val goods: String,
    val pickup: String,
    val drop: String,
    val driver: String,
    val driverPhone: String,
    val vehicle: String,
    val eWayBill: String,
    val steps: List<TcTimelineStep>
)

object ShipmentRepository {
    fun getShipment(id: String): AdminShipmentInfo {
        return when (id) {
            "TC-2026-000138" -> AdminShipmentInfo(
                id = "TC-2026-000138",
                route = "Idukki → Kasaragod",
                status = "Assigned",
                statusKind = TcChipKind.Info,
                goods = "14.0 t · Spices & Tea",
                pickup = "Munnar Spice Depot, Gate 1",
                drop = "Kasaragod Central Hub",
                driver = "Venugopal R",
                driverPhone = "+91 94471 22334",
                vehicle = "KL-02-HR-3797 · 16 t BharatBenz",
                eWayBill = "EWB-2026-4412-IDUKKI",
                steps = listOf(
                    TcTimelineStep(title = "Created", subtitle = "Today 5:30 am", isPending = false),
                    TcTimelineStep(title = "Driver assigned", subtitle = "6:00 am · Venugopal R", isPending = false, isCurrent = true),
                    TcTimelineStep(title = "Driver accepted", subtitle = "Awaiting acceptance", isPending = true),
                    TcTimelineStep(title = "Reached pickup", subtitle = "Estimated 7:30 am", isPending = true),
                    TcTimelineStep(title = "In transit", isPending = true),
                    TcTimelineStep(title = "Delivered", isPending = true)
                )
            )
            "TC-2026-000141" -> AdminShipmentInfo(
                id = "TC-2026-000141",
                route = "Malappuram → Wayanad",
                status = "Rejected",
                statusKind = TcChipKind.Crit,
                goods = "6.0 t · Hardware",
                pickup = "Manjeri Industrial Estate",
                drop = "Kalpetta Hardware Yard",
                driver = "Unassigned (Driver rejected)",
                driverPhone = "",
                vehicle = "Awaiting vehicle reassignment",
                eWayBill = "EWB-2026-9931-MALAPPURAM",
                steps = listOf(
                    TcTimelineStep(title = "Created", subtitle = "Today 5:00 am", isPending = false),
                    TcTimelineStep(title = "Driver assigned", subtitle = "5:15 am · Shaji Varghese", isPending = false),
                    TcTimelineStep(title = "Rejected by driver", subtitle = "5:45 am · Driver on medical leave", isPending = false, isCurrent = true),
                    TcTimelineStep(title = "Reassign required", subtitle = "Pending dispatcher action", isPending = true),
                    TcTimelineStep(title = "Reached pickup", isPending = true),
                    TcTimelineStep(title = "Delivered", isPending = true)
                )
            )
            "TC-2026-000133" -> AdminShipmentInfo(
                id = "TC-2026-000133",
                route = "Thrissur → Kannur",
                status = "Loaded",
                statusKind = TcChipKind.Accent,
                goods = "12.0 t · FMCG & Packaged Foods",
                pickup = "Thrissur Logistics Park",
                drop = "Kannur Wholesale Depot",
                driver = "Babu Thomas",
                driverPhone = "+91 94472 33445",
                vehicle = "KL-08-BX-1571 · 12 t Tata Ultra",
                eWayBill = "EWB-2026-7721-THRISSUR",
                steps = listOf(
                    TcTimelineStep(title = "Created", subtitle = "5:15 am", isPending = false),
                    TcTimelineStep(title = "Driver assigned", subtitle = "5:30 am · Babu Thomas", isPending = false),
                    TcTimelineStep(title = "Driver accepted", subtitle = "5:40 am", isPending = false),
                    TcTimelineStep(title = "Reached pickup", subtitle = "6:30 am · Thrissur Park", isPending = false),
                    TcTimelineStep(title = "Loaded", subtitle = "7:10 am · 12 t FMCG", isPending = false, isCurrent = true),
                    TcTimelineStep(title = "In transit", subtitle = "Estimated 7:45 am start", isPending = true),
                    TcTimelineStep(title = "Delivered", isPending = true)
                )
            )
            "TC-2026-000125" -> AdminShipmentInfo(
                id = "TC-2026-000125",
                route = "Kochi → Palakkad",
                status = "Delivered",
                statusKind = TcChipKind.Ok,
                goods = "10.0 t · Electronics & Textiles",
                pickup = "Kochi Port Container Terminal",
                drop = "Palakkad Industrial Gate 3",
                driver = "Haridas M",
                driverPhone = "+91 94473 44556",
                vehicle = "KL-05-LN-4910 · 10 t Eicher",
                eWayBill = "EWB-2026-3329-KOCHI",
                steps = listOf(
                    TcTimelineStep(title = "Created", subtitle = "Yesterday 4:00 am", isPending = false),
                    TcTimelineStep(title = "Driver assigned", subtitle = "Yesterday 4:15 am", isPending = false),
                    TcTimelineStep(title = "Driver accepted", subtitle = "Yesterday 4:20 am", isPending = false),
                    TcTimelineStep(title = "Reached pickup", subtitle = "Yesterday 5:30 am", isPending = false),
                    TcTimelineStep(title = "Loaded", subtitle = "Yesterday 6:45 am", isPending = false),
                    TcTimelineStep(title = "In transit", subtitle = "Yesterday 7:00 am", isPending = false),
                    TcTimelineStep(title = "Reached drop", subtitle = "Yesterday 1:30 pm", isPending = false),
                    TcTimelineStep(title = "Delivered & Verified via OTP", subtitle = "Yesterday 2:15 pm", isPending = false, isCurrent = true)
                )
            )
            else -> AdminShipmentInfo(
                id = "TC-2026-000131",
                route = "Kasaragod → Malappuram",
                status = "In transit",
                statusKind = TcChipKind.Accent,
                goods = "8.5 t · Vegetables",
                pickup = "Kasaragod APMC Market",
                drop = "Malappuram Wholesale Hub",
                driver = "Suresh Kumar",
                driverPhone = "+91 94470 12891",
                vehicle = "KL-07-AZ-1200 · 10 t Eicher Pro",
                eWayBill = "EWB-2026-8891-KERALA",
                steps = listOf(
                    TcTimelineStep(title = "Created", subtitle = "6:00 am · Anil Kumar", isPending = false),
                    TcTimelineStep(title = "Driver assigned", subtitle = "6:05 am · Suresh Kumar", isPending = false),
                    TcTimelineStep(title = "Driver accepted", subtitle = "6:10 am", isPending = false),
                    TcTimelineStep(title = "Reached pickup", subtitle = "7:50 am · Kasaragod Market", isPending = false),
                    TcTimelineStep(title = "Loaded", subtitle = "8:45 am · 8.5 t vegetables", isPending = false),
                    TcTimelineStep(title = "In transit", subtitle = "Now · speed 48 km/h · Near Kozhikode", isPending = false, isCurrent = true),
                    TcTimelineStep(title = "Reached drop", subtitle = "Estimated 2:40 pm · Malappuram Hub", isPending = true),
                    TcTimelineStep(title = "Delivered", isPending = true)
                )
            )
        }
    }
}

@Composable
fun ShipmentDetailScreen(
    onBackClick: () -> Unit = {},
    onReassignClick: () -> Unit = {},
    onCancelShipmentClick: () -> Unit = {},
    shipmentId: String = "TC-2026-000131"
) {
    val context = LocalContext.current
    val colors = TcThemeState.colors
    val typography = TcThemeState.typography
    val dimens = TcThemeState.dimens

    var showMenu by remember { mutableStateOf(false) }
    var showCancelDialog by remember { mutableStateOf(false) }

    val shipment = remember(shipmentId) {
        ShipmentRepository.getShipment(shipmentId)
    }

    TcScreen(
        title = shipment.id,
        onBackClick = onBackClick,
        isScrollable = true,
        topBarActions = {
            Box {
                TcIconButton(onClick = { showMenu = true }) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_more),
                        contentDescription = "More actions",
                        modifier = Modifier.size(dimens.IconSizeDefault),
                        tint = colors.text
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.background(colors.surface)
                ) {
                    DropdownMenuItem(
                        text = { Text("Edit details", style = typography.btnSm, color = colors.text) },
                        onClick = { showMenu = false }
                    )
                    DropdownMenuItem(
                        text = { Text("Duplicate shipment", style = typography.btnSm, color = colors.text) },
                        onClick = {
                            showMenu = false
                            Toast.makeText(context, "Shipment ${shipment.id} duplicated as draft", Toast.LENGTH_SHORT).show()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Share tracking link", style = typography.btnSm, color = colors.text) },
                        onClick = {
                            showMenu = false
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, "Track shipment ${shipment.id} (${shipment.route}): https://truckconnect.in/track/${shipment.id}")
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, "Share tracking link")
                            context.startActivity(shareIntent)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Cancel shipment", style = typography.btnSm, color = colors.crit) },
                        onClick = {
                            showMenu = false
                            showCancelDialog = true
                        }
                    )
                }
            }
        },
        pinnedBottomActions = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TcButton(
                    onClick = onReassignClick,
                    variant = TcButtonVariant.Outline,
                    size = TcButtonSize.Lg,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Reassign driver")
                }

                TcButton(
                    onClick = { showCancelDialog = true },
                    variant = TcButtonVariant.CritOutline,
                    size = TcButtonSize.Lg,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Cancel shipment")
                }
            }
        }
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Summary Card
            TcCard {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TcStatus(text = shipment.status, kind = shipment.statusKind)
                        Text(
                            text = shipment.goods,
                            style = typography.small,
                            color = colors.muted
                        )
                    }

                    Text(
                        text = shipment.route,
                        style = typography.h1,
                        color = colors.text
                    )

                    Text(
                        text = "${shipment.driver} · ${shipment.vehicle}",
                        style = typography.body,
                        color = colors.muted
                    )

                    // 1-Click Google Maps Track Button
                    TcButton(
                        onClick = {
                            val uri = Uri.parse("https://www.google.com/maps/dir/?api=1&origin=${Uri.encode(shipment.pickup)}&destination=${Uri.encode(shipment.drop)}&travelmode=driving")
                            val mapIntent = Intent(Intent.ACTION_VIEW, uri).apply {
                                setPackage("com.google.android.apps.maps")
                            }
                            try {
                                context.startActivity(mapIntent)
                            } catch (e: Exception) {
                                context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                            }
                        },
                        variant = TcButtonVariant.Outline,
                        size = TcButtonSize.Sm,
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_nav),
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = colors.accent
                        )
                        Text(
                            text = "Track Route on Google Maps",
                            style = typography.btnSm,
                            modifier = Modifier.padding(start = 6.dp)
                        )
                    }
                }
            }

            // Route Details
            Text(
                text = "Route",
                style = typography.label,
                color = colors.muted,
                modifier = Modifier.padding(top = 4.dp)
            )

            TcDetailList(
                items = listOf(
                    TcDetailItem(
                        label = "Pickup",
                        value = shipment.pickup
                    ),
                    TcDetailItem(
                        label = "Drop",
                        value = shipment.drop
                    )
                )
            )

            // Assigned Driver & Vehicle
            Text(
                text = "Assignment",
                style = typography.label,
                color = colors.muted,
                modifier = Modifier.padding(top = 4.dp)
            )

            TcDetailList(
                items = listOf(
                    TcDetailItem(
                        label = "Driver",
                        value = shipment.driver,
                        trailingContent = if (shipment.driverPhone.isNotBlank()) {
                            {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = shipment.driver,
                                        style = typography.btnSm,
                                        color = colors.text
                                    )
                                    TcIconButton(
                                        onClick = {
                                            val cleanPhone = shipment.driverPhone.replace("[^0-9]".toRegex(), "")
                                            val uri = Uri.parse("https://wa.me/$cleanPhone")
                                            val intent = Intent(Intent.ACTION_VIEW, uri)
                                            context.startActivity(intent)
                                        },
                                        modifier = Modifier.size(34.dp)
                                    ) {
                                        Icon(
                                            imageVector = ImageVector.vectorResource(R.drawable.ic_whatsapp),
                                            contentDescription = "WhatsApp driver",
                                            modifier = Modifier.size(16.dp),
                                            tint = colors.ok
                                        )
                                    }
                                    TcIconButton(
                                        onClick = {
                                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${shipment.driverPhone}"))
                                            context.startActivity(intent)
                                        },
                                        modifier = Modifier.size(34.dp)
                                    ) {
                                        Icon(
                                            imageVector = ImageVector.vectorResource(R.drawable.ic_phone),
                                            contentDescription = "Call driver",
                                            modifier = Modifier.size(16.dp),
                                            tint = colors.accent
                                        )
                                    }
                                }
                            }
                        } else null
                    ),
                    TcDetailItem(
                        label = "Vehicle",
                        value = shipment.vehicle
                    ),
                    TcDetailItem(
                        label = "E-Way Bill",
                        value = shipment.eWayBill
                    )
                )
            )

            // Milestone Timeline
            Text(
                text = "Milestone Timeline",
                style = typography.label,
                color = colors.muted,
                modifier = Modifier.padding(top = 4.dp)
            )

            TcCard {
                TcTimeline(steps = shipment.steps)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Cancel Confirmation Dialog
        TcConfirmDialog(
            isOpen = showCancelDialog,
            onDismiss = { showCancelDialog = false },
            onConfirm = {
                showCancelDialog = false
                onCancelShipmentClick()
            },
            title = "Cancel Shipment?",
            message = "This will immediately halt shipment ${shipment.id}, notify driver ${shipment.driver}, and return consignments to the unassigned queue.",
            confirmLabel = "Cancel Shipment",
            dismissLabel = "Keep Active",
            isDestructive = true
        )
    }
}
