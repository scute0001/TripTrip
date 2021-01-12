package com.emil.triptrip.ui.dialog

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.emil.triptrip.R
import com.emil.triptrip.databinding.FragmentLoginSuccessDialogBinding



class AddSpotSuccessDialogFragment : DialogFragment() {

    override fun onStart() {
        super.onStart()
        dialog?.setCanceledOnTouchOutside(false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.MessageDialog)
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

        binding.dialogLottie.apply {
            speed = 1.7F
            playAnimation()
        }

        return binding.root
    }
}