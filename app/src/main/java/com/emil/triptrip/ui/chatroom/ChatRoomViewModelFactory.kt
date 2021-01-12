package com.emil.triptrip.ui.chatroom

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.emil.triptrip.database.source.TripTripRepository
import java.lang.IllegalArgumentException



class ChatRoomViewModelFactory(private val application: Application,
                               private val repository: TripTripRepository,
                               private val tripId: String
): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatRoomViewModel::class.java)) {
            return ChatRoomViewModel(application, repository, tripId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }
}