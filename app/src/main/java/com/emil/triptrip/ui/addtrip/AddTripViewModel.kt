package com.emil.triptrip.ui.addtrip

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AddTripViewModel(app: Application) : AndroidViewModel(app) {

    val startDay = MutableLiveData<Long>()
    val endDay = MutableLiveData<Long>()

}