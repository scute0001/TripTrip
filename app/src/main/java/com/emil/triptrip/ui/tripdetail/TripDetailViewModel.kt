package com.emil.triptrip.ui.tripdetail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.emil.triptrip.database.SpotTag
import com.emil.triptrip.database.Trip

class TripDetailViewModel(app: Application, tripData: Trip) : AndroidViewModel(app) {

    private val _spotsData = MutableLiveData<List<SpotTag>>()
    val spotsData: LiveData<List<SpotTag>>
        get() = _spotsData

    // record selected day data
    val selectedDay = MutableLiveData<String>()
    var selectedDayPosition = -1
    val refreshSelectedDayAdapter = MutableLiveData<Boolean>()

    // record selected day data
    val selectedTime = MutableLiveData<Long>()
    var selectedTimePosition = -1
    val refreshSelectedTimeAdapter = MutableLiveData<Boolean>()


    init {
        _spotsData.value = null
        generateFakeSpot()
    }

    fun onSelectDayAdapterRefreshed() {
        refreshSelectedDayAdapter.value = null
    }

    fun onSelectTimeAdapterRefreshed() {
        refreshSelectedTimeAdapter.value = null
    }


    private fun generateFakeSpot() {
        val fakeSpots = mutableListOf<SpotTag>()

        val spotA = SpotTag(
            id = "QWER",
            positionName = "金幣灰黃",
            daySpotsKey = "",
            startTime = 1605850702139,
            stayTime = "1HR",
            property = 1,
            longitude = 143.321,
            latitude = -35.016,
            content = "金幣黃黃的",
            lastEditor = "阿土伯",
            lastEditTime = 1605850702139,
            photoList = mutableListOf("https://i.imgur.com/PfFPryx.jpg", "https://i.imgur.com/PfFPryx.jpg", "https://i.imgur.com/PfFPryx.jpg"),
            messages = null
        )

        val spotB = SpotTag(
            id = "ASDF",
            positionName = "賣噹噹",
            daySpotsKey = "",
            startTime = 1616058640180,
            stayTime = "1HR",
            property = 1,
            longitude = 145.592,
            latitude = -34.747,
            content = "豬肉滿福堡，大大滿足，大大開心",
            lastEditor = "肥肥",
            lastEditTime = 1605850702139,
            photoList = mutableListOf("https://i.imgur.com/PfFPryx.jpg", "https://i.imgur.com/PfFPryx.jpg", "https://i.imgur.com/PfFPryx.jpg"),
            messages = null
        )

        val spotC = SpotTag(
            id = "ZXCV",
            positionName = "喵空懶車",
            daySpotsKey = "",
            startTime = 16858528993988,
            stayTime = "1HR",
            property = 1,
            longitude = 143.321,
            latitude = -35.016,
            content = "似乎要從這裡移動",
            lastEditor = "匿名蠑螈",
            lastEditTime = 1605850702139,
            photoList = mutableListOf("https://i.imgur.com/PfFPryx.jpg", "https://i.imgur.com/PfFPryx.jpg", "https://i.imgur.com/PfFPryx.jpg"),
            messages = null
        )

        fakeSpots.add(spotA)
        fakeSpots.add(spotB)
        fakeSpots.add(spotC)

        _spotsData.value = fakeSpots

    }

}




