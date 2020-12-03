package com.emil.triptrip.ui.mytrips

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.emil.triptrip.database.source.TripTripRepository
import java.lang.IllegalArgumentException

class MyTripsViewModelFactory(private val application: Application,
                              private val repository: TripTripRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyTripsViewModel::class.java)) {
            return MyTripsViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }
}

