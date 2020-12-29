package com.emil.triptrip.database

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MyLocation(
    val name: String? = "",
    var longitude: Double? = null,
    var latitude: Double? = null,
    val email: String? = "",
    val photoUri: String? = ""
): Parcelable
{
}