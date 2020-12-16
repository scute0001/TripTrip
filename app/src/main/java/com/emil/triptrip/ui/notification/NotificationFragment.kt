package com.emil.triptrip.ui.notification

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.emil.triptrip.R
import com.emil.triptrip.TripTripApplication
import com.emil.triptrip.databinding.NotificationFragmentBinding
import com.emil.triptrip.ui.mytrips.TripsAdapter
import com.emil.triptrip.ui.tripdetail.TripDetailViewModel
import com.emil.triptrip.ui.tripdetail.TripDetailViewModelFactory

class NotificationFragment : Fragment() {

    private lateinit var viewModel: NotificationViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = NotificationFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this


        val notificationList = NotificationFragmentArgs.fromBundle(requireArguments()).notificationList.toList()

        val app = requireNotNull(activity).application
        val repository = (requireContext().applicationContext as TripTripApplication).repository
        val viewModelFactory = NotificationViewModelFactory(app, repository, notificationList)
        viewModel = ViewModelProvider(this, viewModelFactory).get(NotificationViewModel::class.java)

        //set data and create to xml
        binding.viewModel = viewModel

        // setup recyclerView adapter
        val adapter = NotificationAdapter(viewModel)
        binding.recyclerNotification.adapter = adapter


        viewModel.notificationList.observe(viewLifecycleOwner, Observer {notificationList ->
            if (notificationList != null) {
                adapter.submitList(notificationList)
            }
        })

        // if trip data ready and navigation to trip detail
        viewModel.tripData.observe(viewLifecycleOwner, Observer {trip ->
            trip?.let {
                findNavController().navigate(NotificationFragmentDirections.actionNotificationFragmentToTripDetailFragment(it))
                viewModel.navToTripDetailFinished()
            }
        })




        return binding.root
    }

}