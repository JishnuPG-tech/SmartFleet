package com.truckconnect.core.data.fixtures

object MockData {
    // Admin
    const val ADMIN_NAME = "Anil Kumar"
    const val ADMIN_ROLE = "Owner · Super Admin"
    const val ADMIN_EMAIL = "admin@truckconnect.in"
    const val ADMIN_AVATAR = "AK"

    // Primary Driver
    const val DRIVER_NAME = "Suresh Kumar"
    const val DRIVER_PHONE = "+91 98470 12345"
    const val DRIVER_VEHICLE = "KL-07-AZ-1200"
    const val DRIVER_AVATAR = "SK"
    const val DRIVER_TRIPS = 49
    const val DRIVER_ON_TIME_PCT = 82
    const val DRIVER_RATING = 4.6f
    const val DRIVER_LICENCE = "KL-07-2019-0044556"

    // Registrant
    const val REGISTRANT_NAME = "Rajan Nair"
    const val REGISTRANT_PHONE = "+91 98470 55501"
    const val REGISTRANT_LICENCE = "KL-07-2022-0098765"
    const val REGISTRANT_AVATAR = "RN"

    // KPI Metrics
    const val KPI_ON_TRIP = 7
    const val KPI_ON_TRIP_SUB = "+2 today"
    const val KPI_AVAILABLE = 2
    const val KPI_PENDING = 5
    const val KPI_PENDING_SUB = "1 overdue"
    const val KPI_DELAYED = 2
    const val KPI_DELAYED_SUB = "check now"

    // Reports KPI
    const val REPORT_ON_TIME = "94 %"
    const val REPORT_ON_TIME_SUB = "+2 pt"
    const val REPORT_ACCEPT_TIME = "4.2 m"
    const val REPORT_ACCEPT_TIME_SUB = "target 5"
    val REPORT_BAR_VALUES = listOf(49, 44, 42, 40, 36, 34)
    const val REPORT_COMPLETED_COUNT = 246
    const val REPORT_CANCELLED_COUNT = 8

    // App Versions
    const val APP_VERSION_LATEST = "v2.1.0"
    const val APP_VERSION_OFFERED = "v2.1.1"
    const val APP_VERSION_MINIMUM = "v2.0.3"
    const val APP_VERSION_OUTDATED = "v2.0.1"

    data class ShipmentItem(
        val code: String,
        val route: String,
        val goods: String,
        val weight: String,
        val status: String,
        val statusKind: String,
        val time: String,
        val driver: String,
        val plate: String,
        val progressPct: Float
    )

    val SHIPMENTS = listOf(
        ShipmentItem(
            code = "TC-2026-000125",
            route = "Kasaragod → Malappuram",
            goods = "Vegetables",
            weight = "8.5 t",
            status = "In transit",
            statusKind = "info",
            time = "Today 6:00 am",
            driver = "Suresh Kumar",
            plate = "KL-07-AZ-1200",
            progressPct = 0.62f
        ),
        ShipmentItem(
            code = "TC-2026-000131",
            route = "Kochi → Kozhikode",
            goods = "Vegetables",
            weight = "8.5 t",
            status = "Delivered",
            statusKind = "ok",
            time = "Today 2:36 pm",
            driver = "Suresh Kumar",
            plate = "KL-07-AZ-1200",
            progressPct = 1.0f
        ),
        ShipmentItem(
            code = "TC-2026-000133",
            route = "Thrissur → Kannur",
            goods = "Cement",
            weight = "16 t",
            status = "Delayed",
            statusKind = "warn",
            time = "22 Nov, 6:00 am",
            driver = "Ramesh Chandran",
            plate = "KL-08-BX-1571",
            progressPct = 0.38f
        ),
        ShipmentItem(
            code = "TC-2026-000136",
            route = "Alappuzha → Kollam",
            goods = "Rice",
            weight = "6 t",
            status = "Loaded",
            statusKind = "info",
            time = "22 Nov, 8:00 am",
            driver = "Manoj V",
            plate = "KL-02-HR-3797",
            progressPct = 0.25f
        ),
        ShipmentItem(
            code = "TC-2026-000138",
            route = "Palakkad → Ernakulam",
            goods = "Steel",
            weight = "12 t",
            status = "Reached pickup",
            statusKind = "neutral",
            time = "22 Nov, 9:30 am",
            driver = "Rajesh K",
            plate = "KL-13-JQ-4168",
            progressPct = 0.15f
        ),
        ShipmentItem(
            code = "TC-2026-000141",
            route = "Kottayam → Thiruvananthapuram",
            goods = "FMCG",
            weight = "4 t",
            status = "Created",
            statusKind = "neutral",
            time = "22 Nov, 11:00 am",
            driver = "Unassigned",
            plate = "Unassigned",
            progressPct = 0.0f
        )
    )

