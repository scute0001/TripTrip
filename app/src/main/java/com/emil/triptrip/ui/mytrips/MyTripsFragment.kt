package com.emil.triptrip.ui.mytrips

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.emil.triptrip.MainActivityViewModel
import com.emil.triptrip.TripTripApplication
import com.emil.triptrip.database.AttendUser
import com.emil.triptrip.database.DayKey
import com.emil.triptrip.database.SpotTag
import com.emil.triptrip.database.Trip
import com.emil.triptrip.databinding.MyTripsFragmentBinding
import com.google.firebase.firestore.FirebaseFirestore

private const val PERMISSION_ID = 1010

class MyTripsFragment : Fragment() {


    private lateinit var viewModel: MyTripsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // data binding
        val binding = MyTripsFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        // setup viewModel
        val app = requireNotNull(activity).application
        val repository = (requireContext().applicationContext as TripTripApplication).repository
        val viewModelFactory = MyTripsViewModelFactory(app, repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MyTripsViewModel::class.java)
        binding.viewModel = viewModel

        val mainViewModel = ViewModelProvider(requireActivity()).get(MainActivityViewModel::class.java)

        // swipe down refresh
        val swipeRefresh = binding.mytripsSwipeRefresh
        swipeRefresh.setOnRefreshListener {
            viewModel.getTripsData()
            swipeRefresh.isRefreshing = false
        }

        // setup recyclerView adapter
        val adapter = TripsAdapter(viewModel)
        binding.recyclerTrips.adapter = adapter

        // submit trips data to recyclerView
        viewModel.tripsData.observe(viewLifecycleOwner, Observer { trips ->
            if (trips != null) {
                binding.textNoData.visibility = View.GONE
                adapter.submitList(trips)
            } else {
                binding.textNoData.visibility = View.VISIBLE
            }
        })

        // observe navigation to trip detail page
        viewModel.navToTripDetail.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                findNavController().navigate(MyTripsFragmentDirections.actionMyTripsFragmentToTripDetailFragment(it))
                viewModel.navToDetailPageFinished()
            }
        })

        // navigation to add trip page
        binding.buttonAddTrip.setOnClickListener {
            findNavController().navigate(MyTripsFragmentDirections.actionMyTripsFragmentToAddTripFragment())
        }


        // set search bar
//        binding.searchBar.setIconified(false);
        binding.searchBar.setOnQueryTextListener( object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.submitList(viewModel.filter(viewModel.tripsData.value!!, newText!!))
                return true
            }
        })

        // get notification data and sent to mainView Model
        viewModel.liveNotificationData.observe(viewLifecycleOwner, Observer {
            mainViewModel.notificationList.value = it
        })




        //////////////////////

//        binding.button.setOnClickListener {
//            testFirebase()
//        }
//
//        binding.button2.setOnClickListener {
//            testFirebaseSPOTTAG()
//        }

        getLocationPermission()
        return binding.root
    }

    // get permission
    private fun getLocationPermission() {

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_ID)
        }
    }



    private fun testFirebaseSPOTTAG() {
        // Access a Cloud Firestore instance from your Activity
        val db = FirebaseFirestore.getInstance()

        val testSpot = SpotTag(positionName = "this is a pen")

        db.collection("trips").document("3p2f5wkQQjr3xDAUgp77").collection("Spots")
            .add(testSpot)
            .addOnSuccessListener { documentReference ->
                Log.d("Test", "DocumentSnapshot added with ID: ${documentReference.id}")
                documentReference.update("id", documentReference.id)
                documentReference.update("daySpotsKey", "Day33p2f5wkQQjr3xDAUgp77")
            }
            .addOnFailureListener { e ->
                Log.w("test", "Error adding document", e)
            }


    }



}