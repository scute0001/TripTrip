package com.emil.triptrip.ui.tripdetail

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.emil.triptrip.MainActivity
import com.emil.triptrip.R
import com.emil.triptrip.databinding.TripDetailFragmentBinding

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.trip_detail_fragment.view.*

class TripDetailFragment : Fragment() {

    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        val sydney = LatLng(-34.0, 151.0)
        googleMap.addMarker(MarkerOptions()
            .position(sydney)
            .title("Marker in Sydney")
            .snippet("This is a pen"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        return inflater.inflate(R.layout.fragment_trip_detail, container, false)

        val binding = TripDetailFragmentBinding.inflate(inflater, container, false)

        // get data from safe args
        val tripData = TripDetailFragmentArgs.fromBundle(requireArguments()).tripData
        Log.i("tripData", "tripData is $tripData")

        //////
//        val activity = activity as MainActivity
//        activity.toolbar_title.text = tripData.title
        //////

        // click add spot and navigation add spot page
        binding.buttenAddSpot.setOnClickListener {
            findNavController().navigate(TripDetailFragmentDirections.actionTripDetailFragmentToAddSpotFragment())
        }


        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }
}