package com.emil.triptrip.database

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SpotTag(
    val id: String? = "",
    var positionName: String? = "",
    var daySpotsKey: String? = "",
    var startDay: Long? = -1L,
    var startTime: Long? = -1L,
    var stayTime: String? = "",
    var property: Int? = -1,
    var longitude: Double? = null,
    var latitude: Double? = null,
    var content: String? = null,
    var lastEditor: String? = "",
    var lastEditTime: Long = -1L,
    var photoList: MutableList<String>? = null,
    var messages: MutableList<PostMessage>? = null

): Parcelable {

}
