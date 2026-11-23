package com.truckconnect.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.truckconnect.app.gallery.GalleryScreen
import com.truckconnect.core.design.component.AdminTab
import com.truckconnect.core.design.component.DriverTab
import com.truckconnect.core.design.theme.TcMode
import com.truckconnect.core.design.theme.TcTheme
import com.truckconnect.core.design.theme.TruckConnectTheme
import com.truckconnect.feature.admin.AlertsCentreScreen
import com.truckconnect.feature.admin.AppVersionsScreen
import com.truckconnect.feature.admin.AuditLogScreen
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
import com.truckconnect.feature.auth.AuthViewModel
import com.truckconnect.feature.auth.DriverLoginScreen
import com.truckconnect.feature.auth.PendingApprovalScreen
import com.truckconnect.feature.auth.PermissionsScreen
import com.truckconnect.feature.auth.RegisterIdentityScreen
import com.truckconnect.feature.auth.RegisterLicenceScreen
import com.truckconnect.feature.auth.RegisterMobilePinScreen
import com.truckconnect.feature.auth.RoleSelectScreen
import com.truckconnect.feature.auth.SplashScreen
import com.truckconnect.feature.driver.ActiveTripScreen
import com.truckconnect.feature.driver.DeliveredSuccessScreen
import com.truckconnect.feature.driver.DriverAlertsScreen
import com.truckconnect.feature.driver.DriverHistoryScreen
import com.truckconnect.feature.driver.DriverHomeEmptyScreen
import com.truckconnect.feature.driver.DriverProfileScreen
import com.truckconnect.feature.driver.JobAlertScreen
import com.truckconnect.feature.driver.JobDetailsScreen
import com.truckconnect.feature.driver.ProofOfDeliveryScreen
import com.truckconnect.feature.settings.AppVersionScreen
import com.truckconnect.feature.settings.EditProfileScreen
import com.truckconnect.feature.settings.HelpScreen
import com.truckconnect.feature.settings.LanguageScreen
import com.truckconnect.feature.settings.NotificationsScreen
import com.truckconnect.feature.settings.SecurityScreen
import com.truckconnect.feature.settings.SettingsAdminScreen
import com.truckconnect.feature.settings.SettingsDriverScreen
import com.truckconnect.feature.settings.ThemeScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var currentTheme by remember { mutableStateOf(TcTheme.Indigo) }
            var currentMode by remember { mutableStateOf(TcMode.Auto) }
            var isMalayalam by remember { mutableStateOf(false) }

            val initialRoute = remember {
                intent?.getStringExtra("route") ?: NavDestinations.SPLASH
            }
            val autoCaptureTarget = remember {
                if (intent?.getStringExtra("action") == "capture") {
                    intent?.getStringExtra("target") ?: "indigo-light-en"
                } else null
            }

            TruckConnectTheme(
                theme = currentTheme,
                mode = currentMode,
                isMalayalam = isMalayalam
            ) {
                TruckConnectAppNav(
                    startDestination = initialRoute,
                    autoCaptureTarget = autoCaptureTarget,
                    onThemeChange = { currentTheme = it },
                    onModeChange = { currentMode = it },
                    onLanguageChange = { isMalayalam = it }
                )
            }
        }
    }
}

object NavDestinations {
    const val SPLASH = "splash"
    const val GALLERY = "gallery"
    const val ROLE_SELECT = "role_select"
    const val ADMIN_SIGN_IN = "admin_signin"
    const val DRIVER_LOGIN = "driver_login"
    const val DRIVER_REGISTER_PIN = "driver_register_pin"
    const val DRIVER_REGISTER_IDENTITY = "driver_register_identity"
    const val DRIVER_REGISTER_LICENCE = "driver_register_licence"
    const val DRIVER_PENDING = "driver_pending"
    const val DRIVER_PERMISSIONS = "driver_permissions"

    // Admin Main Tabs
    const val ADMIN_DASHBOARD = "admin_dashboard"
    const val ADMIN_LIVEMAP = "admin_livemap"
    const val ADMIN_SHIPMENTS = "admin_shipments"
    const val ADMIN_FLEET = "admin_fleet"
    const val ADMIN_MORE = "admin_more"