    data class VehicleItem(
        val plate: String,
        val type: String,
        val capacity: String,
        val insuranceDays: Int,
        val status: String,
        val statusKind: String
    )

    val VEHICLES = listOf(
        VehicleItem("KL-07-AZ-1200", "Lorry", "10 t", 300, "On trip", "info"),
        VehicleItem("KL-08-BX-1571", "Mini truck", "3 t", 120, "Available", "ok"),
        VehicleItem("KL-02-HR-3797", "Heavy truck", "10 t", 45, "Service", "warn"),
        VehicleItem("KL-13-JQ-4168", "Pickup", "1.5 t", 210, "Available", "ok")
    )

    data class DriverItem(
        val name: String,
        val plate: String,
        val avatar: String,
        val status: String,
        val statusKind: String
    )

    val DRIVERS = listOf(
        DriverItem("Suresh Kumar", "KL-07-AZ-1200", "SK", "On trip", "info"),
        DriverItem("Ramesh Chandran", "KL-08-BX-1571", "RC", "Free", "ok"),
        DriverItem("Manoj V", "KL-02-HR-3797", "MV", "On trip", "info"),
        DriverItem("Rajesh K", "KL-13-JQ-4168", "RK", "Free", "ok"),
        DriverItem("Biju Paul", "-", "BP", "Leave", "neutral")
    )

    data class PendingApprovalItem(
        val name: String,
        val phone: String,
        val licenceNo: String,
        val licenceType: String,
        val expiry: String,
        val vehicleType: String,
        val submittedTime: String,
        val avatar: String
    )

    val PENDING_APPROVALS = listOf(
        PendingApprovalItem(
            name = "Rajan Nair",
            phone = "+91 98470 55501",
            licenceNo = "KL-07-2022-0098765",
            licenceType = "Heavy",
            expiry = "2029",
            vehicleType = "Owner-operator",
            submittedTime = "2 hours ago",
            avatar = "RN"
        ),
        PendingApprovalItem(
            name = "Thomas Mathew",
            phone = "+91 94471 22334",
            licenceNo = "KL-05-2018-0012345",
            licenceType = "LMV Transport",
            expiry = "2028",
            vehicleType = "Company fleet",
            submittedTime = "5 hours ago",
            avatar = "TM"
        )
    )

    // Map Vehicle Pins: (lng, lat, colorKind, id)
    // (75.1,12.1,accent,a) (75.8,11.1,crit,b) (76.2,10.1,accent,c)* (76.8,9.2,warn,d) (76.5,10.7,accent,e); * selected
    data class MapVehiclePin(
        val id: String,
        val lng: Double,
        val lat: Double,
        val colorKind: String,
        val isSelected: Boolean
    )

    val MAP_PINS = listOf(
        MapVehiclePin("a", 75.1, 12.1, "accent", false),
        MapVehiclePin("b", 75.8, 11.1, "crit", false),
        MapVehiclePin("c", 76.2, 10.1, "accent", true),
        MapVehiclePin("d", 76.8, 9.2, "warn", false),
        MapVehiclePin("e", 76.5, 10.7, "accent", false)
    )

    // Kerala coast polygon coordinates from mockup
    val KERALA_COAST = listOf(
        Pair(74.98, 12.87), Pair(75.12, 12.45), Pair(75.35, 12.01),
        Pair(75.76, 11.25), Pair(75.95, 10.92), Pair(76.18, 10.35),
        Pair(76.26, 9.98), Pair(76.45, 9.49), Pair(76.55, 9.12),
        Pair(76.82, 8.82), Pair(77.01, 8.35), Pair(77.25, 8.18),
        Pair(77.55, 8.08), Pair(77.72, 8.35), Pair(77.35, 8.85),
        Pair(77.15, 9.25), Pair(77.22, 9.75), Pair(77.05, 10.15),
        Pair(76.85, 10.55), Pair(76.65, 10.85), Pair(76.45, 11.15),
        Pair(76.25, 11.65), Pair(75.85, 11.95), Pair(75.55, 12.35),
        Pair(75.35, 12.65), Pair(75.05, 12.85)
    )

    val CITIES = listOf(
        Triple("Kannur", 75.37, 11.87),
        Triple("Kozhikode", 75.78, 11.25),
        Triple("Thrissur", 76.21, 10.52),
        Triple("Kochi", 76.27, 9.93),
        Triple("Kollam", 76.59, 8.89),
        Triple("Thiruvananthapuram", 76.95, 8.52)
    )
}
