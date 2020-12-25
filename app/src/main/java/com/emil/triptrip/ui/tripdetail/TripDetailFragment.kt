package com.emil.triptrip.ui.tripdetail

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.util.Calendar
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.emil.triptrip.MainActivityViewModel
import com.emil.triptrip.PositionUpdateService
import com.emil.triptrip.R
import com.emil.triptrip.TripTripApplication
import com.emil.triptrip.databinding.TripDetailFragmentBinding
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetBehavior


class TripDetailFragment : Fragment() {
    private lateinit var viewModel: TripDetailViewModel

    // permission request code, just is a Int and unique.
    private val REQ_CODE_PIC_CAMERA = 9999
    private val PERMISSION_ID = 1010
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var locationPermission = false
    private var myMap: GoogleMap? = null
    private var lastKnownLocation: Location? = null
    var mapFragment: SupportMapFragment? = null
    private lateinit var locationButton:View


    private val callback = OnMapReadyCallback { googleMap ->
        // map UI setting
        googleMap.setPadding(0 , 240, 0 , 200)

        myMap = googleMap
        getLocationPermission()
        updateLocationUI()
        getDeviceLocation()
        //set marker click event
        googleMap.setOnMarkerClickListener {
            // call get spot detail api here

            // set fake data
            viewModel.setSpotDetailData(it.tag.toString())
            false
        }

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */

        //set friends Location


        // set My location btn gone
        @SuppressLint("ResourceType")
        locationButton = mapFragment?.view?.rootView?.findViewById<View>(2)!!
        // Change the visibility of my location button
        locationButton?.let {
            it.setVisibility(View.GONE)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = TripDetailFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        // get data from safe args
        val tripData = TripDetailFragmentArgs.fromBundle(requireArguments()).tripData

        val app = requireNotNull(activity).application
        val repository = (requireContext().applicationContext as TripTripApplication).repository
        val viewModelFactory = TripDetailViewModelFactory(app, tripData, repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(TripDetailViewModel::class.java)

        val mainActivityViewModel = ViewModelProvider(requireActivity()).get(MainActivityViewModel::class.java)

        // for nav to modify trip page
        mainActivityViewModel.navToModifyTripFlag.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                findNavController().navigate(TripDetailFragmentDirections.actionTripDetailFragmentToModifyTripFragment(tripData))
                mainActivityViewModel.clearNavToModifyTripFlag()
            }
        })

        //set data and create to xml
        binding.viewModel = viewModel

        //set bottom sheet controller
        val bottomBehavior = BottomSheetBehavior.from(binding.spotSheet.spotDetailSheet)
        bottomBehavior.state = BottomSheetBehavior.STATE_HIDDEN


        //set select day recyclerView adapter
        val selectDayAdapter = SelectDayAdapter(viewModel, bottomBehavior)
        binding.recyclerTripSelectDays.adapter = selectDayAdapter
//        test submit
        selectDayAdapter.submitList(tripData.dayKeyList)

        // set selected day view
        viewModel.refreshSelectedDayAdapter.observe(viewLifecycleOwner, Observer {
            it?.let {
                selectDayAdapter.notifyDataSetChanged()
                viewModel.onSelectDayAdapterRefreshed()
            }
        })

        // observe users Location and draw at map
        viewModel.usersLocation.observe(viewLifecycleOwner, Observer { usersLocationList ->
            myMap?.let {
                viewModel.clearBeforeUserMarker()
                viewModel.drawUsersLocation(it, usersLocationList) }
        })


        // observe selected DayKey and set spotsList for map
        viewModel.selectedDayKey.observe(viewLifecycleOwner, Observer { dayKey ->
            // call query selected day spots API here
            viewModel.getSpotsData(dayKey)
        })

        // observe other users add spot
        viewModel.liveSpotsData.observe(viewLifecycleOwner, Observer {
            viewModel.setLiveSpotsToLocal()
        })




        //set select time recyclerView adapter
        val selectTimeAdapter = SelectTimeAdapter(viewModel)
        binding.recyclerTripSelectTime.adapter = selectTimeAdapter

        //set data to time recyclerView and draw markers on the map
        viewModel.spotsData.observe(viewLifecycleOwner, Observer {

            it?.let {
                selectTimeAdapter.submitList(it)
                selectTimeAdapter.notifyDataSetChanged()

                // clean before markers
                viewModel.clearBeforeMarker()

                // set spot marker data
                myMap?.let { it1 -> viewModel.drawSpotPosition(it1, it) }
            }

        })

