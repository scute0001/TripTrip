package com.emil.triptrip.ui.location

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import com.emil.triptrip.R
import com.emil.triptrip.databinding.FragmentSelectMapBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlin.math.abs

class SelectMapFragment : Fragment() {

    private var PERMISSION_ID = 1010
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var locationPermission = false
    private var myMap: GoogleMap? = null
    private var lastKnownLocation: Location? = null
    private var marker: Marker?= null

    private val callback = OnMapReadyCallback { googleMap ->

        myMap = googleMap
        getLocationPermission()
        getDeviceLocation()



    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val bingding = FragmentSelectMapBinding.inflate(inflater, container, false)


        bingding.button2.setOnClickListener {
            val position = myMap?.cameraPosition?.target
            Log.d("BBBBBB", "${position?.latitude} ${position?.longitude} ")

            position?.let {
                setMarker(position)
            }
        }


        return bingding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 2. init fusedLocationProviderClient and set LocationServices object
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }



    // 1. check Permission and get user get permission
    private fun getLocationPermission() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            locationPermission = false
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_ID)

        } else {
            locationPermission = true
            updateLocationUI()
        }
    }

    // 3. set map UI isMyLocationButton Enabled
    private fun updateLocationUI() {
        myMap?.apply {
            try {
                if (locationPermission) {
                    isMyLocationEnabled = true
//                    uiSettings.isMyLocationButtonEnabled = true
                } else {
//                    isMyLocationEnabled = false
//                    uiSettings.isMyLocationButtonEnabled = false
                }
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }
    }

    // 4. get permission and update LocationUI: set map UI isMyLocationButton Enabled
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_ID -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermission = true
                    // set map UI isMyLocationButton Enabled
                    updateLocationUI()
                }
            }
        }
    }

    // 5. getDeviceLocation
    private fun getDeviceLocation() {
        try {
            if (locationPermission) {
                val locationRequest = fusedLocationProviderClient.lastLocation
                locationRequest.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        lastKnownLocation = task.result
                        if (lastKnownLocation != null) {

                            myMap?.apply {
//                                addMarker(MarkerOptions()
//                                    .position(LatLng(lastKnownLocation!!.latitude, lastKnownLocation!!.longitude))
//                                    .title("It's ME!!")
//                                    .snippet("${lastKnownLocation!!.latitude}, ${lastKnownLocation!!.longitude}")
//                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_self_64)))

                                moveCamera(
                                    CameraUpdateFactory.newLatLngZoom(
                                        LatLng(lastKnownLocation!!.latitude, lastKnownLocation!!.longitude), 13f))
                            }
                        }
                    } else {
                        myMap?.uiSettings?.isMyLocationButtonEnabled = false
                    }
                }
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    private fun setMarker(position: LatLng) {
        marker?.remove()
        marker = myMap?.addMarker(MarkerOptions()
            .position(position)
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_location_64)))
    }



}



