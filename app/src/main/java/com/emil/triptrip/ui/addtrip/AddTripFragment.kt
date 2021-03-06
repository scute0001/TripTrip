package com.emil.triptrip.ui.addtrip

import android.Manifest
import android.animation.Animator
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.emil.triptrip.R
import com.emil.triptrip.TripTripApplication
import com.emil.triptrip.databinding.AddTripFragmentBinding
import com.emil.triptrip.ui.dialog.SelectUserDialog
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.datepicker.MaterialDatePicker
import java.util.*

class AddTripFragment : Fragment() {
    private val REQ_CODE_PIC_CAMERA = 9999
    private lateinit var viewModel: AddTripViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = AddTripFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        val app = requireNotNull(activity).application
        val repository = (requireContext().applicationContext as TripTripApplication).repository
        val viewModelFactory = AddTripViewModelFactory(app, repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(AddTripViewModel::class.java)
        binding.viewModel = viewModel

        // setup select date
        val builder = MaterialDatePicker.Builder.dateRangePicker()
        val now = Calendar.getInstance()
        builder.setSelection(androidx.core.util.Pair(now.timeInMillis, now.timeInMillis))
        val picker = builder.build()

        // select date and set data to viewModel
        binding.constraintAddTripRange.setOnClickListener {
            fragmentManager?.let { it -> picker.show(it,"") }

            picker.addOnNegativeButtonClickListener {
                picker.dismiss() }
            picker.addOnPositiveButtonClickListener {
                viewModel.startDay.value = it.first
                viewModel.endDay.value = it.second
            }
        }

        // set attend users adapter
        val adapter = AttendUsersAdapter()
        binding.recyclerAddAttendUser.adapter = adapter

        // observe selected users data and to adapter
        viewModel.selectedUsers.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            adapter.submitList(it)
        })

        // observe tripData
        viewModel.tripData.observe(viewLifecycleOwner, androidx.lifecycle.Observer { tripData ->
            Log.d("TripData", "TripData is $tripData")
            if (tripData != null) {
                // submit data to firebase here
                viewModel.uploadTripToFirebase(tripData)
            }
        })

        // lottile setting
        binding.lottieSuccess.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                findNavController().navigate(AddTripFragmentDirections.actionAddTripFragmentToMyTripsFragment())
                viewModel.clearAddTripFinishedNavToMyTrips()
                binding.constraintSuccess.visibility = View.GONE
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
            }
        })

        // add data finished and nav to mytrips
        viewModel.addTripFinishedNavToMyTrips.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it == true) {
                Toast.makeText(requireContext(), getString(R.string.add_trip_success), Toast.LENGTH_SHORT).show()
                binding.constraintSuccess.visibility = View.VISIBLE
                binding.lottieSuccess.playAnimation()
            }
        })

        // check data if completed
        viewModel.checkDataFlag.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it == false) {
                Toast.makeText(requireContext(), resources.getText(R.string.input_data_lack), Toast.LENGTH_SHORT).show()
                viewModel.clearCheckDataFlag()
            }
        })

        viewModel.selectPicUri.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            viewModel.uploadPicToStorage(it)
        })


        binding.buttonAddAttendUser.setOnClickListener {
            fragmentManager?.let { it1 -> SelectUserDialog(viewModel.usersData.value!!, viewModel).show(it1, "GOGO") }
        }

        // set trip data and submit to firebase
        binding.buttonSubmitTrip.setOnClickListener {
            viewModel.setTripData()
        }

        binding.buttonCancelSubmitTrip.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // image picker select photo
        binding.buttonAddPic.setOnClickListener {
            val permission = requireActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            if (permission != PackageManager.PERMISSION_GRANTED) {
                requireActivity().requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQ_CODE_PIC_CAMERA)
            }
            getLocalImg()
        }

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(resultCode) {
            Activity.RESULT_OK -> {
                val filePath = ImagePicker.getFilePath(data) ?: ""
                filePath?.let {
                    // save file path here
                    viewModel.setSelectedPicUri(filePath)
                    Toast.makeText(requireContext(), getString(R.string.load_pic_success), Toast.LENGTH_SHORT).show()
                }
            }
            ImagePicker.RESULT_ERROR -> {
                Toast.makeText(requireContext(), getString(R.string.load_pic_fail), Toast.LENGTH_SHORT).show()
            }

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode) {
            REQ_CODE_PIC_CAMERA -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocalImg()
                }
            }
        }
    }

    private fun getLocalImg() {
        ImagePicker.with(this)
            .crop()
            .start()
    }
}