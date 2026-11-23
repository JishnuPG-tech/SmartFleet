package com.truckconnect.app.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
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
        createNotificationChannel()

        // Acquire partial wake lock for heavy Kerala transport routes
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "TruckConnect::LocationTrackingWakeLock"
        ).apply {
            setReferenceCounted(false)
            acquire(8 * 60 * 60 * 1000L) // Max 8 hours
        }

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        if (action == ACTION_STOP_TRACKING) {
            stopSelf()
            return START_NOT_STICKY
        }

        currentShipmentId = intent?.getStringExtra(EXTRA_SHIPMENT_ID) ?: "TC-2026-000131"

        val notification = buildTrackingNotification(currentShipmentId!!)
        startForeground(NOTIFICATION_ID, notification)

        startLocationUpdates()
        startPeriodicSync()

        return START_STICKY
    }

    private fun startLocationUpdates() {
        try {
            locationManager?.let { lm ->
                if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    lm.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        15000L, // 15 seconds
                        25f,    // 25 meters
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
        } catch (e: SecurityException) {
            // Location permission not granted
        }
    }

    private fun startPeriodicSync() {
        syncJob?.cancel()
        syncJob = serviceScope.launch {
            while (isActive) {
                delay(30000L) // Sync queued pings every 30 seconds
                try {
                    trackingRepository.syncPendingPings()
                } catch (e: Exception) {
                    // Handled gracefully offline
                }
            }
        }
    }

    override fun onLocationChanged(location: Location) {
        val speedKmh = location.speed * 3.6f
        serviceScope.launch {
            trackingRepository.recordPing(
                lat = location.latitude,
                lng = location.longitude,
                speedKmh = speedKmh,
                bearing = location.bearing,
                accuracy = location.accuracy,
                shipmentId = currentShipmentId
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        syncJob?.cancel()
        locationManager?.removeUpdates(this)
        wakeLock?.let {
            if (it.isHeld) it.release()
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "TruckConnect Trip Telemetry",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Active trip background GPS telemetry service"
            }
            val nm = getSystemService(NotificationManager::class.java)
            nm.createNotificationChannel(channel)
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
        const val CHANNEL_ID = "truckconnect_tracking_channel"
        const val NOTIFICATION_ID = 20261
        const val ACTION_STOP_TRACKING = "com.truckconnect.app.STOP_TRACKING"
        const val EXTRA_SHIPMENT_ID = "extra_shipment_id"

        fun startTracking(context: Context, shipmentId: String) {
            val intent = Intent(context, LocationTrackingService::class.java).apply {
                putExtra(EXTRA_SHIPMENT_ID, shipmentId)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stopTracking(context: Context) {
            val intent = Intent(context, LocationTrackingService::class.java).apply {
                action = ACTION_STOP_TRACKING
            }
            context.startService(intent)
        }
    }
}
