package com.emil.triptrip.util

import com.emil.triptrip.TripTripApplication

object Util {

    fun getString(resourceId: Int): String {
        return TripTripApplication.instance.getString(resourceId)
    }

    fun getColor(resourceId: Int): Int {
        return TripTripApplication.instance.getColor(resourceId)
    }
}