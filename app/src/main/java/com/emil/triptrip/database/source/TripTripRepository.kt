package com.emil.triptrip.database.source

import androidx.lifecycle.MutableLiveData
import com.emil.triptrip.database.*

/**
 * Interface to the TripTrip layers.
 */

interface TripTripRepository {

    suspend fun uploadUserDataToFirebase(userData: User): ResultUtil<Boolean>

    suspend fun getUserData(): ResultUtil<List<User>>

    suspend fun uploadTripToFirebase(trip: Trip): ResultUtil<TripIdFeedback>

    suspend fun getTrips(): ResultUtil<List<Trip>>

    suspend fun uploadSpotToFirebase(tripId: String, spotTag: SpotTag): ResultUtil<Boolean>

    suspend fun getSpots(dayKey: DayKey, tripId: String): ResultUtil<List<SpotTag>>

    suspend fun uploadMyLocation(tripId: String, myLocation: MyLocation): ResultUtil<Boolean>

    suspend fun getUsersLocation(tripId: String, myEmail: String): ResultUtil<List<MyLocation>>

    suspend fun uploadTripMainPic(localPath: String): ResultUtil<String>

    fun getLiveSpots(tripId: String): MutableLiveData<List<SpotTag>>

    suspend fun updateSpotInfo(spotData: SpotTag, tripId: String): ResultUtil<Boolean>

    suspend fun updateSpotPhoto(photoList: List<String>, tripId: String, spotId: String): ResultUtil<List<String>>

    fun getLiveUsersLocation(tripId: String): MutableLiveData<List<MyLocation>>

    fun getLiveNotificationInfo(userEmail: String): MutableLiveData<List<NotificationAddTrip>>

    suspend fun getATrip(tripId: String): ResultUtil<Trip>

    suspend fun deleteSpot(tripId: String, spotId: String): ResultUtil<Boolean>

    suspend fun sentMessage(tripId: String, message: Message): ResultUtil<Boolean>

    fun getLiveMessage(tripId: String): MutableLiveData<List<Message>>

    suspend fun modifyTrip(trip: Trip): ResultUtil<Trip>

    suspend fun updateCurrentLocation(tripId: String, latitude: Double, longitude: Double ): ResultUtil<Boolean>

    suspend fun deleteNotification(userEmail: String, notification: NotificationAddTrip): ResultUtil<Boolean>

}