package com.emil.triptrip.ui.addspot

import android.app.TimePickerDialog
import android.icu.util.Calendar
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
import com.emil.triptrip.databinding.AddSpotFragmentBinding
import com.emil.triptrip.ui.addtrip.AddTripViewModel
import com.emil.triptrip.ui.addtrip.AddTripViewModelFactory

class AddSpotFragment : Fragment() {

    private lateinit var viewModel: AddSpotViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = AddSpotFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        val app = requireNotNull(activity).application
        val repository = (requireContext().applicationContext as TripTripApplication).repository
        val viewModelFactory = AddSpotViewModelFactory(app, repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(AddSpotViewModel::class.java)
        binding.viewModel = viewModel


        binding.textStartTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)
            TimePickerDialog(
                requireContext(),
                { timePicker, hour, minute->
                    Log.i("TTTTTTT", "$timePicker , $hour, $minute")
                },
                hour,
                minute,
                true).show()
        }




        return binding.root
    }


}