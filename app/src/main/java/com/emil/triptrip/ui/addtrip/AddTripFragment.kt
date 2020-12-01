package com.emil.triptrip.ui.addtrip

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.emil.triptrip.R
import com.emil.triptrip.databinding.AddTripFragmentBinding
import com.google.android.material.datepicker.MaterialDatePicker
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
        val viewModelFactory = AddTripViewModelFactory(app)
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






        return binding.root
    }


}