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

    //    override suspend fun loginMockData(id: String): Result<Author> {
//        return localDataSource.login(id)
//    }
//
//    override suspend fun getArticles(): Result<List<Article>> {
//        return remoteDataSource.getArticles()
//    }
//
//    override fun getLiveArticles(): MutableLiveData<List<Article>> {
//        return remoteDataSource.getLiveArticles()
//    }
//
//    override suspend fun publish(article: Article): Result<Boolean> {
//        return remoteDataSource.publish(article)
//    }
//
//    override suspend fun delete(article: Article): Result<Boolean> {
//        return remoteDataSource.delete(article)
//    }
}