    // Admin Sub-screens
    const val ADMIN_SETTINGS = "admin_settings"
    const val ADMIN_REPORTS = "admin_reports"
    const val ADMIN_VEHICLES = "admin_vehicles"
    const val ADMIN_DRIVER_APPROVALS = "admin_driver_approvals"
    const val ADMIN_APP_VERSIONS = "admin_app_versions"
    const val ADMIN_ALERTS_CENTRE = "admin_alerts_centre"
    const val ADMIN_AUDIT_LOG = "admin_audit_log"
    const val ADMIN_NEW_SHIPMENT = "admin_new_shipment"
    const val ADMIN_SHIPMENT_DETAIL = "admin_shipment_detail"
    const val ADMIN_DRIVER_DETAIL = "admin_driver_detail"
    const val ADMIN_REVIEW_DRIVER = "admin_review_driver"

    // Driver Main Tabs
    const val DRIVER_HOME = "driver_home"
    const val DRIVER_HISTORY = "driver_history"
    const val DRIVER_ALERTS = "driver_alerts"
    const val DRIVER_PROFILE = "driver_profile"

    // Driver Flows
    const val DRIVER_JOB_ALERT = "driver_job_alert"
    const val DRIVER_JOB_DETAILS = "driver_job_details"
    const val DRIVER_ACTIVE_TRIP = "driver_active_trip"
    const val DRIVER_POD = "driver_pod"
    const val DRIVER_DELIVERED = "driver_delivered"
    const val DRIVER_SETTINGS = "driver_settings"

    // Common Settings
    const val SETTINGS_PROFILE = "settings_profile"
    const val SETTINGS_THEME = "settings_theme"
    const val SETTINGS_LANGUAGE = "settings_language"
    const val SETTINGS_NOTIFICATIONS = "settings_notifications"
    const val SETTINGS_SECURITY = "settings_security"
    const val SETTINGS_HELP = "settings_help"
    const val SETTINGS_APP_VERSION = "settings_app_version"
}

