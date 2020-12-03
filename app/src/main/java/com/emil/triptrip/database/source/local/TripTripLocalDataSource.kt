package com.emil.triptrip.database.source.local

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.emil.triptrip.database.ResultUtil
import com.emil.triptrip.database.User
import com.emil.triptrip.database.source.TripTripDataSource


/**
 * Created by Wayne Chen on 2020-01-15.
 *
 * Concrete implementation of a Publisher source as a db.
 */
class TripTripLocalDataSource(val context: Context) : TripTripDataSource {

    override suspend fun uploadUserDataToFirebase(userData: User): ResultUtil<Boolean> {
        TODO("Not yet implemented")
    }

    //    override suspend fun login(id: String): Result<Author> {
//        return when (id) {
//            "waynechen323" -> Result.Success((Author(
//                id,
//                "AKA小安老師",
//                "wayne@school.appworks.tw"
//            )))
//            "dlwlrma" -> Result.Success((Author(
//                id,
//                "IU",
//                "dlwlrma@school.appworks.tw"
//            )))
//            //TODO add your profile here
//            else -> Result.Fail("You have to add $id info in local data source")
//        }
//    }
//
//    override suspend fun getArticles(): Result<List<Article>> {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//
//    override fun getLiveArticles(): MutableLiveData<List<Article>> {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//
//    override suspend fun publish(article: Article): Result<Boolean> {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//
//    override suspend fun delete(article: Article): Result<Boolean> {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
}