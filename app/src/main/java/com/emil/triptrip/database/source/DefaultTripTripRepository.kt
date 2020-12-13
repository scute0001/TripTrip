package com.emil.triptrip.database.source

import androidx.lifecycle.MutableLiveData
import com.emil.triptrip.database.*


/**
 * Concrete implementation to load TripTrip sources.
 */
class DefaultTripTripRepository(private val remoteDataSource: TripTripDataSource,
                                 private val localDataSource: TripTripDataSource
) : TripTripRepository {
    override suspend fun uploadUserDataToFirebase(userData: User): ResultUtil<Boolean> {
        return remoteDataSource.uploadUserDataToFirebase(userData)
    }

    override suspend fun getUserData(): ResultUtil<List<User>> {
        return remoteDataSource.getUserData()
    }

    override suspend fun uploadTripToFirebase(trip: Trip): ResultUtil<Boolean> {
        return remoteDataSource.uploadTripToFirebase(trip)
    }

    override suspend fun getTrips(): ResultUtil<List<Trip>> {
        return remoteDataSource.getTrips()
    }

    override suspend fun uploadSpotToFirebase(
        tripId: String,
        spotTag: SpotTag
    ): ResultUtil<Boolean> {
        return remoteDataSource.uploadSpotToFirebase(tripId, spotTag)
    }

    override suspend fun getSpots(dayKey: DayKey, tripId: String): ResultUtil<List<SpotTag>> {
        return remoteDataSource.getSpots(dayKey, tripId)
    }

    override suspend fun uploadMyLocation(
        tripId: String,
        myLocation: MyLocation
    ): ResultUtil<Boolean> {
        return remoteDataSource.uploadMyLocation(tripId, myLocation)
    }

    override suspend fun getUsersLocation(
        tripId: String,
        myEmail: String
    ): ResultUtil<List<MyLocation>> {
        return remoteDataSource.getUsersLocation(tripId, myEmail)
    }

    override suspend fun uploadTripMainPic(localPath: String): ResultUtil<String> {
        return remoteDataSource.uploadTripMainPic(localPath)
    }

    override fun getLiveSpots(tripId: String): MutableLiveData<List<SpotTag>> {
        return remoteDataSource.getLiveSpots(tripId)
    }

    override suspend fun updateSpotInfo(spotData: SpotTag, tripId: String): ResultUtil<Boolean> {
        return remoteDataSource.updateSpotInfo(spotData, tripId)
    }

//    override fun getLiveArticles(): MutableLiveData<List<Article>> {
//        return remoteDataSource.getLiveArticles()
//    }

}