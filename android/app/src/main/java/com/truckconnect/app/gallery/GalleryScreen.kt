package com.truckconnect.app.gallery

import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Density
import com.truckconnect.core.design.R
import com.truckconnect.core.design.component.TcAppBar
import com.truckconnect.core.design.component.TcButton
import com.truckconnect.core.design.component.TcButtonSize
import com.truckconnect.core.design.component.TcButtonVariant
import com.truckconnect.core.design.component.TcCard
import com.truckconnect.core.design.component.TcChip
import com.truckconnect.core.design.component.TcChipKind
import com.truckconnect.core.design.component.TcSegmented
import com.truckconnect.core.design.theme.TcDimens
import com.truckconnect.core.design.theme.TcMode
import com.truckconnect.core.design.theme.TcShapes
import com.truckconnect.core.design.theme.TcTheme
import com.truckconnect.core.design.theme.TcThemeState
import com.truckconnect.core.design.theme.TruckConnectTheme
import com.truckconnect.feature.admin.AlertsCentreScreen
import com.truckconnect.feature.admin.AppVersionsScreen
import com.truckconnect.feature.admin.AssignDriverSheet
import com.truckconnect.feature.admin.DashboardScreen
import com.truckconnect.feature.admin.DriverApprovalsScreen
import com.truckconnect.feature.admin.DriverDetailScreen
import com.truckconnect.feature.admin.DriversScreen
import com.truckconnect.feature.admin.LiveMapScreen
import com.truckconnect.feature.admin.MoreScreen
import com.truckconnect.feature.admin.NewShipmentScreen
import com.truckconnect.feature.admin.ReportsScreen
import com.truckconnect.feature.admin.ReviewDriverScreen
import com.truckconnect.feature.admin.ShipmentDetailScreen
import com.truckconnect.feature.admin.ShipmentsScreen
import com.truckconnect.feature.admin.VehiclesScreen
import com.truckconnect.feature.auth.AdminSignInScreen
import com.truckconnect.feature.auth.DriverLoginScreen
import com.truckconnect.feature.auth.PendingApprovalScreen
import com.truckconnect.feature.auth.PermissionsScreen
import com.truckconnect.feature.auth.RegisterIdentityScreen
import com.truckconnect.feature.auth.RegisterLicenceScreen
import com.truckconnect.feature.auth.RegisterMobilePinScreen
import com.truckconnect.feature.auth.RoleSelectScreen
import com.truckconnect.feature.auth.SplashScreen
import com.truckconnect.feature.driver.ActiveTripScreen
import com.truckconnect.feature.driver.ChooseNavAppSheet
import com.truckconnect.feature.driver.DeliveredSuccessScreen
import com.truckconnect.feature.driver.DriverAlertsScreen
import com.truckconnect.feature.driver.DriverHistoryScreen
import com.truckconnect.feature.driver.DriverHomeEmptyScreen
import com.truckconnect.feature.driver.DriverProfileScreen
import com.truckconnect.feature.driver.JobAlertScreen
import com.truckconnect.feature.driver.JobDetailsScreen
import com.truckconnect.feature.driver.ProofOfDeliveryScreen
import com.truckconnect.feature.driver.RejectReasonSheet
import com.truckconnect.feature.driver.SosSheet
import com.truckconnect.feature.settings.AppVersionScreen
import com.truckconnect.feature.settings.ForcedUpdateScreen
import com.truckconnect.feature.settings.HelpScreen
import com.truckconnect.feature.settings.InstallPermissionScreen
import com.truckconnect.feature.settings.LanguageScreen
import com.truckconnect.feature.settings.NotificationsScreen
import com.truckconnect.feature.settings.SecurityScreen
import com.truckconnect.feature.settings.SettingsAdminScreen
import com.truckconnect.feature.settings.SettingsDriverScreen
import com.truckconnect.feature.settings.ThemeScreen
import com.truckconnect.feature.settings.UpdateSheet

