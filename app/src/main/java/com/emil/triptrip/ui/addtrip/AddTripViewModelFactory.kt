package com.emil.triptrip.ui.addtrip

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.emil.triptrip.ui.mytrips.MyTripsViewModel
import java.lang.IllegalArgumentException


class AddTripViewModelFactory(private val application: Application): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddTripViewModel::class.java)) {
            return AddTripViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }
}