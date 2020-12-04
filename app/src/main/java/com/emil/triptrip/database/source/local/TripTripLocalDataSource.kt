package com.emil.triptrip.database.source.local

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.emil.triptrip.database.ResultUtil
import com.emil.triptrip.database.Trip
import com.emil.triptrip.database.User
import com.emil.triptrip.database.source.TripTripDataSource


/**
 * Concrete implementation of a Publisher source as a db.
 */
class TripTripLocalDataSource(val context: Context) : TripTripDataSource {

    override suspend fun uploadUserDataToFirebase(userData: User): ResultUtil<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun getUserData(): ResultUtil<List<User>> {
        TODO("Not yet implemented")
    }

    override suspend fun uploadTripToFirebase(trip: Trip): ResultUtil<Boolean> {
        TODO("Not yet implemented")
    }
}