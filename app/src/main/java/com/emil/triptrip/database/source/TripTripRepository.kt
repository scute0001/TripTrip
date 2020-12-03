package com.emil.triptrip.database.source

import androidx.lifecycle.MutableLiveData
import com.emil.triptrip.database.ResultUtil
import com.emil.triptrip.database.User

/**
 * Interface to the TripTrip layers.
 */

interface TripTripRepository {

    suspend fun uploadUserDataToFirebase(userData: User): ResultUtil<Boolean>

    suspend fun getUserData(): ResultUtil<List<User>>
//    suspend fun loginMockData(id: String): Result<Author>
//
//    suspend fun getArticles(): Result<List<Article>>
//
//    fun getLiveArticles(): MutableLiveData<List<Article>>
//
//    suspend fun publish(article: Article): Result<Boolean>
//
//    suspend fun delete(article: Article): Result<Boolean>
}