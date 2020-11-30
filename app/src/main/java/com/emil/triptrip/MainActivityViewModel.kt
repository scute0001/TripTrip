package com.emil.triptrip

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainActivityViewModel: ViewModel() {
    // Record current fragment to support data binding
    val currentFragmentType = MutableLiveData<String>()
    init {
        currentFragmentType.value = "TripTrip"
    }

}