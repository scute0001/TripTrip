package com.emil.triptrip

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.emil.triptrip.database.Trip
import com.emil.triptrip.database.source.TripTripRepository
import com.emil.triptrip.ui.tripdetail.TripDetailViewModel
import java.lang.IllegalArgumentException



class MainActivityViewModelFactory(private val application: Application,
                                 private val repository: TripTripRepository
): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainActivityViewModel::class.java)) {
            return MainActivityViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }
}