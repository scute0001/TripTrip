package com.emil.triptrip.ui.tripdetail

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.emil.triptrip.MainActivityViewModel
import com.emil.triptrip.R
import com.emil.triptrip.databinding.TripDetailFragmentBinding

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class TripDetailFragment : Fragment() {
    private lateinit var viewModel: TripDetailViewModel

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

        val binding = TripDetailFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        // get data from safe args
        val tripData = TripDetailFragmentArgs.fromBundle(requireArguments()).tripData
        Log.i("tripData", "tripData is $tripData")

        val app = requireNotNull(activity).application

        //set data and create to viewModel
        val viewModelFactory = TripDetailViewModelFactory(app, tripData)
        viewModel = ViewModelProvider(this, viewModelFactory).get(TripDetailViewModel::class.java)
        binding.viewModel = viewModel


        //set select day recyclerView adapter
        val selectDayAdapter = SelectDayAdapter(viewModel)
        binding.recyclerTripSelectDays.adapter = selectDayAdapter
//        test submit
        selectDayAdapter.submitList(tripData.dayKeyList)



        //set select time recyclerView adapter
        val selectTimeAdapter = SelectTimeAdapter(viewModel)
        binding.recyclerTripSelectTime.adapter = selectTimeAdapter

        //set data to time recyclerView
        viewModel.spotsData.observe(viewLifecycleOwner, Observer {
            selectTimeAdapter.submitList(it)
        })




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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // get title bar test from bundle
        activity?.let {
            ViewModelProvider(it).get(MainActivityViewModel::class.java).apply {
                currentFragmentType.value = TripDetailFragmentArgs.fromBundle(requireArguments()).tripData.title
            }
        }
    }

}