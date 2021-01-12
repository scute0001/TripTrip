package com.emil.triptrip.ui.login

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.emil.triptrip.TripTripApplication
import com.emil.triptrip.database.User

object UserManager {

    private const val USER_DATA = "user_data"
    private const val USER_TOKEN = "user_token"

    val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    var userToken: String? = null
        get() = TripTripApplication.instance
            .getSharedPreferences(USER_DATA, Context.MODE_PRIVATE)
            .getString(USER_TOKEN, null)
        set(value) {
            field = when(value) {
                null -> {
                    TripTripApplication.instance
                        .getSharedPreferences(USER_DATA, Context.MODE_PRIVATE).edit()
                        .remove(USER_TOKEN)
                        .apply()
                null
                }
                else -> {
                    TripTripApplication.instance
                        .getSharedPreferences(USER_DATA, Context.MODE_PRIVATE).edit()
                        .putString(USER_TOKEN, value)
                        .apply()
                    value
                }
            }
        }

    /**
     * It can be use to check login status directly
     */
    val isLoggedIn: Boolean
        get() = userToken != null

    /**
     * Clear the [userToken] and the [user]/[_user] data
     */
    fun clear() {
        userToken = null
        _user.value = null
    }

    private var lastChallengeTime: Long = 0
    private var challengeCount: Int = 0
    private const val CHALLENGE_LIMIT = 5

    /**
     * Winter is coming
     */
    fun challenge() {
        if (System.currentTimeMillis() - lastChallengeTime > 5000) {
            lastChallengeTime = System.currentTimeMillis()
            challengeCount = 0
        } else {
            if (challengeCount == CHALLENGE_LIMIT) {
                userToken = null
                Toast.makeText(TripTripApplication.instance,
                    "Logout success!",
                    Toast.LENGTH_SHORT).show()
            } else {
                challengeCount++
            }
        }
    }
}