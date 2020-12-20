package com.emil.triptrip.ui.modify

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.emil.triptrip.R
import com.emil.triptrip.TripTripApplication
import com.emil.triptrip.databinding.ModifyTripFragmentBinding
import com.emil.triptrip.ui.notification.NotificationFragmentArgs
import com.emil.triptrip.ui.notification.NotificationViewModel
import com.emil.triptrip.ui.notification.NotificationViewModelFactory

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



        return binding.root
    }


}