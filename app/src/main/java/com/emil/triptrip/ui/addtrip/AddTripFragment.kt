package com.emil.triptrip.ui.addtrip

import android.icu.text.SimpleDateFormat
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.emil.triptrip.R
import com.emil.triptrip.TripTripApplication
import com.emil.triptrip.databinding.AddTripFragmentBinding
import com.emil.triptrip.ui.dialog.SelectUserDialog
import com.google.android.material.datepicker.MaterialDatePicker
import java.time.ZoneId
import java.util.*
import kotlin.concurrent.fixedRateTimer

class AddTripFragment : Fragment() {

    private lateinit var viewModel: AddTripViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = AddTripFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        val app = requireNotNull(activity).application
        val repository = (requireContext().applicationContext as TripTripApplication).repository
        val viewModelFactory = AddTripViewModelFactory(app, repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(AddTripViewModel::class.java)
        binding.viewModel = viewModel



        // setup select date
        val builder = MaterialDatePicker.Builder.dateRangePicker()
        val now = Calendar.getInstance()
        builder.setSelection(androidx.core.util.Pair(now.timeInMillis, now.timeInMillis))
        val picker = builder.build()

        // select date and set data to viewModel
        binding.constraintAddTripRange.setOnClickListener {
            fragmentManager?.let { it -> picker.show(it,"") }

            picker.addOnNegativeButtonClickListener {
                picker.dismiss() }
            picker.addOnPositiveButtonClickListener {
                viewModel.startDay.value = it.first
                viewModel.endDay.value = it.second
            }
        }

        // set attend users adapter
        val adapter = AttendUsersAdapter()
        binding.recyclerAddAttendUser.adapter = adapter

        // observe all users DATA get from firebase
        viewModel.usersData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {

        })


        // observe selected users data and to adapter
        viewModel.selectedUsers.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            adapter.submitList(it)
        })

        // observe tripData
        viewModel.tripData.observe(viewLifecycleOwner, androidx.lifecycle.Observer { tripData ->
            Log.d("TripData", "TripData is $tripData")
            if (tripData != null) {
                // submit data to firebase here
                viewModel.uploadTripToFirebase(tripData)
            }
        })

        // add data finished and nav to mytrips
        viewModel.addTripFinishedNavToMyTrips.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it == true) {
                Toast.makeText(requireContext(), "新增旅程成功", Toast.LENGTH_SHORT).show()
                findNavController().navigate(AddTripFragmentDirections.actionAddTripFragmentToMyTripsFragment())
                viewModel.clearAddTripFinishedNavToMyTrips()
            }
        })

        // check data if completed
        viewModel.checkDataFlag.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it == false) {
                Toast.makeText(requireContext(), resources.getText(R.string.input_data_lack), Toast.LENGTH_SHORT).show()
                viewModel.clearCheckDataFlag()
            }
        })


        binding.buttonAddAttendUser.setOnClickListener {
            fragmentManager?.let { it1 -> SelectUserDialog(viewModel.usersData.value!!, viewModel).show(it1, "GOGO") }
        }

        // set trip data and submit to firebase
        binding.buttonSubmitTrip.setOnClickListener {
            viewModel.setTripData()
        }





        return binding.root
    }


}