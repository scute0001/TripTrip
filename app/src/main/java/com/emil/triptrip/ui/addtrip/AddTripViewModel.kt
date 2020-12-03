package com.emil.triptrip.ui.addtrip

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.emil.triptrip.database.source.TripTripRepository

class AddTripViewModel(app: Application, private val repository: TripTripRepository) : AndroidViewModel(app) {

    val startDay = MutableLiveData<Long>()
    val endDay = MutableLiveData<Long>()

}