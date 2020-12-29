package com.emil.triptrip.ui.tripdetail

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.emil.triptrip.database.Trip
import com.emil.triptrip.database.source.TripTripRepository
import com.emil.triptrip.ui.mytrips.MyTripsViewModel
import com.google.android.gms.maps.GoogleMap
import java.lang.IllegalArgumentException


class TripDetailViewModelFactory(private val application: Application,
                                 private val tripData: Trip,
                                 private val repository: TripTripRepository
): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TripDetailViewModel::class.java)) {
            return TripDetailViewModel(application, tripData, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }

}