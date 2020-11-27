package com.emil.triptrip.database

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DayKey(
    var dayCount: String? = "",
    var daySpotsKey: String? = ""
): Parcelable {

}
