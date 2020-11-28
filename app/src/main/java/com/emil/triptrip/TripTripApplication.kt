package com.emil.triptrip

import android.app.Application
import com.emil.triptrip.database.source.TripTripRepository
import com.emil.triptrip.util.ServiceLocator
import kotlin.properties.Delegates


/**
 * Created by Wayne Chen on 2020-01-15.
 *
 * An application that lazily provides a repository. Note that this Service Locator pattern is
 * used to simplify the sample. Consider a Dependency Injection framework.
 */
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

    fun isLiveDataDesign() = true
}