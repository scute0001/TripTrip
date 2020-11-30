package com.emil.triptrip.ui.mytrips

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.emil.triptrip.database.AttendUser
import com.emil.triptrip.database.DayKey
import com.emil.triptrip.database.SpotTag
import com.emil.triptrip.database.Trip
import com.emil.triptrip.databinding.MyTripsFragmentBinding
import com.google.firebase.firestore.FirebaseFirestore

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
        val viewModelFactory =
            MyTripsViewModelFactory(app)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MyTripsViewModel::class.java)
        binding.viewModel = viewModel


        // setup recyclerView adapter
        val adapter = TripsAdapter(viewModel)
        binding.recyclerTrips.adapter = adapter

        // submit trips data to recyclerView
        viewModel.tripsData.observe(viewLifecycleOwner, Observer { trips ->
            if (trips != null) {
                adapter.submitList(trips)
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



//        binding.button.setOnClickListener {
//            testFirebase()
//        }
//
//        binding.button2.setOnClickListener {
//            testFirebaseSPOTTAG()
//        }

        return binding.root
    }

    private fun testFirebase() {
        // Access a Cloud Firestore instance from your Activity
        val db = FirebaseFirestore.getInstance()

        val dayKeyList = mutableListOf<DayKey>()
        for (item in 1..5) {
            dayKeyList.add(DayKey(dayCount = "Day$item",daySpotsKey = "DAY$item${item.hashCode()}"))
        }
        val attendUsers = mutableListOf<AttendUser>()
        attendUsers.add(AttendUser("QQ"))
        attendUsers.add(AttendUser("GG"))
        attendUsers.add(AttendUser("PP"))

        val testData = Trip(
            title = "",
            startTime = System.currentTimeMillis(),
            stopTime = System.currentTimeMillis(),
            status = 0,
            attendUserList = attendUsers,
            dayKeyList = dayKeyList,
            private = true
        )

        val testSpot = SpotTag(positionName = "this is a pen")




        db.collection("trips")
            .add(testData)
            .addOnSuccessListener { documentReference ->
                Log.d("Test", "DocumentSnapshot added with ID: ${documentReference.id}")
                documentReference.update("id", documentReference.id)

                val tempDayKeyList = mutableListOf<DayKey>()
                for (item in 1..testData.dayKeyList!!.size) {
                    val tempkey = "Day${item}${documentReference.id}"
                    tempDayKeyList.add(DayKey("Day$item", tempkey))
                }

                testData.dayKeyList = tempDayKeyList
                documentReference.update("dayKeyList", testData.dayKeyList)
            }
            .addOnFailureListener { e ->
                Log.w("test", "Error adding document", e)
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