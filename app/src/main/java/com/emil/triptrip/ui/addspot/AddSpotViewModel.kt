package com.emil.triptrip.ui.addspot

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.emil.triptrip.R
import com.emil.triptrip.TripTripApplication
import com.emil.triptrip.database.DayKey
import com.emil.triptrip.database.ResultUtil
import com.emil.triptrip.database.SpotTag
import com.emil.triptrip.database.source.TripTripRepository
import com.emil.triptrip.ui.login.UserManager
import com.emil.triptrip.util.LoadApiStatus
import com.emil.triptrip.util.Util
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

const val TYPE_FOOD = 0
const val TYPE_SCENE = 1
const val TYPE_TRANS = 2
const val TYPE_HOTEL = 3


class AddSpotViewModel(app: Application,
                       private val repository: TripTripRepository,
                       private val tripId: String,
                       private val dayKey: DayKey) : AndroidViewModel(app)  {

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

    val selectLocation = MutableLiveData<LatLng>()
    val selectLocationString = MutableLiveData<String>()
    var selectSpotType = MutableLiveData<Int>()
    var startTime = MutableLiveData<Long>()
    var spotTitle = ""
    var content = ""
    var stayTime = ""

    // data to firebase
    val _toFirebaseSpotData = MutableLiveData<SpotTag>()
    val toFirebaseSpotData: LiveData<SpotTag>
        get() = _toFirebaseSpotData

    val _navUploadSpotSuccess = MutableLiveData<Boolean>()
    val navUploadSpotSuccess: LiveData<Boolean>
        get() = _navUploadSpotSuccess

    init {
        selectLocationString.value = Util.getString(R.string.search_from_map)
        selectSpotType.value = -1
        startTime.value = 0L
        _navUploadSpotSuccess.value = false
    }

    fun setOnSelectLocationFlag() {
        selectLocationString.value = Util.getString(R.string.selected)
    }

    fun setSpotData() {
        if (spotTitle != "" && startTime.value != 0L && selectSpotType.value != -1 && selectLocation.value != null && content != "" && stayTime != "") {
            val spotData = SpotTag(
                positionName = spotTitle,
                daySpotsKey = dayKey.daySpotsKey,
                startDay = dayKey.dateTimeStamp,
                startTime = startTime.value,
                stayTime = stayTime,
                property = selectSpotType.value,
                longitude = selectLocation.value?.longitude,
                latitude = selectLocation.value?.latitude,
                content = content,
                lastEditor = UserManager.user.value?.name,
                lastEditTime = System.currentTimeMillis()
            )

            // for default photo list
            if (spotData.photoList == null) {
                spotData.photoList = mutableListOf()
            }

            _toFirebaseSpotData.value = spotData
        } else {
            _toFirebaseSpotData.value = null
        }
    }

    fun clearToFirebaseSpotData() {
        _toFirebaseSpotData.value = null
    }

    // set spot start time
    fun setStartTime(hours: Int, minute: Int) {
        dayKey.dateTimeStamp?.let { day ->
            startTime.value = (day + (hours * 60 * 60 * 1000) + (minute * 60 * 1000)).toLong()
        }
    }

    // for click set spot type
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

    fun sendSpotData(spotData: SpotTag) {

        coroutineScope.launch {
            _status.value = LoadApiStatus.LOADING

            when (val result = repository.uploadSpotToFirebase(tripId, spotData)) {
                is ResultUtil.Success -> {
                    _error.value = null
                    _status.value = LoadApiStatus.DONE
                    leave(true)

                    // set flag for add spot success and nav to detail
                    _navUploadSpotSuccess.value = true
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

    fun navUploadSpotSuccessFinished() {
        clearToFirebaseSpotData()
        _navUploadSpotSuccess.value = null
    }

    fun leave(needRefresh: Boolean = false) {
        _leave.value = needRefresh
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}