        // set selected time view
        viewModel.refreshSelectedTimeAdapter.observe(viewLifecycleOwner, Observer {
            it?.let {
                bottomBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
//                bottomBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                selectTimeAdapter.notifyDataSetChanged()
                viewModel.onSelectTimeAdapterRefreshed()
            }
        })

        // set myLocation data and update to firebase
        viewModel.myLocation.observe(viewLifecycleOwner, Observer { myLocation ->
            if (myLocation != null) {
                viewModel.uploadMyLocationData(myLocation)
                viewModel.uploadMyLocationDataFinished()
            }
        })

        // observe live users location change and set to local users data
        viewModel.liveUsersLocation.observe(viewLifecycleOwner, Observer { usersLocation ->
            viewModel.setLiveUsersLocationToLocal()
        })


        // set detail spot pics
        val spotPicsAdapter = SpotPicAdapter(viewModel)
        binding.spotSheet.recyclerSpotDetailPictures.adapter = spotPicsAdapter




        // click add spot and navigation add spot page
        binding.buttenAddSpot.setOnClickListener {
            if (viewModel.tripData.id != null && viewModel.selectedDayKey.value != null) {
                findNavController().navigate(TripDetailFragmentDirections.actionTripDetailFragmentToAddSpotFragment(
                    viewModel.tripData.id!!, viewModel.selectedDayKey.value!! ))
            } else {
                Toast.makeText(requireContext(), "請先選擇日期", Toast.LENGTH_SHORT).show()
            }
        }

        //get my position
        binding.buttenForTest.setOnClickListener {
            locationButton?.callOnClick()
        }

        // edit spot position
        binding.spotSheet.imageEdit.setOnClickListener {
            openEdit(binding)
        }

        binding.spotSheet.imageEditDone.setOnClickListener {
            closeEdit(binding)
            Toast.makeText(requireContext(), "更新資料成功", Toast.LENGTH_SHORT).show()
            // sent data to api here
            // set change data for update
            viewModel.uploadChangeSpotData()

        }

        binding.spotSheet.imageEditCancel.setOnClickListener {
            closeEdit(binding)
        }

        binding.spotSheet.imageEditDel.setOnClickListener {
            // del spot data api here
            viewModel.delSpotData()
            // show dialog
            Toast.makeText(requireContext(), "地點刪除成功", Toast.LENGTH_SHORT).show()
            // close all edit function here
            closeEdit(binding)

        }


