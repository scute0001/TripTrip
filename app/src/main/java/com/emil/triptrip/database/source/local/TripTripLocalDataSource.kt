package com.emil.triptrip.database.source.local

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.emil.triptrip.database.*
import com.emil.triptrip.database.source.TripTripDataSource


/**
 * Concrete implementation of a Publisher source as a db.
 */
class TripTripLocalDataSource(val context: Context) : TripTripDataSource {
    override suspend fun uploadSpotToFirebase(
        tripId: String,
        spotTag: SpotTag
    ): ResultUtil<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun uploadMyLocation(
        tripId: String,
        myLocation: MyLocation
    ): ResultUtil<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun getSpots(dayKey: DayKey, tripId: String): ResultUtil<List<SpotTag>> {
        TODO("Not yet implemented")
    }

    override suspend fun uploadUserDataToFirebase(userData: User): ResultUtil<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun getUserData(): ResultUtil<List<User>> {
        TODO("Not yet implemented")
    }

    override suspend fun uploadTripToFirebase(trip: Trip): ResultUtil<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun getTrips(): ResultUtil<List<Trip>> {
        TODO("Not yet implemented")
    }

    override suspend fun getUsersLocation(
        tripId: String,
        myEmail: String
    ): ResultUtil<List<MyLocation>> {
        TODO("Not yet implemented")
    }

    override suspend fun uploadTripMainPic(localPath: String): ResultUtil<String> {
        TODO("Not yet implemented")
    }

    override fun getLiveSpots(tripId: String): MutableLiveData<List<SpotTag>> {
        TODO("Not yet implemented")
    }

    override suspend fun updateSpotInfo(spotData: SpotTag, tripId: String): ResultUtil<Boolean> {
        TODO("Not yet implemented")
    }
}