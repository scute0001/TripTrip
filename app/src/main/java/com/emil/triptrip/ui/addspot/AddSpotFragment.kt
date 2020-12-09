package com.emil.triptrip.ui.addspot

import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.emil.triptrip.TripTripApplication
import com.emil.triptrip.databinding.AddSpotFragmentBinding
import com.emil.triptrip.ui.location.SelectMapFragment

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
        val tripId = AddSpotFragmentArgs.fromBundle(requireArguments()).tripId
        val dayKey = AddSpotFragmentArgs.fromBundle(requireArguments()).dayKey

        val viewModelFactory = AddSpotViewModelFactory(app, repository, tripId, dayKey)
        viewModel = ViewModelProvider(this, viewModelFactory).get(AddSpotViewModel::class.java)
        binding.viewModel = viewModel

        binding.textStartTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)
            TimePickerDialog(
                requireContext(),
                { timePicker, hour, minute->
                    viewModel.setStartTime(hour, minute)
                },
                hour,
                minute,
                true).show()
        }

        //set test for navigation to select map and set spot location
        binding.buttonChoiceFromMap.setOnClickListener {
            fragmentManager?.let { it1 -> SelectMapFragment(viewModel).show(it1, "select") }
        }


        // set select spot type
        binding.buttonFood.setOnClickListener {
            viewModel.setTypeFood()
        }
        binding.buttonScene.setOnClickListener {
            viewModel.setTypeScene()
        }
        binding.buttonTrans.setOnClickListener {
            viewModel.setTypeTrans()
        }
        binding.buttonHotel.setOnClickListener {
            viewModel.setTypeHotel()
        }


        viewModel.selectLocation.observe(viewLifecycleOwner, Observer {
            Log.i("TTTTT", "$it")
            viewModel.setOnSelectLocationFlag()
        })


        return binding.root
    }


}