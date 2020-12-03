package com.emil.triptrip.database.source

import androidx.lifecycle.MutableLiveData
import com.emil.triptrip.database.ResultUtil
import com.emil.triptrip.database.User

/**
 * Main entry point for accessing TripTrip sources.
 */
interface TripTripDataSource {

    suspend fun uploadUserDataToFirebase(userData: User): ResultUtil<Boolean>
//    suspend fun login(id: String): Result<Author>
//
//    suspend fun getArticles(): Result<List<Article>>
//
//    fun getLiveArticles(): MutableLiveData<List<Article>>
//
//    suspend fun publish(article: Article): Result<Boolean>
//
//    suspend fun delete(article: Article): Result<Boolean>

}