data class GalleryItem(
    val id: String,
    val slug: String,
    val board: String,
    val title: String,
    val isDriverScreen: Boolean = false,
    val composable: @Composable () -> Unit
)

object GalleryCatalog {
    val items = listOf(
        // Start Board (9)
        GalleryItem("A1", "A1_splash", "Start", "Splash") { SplashScreen() },
        GalleryItem("A2", "A2_role_select", "Start", "Role Select") { RoleSelectScreen() },
        GalleryItem("A3", "A3_admin_sign_in", "Start", "Admin Sign In") { AdminSignInScreen() },
        GalleryItem("D1", "D1_driver_login", "Start", "Driver Login", isDriverScreen = true) { DriverLoginScreen() },
        GalleryItem("D2", "D2_register_mobile_pin", "Start", "Register Mobile/PIN", isDriverScreen = true) { RegisterMobilePinScreen() },
        GalleryItem("D3", "D3_register_identity", "Start", "Register Identity", isDriverScreen = true) { RegisterIdentityScreen() },
        GalleryItem("D4", "D4_register_licence", "Start", "Register Licence", isDriverScreen = true) { RegisterLicenceScreen() },
        GalleryItem("D5", "D5_pending_approval", "Start", "Pending Approval", isDriverScreen = true) { PendingApprovalScreen() },
        GalleryItem("D6", "D6_permissions", "Start", "Permissions", isDriverScreen = true) { PermissionsScreen() },

        // Driver Board (12)
        GalleryItem("V1", "V1_home_no_job", "Driver", "Driver Home (Empty)", isDriverScreen = true) { DriverHomeEmptyScreen() },
        GalleryItem("V2", "V2_job_alert", "Driver", "Job Alert", isDriverScreen = true) { JobAlertScreen() },
        GalleryItem("V3", "V3_reject_reason", "Driver", "Reject Reason Sheet", isDriverScreen = true) { RejectReasonSheet() },
        GalleryItem("V4", "V4_job_details", "Driver", "Job Details", isDriverScreen = true) { JobDetailsScreen() },
        GalleryItem("V5", "V5_active_trip", "Driver", "Active Trip", isDriverScreen = true) { ActiveTripScreen() },
        GalleryItem("V6", "V6_choose_navigation_app", "Driver", "Choose Nav App Sheet", isDriverScreen = true) { ChooseNavAppSheet() },
        GalleryItem("V7", "V7_proof_of_delivery", "Driver", "Proof of Delivery", isDriverScreen = true) { ProofOfDeliveryScreen() },
        GalleryItem("V8", "V8_delivered", "Driver", "Delivered Success", isDriverScreen = true) { DeliveredSuccessScreen() },
        GalleryItem("V9", "V9_history", "Driver", "Driver History", isDriverScreen = true) { DriverHistoryScreen() },
        GalleryItem("V10", "V10_sos", "Driver", "SOS Sheet", isDriverScreen = true) { SosSheet() },
        GalleryItem("V11", "V11_alerts", "Driver", "Driver Alerts", isDriverScreen = true) { DriverAlertsScreen() },
        GalleryItem("V12", "V12_profile", "Driver", "Driver Profile", isDriverScreen = true) { DriverProfileScreen() },

        // Admin Board (15)
        GalleryItem("M1", "M1_dashboard", "Admin", "Admin Dashboard") { DashboardScreen() },
        GalleryItem("M2", "M2_live_map", "Admin", "Live Map") { LiveMapScreen() },
        GalleryItem("M3", "M3_shipments", "Admin", "Shipments") { ShipmentsScreen() },
        GalleryItem("M4", "M4_shipment_detail", "Admin", "Shipment Detail") { ShipmentDetailScreen() },
        GalleryItem("M5", "M5_new_shipment", "Admin", "New Shipment") { NewShipmentScreen() },
        GalleryItem("M6", "M6_assign_driver", "Admin", "Assign Driver Sheet") { AssignDriverSheet() },
        GalleryItem("M7", "M7_drivers", "Admin", "Drivers Directory") { DriversScreen() },
        GalleryItem("M8", "M8_driver_detail", "Admin", "Driver Detail") { DriverDetailScreen() },
        GalleryItem("M9", "M9_driver_approvals", "Admin", "Driver Approvals") { DriverApprovalsScreen() },
        GalleryItem("M10", "M10_review_driver", "Admin", "Review Driver") { ReviewDriverScreen() },
        GalleryItem("M11", "M11_vehicles", "Admin", "Vehicles Directory") { VehiclesScreen() },
        GalleryItem("M12", "M12_reports", "Admin", "Reports") { ReportsScreen() },
        GalleryItem("M13", "M13_alerts_centre", "Admin", "Alerts Centre") { AlertsCentreScreen() },
        GalleryItem("M14", "M14_more", "Admin", "More Menu") { MoreScreen() },
        GalleryItem("M15", "M15_app_versions", "Admin", "App Versions") { AppVersionsScreen() },

        // Settings & Updates Board (11)
        GalleryItem("S1", "S1_settings_admin", "Settings", "Settings (Admin)") { SettingsAdminScreen() },
        GalleryItem("S2", "S2_settings_driver", "Settings", "Settings (Driver)", isDriverScreen = true) { SettingsDriverScreen() },
        GalleryItem("S3", "S3_theme", "Settings", "Theme Settings") { ThemeScreen() },
        GalleryItem("S4", "S4_language", "Settings", "Language Settings", isDriverScreen = true) { LanguageScreen() },
        GalleryItem("S5", "S5_notifications", "Settings", "Notifications") { NotificationsScreen() },
        GalleryItem("S6", "S6_security", "Settings", "Security") { SecurityScreen() },
        GalleryItem("S7", "S7_help", "Settings", "Help & Support", isDriverScreen = true) { HelpScreen() },
        GalleryItem("S8", "S8_app_version", "Settings", "App Version") { AppVersionScreen() },
        GalleryItem("U1", "U1_update_available", "Settings", "Update Available Sheet", isDriverScreen = true) { UpdateSheet() },
        GalleryItem("U2", "U2_install_permission", "Settings", "Install Permission") { InstallPermissionScreen() },
        GalleryItem("U3", "U3_forced_update", "Settings", "Forced Update", isDriverScreen = true) { ForcedUpdateScreen() }
    )
}

