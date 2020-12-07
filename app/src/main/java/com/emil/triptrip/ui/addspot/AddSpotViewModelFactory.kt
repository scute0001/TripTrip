package com.emil.triptrip.ui.addspot

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.emil.triptrip.database.source.TripTripRepository
import java.lang.IllegalArgumentException


class AddSpotViewModelFactory(private val application: Application,
                              private val repository: TripTripRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddSpotViewModel::class.java)) {
            return AddSpotViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }
}