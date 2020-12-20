package com.emil.triptrip.ui.modify

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.emil.triptrip.database.NotificationAddTrip
import com.emil.triptrip.database.Trip
import com.emil.triptrip.database.User
import com.emil.triptrip.database.source.TripTripRepository

class ModifyTripViewModel(app: Application, private val repository: TripTripRepository, private val trip: Trip) : AndroidViewModel(app) {

    // tripData for upload to firebase after click
    private val _tripData = MutableLiveData<Trip>()
    val tripData: LiveData<Trip>
        get() = _tripData

    val startDay = MutableLiveData<Long>()
    val endDay = MutableLiveData<Long>()
    val tripTitle = MutableLiveData<String>()
    val userList = MutableLiveData<List<String>>()


    init {
        _tripData.value = trip
        startDay.value = trip.startTime
        endDay.value = trip.stopTime
        tripTitle.value = trip.title
        userList.value = trip.users
    }


}