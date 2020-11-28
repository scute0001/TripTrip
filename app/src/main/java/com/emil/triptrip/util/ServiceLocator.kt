package com.emil.triptrip.util

import android.content.Context
import androidx.annotation.VisibleForTesting
import com.emil.triptrip.database.source.DefaultTripTripRepository
import com.emil.triptrip.database.source.TripTripDataSource
import com.emil.triptrip.database.source.TripTripRepository
import com.emil.triptrip.database.source.local.TripTripLocalDataSource
import com.emil.triptrip.database.source.remote.TripTripRemoteDataSource

/**
 * A Service Locator for the [TripTripRepository].
 */
object ServiceLocator {

    @Volatile
    var repository: TripTripRepository? = null
        @VisibleForTesting set

    fun provideRepository(context: Context): TripTripRepository {
        synchronized(this) {
            return repository
                ?: repository
                ?: createPublisherRepository(context)
        }
    }

    private fun createPublisherRepository(context: Context): TripTripRepository {
        return DefaultTripTripRepository(
            TripTripRemoteDataSource,
            createLocalDataSource(context)
        )
    }

    private fun createLocalDataSource(context: Context): TripTripDataSource {
        return TripTripLocalDataSource(context)
    }
}