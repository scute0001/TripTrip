package com.emil.triptrip

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.IBinder
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.*
import kotlin.concurrent.schedule

class PositionUpdateService: Service() {

    val timer = Timer()

    private val PERMISSION_ID = 1010
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var lastKnownLocation: Location? = null

    override fun onCreate() {
        super.onCreate()
        // init fusedLocationProviderClient and set LocationServices object
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        startTimer()

        Log.i("service", "service onCreate")

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
                }
            }
        }
    }


    private fun startTimer(){

        var second = 0
        val REPEAT_TIME = 10
        timer.schedule(1000, 1000) {
            Log.i("service", "second is $second")
            second += 1
            if (second == 10) {
                getDeviceLocation()
                second -= REPEAT_TIME
            }
        }
    }

    private fun stopTimer() {
        timer.cancel()
    }


}