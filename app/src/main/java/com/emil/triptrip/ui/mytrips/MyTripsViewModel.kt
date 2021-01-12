package com.emil.triptrip.ui.mytrips

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.emil.triptrip.R
import com.emil.triptrip.TripTripApplication
import com.emil.triptrip.database.*
import com.emil.triptrip.database.source.TripTripRepository
import com.emil.triptrip.ui.login.UserManager
import com.emil.triptrip.util.LoadApiStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MyTripsViewModel(app: Application, private val repository: TripTripRepository) :
    AndroidViewModel(app) {

    private val _tripsData = MutableLiveData<List<Trip>>()
    val tripsData: LiveData<List<Trip>>
        get() = _tripsData

    val _navToTripDetail = MutableLiveData<Trip>()
    val navToTripDetail: LiveData<Trip>
        get() = _navToTripDetail

    // status: The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<LoadApiStatus>()
    val status: LiveData<LoadApiStatus>
        get() = _status

    // error: The internal MutableLiveData that stores the error of the most recent request
    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    private val _leave = MutableLiveData<Boolean>()
    val leave: LiveData<Boolean>
        get() = _leave

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    // notification live data
    var liveNotificationData = MutableLiveData<List<NotificationAddTrip>>()

    init {
        _navToTripDetail.value = null
        getTripsData()
        getNotificationLiveData()
    }

    // set notification snapshot and get notification live data
    private fun getNotificationLiveData() {
        UserManager.user.value?.email?.let {
            liveNotificationData = repository.getLiveNotificationInfo(it)
        }
    }

    // navigation to trip detail page completed and clean data
    fun navToDetailPageFinished() {
        _navToTripDetail.value = null
    }

    fun getTripsData() {
        coroutineScope.launch {
            _status.value = LoadApiStatus.LOADING

            when (val result = repository.getTrips()) {
                is ResultUtil.Success -> {
                    _error.value = null
                    _status.value = LoadApiStatus.DONE
                    leave(true)
                    Log.d("Firebase", "trip data is ${result.data}")
                    _tripsData.value = result.data
                }
                is ResultUtil.Fail -> {
                    _error.value = result.error
                    _status.value = LoadApiStatus.ERROR
                }
                is ResultUtil.Error -> {
                    _error.value = result.exception.toString()
                    _status.value = LoadApiStatus.ERROR
                }
                else -> {
                    _error.value = TripTripApplication.instance.getString(R.string.Unknown_error)
                    _status.value = LoadApiStatus.ERROR
                }
            }
        }
    }

    fun leave(needRefresh: Boolean = false) {
        _leave.value = needRefresh
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    // return query list
    fun filter(list: List<Trip>, query: String): List<Trip> {
        val lowerCaseQueryString = query.toLowerCase()
        val filteredList = mutableListOf<Trip>()
        for (trip in list) {
            val tripTitle = trip.title?.toLowerCase()
            if (tripTitle?.contains(lowerCaseQueryString) as Boolean) {
                filteredList.add(trip)
            }
        }
        return filteredList
    }
}