package com.emil.triptrip.database

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NotificationAddTrip(
    val inviterPhoto: String? = "",
    val inviterName: String? = "",
    val tripId: String? = "",
    val tripTitle: String? = "",
    val createTime: Long? = System.currentTimeMillis(),
    var status: Int? = 0
): Parcelable {
}