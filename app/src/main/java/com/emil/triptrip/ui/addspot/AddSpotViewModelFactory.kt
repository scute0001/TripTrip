package com.emil.triptrip.ui.addspot

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.emil.triptrip.database.DayKey
import com.emil.triptrip.database.source.TripTripRepository
import java.lang.IllegalArgumentException


class AddSpotViewModelFactory(private val application: Application,
                              private val repository: TripTripRepository,
                              private val tripId: String,
                              private val dayKey: DayKey
): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddSpotViewModel::class.java)) {
            return AddSpotViewModel(application, repository, tripId, dayKey) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }
}