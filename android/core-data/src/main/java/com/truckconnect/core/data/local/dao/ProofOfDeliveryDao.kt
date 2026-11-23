package com.truckconnect.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.truckconnect.core.data.local.entity.ProofOfDeliveryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProofOfDeliveryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPoD(pod: ProofOfDeliveryEntity)

    @Query("SELECT * FROM proof_of_delivery WHERE isSynced = 0")
    suspend fun getUnsyncedPoDs(): List<ProofOfDeliveryEntity>

    @Query("UPDATE proof_of_delivery SET isSynced = 1 WHERE shipmentId = :shipmentId")
    suspend fun markPoDSynced(shipmentId: String)

    @Query("SELECT * FROM proof_of_delivery WHERE shipmentId = :shipmentId LIMIT 1")
    fun observePoD(shipmentId: String): Flow<ProofOfDeliveryEntity?>
}
