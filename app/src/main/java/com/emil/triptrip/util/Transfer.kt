package com.emil.triptrip.util


class Transfer {
    fun combineToTimeStamp(day: Long = 0L, hour: Int = 0, min: Int = 0): Long {
        return (day + (hour * 60 * 60 * 1000) + (min * 60 * 1000))
    }
}