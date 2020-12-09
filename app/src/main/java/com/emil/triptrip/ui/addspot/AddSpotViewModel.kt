package com.emil.triptrip.ui.addspot

import android.app.Application
import android.icu.text.SimpleDateFormat
import android.icu.util.TimeZone
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.emil.triptrip.database.DayKey
import com.emil.triptrip.database.source.TripTripRepository
import com.google.android.gms.maps.model.LatLng
import java.util.*

const val TYPE_FOOD = 0
const val TYPE_SCENE = 1
const val TYPE_TRANS = 2
const val TYPE_HOTEL = 3


class AddSpotViewModel(app: Application,
                       private val repository: TripTripRepository,
                       private val tripId: String,
                       private val dayKey: DayKey) : AndroidViewModel(app)  {



    val selectLocation = MutableLiveData<LatLng>()
    val selectLocationString = MutableLiveData<String>()
    var selectSpotType = MutableLiveData<Int>()
    var spotTitle = ""
    var startTime = 0L
    var content = ""
    var stayTime = ""




    init {
        selectLocationString.value = "從地圖搜尋"
        selectSpotType.value = -1
    }

    fun setOnSelectLocationFlag() {
        selectLocationString.value = "已選擇"
    }

    fun setSpotData() {

    }

    fun setStartTime(hours: Int, minute: Int) {
        dayKey.dateTimeStamp?.let { day ->

            startTime = (day + (hours * 60 * 60 * 1000) + (minute * 60 * 1000)).toLong()

//            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm")
//            sdf.setTimeZone(TimeZone.getTimeZone("GMT"))
//            Log.i("TTTTT", "${sdf.format(startTime)}")

        }
    }

    fun setTypeFood() {
        selectSpotType.value = TYPE_FOOD
    }

    fun setTypeScene() {
        selectSpotType.value = TYPE_SCENE
    }

    fun setTypeTrans() {
        selectSpotType.value = TYPE_TRANS
    }

    fun setTypeHotel() {
        selectSpotType.value = TYPE_HOTEL
    }
}

