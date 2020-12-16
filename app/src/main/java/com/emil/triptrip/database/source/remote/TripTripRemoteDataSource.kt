package com.emil.triptrip.database.source.remote

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.emil.triptrip.database.*
import com.emil.triptrip.database.source.TripTripDataSource
import com.emil.triptrip.ui.login.UserManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


/**
 * Implementation of the TripTrip source that from network.
 */
object TripTripRemoteDataSource : TripTripDataSource {
    private const val PATH_USER = "user"
    private const val PATH_TRIPS = "trips"
    private const val PATH_SPOTS = "spots"
    private const val PATH_MYLOCATIONS = "myLocations"
    private const val PATH_NOTIFICATION = "notification"
    private const val QUERY_MY_TRIPS = "users"
    private const val QUERY_SPOTS = "daySpotsKey"
    private const val QUERY_PHOTOLIST = "photoList"



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
    override suspend fun uploadTripToFirebase(trip: Trip): ResultUtil<TripIdFeedback> = suspendCoroutine { continuation ->

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

                // set notification data
                val result = TripIdFeedback(
                    tripId = documentReference.id,
                    tripTitle = trip.title,
                    users = trip.users
                )

                // set notification data
                val notificationData = NotificationAddTrip(
                    inviterName = UserManager.user.value?.name,
                    inviterPhoto = UserManager.user.value?.photoUri,
                    tripId = documentReference.id,
                    tripTitle = trip.title
                )
                val users = trip.users?.filter { it != UserManager.user.value?.email } as List

                // set notification data to firebase
                users.forEach { userEmail ->
                    FirebaseFirestore.getInstance()
                        .collection(PATH_USER).document(userEmail).collection(PATH_NOTIFICATION)
                        .add(notificationData)
                        .addOnSuccessListener { documentReference ->
                            Log.d("Firebase", "set $userEmail notification success")
                        }
                        .addOnFailureListener {
                            Log.d("Firebase", "Add Spot data error!!!!! ${it.message}")
                        }
                }
                continuation.resume(ResultUtil.Success(result))
            }
            .addOnFailureListener {
                Log.d("Firebase", "Add trip data error!!!!! ${it.message}")
                continuation.resume(ResultUtil.Error(it))
            }
    }

    override suspend fun getTrips(): ResultUtil<List<Trip>> = suspendCoroutine { continuation ->

        FirebaseFirestore.getInstance()
            .collection(PATH_TRIPS).whereArrayContains(QUERY_MY_TRIPS, "${UserManager.user.value?.email}")
            .get()
            .addOnSuccessListener {
                val trips = it.toObjects(Trip::class.java)
                continuation.resume(ResultUtil.Success(trips))
            }
            .addOnFailureListener {
                Log.d("Firebase", "get trip data error!!!!! ${it.message}")
                continuation.resume(ResultUtil.Error(it))
            }

    }

    override suspend fun uploadSpotToFirebase(
        tripId: String,
        spotTag: SpotTag
    ): ResultUtil<Boolean> = suspendCoroutine { continuation->

        FirebaseFirestore.getInstance()
            .collection(PATH_TRIPS).document(tripId).collection(PATH_SPOTS)
            .add(spotTag)
            .addOnSuccessListener { documentReference ->

                // auto update id
                documentReference.update("id" , documentReference.id)

                continuation.resume(ResultUtil.Success(true))
            }
            .addOnFailureListener {
                Log.d("Firebase", "Add Spot data error!!!!! ${it.message}")
                continuation.resume(ResultUtil.Error(it))
            }
    }

    override suspend fun getSpots(dayKey: DayKey, tripId: String): ResultUtil<List<SpotTag>> = suspendCoroutine { continuation ->
        FirebaseFirestore.getInstance()
            .collection(PATH_TRIPS).document(tripId).collection(PATH_SPOTS)
            .whereEqualTo(QUERY_SPOTS, "${dayKey.daySpotsKey}")
            .get()
            .addOnSuccessListener {

                val spots = it.toObjects(SpotTag::class.java)
                spots.sortBy { it.startTime }
                Log.d("Firebase", "Spots data is $spots")
                continuation.resume(ResultUtil.Success(spots))

            }
            .addOnFailureListener {
                Log.d("Firebase", "get Spots data error!!!!! ${it.message}")
                continuation.resume(ResultUtil.Error(it))
            }

    }

    override suspend fun uploadMyLocation(
        tripId: String,
        myLocation: MyLocation
    ): ResultUtil<Boolean> = suspendCoroutine { continuation ->

        FirebaseFirestore.getInstance()
            .collection(PATH_TRIPS).document(tripId).collection(PATH_MYLOCATIONS)
            .document(myLocation.email as String)
            .set(myLocation)
            .addOnSuccessListener { documentReference ->
                Log.d("Firebase", "Update my location data is success")
                continuation.resume(ResultUtil.Success(true))
            }
            .addOnFailureListener {
                Log.d("Firebase", "Add Spot data error!!!!! ${it.message}")
                continuation.resume(ResultUtil.Error(it))
            }
    }

    override suspend fun getUsersLocation(
        tripId: String,
        myEmail: String
    ): ResultUtil<List<MyLocation>> = suspendCoroutine{ continuation ->

        FirebaseFirestore.getInstance()
            .collection(PATH_TRIPS).document(tripId).collection(PATH_MYLOCATIONS).whereNotEqualTo("email", myEmail)
            .get()
            .addOnSuccessListener {
                val usersLocationList = it.toObjects(MyLocation::class.java)
                usersLocationList.forEach {
                    Log.d("Firebase", "usersLocationList is $it")
                }
                continuation.resume(ResultUtil.Success(usersLocationList))
            }
            .addOnFailureListener {
                Log.d("Firebase", "get usersLocation data error!!!!! ${it.message}")
                continuation.resume(ResultUtil.Error(it))
            }

    }

    override suspend fun uploadTripMainPic(localPath: String): ResultUtil<String> = suspendCoroutine {continuation ->

        // Create a storage reference from our app
        var storageRef = FirebaseStorage.getInstance().getReference()

        var file = Uri.fromFile(File(localPath))
        var imagesRef= storageRef.child("images/${file.lastPathSegment}")
        val uploadTask = imagesRef.putFile(file)

        // Register observers to listen for when the download is done or if it fails
        uploadTask
            .addOnSuccessListener { taskSnapshot ->
                // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
                val storagePath = taskSnapshot.metadata?.path as String
                storageRef.child(storagePath).downloadUrl
                    .addOnSuccessListener {
                        val uri = it
                        Log.d("Firebase", "picture uri $uri")
                        continuation.resume(ResultUtil.Success(uri.toString()))
                    }
                    .addOnFailureListener {
                        continuation.resume(ResultUtil.Error(it))
                    }
            }
            .addOnFailureListener {
            // Handle unsuccessful uploads
            continuation.resume(ResultUtil.Error(it))
            }

    }

    override fun getLiveSpots(tripId: String): MutableLiveData<List<SpotTag>> {
        val liveData = MutableLiveData<List<SpotTag>>()

        FirebaseFirestore.getInstance()
            .collection(PATH_TRIPS).document(tripId).collection(PATH_SPOTS)
            .addSnapshotListener { snapshot, error ->
                Log.i("Firebase", "add Spots SnapshotListener detect")

                error?.let {
                    Log.w("Firebase", "[${this::class.simpleName}] Error getting documents. ${it.message}")
                }

                val list = mutableListOf<SpotTag>()
                for (document in snapshot!!) {

                    val spots = document.toObject(SpotTag::class.java)
                    list.add(spots)
                }

                Log.d("Firebase", "Change Spot list is $list")
                liveData.value = list
            }
        return liveData
    }

    override suspend fun updateSpotInfo(spotData: SpotTag, tripId: String): ResultUtil<Boolean> = suspendCoroutine {continuation->

        FirebaseFirestore.getInstance()
            .collection(PATH_TRIPS).document(tripId).collection(PATH_SPOTS).document(spotData.id!!)
            .set(spotData)
            .addOnSuccessListener { documentReference ->

                continuation.resume(ResultUtil.Success(true))
            }
            .addOnFailureListener {
                Log.d("Firebase", "Add Spot data error!!!!! ${it.message}")
                continuation.resume(ResultUtil.Error(it))
            }
    }

    override suspend fun updateSpotPhoto(
        photoList: List<String>,
        tripId: String,
        spotId: String
    ): ResultUtil<Boolean> = suspendCoroutine{continuation ->
        FirebaseFirestore.getInstance()
            .collection(PATH_TRIPS).document(tripId).collection(PATH_SPOTS).document(spotId)
            .update(QUERY_PHOTOLIST, photoList)
            .addOnSuccessListener { documentReference ->

                continuation.resume(ResultUtil.Success(true))
            }
            .addOnFailureListener {
                Log.d("Firebase", "Add Spot data error!!!!! ${it.message}")
                continuation.resume(ResultUtil.Error(it))
            }
    }

    override fun getLiveUsersLocation(tripId: String): MutableLiveData<List<MyLocation>> {
        val liveData = MutableLiveData<List<MyLocation>>()

        FirebaseFirestore.getInstance()
            .collection(PATH_TRIPS).document(tripId).collection(PATH_MYLOCATIONS)
            .addSnapshotListener { snapshot, error ->
                Log.i("Firebase", "add Users SnapshotListener detect")

                error?.let {
                    Log.w("Firebase", "[${this::class.simpleName}] Error getting documents. ${it.message}")
                }

                val list = mutableListOf<MyLocation>()
                for (document in snapshot!!) {

                    val location = document.toObject(MyLocation::class.java)
                    list.add(location)
                }

                Log.d("Firebase", "Change Users Location is $list")
                liveData.value = list
            }
        return liveData
    }


