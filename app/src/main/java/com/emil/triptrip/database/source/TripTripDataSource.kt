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

//    suspend fun login(id: String): Result<Author>
//
//    suspend fun getArticles(): Result<List<Article>>
//
//    fun getLiveArticles(): MutableLiveData<List<Article>>
//
//    suspend fun publish(article: Article): Result<Boolean>
//
//    suspend fun delete(article: Article): Result<Boolean>

}
