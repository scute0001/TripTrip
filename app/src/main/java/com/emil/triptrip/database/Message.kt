package com.emil.triptrip.database

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Message(
    val id: String? = "",
    val name: String? = "",
    val image: String? = "",
    var time: Long? = System.currentTimeMillis(),
    var message: String? = ""
): Parcelable {
}