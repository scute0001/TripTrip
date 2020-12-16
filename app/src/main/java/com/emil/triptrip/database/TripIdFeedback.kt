package com.emil.triptrip.database

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TripIdFeedback(
    var tripId: String? = "",
    var tripTitle: String? = "",
    var users: List<String>? = null
): Parcelable {
}