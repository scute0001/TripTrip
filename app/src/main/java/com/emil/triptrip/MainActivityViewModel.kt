package com.emil.triptrip

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.emil.triptrip.database.NotificationAddTrip
import com.emil.triptrip.database.User
import com.emil.triptrip.database.source.TripTripRepository
import com.emil.triptrip.ui.login.UserManager

class MainActivityViewModel(app: Application, private val repository: TripTripRepository) : AndroidViewModel(app) {

    // Record current fragment to support data binding
    val currentFragmentType = MutableLiveData<String>()

    // for back stack check if home
    val isHome = MutableLiveData<Boolean>()

    //
    val clickStatu = MutableLiveData<Boolean>()

    // user: MainViewModel has User info to provide Drawer UI
    val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    val notificationList = MutableLiveData<List<NotificationAddTrip>>()
    val navToNotificationList = MutableLiveData<List<NotificationAddTrip>>()

    // for trip chatRoom record tripId
    val selectTripId = MutableLiveData<String>()

    val navToModifyTripFlag = MutableLiveData<Boolean>()

    init {
        currentFragmentType.value = "TripTrip"
        clickStatu.value = false
        navToModifyTripFlag.value = null
    }

    fun setCurrentUser(currentUser: User) {
        _user.value = currentUser
    }

    fun clearNavToModifyTripFlag() {
        navToModifyTripFlag.value = null
    }
}