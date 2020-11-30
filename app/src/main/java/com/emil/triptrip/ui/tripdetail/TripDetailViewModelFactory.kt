package com.emil.triptrip.ui.tripdetail

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.emil.triptrip.database.Trip
import com.emil.triptrip.ui.mytrips.MyTripsViewModel
import java.lang.IllegalArgumentException


class TripDetailViewModelFactory(private val application: Application,
                                 private val tripData: Trip): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TripDetailViewModel::class.java)) {
            return TripDetailViewModel(application, tripData) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }

}