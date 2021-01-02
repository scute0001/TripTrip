package com.emil.triptrip

import androidx.lifecycle.ViewModelProvider
import com.emil.triptrip.util.ServiceLocator
import com.emil.triptrip.util.Transfer
import org.junit.Assert
import org.junit.Before
import org.junit.Test


class MyUnitTest {

    @Test
    fun testCombineToTimeStamp() {
        //輸入的時間：2021-01-01 8:10 -> 1609459800000
        //輸入的時間：2021-01-01 0:0  -> 1609430400000

        val day = 1609430400000L
        val hours = 8
        val min = 10
        Assert.assertEquals(1609459800000, Transfer().combineToTimeStamp(day, hours, min))

    }
}