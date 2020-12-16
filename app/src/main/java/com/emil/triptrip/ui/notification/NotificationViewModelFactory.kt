package com.emil.triptrip.ui.notification

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.emil.triptrip.database.NotificationAddTrip
import com.emil.triptrip.database.source.TripTripRepository
import com.emil.triptrip.ui.mytrips.MyTripsViewModel
import java.lang.IllegalArgumentException




class NotificationViewModelFactory(private val application: Application,
                                   private val repository: TripTripRepository,
                                   private val notificationList: List<NotificationAddTrip>
): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotificationViewModel::class.java)) {
            return NotificationViewModel(application, repository, notificationList) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }
}