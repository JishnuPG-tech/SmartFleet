package com.truckconnect.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TruckConnectApp : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}
