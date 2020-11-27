package com.emil.triptrip.database

import android.os.Parcelable
import com.google.firebase.firestore.GeoPoint
import kotlinx.android.parcel.Parcelize
@Parcelize
data class AttendUser(
    val userId: String? = "",
    var longitude: Double? = null,
    var latitude: Double? = null
): Parcelable {

}
