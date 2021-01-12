package com.emil.triptrip

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.IBinder
import android.util.Log
import com.emil.triptrip.database.source.TripTripRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*
import kotlin.concurrent.schedule

class PositionUpdateService: Service() {

    val timer = Timer()

    private val PERMISSION_ID = 1010
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var lastKnownLocation: Location? = null
    private var tripId: String? = ""

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var myJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(myJob + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        // init fusedLocationProviderClient and set LocationServices object
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        Log.i("service", "service onCreate")

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        tripId = intent?.getStringExtra("MY TRIP ID")
        startTimer()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.i("service", "currentTime onBind")
        return null
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
        Log.i("service", "service onUnbind")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("service", "service onDestroy")
        stopTimer()
    }

    @SuppressLint("MissingPermission")
    private fun getDeviceLocation() {
        val locationRequest = fusedLocationProviderClient.lastLocation
        locationRequest.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                lastKnownLocation = task.result
                if (lastKnownLocation != null) {
                    Log.i("service", "location data ${lastKnownLocation!!.latitude}, ${lastKnownLocation!!.longitude}")
                    updateMyLocation(lastKnownLocation!!.latitude, lastKnownLocation!!.longitude)
                }
            }
        }
    }

    private fun startTimer(){
        var second = 0
        val REPEAT_TIME = 60
        timer.schedule(1000, 1000) {
            Log.i("service", "second is $second")
            second += 1
            if (second == REPEAT_TIME) {
                getDeviceLocation()
                second = 0
            }
        }
    }

    private fun stopTimer() {
        timer.cancel()
    }

    private fun updateMyLocation(latitude: Double, longitude: Double) {
        val repository = TripTripApplication.instance.repository
        coroutineScope.launch {
            tripId?.let {
                repository.updateCurrentLocation(it, latitude, longitude)
            }
        }
    }
}