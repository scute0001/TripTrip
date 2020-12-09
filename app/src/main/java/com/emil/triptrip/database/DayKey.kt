package com.emil.triptrip.database

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DayKey(
    var dayCount: String? = "",
    var date: String? = "",
    var dateTimeStamp: Long? = 0L,
    var daySpotsKey: String? = ""
): Parcelable {

}
