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

    override suspend fun uploadTripToFirebase(trip: Trip): ResultUtil<TripIdFeedback> {
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

    override suspend fun updateSpotPhoto(
        photoList: List<String>,
        tripId: String,
        spotId: String
    ): ResultUtil<List<String>> {
        TODO("Not yet implemented")
    }

    override fun getLiveUsersLocation(tripId: String): MutableLiveData<List<MyLocation>> {
        TODO("Not yet implemented")
    }

    override fun getLiveNotificationInfo(userEmail: String): MutableLiveData<List<NotificationAddTrip>> {
        TODO("Not yet implemented")
    }

    override suspend fun getATrip(tripId: String): ResultUtil<Trip> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteSpot(tripId: String, spotId: String): ResultUtil<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun sentMessage(tripId: String, message: Message): ResultUtil<Boolean> {
        TODO("Not yet implemented")
    }

    override fun getLiveMessage(tripId: String): MutableLiveData<List<Message>> {
        TODO("Not yet implemented")
    }

    override suspend fun modifyTrip(trip: Trip): ResultUtil<Trip> {
        TODO("Not yet implemented")
    }

    override suspend fun updateCurrentLocation(
        tripId: String,
        latitude: Double,
        longitude: Double
    ): ResultUtil<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteNotification(
        userEmail: String,
        notification: NotificationAddTrip
    ): ResultUtil<Boolean> {
        TODO("Not yet implemented")
    }
}