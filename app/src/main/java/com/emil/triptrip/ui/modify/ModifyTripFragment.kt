package com.emil.triptrip.ui.modify

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.emil.triptrip.R
import com.emil.triptrip.TripTripApplication
import com.emil.triptrip.databinding.ModifyTripFragmentBinding
import com.emil.triptrip.ui.addtrip.AttendUsersAdapter
import com.emil.triptrip.ui.dialog.ModifyUsersDialog
import com.emil.triptrip.ui.dialog.SelectUserDialog
import com.emil.triptrip.ui.notification.NotificationFragmentArgs
import com.emil.triptrip.ui.notification.NotificationViewModel
import com.emil.triptrip.ui.notification.NotificationViewModelFactory
import com.google.android.material.datepicker.MaterialDatePicker
import java.util.*

class ModifyTripFragment : Fragment() {

    private lateinit var viewModel: ModifyTripViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = ModifyTripFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this


        val trip = ModifyTripFragmentArgs.fromBundle(requireArguments()).trip
        val app = requireNotNull(activity).application
        val repository = (requireContext().applicationContext as TripTripApplication).repository
        val viewModelFactory = ModifyTripViewModelFactory(app, repository, trip)
        viewModel = ViewModelProvider(this, viewModelFactory).get(ModifyTripViewModel::class.java)

        //set data and create to xml
        binding.viewModel = viewModel


        // setup select date
        val builder = MaterialDatePicker.Builder.dateRangePicker()
        builder.setSelection(androidx.core.util.Pair(trip.startTime, trip.stopTime))
        val picker = builder.build()

        // set attend users adapter
        val adapter = AttendUsersAdapter()
        binding.recyclerModifyAttendUser.adapter = adapter

        // select date and set data to viewModel
        binding.constraintModifyTripRange.setOnClickListener {
            fragmentManager?.let { it -> picker.show(it,"") }

            picker.addOnNegativeButtonClickListener {
                picker.dismiss() }
            picker.addOnPositiveButtonClickListener {
                viewModel.startDay.value = it.first
                viewModel.endDay.value = it.second
            }
        }


        // set modify users click
        binding.buttonModifyAttendUser.setOnClickListener {
            fragmentManager?.let { it1 -> ModifyUsersDialog(viewModel.unAttendUsers.value!!, viewModel).show(it1, "Modify") }
        }

        viewModel.allUsersData.observe(viewLifecycleOwner, androidx.lifecycle.Observer { allUsers ->
            allUsers?.let {
                viewModel.filterSelectUsers()
            }
        })

        viewModel.currentUsersData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            Log.i("TTTTT", "current $it")
            adapter.submitList(it)
            adapter.notifyDataSetChanged()
        })



        return binding.root
    }


}