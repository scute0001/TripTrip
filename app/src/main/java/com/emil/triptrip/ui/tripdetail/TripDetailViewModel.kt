package com.emil.triptrip.ui.tripdetail

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.emil.triptrip.R
import com.emil.triptrip.TripTripApplication
import com.emil.triptrip.database.*
import com.emil.triptrip.database.source.TripTripRepository
import com.emil.triptrip.ui.login.UserManager
import com.emil.triptrip.util.GlideCircleBorderTransform
import com.emil.triptrip.util.LoadApiStatus
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class TripDetailViewModel(app: Application,val tripData: Trip,private val repository: TripTripRepository
) : AndroidViewModel(app) {

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

    private val _spotsData = MutableLiveData<List<SpotTag>>()
    val spotsData: LiveData<List<SpotTag>>
        get() = _spotsData

    // need to be change by user editor
    val spotDetail = MutableLiveData<SpotTag>()
//    val spotDetail: LiveData<SpotTag>
//        get() = _spotDetail

    // for selected diff day show different Spot's Marker
    private val _markerList = MutableLiveData<List<Marker>>()
    val markerList: LiveData<List<Marker>>
        get() = _markerList

    // for users change location Marker
    private val _userMarkerList = MutableLiveData<List<Marker>>()
    val userMarkerList: LiveData<List<Marker>>
        get() = _userMarkerList

    // for selected diff day show different Spot's polyline
    private val _polyLineList = MutableLiveData<List<Polyline>>()
    val polyLineList: LiveData<List<Polyline>>
        get() = _polyLineList

    // for move camera
    val _moveToSelectedSpot = MutableLiveData<LatLng>()

    // record selected day data
    val selectedDay = MutableLiveData<String>()
    var selectedDayPosition = -1
    val refreshSelectedDayAdapter = MutableLiveData<Boolean>()

    // for navigation to add spot page data
    val selectedDayKey = MutableLiveData<DayKey>()


    // record selected day data
    val selectedTime = MutableLiveData<Long>()
    var selectedTimePosition = -1
    val refreshSelectedTimeAdapter = MutableLiveData<Boolean>()

    // for first update self location to firebase
    val _myLocation = MutableLiveData<MyLocation>()
    val myLocation: LiveData<MyLocation>
        get() = _myLocation

    // for draw users Location
    val _usersLocation = MutableLiveData<List<MyLocation>>()
    val usersLocation: LiveData<List<MyLocation>>
        get() = _usersLocation

    // get live spots data change by other users
    var liveSpotsData = MutableLiveData<List<SpotTag>>()

    // get live users location change
    var liveUsersLocation = MutableLiveData<List<MyLocation>>()


    init {
        _spotsData.value = null
        getUsersLocation()

        // set live spots and users listener
        getLiveSpots()
        getLiveUsersLocation()
    }

    fun onSelectDayAdapterRefreshed() {
        refreshSelectedDayAdapter.value = null
    }

    fun onSelectTimeAdapterRefreshed() {
        refreshSelectedTimeAdapter.value = null
    }

    fun drawSpotPosition(map: GoogleMap, spot: List<SpotTag>) {

        var startSpotLocation: LatLng? = null

        map?.apply {
            if (spot.isNotEmpty()) {
                startSpotLocation = LatLng(spot[0].latitude!!, spot[0].longitude!!)
            }

            val markerList = mutableListOf<Marker>()
            val polylineList = mutableListOf<Polyline>()

            if (spot.isNotEmpty()) {
                spot.forEach { spot ->
                    val marker = addMarker(MarkerOptions()
                        .position(LatLng(spot.latitude!!, spot.longitude!!))
                        .title(spot.positionName)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker)))
                    marker.tag = spot.positionName
                    markerList.add(marker)
                }
            }

            if (spot.size >= 2) {
                for (index in 0..spot.size - 2) {

                    val polyline = addPolyline(PolylineOptions()
                        .add(LatLng(spot[index].latitude!!, spot[index].longitude!!), LatLng(spot[index + 1].latitude!!, spot[index + 1].longitude!!))
                        .color(0xFF2286c3.toInt())
                        .width(10F)
                        .pattern(listOf(Dot(), Gap(20F), Dash(40F), Gap(20F)))
                        .endCap(CustomCap(BitmapDescriptorFactory.fromResource(R.drawable.ic_triangle_up))))
                    polylineList.add(polyline)
                }
            }



            // record current markers and polyLines
            _markerList.value = markerList
            _polyLineList.value = polylineList

            if (startSpotLocation != null) {
                moveCamera(CameraUpdateFactory.newLatLngZoom(startSpotLocation, 10F))
            }
        }
    }


    fun drawUsersLocation(map: GoogleMap, usersLocationList: List<MyLocation>) {

        val markerList = mutableListOf<Marker>()

        if (usersLocationList.isNotEmpty()) {
            usersLocationList.forEach {myLocation ->

                Glide.with(TripTripApplication.instance.applicationContext)
                    .asBitmap()
                    .load(myLocation.photoUri)
                    .apply(
//                    RequestOptions().transform(CenterCrop(), RoundedCorners(50), GlideCircleBorderTransform(100f, 10))
                    RequestOptions().transform(CenterCrop(), GlideCircleBorderTransform(135f, 0))
//                        RequestOptions().transform(GlideCircleBorderTransform(135f, 0))
                    )
                    .into( object : SimpleTarget<Bitmap>(150, 150) {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            map?.apply {
                                val marker = addMarker(MarkerOptions()
                                    .title(myLocation.name)
                                    .position(LatLng(myLocation.latitude!!, myLocation.longitude!!))
                                    .icon(BitmapDescriptorFactory.fromBitmap(resource)))
                                marker.tag = myLocation.name
                                markerList.add(marker)
                            }
                        }
                    })
            }
            _userMarkerList.value = markerList
        }

    }

    fun clearBeforeMarker() {
        markerList.value?.forEach {
            it.remove()
        }
        polyLineList.value?.forEach {
            it.remove()
        }
    }

    fun clearBeforeUserMarker() {
        userMarkerList.value?.forEach {
            it.remove()
        }
    }

    // click and set spot detail data to under page
    fun setSpotDetailData(spotName: String) {
        spotsData.value?.forEach {
            if (it.positionName == spotName) {
                spotDetail.value = it
            }
        }
    }

    // clear moveToSelectedSpot
    fun clearMoveToSelectedSpot() {
        _moveToSelectedSpot.value = null
    }


    // get selected day spots data form firebase
    fun getSpotsData(dayKey: DayKey) {

        tripData.id?.let {
            coroutineScope.launch {
                _status.value = LoadApiStatus.LOADING

                when (val result = repository.getSpots(dayKey, tripData.id)) {
                    is ResultUtil.Success -> {
                        _error.value = null
                        _status.value = LoadApiStatus.DONE
                        leave(true)
                        _spotsData.value = result.data
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


    }

    // set my Location data for upload to firebase
    fun setMyLocationData(latitude: Double?, longitude: Double?) {

        if (latitude != null && longitude != null) {

            val myLocation = MyLocation(
                name = UserManager.user.value?.name,
                email = UserManager.user.value?.email,
                photoUri = UserManager.user.value?.photoUri,
                longitude = longitude,
                latitude = latitude
            )
            _myLocation.value = myLocation
        }
    }

    // update my location to firebase
    fun uploadMyLocationData(myLocation: MyLocation) {

        tripData.id?.let {
            coroutineScope.launch {
                _status.value = LoadApiStatus.LOADING

                when (val result = repository.uploadMyLocation(tripData.id, myLocation)) {
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

    }

    // get Users location list
    fun getUsersLocation() {
        tripData.id?.let {
            UserManager.user.value?.email?.let {email ->

                coroutineScope.launch {
                    _status.value = LoadApiStatus.LOADING

                    when (val result = repository.getUsersLocation(tripData.id, email)) {
                        is ResultUtil.Success -> {
                            _error.value = null
                            _status.value = LoadApiStatus.DONE
                            leave(true)
                            _usersLocation.value = result.data
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
        }
    }

    //get live spot data when other users add spots
    private fun getLiveSpots() {
        tripData.id?.let {
            liveSpotsData = repository.getLiveSpots(tripData.id)
            _status.value = LoadApiStatus.DONE
        }

    }


    //get live Users location data when other users position change
    private fun getLiveUsersLocation() {
        tripData.id?.let {
            liveUsersLocation = repository.getLiveUsersLocation(tripData.id)
            _status.value = LoadApiStatus.DONE
        }

    }

    // set live spots to local spots list
    fun setLiveSpotsToLocal() {
        if (selectedDayKey.value?.daySpotsKey != null && selectedDayKey.value?.daySpotsKey!= "") {
            val list = liveSpotsData.value?.filter { it.daySpotsKey == selectedDayKey.value?.daySpotsKey}?.sortedBy { it.startTime }?.map { it }
            _spotsData.value = list
        }
    }

    // set live users location to local users location list
    fun setLiveUsersLocationToLocal() {
        val list = liveUsersLocation.value?.filter { it.email != UserManager.user.value?.email }
        _usersLocation.value = list
    }

    // set change spot start time
    fun changeStartTime(hours: Int, minute: Int) {
        spotDetail.value?.startDay?.let {
            spotDetail.value?.startTime =  it + (hours * 60 * 60 * 1000) + (minute * 60 * 1000)
        }
    }

    // set change spot data and update to firebase
    fun uploadChangeSpotData() {
        val data = spotDetail.value as SpotTag
        data.lastEditor = UserManager.user.value?.name
        data.lastEditTime = System.currentTimeMillis()

        // upload data to firebase
        coroutineScope.launch {
            tripData.id?.let {
                repository.updateSpotInfo(data, tripData.id)
            }
        }

        // update local select spot
        spotDetail.value = data
    }

    // upload pic to storage
    fun uploadPicToStorage(path: String) {
        coroutineScope.launch {
            _status.value = LoadApiStatus.LOADING

            when (val result = repository.uploadTripMainPic(path)) {
                is ResultUtil.Success -> {
                    _error.value = null
                    _status.value = LoadApiStatus.DONE
                    leave(true)

                    // get the fire storage download URI here and save to firebase
                    updateSpotPhotoList(photoUri = result.data)

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

    fun updateSpotPhotoList(photoUri: String) {
        // set new photoList
        val tripId = tripData.id as String
        val spotId = spotDetail.value?.id as String
        val list = spotDetail.value?.photoList as MutableList<String>
        list.add(photoUri)
        // update data to firebase
        coroutineScope.launch {
            _status.value = LoadApiStatus.LOADING

            when (val result = repository.updateSpotPhoto(list, tripId, spotId)) {
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

    // update my location to firebase done and clear data.
    fun uploadMyLocationDataFinished() {
        _myLocation.value = null
    }


    fun leave(needRefresh: Boolean = false) {
        _leave.value = needRefresh
    }


    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

}






//fun generateFakeSpot() {
//    val fakeSpots = mutableListOf<SpotTag>()
//
//    val spotA = SpotTag(
//        id = "QWER",
//        positionName = "金幣灰黃",
//        daySpotsKey = "",
//        startTime = 1605850702139,
//        stayTime = "1HR",
//        property = 1,
//        longitude = 121.5626907,
//        latitude = 25.0424825,
//        content = "金幣黃黃的",
//        lastEditor = "匿名蠑螈",
//        lastEditTime = 1605850702139,
//        photoList = mutableListOf("https://i.imgur.com/QZdKyq8.jpg", "https://i.imgur.com/QZdKyq8.jpg", "https://i.imgur.com/QZdKyq8.jpg"),
//        messages = null
//    )
//
//    val spotB = SpotTag(
//        id = "ASDF",
//        positionName = "賣噹噹",
//        daySpotsKey = "",
//        startTime = 1616058640180,
//        stayTime = "1HR",
//        property = 0,
//        longitude = 121.5152081,
//        latitude = 25.0476935,
//        content = "豬肉滿福堡，大大滿足，大大開心",
//        lastEditor = "Tzi scute",
//        lastEditTime = 1605850702139,
//        photoList = mutableListOf("https://i.imgur.com/QZdKyq8.jpg", "https://i.imgur.com/QZdKyq8.jpg", "https://i.imgur.com/QZdKyq8.jpg"),
//        messages = null
//    )
//
//    val spotC = SpotTag(
//        id = "ZXCV",
//        positionName = "喵空懶車",
//        daySpotsKey = "",
//        startTime = 16858528993988,
//        stayTime = "1HR",
//        property = 2,
//        longitude = 121.2331741,
//        latitude = 25.0774806,
//        content = "似乎要從這裡移動",
//        lastEditor = "匿名蠑螈",
//        lastEditTime = 1605850702139,
//        photoList = mutableListOf("https://i.imgur.com/QZdKyq8.jpg","https://i.imgur.com/QZdKyq8.jpg","https://i.imgur.com/QZdKyq8.jpg","https://i.imgur.com/QZdKyq8.jpg"),
//        messages = null
//    )
//
//    val spotD = SpotTag(
//        id = "ZXCV",
//        positionName = "山頂黑洞",
//        daySpotsKey = "",
//        startTime = 16858529993988,
//        stayTime = "1HR",
//        property = 3,
//        longitude = 121.5462675,
//        latitude = 25.1763029,
//        content = "重置人生(?)",
//        lastEditor = "匿名蠑螈",
//        lastEditTime = 1605850702139,
//        photoList = mutableListOf("https://i.imgur.com/QZdKyq8.jpg","https://i.imgur.com/QZdKyq8.jpg","https://i.imgur.com/QZdKyq8.jpg","https://i.imgur.com/QZdKyq8.jpg"),
//        messages = null
//    )
//
//    fakeSpots.add(spotA)
//    fakeSpots.add(spotB)
//    fakeSpots.add(spotC)
//    fakeSpots.add(spotD)
//
//    _spotsData.value = fakeSpots
//
//}
//
//
//fun generateFakeSpot2() {
//    val fakeSpots = mutableListOf<SpotTag>()
//
//    val spotC = SpotTag(
//        id = "ZXCV",
//        positionName = "台中車站",
//        daySpotsKey = "",
//        startTime = 16858528993988,
//        stayTime = "1HR",
//        property = 2,
//        longitude = 120.6844719,
//        latitude = 24.1372593,
//        content = "轉程豐原客運 C8763 路線",
//        lastEditor = "匿名蠑螈",
//        lastEditTime = 1605850702139,
//        photoList = mutableListOf("https://i.imgur.com/QZdKyq8.jpg","https://i.imgur.com/QZdKyq8.jpg","https://i.imgur.com/QZdKyq8.jpg","https://i.imgur.com/QZdKyq8.jpg"),
//        messages = null
//    )
//
//    val spotD = SpotTag(
//        id = "ZXCV",
//        positionName = "大坑",
//        daySpotsKey = "",
//        startTime = 16858529666688,
//        stayTime = "1HR",
//        property = 3,
//        longitude = 120.7318791,
//        latitude = 24.1802453,
//        content = "再怎麼累也要注意不能思考為什麼要來這裡!!",
//        lastEditor = "匿名蠑螈",
//        lastEditTime = 1605850702139,
//        photoList = mutableListOf("https://i.imgur.com/QZdKyq8.jpg","https://i.imgur.com/QZdKyq8.jpg","https://i.imgur.com/QZdKyq8.jpg","https://i.imgur.com/QZdKyq8.jpg"),
//        messages = null
//    )
//
//    fakeSpots.add(spotC)
//    fakeSpots.add(spotD)
//
//    _spotsData.value = fakeSpots
//
//}
//
//
//fun generateFakeSpot3() {
//    val fakeSpots = mutableListOf<SpotTag>()
//
//    val spotC = SpotTag(
//        id = "ZXCV",
//        positionName = "關西空港",
//        daySpotsKey = "",
//        startTime = 16858528993988,
//        stayTime = "1HR",
//        property = 2,
//        longitude = 135.2281999,
//        latitude = 34.4320024,
//        content = "一不小心就到這裡了",
//        lastEditor = "匿名蠑螈",
//        lastEditTime = 1605850702139,
//        photoList = mutableListOf("https://i.imgur.com/QZdKyq8.jpg","https://i.imgur.com/QZdKyq8.jpg","https://i.imgur.com/QZdKyq8.jpg","https://i.imgur.com/QZdKyq8.jpg"),
//        messages = null
//    )
//
////        val spotD = SpotTag(
////            id = "ZXCV",
////            positionName = "環球影城",
////            daySpotsKey = "",
////            startTime = 16858529993988,
////            stayTime = "1HR",
////            property = 3,
////            longitude = 135.4301442,
////            latitude = 34.665442,
////            content = "睡吧，夢裡甚麼都有。",
////            lastEditor = "匿名蠑螈",
////            lastEditTime = 1605850702139,
////            photoList = mutableListOf("https://i.imgur.com/QZdKyq8.jpg","https://i.imgur.com/QZdKyq8.jpg","https://i.imgur.com/QZdKyq8.jpg","https://i.imgur.com/QZdKyq8.jpg"),
////            messages = null
////        )
//
//    fakeSpots.add(spotC)
////        fakeSpots.add(spotD)
//
//    _spotsData.value = fakeSpots
//
//}





