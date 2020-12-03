package com.emil.triptrip.database.source

import androidx.lifecycle.MutableLiveData
import com.emil.triptrip.database.ResultUtil
import com.emil.triptrip.database.User


/**
 * Concrete implementation to load TripTrip sources.
 */
class DefaultTripTripRepository(private val remoteDataSource: TripTripDataSource,
                                 private val localDataSource: TripTripDataSource
) : TripTripRepository {
    override suspend fun uploadUserDataToFirebase(userData: User): ResultUtil<Boolean> {
        return remoteDataSource.uploadUserDataToFirebase(userData)
    }

    //    override suspend fun loginMockData(id: String): Result<Author> {
//        return localDataSource.login(id)
//    }
//
//    override suspend fun getArticles(): Result<List<Article>> {
//        return remoteDataSource.getArticles()
//    }
//
//    override fun getLiveArticles(): MutableLiveData<List<Article>> {
//        return remoteDataSource.getLiveArticles()
//    }
//
//    override suspend fun publish(article: Article): Result<Boolean> {
//        return remoteDataSource.publish(article)
//    }
//
//    override suspend fun delete(article: Article): Result<Boolean> {
//        return remoteDataSource.delete(article)
//    }
}