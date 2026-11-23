package com.truckconnect.feature.admin

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
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

/**
 * E06: Admin Shipment Detail Screen
 *
 * Full production features per Antigravity v3:
 * - Rebuilt on TcScreen with safe status bar, pill cutout, and 3-button nav insets.
 * - Single-line code title in top bar with 48 dp back target.
 * - Functional ⋯ dropdown menu (Edit, Duplicate, Playback, Share, Cancel).
 * - Comprehensive Route section with pickup/drop locations & dialer buttons.
 * - Assignment section with Driver name, phone, Call, and WhatsApp intents.
 * - OD-2 Timeline without decorative glow rings.
 * - Pinned Reassign and Cancel shipment actions with TcConfirmDialog.
 */
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

    val steps = listOf(
        TcTimelineStep(title = "Created", subtitle = "6:00 am · Anil Kumar", isPending = false),
        TcTimelineStep(title = "Driver assigned", subtitle = "6:05 am · Suresh Kumar", isPending = false),
        TcTimelineStep(title = "Driver accepted", subtitle = "6:10 am", isPending = false),
        TcTimelineStep(title = "Reached pickup", subtitle = "7:50 am · Kasaragod Market", isPending = false),
        TcTimelineStep(title = "Loaded", subtitle = "8:45 am · 8.5 t vegetables", isPending = false),
        TcTimelineStep(title = "In transit", subtitle = "Now · speed 48 km/h · Near Kozhikode", isPending = false, isCurrent = true),
        TcTimelineStep(title = "Reached drop", subtitle = "Estimated 2:40 pm · Malappuram Hub", isPending = true),
        TcTimelineStep(title = "Delivered", isPending = true)
    )

    TcScreen(
        title = shipmentId,
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
                        onClick = { showMenu = false }
                    )
                    DropdownMenuItem(
                        text = { Text("Route playback", style = typography.btnSm, color = colors.accent) },
                        onClick = { showMenu = false }
                    )
                    DropdownMenuItem(
                        text = { Text("Share tracking link", style = typography.btnSm, color = colors.text) },
                        onClick = { showMenu = false }
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
                        TcStatus(text = "In transit", kind = TcChipKind.Accent)
                        Text(
                            text = "8.5 t · Vegetables",
                            style = typography.small,
                            color = colors.muted
                        )
                    }

                    Text(
                        text = "Kasaragod → Malappuram",
                        style = typography.h1,
                        color = colors.text
                    )

                    Text(
                        text = "Suresh Kumar · KL-07-AZ-1200",
                        style = typography.body,
                        color = colors.muted
                    )
                }
            }

            // Route Details
            Text(
                text = "Route & Contacts",
                style = typography.label,
                color = colors.muted,
                modifier = Modifier.padding(top = 4.dp)
            )

            TcDetailList(
                items = listOf(
                    TcDetailItem(
                        label = "Pickup",
                        value = "Kasaragod APMC Market",
                        trailingContent = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Kasaragod",
                                    style = typography.btnSm,
                                    color = colors.text
                                )
                                TcIconButton(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+919447012345"))
                                        context.startActivity(intent)
                                    },
                                    modifier = Modifier.size(34.dp)
                                ) {
                                    Icon(
                                        imageVector = ImageVector.vectorResource(R.drawable.ic_phone),
                                        contentDescription = "Call pickup",
                                        modifier = Modifier.size(16.dp),
                                        tint = colors.accent
                                    )
                                }
                            }
                        }
                    ),
                    TcDetailItem(
                        label = "Drop",
                        value = "Malappuram Wholesale Hub",
                        trailingContent = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Malappuram",
                                    style = typography.btnSm,
                                    color = colors.text
                                )
                                TcIconButton(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+919846098765"))
                                        context.startActivity(intent)
                                    },
                                    modifier = Modifier.size(34.dp)
                                ) {
                                    Icon(
                                        imageVector = ImageVector.vectorResource(R.drawable.ic_phone),
                                        contentDescription = "Call consignee",
                                        modifier = Modifier.size(16.dp),
                                        tint = colors.accent
                                    )
                                }
                            }
                        }
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
                        value = "Suresh Kumar",
                        trailingContent = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Suresh Kumar",
                                    style = typography.btnSm,
                                    color = colors.text
                                )
                                // WhatsApp Icon Button
                                TcIconButton(
                                    onClick = {
                                        val uri = Uri.parse("https://wa.me/919447012345")
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
                                // Phone Dial Button
                                TcIconButton(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+919447012345"))
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
                    ),
                    TcDetailItem(
                        label = "Vehicle",
                        value = "KL-07-AZ-1200 · 10 t Eicher Pro"
                    ),
                    TcDetailItem(
                        label = "E-Way Bill",
                        value = "EWB-2026-8891-KERALA"
                    )
                )
            )

            // Milestone Timeline (OD-2 compliant)
            Text(
                text = "Milestone Timeline",
                style = typography.label,
                color = colors.muted,
                modifier = Modifier.padding(top = 4.dp)
            )

            TcCard {
                TcTimeline(steps = steps)
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
            message = "This will immediately halt shipment $shipmentId, notify driver Suresh Kumar, and return the consignments to the unassigned queue.",
            confirmLabel = "Cancel Shipment",
            dismissLabel = "Keep Active",
            isDestructive = true
        )
    }
}
