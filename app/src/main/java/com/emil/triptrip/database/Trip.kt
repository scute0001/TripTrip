package com.emil.triptrip.database

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Trip(
    val id: String? = "",
    val title: String? = "",
    var startTime: Long? = -1L,
    var stopTime: Long? = -1L,
    var status: Int? = -1,
    var attendUserList: MutableList<AttendUser>? = null,
    var dayKeyList: MutableList<DayKey>? = null,
    // route line class, wait addition
    var private: Boolean? = true,
    var mainImg: String? = "https://i.imgur.com/QZdKyq8.jpg"
): Parcelable {

}