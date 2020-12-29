package com.emil.triptrip.database

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PostMessage(
    val userId: String? = "",
    val time: Long? = System.currentTimeMillis(),
    val content: String? = ""
): Parcelable {

}
