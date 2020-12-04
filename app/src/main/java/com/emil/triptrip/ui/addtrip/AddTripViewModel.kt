package com.emil.triptrip.ui.addtrip

import android.app.Application
import android.icu.util.Calendar
import android.icu.util.TimeUnit
import android.util.Log
import android.util.TimeUtils
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.emil.triptrip.R
import com.emil.triptrip.TripTripApplication
import com.emil.triptrip.database.*
import com.emil.triptrip.database.source.TripTripRepository
import com.emil.triptrip.util.LoadApiStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.sql.Timestamp
import java.text.SimpleDateFormat

class AddTripViewModel(app: Application, private val repository: TripTripRepository) : AndroidViewModel(app) {

    val startDay = MutableLiveData<Long>()
    val endDay = MutableLiveData<Long>()

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

    // Users List for add attend user function
    private val _usersData = MutableLiveData<List<User>>()
    val usersData: LiveData<List<User>>
        get() = _usersData

    //for attend selected users data
    val _selectedUsers = MutableLiveData<List<User>>()
    val selectedUsers: LiveData<List<User>>
        get() = _selectedUsers

    // set data for XML editTEXT
    val _tripTitle = MutableLiveData<String?>()
    val _tripPhoto = MutableLiveData<String?>()

    // tripData for upload to firebase after click
    private val _tripData = MutableLiveData<Trip>()
    val tripData: LiveData<Trip>
        get() = _tripData

    // data check flag
    val _checkDataFlag = MutableLiveData<Boolean>()
    val checkDataFlag: LiveData<Boolean>
        get() = _checkDataFlag

    // for add trip data finished navigation back to myTrips page
    private val _addTripFinishedNavToMyTrips = MutableLiveData<Boolean>()
    val addTripFinishedNavToMyTrips: LiveData<Boolean>
        get() = _addTripFinishedNavToMyTrips


    // for Navigation to myTrips page finished clear
    fun clearAddTripFinishedNavToMyTrips() {
        _addTripFinishedNavToMyTrips.value = false
    }

    // uploadUserDataToFirebase
    fun getUsersData() {

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            when (val result = repository.getUserData()) {
                is ResultUtil.Success -> {
                    _error.value = null
                    _status.value = LoadApiStatus.DONE
                    leave(true)

                    // set got userData to LiveData
                    _usersData.value = result.data
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


    // add trip to firebase
    fun uploadTripToFirebase(trip: Trip) {

        coroutineScope.launch {
            _status.value = LoadApiStatus.LOADING

            when (val result = repository.uploadTripToFirebase(trip)) {
                is ResultUtil.Success -> {
                    _error.value = null
                    _status.value = LoadApiStatus.DONE
                    leave(true)

                    // set nav to mytrips
                    _addTripFinishedNavToMyTrips.value = true
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



    // set add tripData for upload to firebase
    fun setTripData() {

        // set UserData
        val userList = mutableListOf<AttendUser>()
        selectedUsers.value?.forEach {
            val temp = AttendUser(userId = it.email)
            userList.add(temp)
        }


        // set day list  ///////////////////
        val dayList = mutableListOf<DayKey>()
        startDay.value?.let { startDay ->

            var timeCounter = startDay
            val oneDaySec = (60 * 60 * 24 * 1000).toLong()
            val simpleDateFormat = SimpleDateFormat("MM/dd")
            var counter = 0

            while ( timeCounter <= endDay.value!!) {

                var date = startDay + ( oneDaySec * counter )
                val dayKey = DayKey (dayCount = "Day${counter+1}", date = simpleDateFormat.format(date))

                dayList.add(dayKey)
                timeCounter += oneDaySec
                counter++
            }
            Log.i("TimeTime", "Start time $dayList")
        }
        ///////////////////////////////////

        // check data source and set trip data
        if ( _tripTitle.value != null && startDay.value != null && endDay.value != null && selectedUsers.value != null) {
            val data = Trip(
                title = _tripTitle.value,
                stopTime = endDay.value,
                startTime = startDay.value,
                attendUserList = userList,
                dayKeyList = dayList
            )
            _tripData.value = data
        } else {
            _checkDataFlag.value = false
        }

    }



    fun clearCheckDataFlag() {
        _checkDataFlag.value = null
    }



    fun leave(needRefresh: Boolean = false) {
        _leave.value = needRefresh
    }


    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    init {
        getUsersData()
    }
}