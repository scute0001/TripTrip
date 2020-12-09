package com.emil.triptrip.ui.location

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.emil.triptrip.R
import com.emil.triptrip.databinding.FragmentSelectMapBinding
import com.emil.triptrip.ui.addspot.AddSpotViewModel
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import java.util.*


class SelectMapFragment(val viewModel: AddSpotViewModel) : DialogFragment() {

    private var PERMISSION_ID = 1010
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var locationPermission = false
    private var myMap: GoogleMap? = null
    private var lastKnownLocation: Location? = null
    private var marker: Marker?= null

    lateinit var placeClient: PlacesClient

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
        val binding = FragmentSelectMapBinding.inflate(inflater, container, false)



        // set select location
        binding.buttonAddPosition.setOnClickListener {
            val position = myMap?.cameraPosition?.target

            position?.let {
                setMarker(position)
            }
        }

        // submit location data and back

        binding.buttonSubmit.setOnClickListener {
            if (marker?.position != null) {
                viewModel._selectLocation.value = marker?.position
                dismiss()
                Toast.makeText(requireContext(), "新增成功", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "你還沒有選擇地點喔", Toast.LENGTH_SHORT).show()
            }
        }




        // Initialize the AutocompleteSupportFragment.
        val autocompleteFragment = childFragmentManager.findFragmentById(R.id.search) as AutocompleteSupportFragment
//        autocompleteFragment.view?.apply {
//            setBackgroundResource(R.drawable.shape_login_background)
//        }

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))

        // Search and get position marker
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(p0: Place) {
                val position = p0.latLng
                myMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 13F))
                p0.latLng?.let { setMarker(it) }
            }
            override fun onError(p0: Status) {
                Log.e("GoogleMap" , "An error occurred: $p0")
            }
        })


        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, android.R.style.Theme_Material_NoActionBar_Fullscreen)


        // initial placeClient
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), resources.getString(R.string.google_maps_key))
        }
        placeClient = Places.createClient(requireContext())

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



