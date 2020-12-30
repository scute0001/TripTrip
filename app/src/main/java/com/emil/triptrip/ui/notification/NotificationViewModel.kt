package com.emil.triptrip.ui.notification

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.emil.triptrip.R
import com.emil.triptrip.TripTripApplication
import com.emil.triptrip.database.NotificationAddTrip
import com.emil.triptrip.database.ResultUtil
import com.emil.triptrip.database.Trip
import com.emil.triptrip.database.source.TripTripRepository
import com.emil.triptrip.ui.login.UserManager
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

    private var _liveNotificationData = MutableLiveData<List<NotificationAddTrip>>()
    val liveNotificationData: LiveData<List<NotificationAddTrip>>
        get() = _liveNotificationData

    init {
//        _notificationList.value = notificationList
        getNotificationLiveData()
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

    fun deleteNotification(notification: NotificationAddTrip) {
        val userEmail = UserManager.user.value?.email as String

        coroutineScope.launch {
            _status.value = LoadApiStatus.LOADING

            when (val result = repository.deleteNotification(userEmail, notification)) {
                is ResultUtil.Success -> {
                    _error.value = null
                    _status.value = LoadApiStatus.DONE
                    leave(true)

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

    // set notification snapshot and get notification live data
    private fun getNotificationLiveData() {
        UserManager.user.value?.email?.let {
            _liveNotificationData = repository.getLiveNotificationInfo(it)
        }
    }

    // set notification snapshot and get notification live data
    fun setLiveNotifyToShow(notificationList: List<NotificationAddTrip>) {
        _notificationList.value = notificationList.filter { it.status == 0 }
    }

}