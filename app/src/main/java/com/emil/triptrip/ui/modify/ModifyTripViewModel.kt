package com.emil.triptrip.ui.modify

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.emil.triptrip.R
import com.emil.triptrip.TripTripApplication
import com.emil.triptrip.database.*
import com.emil.triptrip.database.source.TripTripRepository
import com.emil.triptrip.util.LoadApiStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat

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

    private val _modifyDataFlag = MutableLiveData<Boolean>()
    val modifyDataFlag: LiveData<Boolean>
        get() = _modifyDataFlag

    private val _navToTripDetailData = MutableLiveData<Trip>()
    val navToTripDetailData: LiveData<Trip>
        get() = _navToTripDetailData


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


    // set modify tripData for upload to firebase
    fun setTripData() {

        // set UserData
        val userList = mutableListOf<String>()
        val attendUserList = mutableListOf<AttendUser>()
        val selectedUsers = currentUsersData.value
        selectedUsers?.forEach {
            it.email?.let { email ->
                userList.add(email)
                attendUserList.add(AttendUser(userId = it.name, photo = it.photoUri))
            }
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
                val dayKey = DayKey (dayCount = "Day${counter+1}", date = simpleDateFormat.format(date), dateTimeStamp = date, daySpotsKey = "Day${counter+1}${trip.id}")

                dayList.add(dayKey)
                timeCounter += oneDaySec
                counter++
            }
            Log.i("TimeTime", "Start time $dayList")
        }
        ///////////////////////////////////

        // check data source and set modify trip data
        val currentTripData = tripData.value as Trip
        currentTripData.title = tripTitle.value
        currentTripData.dayKeyList = dayList
        currentTripData.users = userList
        currentTripData.startTime = startDay.value
        currentTripData.stopTime = endDay.value
        currentTripData.attendUserList = attendUserList

        _tripData.value = currentTripData

    }

    // modify trip to firebase
    fun modifyTripToFirebase() {

        coroutineScope.launch {
            _status.value = LoadApiStatus.LOADING

            when (val result = repository.modifyTrip(tripData.value as Trip)) {
                is ResultUtil.Success -> {
                    _error.value = null
                    _status.value = LoadApiStatus.DONE
                    leave(true)
                    Log.d("Firebase", "result of add trip is $result")

                    // set nav to mytrips
                    _navToTripDetailData.value = result.data
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
        _navToTripDetailData.value = null
    }

    fun modifyData() {
        _modifyDataFlag.value = true
        setTripData()
    }

    fun modifyDataFinished() {
        _modifyDataFlag.value = null
    }


    fun leave(needRefresh: Boolean = false) {
        _leave.value = needRefresh
    }


    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

}