@Composable
fun GalleryScreen(
    autoCaptureTarget: String? = null,
    onCloseGallery: () -> Unit = {}
) {
    var selectedTheme by remember { mutableStateOf(TcTheme.Indigo) }
    var selectedMode by remember { mutableStateOf(TcMode.Light) }
    var isMalayalam by remember { mutableStateOf(false) }
    var selectedBoard by remember { mutableStateOf("All") }
    var activeItemIndex by remember { mutableIntStateOf(-1) }

    var isCapturing by remember { mutableStateOf(autoCaptureTarget != null) }
    var captureIndex by remember { mutableIntStateOf(0) }
    val graphicsLayer = rememberGraphicsLayer()
    val context = LocalContext.current

    val colors = TcThemeState.colors
    val typography = TcThemeState.typography
    val dimens = TcThemeState.dimens

    LaunchedEffect(isCapturing, captureIndex) {
        if (isCapturing) {
            kotlinx.coroutines.delay(200)
            try {
                val imageBitmap = graphicsLayer.toImageBitmap()
                val bitmap = imageBitmap.asAndroidBitmap()
                val item = GalleryCatalog.items[captureIndex]
                val dir = File(context.getExternalFilesDir(null), "captures")
                dir.mkdirs()
                val file = File(dir, "${item.slug}.png")
                FileOutputStream(file).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                }
                android.util.Log.i("SCREEN_CAP", "CAPTURED ${item.slug}.png (${captureIndex + 1}/47)")
                if (captureIndex < GalleryCatalog.items.size - 1) {
                    captureIndex++
                } else {
                    isCapturing = false
                    android.util.Log.i("SCREEN_CAP", "ALL_47_CAPTURED")
                }
            } catch (e: Exception) {
                android.util.Log.e("SCREEN_CAP", "Capture error: ${e.message}", e)
            }
        }
    }

    val filteredItems = remember(selectedBoard) {
        if (selectedBoard == "All") {
            GalleryCatalog.items
        } else {
            GalleryCatalog.items.filter { it.board == selectedBoard }
        }
    }

    if (isCapturing) {
        val currentItem = GalleryCatalog.items[captureIndex]
        androidx.compose.runtime.CompositionLocalProvider(
            LocalDensity provides Density(2.0f, 1.0f)
        ) {
            TruckConnectTheme(
                theme = selectedTheme,
                mode = selectedMode,
                isDriver = currentItem.isDriverScreen,
                isMalayalam = isMalayalam
            ) {
                Box(
                    modifier = Modifier
                        .size(width = dimens.CanvasWidth, height = dimens.CanvasHeight)
                        .drawWithContent {
                            graphicsLayer.record {
                                this@drawWithContent.drawContent()
                            }
                            drawLayer(graphicsLayer)
                        }
                ) {
                    currentItem.composable()
                }
            }
        }
    } else if (activeItemIndex >= 0 && activeItemIndex < GalleryCatalog.items.size) {
        val currentItem = GalleryCatalog.items[activeItemIndex]

        TruckConnectTheme(
            theme = selectedTheme,
            mode = selectedMode,
            isDriver = currentItem.isDriverScreen,
            isMalayalam = isMalayalam
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(TcThemeState.colors.bg),
                contentAlignment = Alignment.Center
            ) {
                // Scaled 324 x 648 dp canvas container
                Box(
                    modifier = Modifier
                        .size(width = dimens.CanvasWidth, height = dimens.CanvasHeight)
                        .background(TcThemeState.colors.bg)
                ) {
                    currentItem.composable()
                }

                // Overlay Controls to switch or dismiss
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = dimens.Padding16)
                        .background(
                            color = TcThemeState.colors.surface.copy(alpha = 0.92f),
                            shape = TcShapes.Pill
                        )
                        .border(dimens.CardBorder, TcThemeState.colors.border, TcShapes.Pill)
                        .padding(horizontal = dimens.Padding12, vertical = dimens.Padding6),
                    horizontalArrangement = Arrangement.spacedBy(dimens.Gap8),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TcButton(
                        text = "Prev",
                        variant = TcButtonVariant.Outline,
                        size = TcButtonSize.Sm,
                        onClick = {
                            if (activeItemIndex > 0) activeItemIndex--
                        }
                    )

                    Text(
                        text = "${currentItem.id} (${activeItemIndex + 1}/47)",
                        style = typography.caption,
                        color = TcThemeState.colors.text
                    )

                    TcButton(
                        text = "Next",
                        variant = TcButtonVariant.Outline,
                        size = TcButtonSize.Sm,
                        onClick = {
                            if (activeItemIndex < GalleryCatalog.items.size - 1) activeItemIndex++
                        }
                    )

                    TcButton(
                        text = "Close",
                        variant = TcButtonVariant.Primary,
                        size = TcButtonSize.Sm,
                        onClick = { activeItemIndex = -1 }
                    )
                }
            }
        }
    } else {
        // Gallery Dashboard List
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.bg)
        ) {
            TcAppBar(
                title = "Screen Gallery (47)",
                onBackClick = onCloseGallery
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimens.ScreenPaddingHorizontal),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TcButton(
                    text = "Capture All (47 PNGs)",
                    size = TcButtonSize.Sm,
                    variant = TcButtonVariant.Primary,
                    onClick = {
                        captureIndex = 0
                        isCapturing = true
                    }
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = dimens.ScreenPaddingHorizontal),
                verticalArrangement = Arrangement.spacedBy(dimens.Gap10)
            ) {
                // Theme selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(dimens.Gap6)
                ) {
                    TcTheme.entries.forEach { theme ->
                        val isSel = theme == selectedTheme
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(TcShapes.Badge)
                                .background(if (isSel) colors.accent else colors.surface)
                                .border(dimens.CardBorder, if (isSel) colors.accent else colors.border, TcShapes.Badge)
                                .clickable { selectedTheme = theme }
                                .padding(vertical = dimens.Padding6),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = theme.name,
                                style = typography.xsmall,
                                color = if (isSel) colors.accentInk else colors.text
                            )
                        }
                    }
                }

                // Mode & Language selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(dimens.Gap6)
                ) {
                    // Mode
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(TcShapes.Badge)
                            .background(if (selectedMode == TcMode.Light) colors.accent else colors.surface)
                            .border(dimens.CardBorder, colors.border, TcShapes.Badge)
                            .clickable { selectedMode = TcMode.Light }
                            .padding(vertical = dimens.Padding6),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Light",
                            style = typography.xsmall,
                            color = if (selectedMode == TcMode.Light) colors.accentInk else colors.text
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(TcShapes.Badge)
                            .background(if (selectedMode == TcMode.Dark) colors.accent else colors.surface)
                            .border(dimens.CardBorder, colors.border, TcShapes.Badge)
                            .clickable { selectedMode = TcMode.Dark }
                            .padding(vertical = dimens.Padding6),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Dark",
                            style = typography.xsmall,
                            color = if (selectedMode == TcMode.Dark) colors.accentInk else colors.text
                        )
                    }

                    // Language
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(TcShapes.Badge)
                            .background(if (!isMalayalam) colors.accent else colors.surface)
                            .border(dimens.CardBorder, colors.border, TcShapes.Badge)
                            .clickable { isMalayalam = false }
                            .padding(vertical = dimens.Padding6),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "EN",
                            style = typography.xsmall,
                            color = if (!isMalayalam) colors.accentInk else colors.text
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(TcShapes.Badge)
                            .background(if (isMalayalam) colors.accent else colors.surface)
                            .border(dimens.CardBorder, colors.border, TcShapes.Badge)
                            .clickable { isMalayalam = true }
                            .padding(vertical = dimens.Padding6),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "മലയാളം",
                            style = typography.xsmall,
                            color = if (isMalayalam) colors.accentInk else colors.text
                        )
                    }
                }

                // Board Tabs
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(dimens.Gap6)
                ) {
                    val boards = listOf("All", "Start", "Driver", "Admin", "Settings")
                    items(boards) { board ->
                        val isSel = board == selectedBoard
                        Box(
                            modifier = Modifier
                                .clip(TcShapes.Pill)
                                .background(if (isSel) colors.accent else colors.surface)
                                .border(dimens.CardBorder, if (isSel) colors.accent else colors.border, TcShapes.Pill)
                                .clickable { selectedBoard = board }
                                .padding(horizontal = dimens.Padding12, vertical = dimens.Padding4)
                        ) {
                            Text(
                                text = board,
                                style = typography.small,
                                color = if (isSel) colors.accentInk else colors.text
                            )
                        }
                    }
                }

                // Screen list
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(dimens.Gap6)
                ) {
                    items(filteredItems) { item ->
                        val globalIndex = GalleryCatalog.items.indexOf(item)
                        TcCard(
                            onClick = { activeItemIndex = globalIndex }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(dimens.Gap8),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    TcChip(text = item.id, kind = TcChipKind.Accent)
                                    Column {
                                        Text(
                                            text = item.title,
                                            style = typography.body,
                                            color = colors.text
                                        )
                                        Text(
                                            text = item.slug,
                                            style = typography.xsmall,
                                            color = colors.muted
                                        )
                                    }
                                }

                                TcChip(text = item.board, kind = TcChipKind.Neutral)
                            }
                        }
                    }
                }
            }
        }
    }
}
