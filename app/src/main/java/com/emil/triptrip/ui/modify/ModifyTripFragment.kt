package com.emil.triptrip.ui.modify

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.emil.triptrip.TripTripApplication
import com.emil.triptrip.databinding.ModifyTripFragmentBinding
import com.emil.triptrip.ui.addtrip.AttendUsersAdapter
import com.emil.triptrip.ui.dialog.ModifyUsersDialog
import com.google.android.material.datepicker.MaterialDatePicker


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

        // change modify data true and set modify data
        binding.buttonSubmitModify.setOnClickListener {
            showDialog()
        }


        // set modify users click
        binding.buttonModifyAttendUser.setOnClickListener {
            fragmentManager?.let { it1 -> ModifyUsersDialog(viewModel.unAttendUsers.value!!, viewModel).show(it1, "Modify") }
        }

        // set backpress on cancel btn
        binding.buttonCancelSubmitModify.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        viewModel.allUsersData.observe(viewLifecycleOwner, androidx.lifecycle.Observer { allUsers ->
            allUsers?.let {
                viewModel.filterSelectUsers()
            }
        })

        viewModel.currentUsersData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            adapter.submitList(it.toList())
        })


        viewModel.modifyDataFlag.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it == true) {
                //call update firebase here
                viewModel.modifyTripToFirebase()
                viewModel.modifyDataFinished()
            }
        })

        viewModel.navToTripDetailData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {trip ->
            trip?.let {
                //show success dialog
                findNavController().navigate(ModifyTripFragmentDirections.actionModifyTripFragmentToTripDetailFragment(trip))
                viewModel.navToTripDetailFinished()
            }
        })




        return binding.root
    }

    private fun showDialog() {

        val alertDialog = AlertDialog.Builder(requireContext())
        alertDialog.apply {
            setTitle("設定")
            setMessage("確定要變更設定?")
            setPositiveButton("確定", DialogInterface.OnClickListener { dialog, which ->
                viewModel.modifyData()
            })
            setNegativeButton("取消", DialogInterface.OnClickListener { dialog, which ->

            })
            setCancelable(false)
        }.show()
    }


}