//

    override fun getLiveNotificationInfo(userEmail: String): MutableLiveData<List<NotificationAddTrip>> {

        val liveData = MutableLiveData<List<NotificationAddTrip>>()
        FirebaseFirestore.getInstance()
            .collection(PATH_USER).document(userEmail).collection(PATH_NOTIFICATION)
            .addSnapshotListener { snapshot, error ->
                val list = mutableListOf<NotificationAddTrip>()

                Log.i("Firebase", "add notification SnapshotListener detect")

                error?.let {
                    Log.w("Firebase", "[${this::class.simpleName}] Error getting documents. ${it.message}")
                }

                for (document in snapshot!!) {

                    val notification = document.toObject(NotificationAddTrip::class.java)
                    list.add(notification)
                }

                liveData.value = list
            }
        return liveData
    }

    override suspend fun getATrip(tripId: String): ResultUtil<Trip> = suspendCoroutine{continuation ->

        FirebaseFirestore.getInstance()
            .collection(PATH_TRIPS).document(tripId)
            .get()
            .addOnSuccessListener {
                val trip = it.toObject(Trip::class.java)
                trip?.let {
                    continuation.resume(ResultUtil.Success(trip))
                }

            }
            .addOnFailureListener {
                Log.d("Firebase", "get trip data error!!!!! ${it.message}")
                continuation.resume(ResultUtil.Error(it))
            }
    }
}



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
