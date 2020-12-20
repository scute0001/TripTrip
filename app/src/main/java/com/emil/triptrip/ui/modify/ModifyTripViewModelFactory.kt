package com.emil.triptrip.ui.modify

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.emil.triptrip.database.Trip
import com.emil.triptrip.database.source.TripTripRepository
import java.lang.IllegalArgumentException



class ModifyTripViewModelFactory(private val application: Application,
                                 private val repository: TripTripRepository,
                                 private val trip: Trip
): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ModifyTripViewModel::class.java)) {
            return ModifyTripViewModel(application, repository, trip) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }
}