package com.emil.triptrip.ui.addspot

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.emil.triptrip.database.source.TripTripRepository
import com.google.android.gms.maps.model.LatLng

class AddSpotViewModel(app: Application, private val repository: TripTripRepository) : AndroidViewModel(app)  {

    val _selectLocation = MutableLiveData<LatLng>()

}