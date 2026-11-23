package com.truckconnect.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.truckconnect.core.data.local.dao.LocationPingDao
import com.truckconnect.core.data.local.dao.ProofOfDeliveryDao
import com.truckconnect.core.data.local.dao.ShipmentDao
import com.truckconnect.core.data.local.entity.LocationPingEntity
import com.truckconnect.core.data.local.entity.ProofOfDeliveryEntity
import com.truckconnect.core.data.local.entity.ShipmentEntity

@Database(
    entities = [
        ShipmentEntity::class,
        LocationPingEntity::class,
        ProofOfDeliveryEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class TruckConnectDatabase : RoomDatabase() {
    abstract fun shipmentDao(): ShipmentDao
    abstract fun locationPingDao(): LocationPingDao
    abstract fun proofOfDeliveryDao(): ProofOfDeliveryDao

    companion object {
        const val DATABASE_NAME = "truckconnect.db"
    }
}
