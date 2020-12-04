package com.emil.triptrip.ui.mytrips

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.emil.triptrip.R
import com.emil.triptrip.TripTripApplication
import com.emil.triptrip.database.AttendUser
import com.emil.triptrip.database.DayKey
import com.emil.triptrip.database.ResultUtil
import com.emil.triptrip.database.Trip
import com.emil.triptrip.database.source.TripTripRepository
import com.emil.triptrip.util.LoadApiStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MyTripsViewModel(app: Application, private val repository: TripTripRepository) : AndroidViewModel(app) {

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

    init {
        _navToTripDetail.value = null
//        fakeData()
        getTripsData()
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

    //////////////////////////////////////////////////////////////////////////////////////////////////
    // Create Fake data
    fun fakeData() {
        val fakeTrips = mutableListOf<Trip>()

        val testData1 = Trip(
            title = "民主聖地五日遊",
            startTime = 1605850702139,
            stopTime = 1605850702139,
            status = 0,
            attendUserList = mutableListOf(AttendUser(userId = "Emil"), AttendUser(userId = "Nori"), AttendUser(userId = "Rukka")),
            dayKeyList = mutableListOf(DayKey("Day1","Day1QQ"),DayKey("Day2","Day2QQ"),DayKey("Day3")),
            private = true,
            mainImg = "https://www.taiwan.net.tw/att/1/big_scenic_spots/pic_R29_12.jpg"
        )

        val testData2 = Trip(
            title = "C8763",
            startTime = System.currentTimeMillis(),
            stopTime = System.currentTimeMillis(),
            status = 0,
            attendUserList = mutableListOf(AttendUser(userId = "TEAR"), AttendUser(userId = "MAMAMIYA"), AttendUser(userId = "ARARA")),
            dayKeyList = mutableListOf(DayKey("Day1","Day1YY"),DayKey("Day2","Day2YY"),DayKey("Day3")),
            private = true,
            mainImg = "https://i.imgur.com/PfFPryx.jpg"
        )

        val testData3 = Trip(
            title = "真心不騙老司機上車旅",
            startTime = 1605859702139,
            stopTime = 1606859702139,
            status = 0,
            attendUserList = mutableListOf(AttendUser(userId = "Gary"), AttendUser(userId = "BigGuy"), AttendUser(userId = "Moto")),
            dayKeyList = mutableListOf(DayKey("Day1","Day1GG"),DayKey("Day2","Day2GG"),DayKey("Day3")),
            private = true,
            mainImg = "https://api.appworks-school.tw/assets/201807242232/0.jpg"
        )
        fakeTrips.add(testData1)
        fakeTrips.add(testData2)
        fakeTrips.add(testData3)

        _tripsData.value = fakeTrips
    }


}