@Composable
fun TruckConnectAppNav(
    startDestination: String = NavDestinations.SPLASH,
    autoCaptureTarget: String? = null,
    onThemeChange: (TcTheme) -> Unit = {},
    onModeChange: (TcMode) -> Unit = {},
    onLanguageChange: (Boolean) -> Unit = {}
) {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()

    fun navigateAdminTab(tabIndex: Int) {
        val destination = when (tabIndex) {
            AdminTab.HOME.ordinal -> NavDestinations.ADMIN_DASHBOARD
            AdminTab.MAP.ordinal -> NavDestinations.ADMIN_LIVEMAP
            AdminTab.SHIPMENTS.ordinal -> NavDestinations.ADMIN_SHIPMENTS
            AdminTab.FLEET.ordinal -> NavDestinations.ADMIN_FLEET
            AdminTab.MORE.ordinal -> NavDestinations.ADMIN_MORE
            else -> NavDestinations.ADMIN_DASHBOARD
        }
        if (navController.currentBackStackEntry?.destination?.route != destination) {
            navController.navigate(destination) {
                popUpTo(NavDestinations.ADMIN_DASHBOARD) {
                    saveState = false
                }
                launchSingleTop = true
            }
        }
    }

    fun navigateDriverTab(tabIndex: Int) {
        val destination = when (tabIndex) {
            0 -> NavDestinations.DRIVER_HOME
            1 -> NavDestinations.DRIVER_HISTORY
            2 -> NavDestinations.DRIVER_ALERTS
            3 -> NavDestinations.DRIVER_PROFILE
            else -> NavDestinations.DRIVER_HOME
        }
        if (navController.currentBackStackEntry?.destination?.route != destination) {
            navController.navigate(destination) {
                popUpTo(NavDestinations.DRIVER_HOME) {
                    saveState = false
                }
                launchSingleTop = true
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // --- AUTH FLOW ---
        composable(NavDestinations.SPLASH) {
            SplashScreen(
                onSplashComplete = {
                    navController.navigate(NavDestinations.ROLE_SELECT) {
                        popUpTo(NavDestinations.SPLASH) { inclusive = true }
                    }
                },
                onOpenGallery = {
                    navController.navigate(NavDestinations.GALLERY)
                }
            )
        }

        composable(NavDestinations.GALLERY) {
            GalleryScreen(
                autoCaptureTarget = autoCaptureTarget,
                onCloseGallery = { navController.popBackStack() }
            )
        }

        composable(NavDestinations.ROLE_SELECT) {
            RoleSelectScreen(
                onAdminSelected = { navController.navigate(NavDestinations.ADMIN_SIGN_IN) },
                onDriverSelected = { navController.navigate(NavDestinations.DRIVER_LOGIN) }
            )
        }

        composable(NavDestinations.ADMIN_SIGN_IN) {
            AdminSignInScreen(
                onSignInClick = {
                    navController.navigate(NavDestinations.ADMIN_DASHBOARD) {
                        popUpTo(NavDestinations.ROLE_SELECT) { inclusive = true }
                    }
                }
            )
        }

        composable(NavDestinations.DRIVER_LOGIN) {
            DriverLoginScreen(
                onLoginClick = {
                    navController.navigate(NavDestinations.DRIVER_HOME) {
                        popUpTo(NavDestinations.ROLE_SELECT) { inclusive = true }
                    }
                },
                onRegisterClick = { navController.navigate(NavDestinations.DRIVER_REGISTER_PIN) }
            )
        }

        composable(NavDestinations.DRIVER_REGISTER_PIN) {
            RegisterMobilePinScreen(
                onNextClick = { navController.navigate(NavDestinations.DRIVER_REGISTER_IDENTITY) },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(NavDestinations.DRIVER_REGISTER_IDENTITY) {
            RegisterIdentityScreen(
                onNextClick = { navController.navigate(NavDestinations.DRIVER_REGISTER_LICENCE) },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(NavDestinations.DRIVER_REGISTER_LICENCE) {
            RegisterLicenceScreen(
                onSubmitClick = { navController.navigate(NavDestinations.DRIVER_PENDING) },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(NavDestinations.DRIVER_PENDING) {
            PendingApprovalScreen(
                onCallOfficeClick = { },
                onContinueClick = { navController.navigate(NavDestinations.DRIVER_PERMISSIONS) }
            )
        }

        composable(NavDestinations.DRIVER_PERMISSIONS) {
            PermissionsScreen(
                onNextClick = { navController.navigate(NavDestinations.DRIVER_HOME) }
            )
        }

        // --- ADMIN MAIN TABS ---
        composable(NavDestinations.ADMIN_DASHBOARD) {
            DashboardScreen(
                onBellClick = { navController.navigate(NavDestinations.ADMIN_ALERTS_CENTRE) },
                onSosCardClick = { navigateAdminTab(AdminTab.MAP.ordinal) },
                onViewMapClick = { navigateAdminTab(AdminTab.MAP.ordinal) },
                onTripClick = { navController.navigate(NavDestinations.ADMIN_SHIPMENT_DETAIL) },
                onKpiClick = { navController.navigate(NavDestinations.ADMIN_SHIPMENTS) },
                onTabSelected = { tab -> navigateAdminTab(tab) }
            )
        }

        composable(NavDestinations.ADMIN_LIVEMAP) {
            LiveMapScreen(
                onTimelineClick = { navController.navigate(NavDestinations.ADMIN_SHIPMENT_DETAIL) },
                onTabSelected = { tab -> navigateAdminTab(tab) }
            )
        }

        composable(NavDestinations.ADMIN_SHIPMENTS) {
            ShipmentsScreen(
                onShipmentClick = { navController.navigate(NavDestinations.ADMIN_SHIPMENT_DETAIL) },
                onFabClick = { navController.navigate(NavDestinations.ADMIN_NEW_SHIPMENT) },
                onTabSelected = { tab -> navigateAdminTab(tab) }
            )
        }

        composable(NavDestinations.ADMIN_FLEET) {
            DriversScreen(
                onDriverClick = { navController.navigate(NavDestinations.ADMIN_DRIVER_DETAIL) },
                onTabSelected = { tab -> navigateAdminTab(tab) }
            )
        }

        composable(NavDestinations.ADMIN_MORE) {
            MoreScreen(
                onProfileClick = { navController.navigate(NavDestinations.SETTINGS_PROFILE) },
                onMenuItemClick = { item ->
                    when (item) {
                        "Reports" -> navController.navigate(NavDestinations.ADMIN_REPORTS)
                        "Vehicles" -> navController.navigate(NavDestinations.ADMIN_VEHICLES)
                        "Driver approvals" -> navController.navigate(NavDestinations.ADMIN_DRIVER_APPROVALS)
                        "App versions" -> navController.navigate(NavDestinations.ADMIN_APP_VERSIONS)
                        "Audit log" -> navController.navigate(NavDestinations.ADMIN_AUDIT_LOG)
                        "Settings" -> navController.navigate(NavDestinations.ADMIN_SETTINGS)
                    }
                },
                onSignOutClick = {
                    navController.navigate(NavDestinations.ROLE_SELECT) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onTabSelected = { tab -> navigateAdminTab(tab) }
            )
        }

        // --- ADMIN SUB-SCREENS ---
        composable(NavDestinations.ADMIN_SETTINGS) {
            SettingsAdminScreen(
                onBackClick = { navController.popBackStack() },
                onProfileEditClick = { navController.navigate(NavDestinations.SETTINGS_PROFILE) },
                onSettingClick = { item ->
                    when (item) {
                        "Theme" -> navController.navigate(NavDestinations.SETTINGS_THEME)
                        "Language" -> navController.navigate(NavDestinations.SETTINGS_LANGUAGE)
                        "Notifications" -> navController.navigate(NavDestinations.SETTINGS_NOTIFICATIONS)
                        "Security" -> navController.navigate(NavDestinations.SETTINGS_SECURITY)
                        "Help" -> navController.navigate(NavDestinations.SETTINGS_HELP)
                        "App version" -> navController.navigate(NavDestinations.SETTINGS_APP_VERSION)
                        "Profile" -> navController.navigate(NavDestinations.SETTINGS_PROFILE)
                    }
                }
            )
        }

        composable(NavDestinations.SETTINGS_PROFILE) {
            EditProfileScreen(
                onBackClick = { navController.popBackStack() },
                onSaved = { navController.popBackStack() }
            )
        }

        composable(NavDestinations.ADMIN_AUDIT_LOG) {
            AuditLogScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(NavDestinations.ADMIN_REPORTS) {
            ReportsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(NavDestinations.ADMIN_VEHICLES) {
            VehiclesScreen(
                onBackClick = { navController.popBackStack() },
                onVehicleClick = { navController.navigate(NavDestinations.ADMIN_DRIVER_DETAIL) }
            )
        }

        composable(NavDestinations.ADMIN_DRIVER_APPROVALS) {
            DriverApprovalsScreen(
                onBackClick = { navController.popBackStack() },
                onReviewClick = { navController.navigate(NavDestinations.ADMIN_REVIEW_DRIVER) }
            )
        }

        composable(NavDestinations.ADMIN_REVIEW_DRIVER) {
            ReviewDriverScreen(
                onBackClick = { navController.popBackStack() },
                onApproveClick = { navController.popBackStack() },
                onRejectClick = { navController.popBackStack() }
            )
        }

        composable(NavDestinations.ADMIN_APP_VERSIONS) {
            AppVersionsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(NavDestinations.ADMIN_ALERTS_CENTRE) {
            AlertsCentreScreen(
                onTabSelected = { tab -> navigateAdminTab(tab) }
            )
        }

        composable(NavDestinations.ADMIN_NEW_SHIPMENT) {
            NewShipmentScreen(
                onBackClick = { navController.popBackStack() },
                onNextAssignClick = { navController.popBackStack() }
            )
        }

        composable(NavDestinations.ADMIN_SHIPMENT_DETAIL) {
            ShipmentDetailScreen(
                onBackClick = { navController.popBackStack() },
                onReassignClick = { navController.navigate(NavDestinations.ADMIN_FLEET) },
                onCancelShipmentClick = { navController.popBackStack() }
            )
        }

        composable(NavDestinations.ADMIN_DRIVER_DETAIL) {
            DriverDetailScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        // --- DRIVER MAIN TABS & FLOWS ---
        composable(NavDestinations.DRIVER_HOME) {
            DriverHomeEmptyScreen(
                onAlertsClick = { navController.navigate(NavDestinations.DRIVER_ALERTS) },
                onTabSelected = { tab -> navigateDriverTab(tab) },
                onJobAlertClick = { navController.navigate(NavDestinations.DRIVER_JOB_ALERT) }
            )
        }

        composable(NavDestinations.DRIVER_HISTORY) {
            DriverHistoryScreen(
                onTabSelected = { tab -> navigateDriverTab(tab) }
            )
        }

        composable(NavDestinations.DRIVER_ALERTS) {
            DriverAlertsScreen(
                onTabSelected = { tab -> navigateDriverTab(tab) }
            )
        }

        composable(NavDestinations.DRIVER_PROFILE) {
            DriverProfileScreen(
                onSettingsClick = { navController.navigate(NavDestinations.DRIVER_SETTINGS) },
                onLogoutClick = {
                    navController.navigate(NavDestinations.ROLE_SELECT) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onTabSelected = { tab -> navigateDriverTab(tab) }
            )
        }

        composable(NavDestinations.DRIVER_JOB_ALERT) {
            JobAlertScreen(
                onAcceptClick = { navController.navigate(NavDestinations.DRIVER_JOB_DETAILS) },
                onRejectClick = { navController.popBackStack() }
            )
        }

        composable(NavDestinations.DRIVER_JOB_DETAILS) {
            JobDetailsScreen(
                onBackClick = { navController.popBackStack() },
                onStartTripClick = { navController.navigate(NavDestinations.DRIVER_ACTIVE_TRIP) }
            )
        }

        composable(NavDestinations.DRIVER_ACTIVE_TRIP) {
            ActiveTripScreen(
                onBackClick = { navController.popBackStack() },
                onReachedDropClick = { navController.navigate(NavDestinations.DRIVER_POD) }
            )
        }

        composable(NavDestinations.DRIVER_POD) {
            ProofOfDeliveryScreen(
                onBackClick = { navController.popBackStack() },
                onCompleteDeliveryClick = { navController.navigate(NavDestinations.DRIVER_DELIVERED) }
            )
        }

        composable(NavDestinations.DRIVER_DELIVERED) {
            DeliveredSuccessScreen(
                onBackToHomeClick = {
                    navController.navigate(NavDestinations.DRIVER_HOME) {
                        popUpTo(NavDestinations.DRIVER_HOME) { inclusive = true }
                    }
                }
            )
        }

        composable(NavDestinations.DRIVER_SETTINGS) {
            SettingsDriverScreen(
                onBackClick = { navController.popBackStack() },
                onSettingClick = { item ->
                    when {
                        item.contains("Theme") -> navController.navigate(NavDestinations.SETTINGS_THEME)
                        item.contains("Language") || item.contains("ഭാഷ") -> navController.navigate(NavDestinations.SETTINGS_LANGUAGE)
                        item.contains("Notifications") -> navController.navigate(NavDestinations.SETTINGS_NOTIFICATIONS)
                        item.contains("Help") || item.contains("സഹായം") -> navController.navigate(NavDestinations.SETTINGS_HELP)
                    }
                }
            )
        }

        // --- SHARED SETTINGS SCREENS ---
        composable(NavDestinations.SETTINGS_THEME) {
            ThemeScreen(
                onBackClick = { navController.popBackStack() },
                onThemeSelect = { themeName ->
                    when (themeName.lowercase()) {
                        "indigo" -> onThemeChange(TcTheme.Indigo)
                        "emerald" -> onThemeChange(TcTheme.Emerald)
                        "graphite" -> onThemeChange(TcTheme.Graphite)
                        "sunset" -> onThemeChange(TcTheme.Sunset)
                    }
                },
                onModeSelect = { modeName ->
                    when (modeName.lowercase()) {
                        "light" -> onModeChange(TcMode.Light)
                        "dark" -> onModeChange(TcMode.Dark)
                        "auto" -> onModeChange(TcMode.Auto)
                    }
                }
            )
        }

        composable(NavDestinations.SETTINGS_LANGUAGE) {
            LanguageScreen(
                onBackClick = { navController.popBackStack() },
                onLanguageSelect = { lang ->
                    onLanguageChange(lang == "മലയാളം" || lang.lowercase().contains("malayalam"))
                }
            )
        }

        composable(NavDestinations.SETTINGS_NOTIFICATIONS) {
            NotificationsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(NavDestinations.SETTINGS_SECURITY) {
            SecurityScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(NavDestinations.SETTINGS_HELP) {
            HelpScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(NavDestinations.SETTINGS_APP_VERSION) {
            AppVersionScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
