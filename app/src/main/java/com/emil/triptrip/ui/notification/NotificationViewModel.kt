package com.emil.triptrip.ui.notification

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.emil.triptrip.R
import com.emil.triptrip.TripTripApplication
import com.emil.triptrip.database.NotificationAddTrip
import com.emil.triptrip.database.ResultUtil
import com.emil.triptrip.database.Trip
import com.emil.triptrip.database.source.TripTripRepository
import com.emil.triptrip.util.LoadApiStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class NotificationViewModel(app: Application, private val repository: TripTripRepository, notificationList: List<NotificationAddTrip>) : AndroidViewModel(app) {

    private val _notificationList = MutableLiveData<List<NotificationAddTrip>>()
    val notificationList: LiveData<List<NotificationAddTrip>>
        get() = _notificationList

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

    private val _tripData = MutableLiveData<Trip>()
    val tripData: LiveData<Trip>
        get() = _tripData

    init {
        _notificationList.value = notificationList
    }

    fun getTripData(tripId: String) {
        coroutineScope.launch {
            _status.value = LoadApiStatus.LOADING

            when (val result = repository.getATrip(tripId)) {
                is ResultUtil.Success -> {
                    _error.value = null
                    _status.value = LoadApiStatus.DONE
                    leave(true)
                    Log.d("Firebase", "A trip data is ${result.data}")

                    _tripData.value = result.data

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

    fun navToTripDetailFinished() {
        _tripData.value = null
    }


    fun leave(needRefresh: Boolean = false) {
        _leave.value = needRefresh
    }


    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

}