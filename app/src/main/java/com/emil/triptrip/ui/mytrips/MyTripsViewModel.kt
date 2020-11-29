package com.emil.triptrip.ui.mytrips

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.emil.triptrip.database.AttendUser
import com.emil.triptrip.database.DayKey
import com.emil.triptrip.database.Trip

class MyTripsViewModel(app: Application) : AndroidViewModel(app) {

    private val _tripsData = MutableLiveData<List<Trip>>()
    val tripsData: LiveData<List<Trip>>
        get() = _tripsData

    val _navToTripDetail = MutableLiveData<Trip>()
    val navToTripDetail: LiveData<Trip>
        get() = _navToTripDetail

    init {
        _navToTripDetail.value = null
        fakeData()
    }

    // navigation to trip detail page completed and clean data
    fun navToDetailPageFinished() {
        _navToTripDetail.value = null
    }


    // Create Fake data
    fun fakeData() {
        val fakeTrips = mutableListOf<Trip>()

        val testData1 = Trip(
            title = "民主聖地五日遊",
            startTime = 1605850702139,
            stopTime = 1605850702139,
            status = 0,
            attendUserList = mutableListOf(AttendUser(userId = "Emil"), AttendUser(userId = "Nori"), AttendUser(userId = "Rukka")),
            dayKeyList = mutableListOf(DayKey("Day1QQ","Day2QQ"),DayKey("Day3QQ")),
            private = true,
            mainImg = "https://www.taiwan.net.tw/att/1/big_scenic_spots/pic_R29_12.jpg"
        )

        val testData2 = Trip(
            title = "C8763",
            startTime = System.currentTimeMillis(),
            stopTime = System.currentTimeMillis(),
            status = 0,
            attendUserList = mutableListOf(AttendUser(userId = "TEAR"), AttendUser(userId = "MAMAMIYA"), AttendUser(userId = "ARARA")),
            dayKeyList = mutableListOf(DayKey("Day1GG","Day2GG"),DayKey("Day3GG")),
            private = true,
            mainImg = "https://i.imgur.com/PfFPryx.jpg"
        )

        val testData3 = Trip(
            title = "真心不騙老司機上車旅",
            startTime = 1605859702139,
            stopTime = 1606859702139,
            status = 0,
            attendUserList = mutableListOf(AttendUser(userId = "Gary"), AttendUser(userId = "BigGuy"), AttendUser(userId = "Moto")),
            dayKeyList = mutableListOf(DayKey("Day1GG","Day2GG"),DayKey("Day3GG")),
            private = true,
            mainImg = "https://api.appworks-school.tw/assets/201807242232/0.jpg"
        )
        fakeTrips.add(testData1)
        fakeTrips.add(testData2)
        fakeTrips.add(testData3)

        _tripsData.value = fakeTrips
    }


}