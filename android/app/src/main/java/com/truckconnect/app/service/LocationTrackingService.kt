package com.truckconnect.app.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import com.truckconnect.core.data.repository.TrackingRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LocationTrackingService : Service(), LocationListener {

    @Inject
    lateinit var trackingRepository: TrackingRepository

    private val serviceScope = CoroutineScope(Dispatchers.IO + Job())
    private var wakeLock: PowerManager.WakeLock? = null
    private var locationManager: LocationManager? = null
    private var currentShipmentId: String? = null
    private var syncJob: Job? = null

    override fun onCreate() {
        super.onCreate()
        try {
            createNotificationChannel()

            val powerManager = getSystemService(Context.POWER_SERVICE) as? PowerManager
            wakeLock = powerManager?.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK,
                "TruckConnect::LocationTrackingWakeLock"
            )?.apply {
                setReferenceCounted(false)
                acquire(8 * 60 * 60 * 1000L) // Max 8 hours
            }

            locationManager = getSystemService(Context.LOCATION_SERVICE) as? LocationManager
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        if (action == ACTION_STOP_TRACKING) {
            stopSelf()
            return START_NOT_STICKY
        }

        currentShipmentId = intent?.getStringExtra(EXTRA_SHIPMENT_ID) ?: "TC-2026-000131"

        try {
            val notification = buildTrackingNotification(currentShipmentId!!)

            val hasFine = checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            val hasCoarse = checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // Android 14+
                if (hasFine || hasCoarse) {
                    startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION)
                    startLocationUpdates()
                    startPeriodicSync()
                } else {
                    stopSelf()
                    return START_NOT_STICKY
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { // Android 10-13
                if (hasFine || hasCoarse) {
                    startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION)
                    startLocationUpdates()
                    startPeriodicSync()
                } else {
                    startForeground(NOTIFICATION_ID, notification)
                }
            } else {
                startForeground(NOTIFICATION_ID, notification)
                startLocationUpdates()
                startPeriodicSync()
            }
        } catch (e: Throwable) {
            e.printStackTrace()
            stopSelf()
            return START_NOT_STICKY
        }

        return START_STICKY
    }

    private fun startLocationUpdates() {
        try {
            locationManager?.let { lm ->
                if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    lm.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        15000L,
                        25f,
                        this
                    )
                } else if (lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    lm.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        15000L,
                        25f,
                        this
                    )
                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    private fun startPeriodicSync() {
        syncJob?.cancel()
        syncJob = serviceScope.launch {
            while (isActive) {
                delay(30000L)
                try {
                    trackingRepository.syncPendingPings()
                } catch (e: Throwable) {
                    // Handled gracefully offline
                }
            }
        }
    }

    override fun onLocationChanged(location: Location) {
        val speedKmh = location.speed * 3.6f
        serviceScope.launch {
            try {
                trackingRepository.recordPing(
                    lat = location.latitude,
                    lng = location.longitude,
                    speedKmh = speedKmh,
                    bearing = location.bearing,
                    accuracy = location.accuracy,
                    shipmentId = currentShipmentId
                )
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            syncJob?.cancel()
            locationManager?.removeUpdates(this)
            wakeLock?.let {
                if (it.isHeld) it.release()
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    "TruckConnect Trip Telemetry",
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    description = "Active trip background GPS telemetry service"
                }
                val nm = getSystemService(NotificationManager::class.java)
                nm?.createNotificationChannel(channel)
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

    private fun buildTrackingNotification(shipmentId: String): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("TruckConnect Live Tracking Active")
            .setContentText("Trip $shipmentId · Transmitting GPS telemetry")
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }

    companion object {
        const val NOTIFICATION_ID = 9001
        const val CHANNEL_ID = "tc_location_tracking_channel"
        const val ACTION_START_TRACKING = "START_TRACKING"
        const val ACTION_STOP_TRACKING = "STOP_TRACKING"
        const val EXTRA_SHIPMENT_ID = "EXTRA_SHIPMENT_ID"

        fun start(context: Context, shipmentId: String) {
            try {
                val intent = Intent(context, LocationTrackingService::class.java).apply {
                    action = ACTION_START_TRACKING
                    putExtra(EXTRA_SHIPMENT_ID, shipmentId)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intent)
                } else {
                    context.startService(intent)
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }

        fun stop(context: Context) {
            try {
                val intent = Intent(context, LocationTrackingService::class.java).apply {
                    action = ACTION_STOP_TRACKING
                }
                context.startService(intent)
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }
}
