package com.emil.triptrip.ui.login

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.emil.triptrip.database.source.TripTripRepository
import java.lang.IllegalArgumentException


class LoginViewModelFactory(private val application: Application, private val repository: TripTripRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }
}