package com.emil.triptrip.ui.modify

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.emil.triptrip.R
import com.emil.triptrip.TripTripApplication
import com.emil.triptrip.database.NotificationAddTrip
import com.emil.triptrip.database.ResultUtil
import com.emil.triptrip.database.Trip
import com.emil.triptrip.database.User
import com.emil.triptrip.database.source.TripTripRepository
import com.emil.triptrip.util.LoadApiStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ModifyTripViewModel(app: Application, private val repository: TripTripRepository, private val trip: Trip) : AndroidViewModel(app) {

    // tripData for upload to firebase after click
    private val _tripData = MutableLiveData<Trip>()
    val tripData: LiveData<Trip>
        get() = _tripData

    val startDay = MutableLiveData<Long>()
    val endDay = MutableLiveData<Long>()
    val tripTitle = MutableLiveData<String>()
    val userList = MutableLiveData<List<String>>()

    // Users List for add attend user function
    private val _allUsersData = MutableLiveData<List<User>>()
    val allUsersData: LiveData<List<User>>
        get() = _allUsersData


    // Current Users List of add attend users
    val _currentUsersData = MutableLiveData<List<User>>()
    val currentUsersData: LiveData<List<User>>
        get() = _currentUsersData


    // Un attend Users
    val unAttendUsers = MutableLiveData<List<User>>()



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


    init {
        _tripData.value = trip
        startDay.value = trip.startTime
        endDay.value = trip.stopTime
        tripTitle.value = trip.title
        userList.value = trip.users

        getAllUsersData()
    }

    // getAllUserDataToFirebase
    private fun getAllUsersData() {

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            when (val result = repository.getUserData()) {
                is ResultUtil.Success -> {
                    _error.value = null
                    _status.value = LoadApiStatus.DONE
                    leave(true)

                    // set got userData to LiveData
                    _allUsersData.value = result.data
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

    fun filterSelectUsers() {
        val list = userList.value as List
        val allUsers = allUsersData.value as List
        val notSelectUsers = mutableListOf<User>()
        val selectUsers = mutableListOf<User>()

        for (member in allUsers) {
            if (member.email in list) {
                selectUsers.add(member)
            } else {
                notSelectUsers.add(member)
            }
        }

        _currentUsersData.value = selectUsers
        unAttendUsers.value = notSelectUsers
    }


    fun leave(needRefresh: Boolean = false) {
        _leave.value = needRefresh
    }


    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

}