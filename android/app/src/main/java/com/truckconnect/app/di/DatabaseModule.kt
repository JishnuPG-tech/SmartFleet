package com.truckconnect.app.di

import android.content.Context
import androidx.room.Room
import com.truckconnect.core.data.local.TruckConnectDatabase
import com.truckconnect.core.data.local.dao.LocationPingDao
import com.truckconnect.core.data.local.dao.ProofOfDeliveryDao
import com.truckconnect.core.data.local.dao.ShipmentDao
import com.truckconnect.core.data.repository.ShipmentRepository
import com.truckconnect.core.data.repository.ShipmentRepositoryImpl
import com.truckconnect.core.data.repository.TrackingRepository
import com.truckconnect.core.data.repository.TrackingRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): TruckConnectDatabase {
        return Room.databaseBuilder(
            context,
            TruckConnectDatabase::class.java,
            TruckConnectDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideShipmentDao(database: TruckConnectDatabase): ShipmentDao {
        return database.shipmentDao()
    }

    @Provides
    fun provideLocationPingDao(database: TruckConnectDatabase): LocationPingDao {
        return database.locationPingDao()
    }

    @Provides
    fun provideProofOfDeliveryDao(database: TruckConnectDatabase): ProofOfDeliveryDao {
        return database.proofOfDeliveryDao()
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindShipmentRepository(
        impl: ShipmentRepositoryImpl
    ): ShipmentRepository

    @Binds
    @Singleton
    abstract fun bindTrackingRepository(
        impl: TrackingRepositoryImpl
    ): TrackingRepository
}
