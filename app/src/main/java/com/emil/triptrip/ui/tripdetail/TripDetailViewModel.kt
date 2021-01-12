package com.emil.triptrip.ui.tripdetail

import android.app.Application
import android.graphics.Bitmap
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
import com.emil.triptrip.util.Transfer
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

    // for photo update refresh photo list
    private val _photoList = MutableLiveData<List<String>>()
    val photoList: LiveData<List<String>>
        get() = _photoList

    // get live spots data change by other users
    var liveSpotsData = MutableLiveData<List<SpotTag>>()

    // get live users location change
    var liveUsersLocation = MutableLiveData<List<MyLocation>>()

    init {
        _spotsData.value = null
        _photoList.value = null
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
//                        .color(0xFF2286c3.toInt())
                        .color(0xDDff00ac.toInt())
                        .width(15F)
                        .pattern(listOf(Dot(), Gap(10F), Dash(80F), Gap(10F)))
                        .endCap(CustomCap(BitmapDescriptorFactory.fromResource(R.drawable.ic_triangle_up_murasaki))))
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
                    RequestOptions().transform(CenterCrop(), GlideCircleBorderTransform(135f, 0))
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
            val newSpotData = spotDetail.value
            newSpotData?.startTime = Transfer().combineToTimeStamp(it, hours, minute)
            spotDetail.value = newSpotData
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

    fun delSpotData() {
        val tripId = tripData.id
        val spotId = spotDetail.value?.id
        if (tripId != null && spotId != null) {

            coroutineScope.launch {
                _status.value = LoadApiStatus.LOADING

                when (val result = repository.deleteSpot(tripId, spotId)) {
                    is ResultUtil.Success -> {
                        _error.value = null
                        _status.value = LoadApiStatus.DONE
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
                    _photoList.value = result.data

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

    // after refresh photo list to spot detail
    fun clearPhotoList() {
        _photoList.value = null
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






