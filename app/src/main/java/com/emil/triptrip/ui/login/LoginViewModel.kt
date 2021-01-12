package com.emil.triptrip.ui.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.emil.triptrip.R
import com.emil.triptrip.TripTripApplication
import com.emil.triptrip.database.ResultUtil
import com.emil.triptrip.database.User
import com.emil.triptrip.database.source.TripTripRepository
import com.emil.triptrip.util.LoadApiStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class LoginViewModel(app: Application, private val repository: TripTripRepository) : AndroidViewModel(app) {

    val userData = MutableLiveData<User>()

    // status: The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<LoadApiStatus>()
    val status: LiveData<LoadApiStatus>
        get() = _status

    // error: The internal MutableLiveData that stores the error of the most recent request
    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    private val _leave = MutableLiveData<Boolean>()
    val leave: LiveData<Boolean>
        get() = _leave

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    // for upload User to Firebase done and navigation to main page
    private val _uploadUserDataNavFlag = MutableLiveData<Boolean>()
    val uploadUserDataNavFlag: LiveData<Boolean>
        get() = _uploadUserDataNavFlag

    // uploadUserDataToFirebase
    fun uploadUserDataToFirebase(userDara: User) {
        coroutineScope.launch {
            _status.value = LoadApiStatus.LOADING

            when (val result = repository.uploadUserDataToFirebase(userDara)) {
                is ResultUtil.Success -> {
                    _error.value = null
                    _status.value = LoadApiStatus.DONE
                    leave(true)
                }
                is ResultUtil.Fail -> {
                    _error.value = result.error
                    _status.value = LoadApiStatus.ERROR
                }
                is ResultUtil.Error -> {
                    _error.value = result.exception.toString()
                    _status.value = LoadApiStatus.ERROR
                }
                else -> {
                    _error.value = TripTripApplication.instance.getString(R.string.Unknown_error)
                    _status.value = LoadApiStatus.ERROR
                }
            }
        }
    }

    fun setUploadUserDoneAndNavToMyTrips() {
        _uploadUserDataNavFlag.value = true
    }

    fun setUploadUserDoneAndNavToMyTripsFinished() {
        _uploadUserDataNavFlag.value = null
    }

    fun leave(needRefresh: Boolean = false) {
        _leave.value = needRefresh
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}