        // change start time
        binding.spotSheet.editTextSpotDetailStartTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)
            TimePickerDialog(
                requireContext(),
                { timePicker, hour, minute->
                    viewModel.changeStartTime(hour, minute)
                },
                hour,
                minute,
                true).show()
        }

        binding.spotSheet.addPics.setOnClickListener {
            val permission = requireActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            if (permission != PackageManager.PERMISSION_GRANTED) {
                requireActivity().requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQ_CODE_PIC_CAMERA)
            }
            getLocalImg()
        }




        viewModel._moveToSelectedSpot.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                myMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(it, 12F))
                viewModel.clearMoveToSelectedSpot()
            }
        })




        // show spot detail
        viewModel.spotDetail.observe(viewLifecycleOwner, Observer { spot ->
            binding.spot = spot
            spotPicsAdapter.submitList(spot.photoList?.toList())
            binding.spotSheet.spotDetailSheet
//            bottomBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            bottomBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        })

        // refresh photo after update photo
        viewModel.photoList.observe(viewLifecycleOwner, Observer { photoList ->
            photoList?.let {
                spotPicsAdapter.submitList(photoList)
                binding.spotSheet.recyclerSpotDetailPictures.smoothScrollToPosition(photoList.size)
                viewModel.clearPhotoList()
            }
        })

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?

        updateLocationUI()
        mapFragment?.getMapAsync(callback)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // get title bar test from bundle
        activity?.let {
            ViewModelProvider(it).get(MainActivityViewModel::class.java).apply {
                currentFragmentType.value = TripDetailFragmentArgs.fromBundle(requireArguments()).tripData.title
                selectTripId.value = TripDetailFragmentArgs.fromBundle(requireArguments()).tripData.id

//                clickStatu.observe(this@TripDetailFragment, Observer { status ->
//                    Log.i("service", "$status")
//                    if (status == true) {
//                        val startIntent = Intent(context, PositionUpdateService::class.java)
//                        context?.startService(startIntent)
//                    } else {
//                        val stopIntent = Intent(context, PositionUpdateService::class.java)
//                        context?.stopService(stopIntent)
//                    }
//
//                })
            }
        }

        // start service update my position
        val startIntent = Intent(context, PositionUpdateService::class.java).putExtra(
            "MY TRIP ID",
            TripDetailFragmentArgs.fromBundle(requireArguments()).tripData.id)
        context?.startService(startIntent)


        // 2. init fusedLocationProviderClient and set LocationServices object
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
    }



    // 1. check Permission and get user get permission
    private fun getLocationPermission() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            locationPermission = false
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_ID)
        } else {
            locationPermission = true
        }
    }

    // 3. set map UI isMyLocationButton Enabled
    private fun updateLocationUI() {
        myMap?.apply {
            try {
                if (locationPermission) {
                    isMyLocationEnabled = true
//                    uiSettings.isMyLocationButtonEnabled = true
                } else {
//                    isMyLocationEnabled = false
//                    uiSettings.isMyLocationButtonEnabled = false
                }
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }
    }

    // 4. get permission and update LocationUI: set map UI isMyLocationButton Enabled
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_ID -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermission = true
                    // set map UI isMyLocationButton Enabled
                    updateLocationUI()
                }
            }

            REQ_CODE_PIC_CAMERA -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocalImg()
                } else {

                }
            }
        }
    }

    // 5. getDeviceLocation
    private fun getDeviceLocation() {
        try {
            if (locationPermission) {
                val locationRequest = fusedLocationProviderClient.lastLocation
                locationRequest.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        lastKnownLocation = task.result
                        if (lastKnownLocation != null) {

                            myMap?.apply {
//                                addMarker(MarkerOptions()
//                                    .position(LatLng(lastKnownLocation!!.latitude, lastKnownLocation!!.longitude))
//                                    .title("It's ME!!")
//                                    .snippet("${lastKnownLocation!!.latitude}, ${lastKnownLocation!!.longitude}")
//                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_self_64)))

                                moveCamera(
                                    CameraUpdateFactory.newLatLngZoom(
                                        LatLng(lastKnownLocation!!.latitude, lastKnownLocation!!.longitude), 11f))
                            }
                            // set update my location to firebase here
                            viewModel.setMyLocationData(lastKnownLocation?.latitude, lastKnownLocation?.longitude)

                        }
                    } else {
                        myMap?.uiSettings?.isMyLocationButtonEnabled = false
                    }
                }
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }


    private fun getLocalImg() {
        ImagePicker.with(this)
            .compress(1024)
            .crop()
            .maxResultSize(300, 150)
            .start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(resultCode) {
            Activity.RESULT_OK -> {
                val filePath = ImagePicker.getFilePath(data) ?: ""
                filePath?.let {
                    // save file path here
                    viewModel.uploadPicToStorage(filePath)
                    Toast.makeText(requireContext(), "Load picture success.", Toast.LENGTH_SHORT).show()
                }
            }
            ImagePicker.RESULT_ERROR -> {
                Toast.makeText(requireContext(), "Load picture fail.", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun closeEdit(binding: TripDetailFragmentBinding) {
        binding.spotSheet.editTextSpotDetailTitle.isEnabled = false
        binding.spotSheet.editTextSpotDetailContent.isEnabled = false
        binding.spotSheet.editTextSpotDetailStartTime.isEnabled = false
        binding.spotSheet.editTextSpotDetailStayTime.isEnabled = false
        binding.spotSheet.imageEditDone.visibility = View.GONE
        binding.spotSheet.imageEdit.visibility = View.VISIBLE
        binding.spotSheet.imageEditDel.visibility = View.GONE
        binding.spotSheet.imageEditCancel.visibility = View.GONE
    }

    private fun openEdit(binding: TripDetailFragmentBinding) {
        binding.spotSheet.editTextSpotDetailTitle.isEnabled = true
        binding.spotSheet.editTextSpotDetailContent.isEnabled = true
        binding.spotSheet.editTextSpotDetailStartTime.isEnabled = true
        binding.spotSheet.editTextSpotDetailStayTime.isEnabled = true
        binding.spotSheet.imageEditDone.visibility = View.VISIBLE
        binding.spotSheet.imageEdit.visibility = View.GONE
        binding.spotSheet.imageEditDel.visibility = View.VISIBLE
        binding.spotSheet.imageEditCancel.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        val stopIntent = Intent(context, PositionUpdateService::class.java)
        context?.stopService(stopIntent)
        super.onDestroy()
    }
}


//    private fun getUsersLocation() {
//
//        val userA = LatLng(25.0250383, 121.5327086)
//        val userB = LatLng(25.1714657, 121.4359783)
//        val userC = LatLng(25.0669043, 121.469388)
//        val userList = listOf(userA, userB, userC)
//
//        userList.forEach {
//
//            Glide.with(requireActivity())
//                .asBitmap()
//                .load("https://lh3.googleusercontent.com/a-/AOh14GiTzookZLQ8BzSdQs_lRfoczt6DpfEBMYXg6erEtQ=s96-c")
//                .apply(
////                    RequestOptions().transform(CenterCrop(), RoundedCorners(50), GlideCircleBorderTransform(100f, 10))
////                    RequestOptions().transform(GlideCircleBorderTransform(100f, 10))
//                    RequestOptions().transform(GlideCircleBorderTransform(135f, 0))
//                )
//                .into( object : SimpleTarget<Bitmap>(150, 150) {
//                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
//                        myMap?.apply {
//                            addMarker(MarkerOptions()
//                                .title("匿名蠑螈")
//                                .position(it)
//                                .icon(BitmapDescriptorFactory.fromBitmap(resource)))
//                        }
//
//                    }
//                })
//
//        }
//
//
//
////        myMap?.apply {
////            val userA = LatLng(25.0250383, 121.5327086)
////            val userB = LatLng(25.1714657, 121.4359783)
////            val userC = LatLng(25.0669043, 121.469388)
////            val userList = listOf(userA, userB, userC)
////
////
////            addMarker(MarkerOptions()
////                .position(userA)
////                .title("匿名蠑螈")
////                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_user_location_64)))
////
////            addMarker(MarkerOptions()
////                .position(userB)
////                .title("匿名海豹")
////                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_user_location_64)))
////
////            addMarker(MarkerOptions()
////                .position(userC)
////                .title("匿名喵喵")
////                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_user_location_64)))
////        }
//
//
//    }






//        val spotA = LatLng(25.0424825, 121.5626907)
//        val spotB = LatLng(25.0476935, 121.5152081)
//        val spotC = LatLng(25.0774806, 121.2331741)
//        val spotD = LatLng(25.1763029, 121.5462675)
//        val spotList = listOf(spotA, spotB, spotC, spotD)
//
//        googleMap?.apply {
//            // close google map link btn
//            uiSettings.isMapToolbarEnabled = false
//
//            for (spot in spotList) {
//                addMarker(MarkerOptions()
//                    .position(spot)
//                    .title("Spot")
//                    .snippet("${spot.latitude}, ${spot.longitude}")
//                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker)))
//            }
//            addPolyline(PolylineOptions()
//                .add(spotA, spotB)
//                .color(0xFF2286c3.toInt())
//                .width(10F)
//                .pattern(listOf(Dot(),Gap(20F), Dash(40F), Gap(20F)))
//                .endCap(CustomCap(BitmapDescriptorFactory.fromResource(R.drawable.ic_triangle_up))))
//
//            addPolyline(PolylineOptions()
//                .add(spotB, spotC)
//                .color(0xFF2286c3.toInt())
//                .width(10F)
//                .pattern(listOf(Dot(),Gap(20F), Dash(40F), Gap(20F)))
//                .endCap(CustomCap(BitmapDescriptorFactory.fromResource(R.drawable.ic_triangle_up))))
//
//            addPolyline(PolylineOptions()
//                .add(spotC, spotD)
//                .color(0xFF2286c3.toInt())
//                .width(10F)
//                .pattern(listOf(Dot(),Gap(20F), Dash(40F), Gap(20F)))
//                .endCap(CustomCap(BitmapDescriptorFactory.fromResource(R.drawable.ic_triangle_up))))
//
//            moveCamera(CameraUpdateFactory.newLatLngZoom(spotA, 10F))
//
//
//        }


//            val bounds = googleMap.projection.visibleRegion.latLngBounds
//            var up: Double = bounds.northeast.latitude
//            var down: Double = bounds.southwest.latitude
//            var right: Double = bounds.northeast.longitude
//            var left: Double = bounds.southwest.longitude
//
//            var newUp = up - ((abs(up - down)) / 4 )
//            var newDown = down - ((abs(up - down)) / 4 )
//            val newBound = LatLngBounds(LatLng(newDown, left), LatLng(newUp, right))
//
//            Log.d("BBBBBB", "${bounds.southwest} ${bounds.northeast}")
//            Log.d("BBBBBB", "${newBound.southwest} ${newBound.northeast}")
//            googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(newBound, 0))