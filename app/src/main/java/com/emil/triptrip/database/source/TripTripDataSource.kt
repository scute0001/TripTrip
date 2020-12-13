package com.emil.triptrip.database.source

import androidx.lifecycle.MutableLiveData
import com.emil.triptrip.database.*

/**
 * Main entry point for accessing TripTrip sources.
 */
interface TripTripDataSource {

    suspend fun uploadUserDataToFirebase(userData: User): ResultUtil<Boolean>

    suspend fun getUserData(): ResultUtil<List<User>>

    suspend fun uploadTripToFirebase(trip: Trip): ResultUtil<Boolean>

    suspend fun getTrips(): ResultUtil<List<Trip>>

    suspend fun uploadSpotToFirebase(tripId: String, spotTag: SpotTag): ResultUtil<Boolean>

    suspend fun getSpots(dayKey: DayKey, tripId: String): ResultUtil<List<SpotTag>>

    suspend fun uploadMyLocation(tripId: String, myLocation: MyLocation): ResultUtil<Boolean>

    suspend fun getUsersLocation(tripId: String, myEmail: String): ResultUtil<List<MyLocation>>

    suspend fun uploadTripMainPic(localPath: String): ResultUtil<String>

    fun getLiveSpots(tripId: String): MutableLiveData<List<SpotTag>>


//    fun getLiveArticles(): MutableLiveData<List<Article>>


}
