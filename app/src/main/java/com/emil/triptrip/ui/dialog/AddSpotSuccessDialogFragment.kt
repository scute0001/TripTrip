package com.emil.triptrip.ui.dialog

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.emil.triptrip.R
import com.emil.triptrip.databinding.FragmentLoginSuccessDialogBinding



class AddSpotSuccessDialogFragment : DialogFragment() {

    override fun onStart() {
        super.onStart()
        dialog?.setCanceledOnTouchOutside(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentLoginSuccessDialogBinding.inflate(inflater, container, false)
        binding.textView2.text = resources.getString(R.string.add_success)

        Handler().postDelayed(
            {
                dismiss()
                requireActivity().onBackPressedDispatcher.onBackPressed()
            },
            1500)

        return binding.root
    }


}