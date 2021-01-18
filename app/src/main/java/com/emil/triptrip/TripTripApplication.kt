package com.emil.triptrip

import android.app.Application
import com.emil.triptrip.database.source.TripTripRepository
import com.emil.triptrip.util.ServiceLocator
import kotlin.properties.Delegates

class TripTripApplication : Application() {

    // Depends on the flavor,
    val repository: TripTripRepository
        get() = ServiceLocator.provideRepository(this)

    companion object {
        var instance: TripTripApplication by Delegates.notNull()
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}