package com.emil.triptrip.database.source.remote

import android.util.Log
import com.emil.triptrip.database.ResultUtil
import com.emil.triptrip.database.Trip
import com.emil.triptrip.database.User
import com.emil.triptrip.database.source.TripTripDataSource
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


/**
 * Implementation of the TripTrip source that from network.
 */
object TripTripRemoteDataSource : TripTripDataSource {
    private const val PATH_USER = "user"
    private const val PATH_TRIPS = "trips"


    override suspend fun uploadUserDataToFirebase(userData: User): ResultUtil<Boolean> = suspendCoroutine { continuation ->

        FirebaseFirestore.getInstance()
            .collection(PATH_USER).document("${userData.email}")
            .set(userData)
            .addOnSuccessListener {
                Log.d("Firebase", "Add user data ${it}")
                continuation.resume(ResultUtil.Success(true))
            }
            .addOnFailureListener {
                Log.d("Firebase", "Add user data error!!!!! ${it.message}")
                continuation.resume(ResultUtil.Error(it))
            }

    }

    override suspend fun getUserData(): ResultUtil<List<User>> = suspendCoroutine{ continuation ->

        FirebaseFirestore.getInstance()
            .collection(PATH_USER)
            .get()
            .addOnSuccessListener {
                val items = it.toObjects(User::class.java)
                Log.d("Firebase", "get users data ${items}")
                continuation.resume(ResultUtil.Success(items))
            }
            .addOnFailureListener {
                Log.d("Firebase", "get user data error!!!!! ${it.message}")
                continuation.resume(ResultUtil.Error(it))
            }

    }

    // add Trip data to firebase
    override suspend fun uploadTripToFirebase(trip: Trip): ResultUtil<Boolean> = suspendCoroutine { continuation ->

        FirebaseFirestore.getInstance()
            .collection(PATH_TRIPS)
            .add(trip)
            .addOnSuccessListener { documentReference ->
                Log.d("Firebase", "Add trip data ${documentReference}")

                // updata dayKey
                val newDayKey = trip.dayKeyList
                trip.dayKeyList?.let {
                    newDayKey?.forEach {
                        it.daySpotsKey = "${it.dayCount}${documentReference.id}"
                    }
                    trip.dayKeyList = newDayKey
                }


                // auto update trip ID
                documentReference.update("id" , documentReference.id)
                documentReference.update("dayKeyList", newDayKey)

                continuation.resume(ResultUtil.Success(true))
            }
            .addOnFailureListener {
                Log.d("Firebase", "Add trip data error!!!!! ${it.message}")
                continuation.resume(ResultUtil.Error(it))
            }
    }




//    private const val PATH_ARTICLES = "articles"
//    private const val KEY_CREATED_TIME = "createdTime"
//
//    override suspend fun login(id: String): Result<Author> {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//
//    override suspend fun getArticles(): Result<List<Article>> = suspendCoroutine { continuation ->
//        FirebaseFirestore.getInstance()
//            .collection(PATH_ARTICLES)
//            .orderBy(KEY_CREATED_TIME, Query.Direction.DESCENDING)
//            .get()
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    val list = mutableListOf<Article>()
//                    for (document in task.result!!) {
//                        Logger.d(document.id + " => " + document.data)
//
//                        val article = document.toObject(Article::class.java)
//                        list.add(article)
//                    }
//                    continuation.resume(Result.Success(list))
//                } else {
//                    task.exception?.let {
//
//                        Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
//                        continuation.resume(Result.Error(it))
//                        return@addOnCompleteListener
//                    }
//                    continuation.resume(Result.Fail(PublisherApplication.instance.getString(R.string.you_know_nothing)))
//                }
//            }
//    }
//
//    override fun getLiveArticles(): MutableLiveData<List<Article>> {
//
//        val liveData = MutableLiveData<List<Article>>()
//
//        FirebaseFirestore.getInstance()
//            .collection(PATH_ARTICLES)
//            .orderBy(KEY_CREATED_TIME, Query.Direction.DESCENDING)
//            .addSnapshotListener { snapshot, exception ->
//
//                Logger.i("addSnapshotListener detect")
//
//                exception?.let {
//                    Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
//                }
//
//                val list = mutableListOf<Article>()
//                for (document in snapshot!!) {
//                    Logger.d(document.id + " => " + document.data)
//
//                    val article = document.toObject(Article::class.java)
//                    list.add(article)
//                }
//
//                liveData.value = list
//            }
//        return liveData
//    }
//
//    override suspend fun publish(article: Article): Result<Boolean> = suspendCoroutine { continuation ->
//        val articles = FirebaseFirestore.getInstance().collection(PATH_ARTICLES)
//        val document = articles.document()
//
//        article.id = document.id
//        article.createdTime = Calendar.getInstance().timeInMillis
//
//        document
//            .set(article)
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    Logger.i("Publish: $article")
//
//                    continuation.resume(Result.Success(true))
//                } else {
//                    task.exception?.let {
//
//                        Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
//                        continuation.resume(Result.Error(it))
//                        return@addOnCompleteListener
//                    }
//                    continuation.resume(Result.Fail(PublisherApplication.instance.getString(R.string.you_know_nothing)))
//                }
//            }
//    }
//
//    override suspend fun delete(article: Article): Result<Boolean> = suspendCoroutine { continuation ->
//
//        when {
//            article.author?.id == "waynechen323"
//                    && article.tag.toLowerCase(Locale.TAIWAN) != "test"
//                    && article.tag.trim().isNotEmpty() -> {
//
//                continuation.resume(Result.Fail("You know nothing!! ${article.author?.name}"))
//            }
//            else -> {
//                FirebaseFirestore.getInstance()
//                    .collection(PATH_ARTICLES)
//                    .document(article.id)
//                    .delete()
//                    .addOnSuccessListener {
//                        Logger.i("Delete: $article")
//
//                        continuation.resume(Result.Success(true))
//                    }.addOnFailureListener {
//                        Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
//                        continuation.resume(Result.Error(it))
//                    }
//            }
//        }
//
//    }

}