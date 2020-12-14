package com.emil.triptrip

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.emil.triptrip.database.User

class MainActivityViewModel: ViewModel() {
    // Record current fragment to support data binding
    val currentFragmentType = MutableLiveData<String>()

    //
    val clickStatu = MutableLiveData<Boolean>()

    init {
        currentFragmentType.value = "TripTrip"
        clickStatu.value = false
    }


    // user: MainViewModel has User info to provide Drawer UI
    val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

}