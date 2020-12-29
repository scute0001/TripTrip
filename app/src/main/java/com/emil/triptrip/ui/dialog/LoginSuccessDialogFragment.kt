package com.emil.triptrip.ui.dialog

import android.animation.Animator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.emil.triptrip.R
import com.emil.triptrip.databinding.FragmentLoginSuccessDialogBinding

class LoginSuccessDialogFragment: DialogFragment() {

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

        // lottile setting
        binding.dialogLottie.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                dismiss()
                findNavController().navigate(LoginSuccessDialogFragmentDirections.actionLoginSuccessDialogFragmentToMyTripsFragment())
            }
            override fun onAnimationCancel(animation: Animator?) {

            }
            override fun onAnimationStart(animation: Animator?) {

            }
        })

        binding.dialogLottie.apply {
            speed = 1.7F
            playAnimation()
        }


//        Handler().postDelayed(
//            {dismiss()
//                findNavController().navigate(LoginSuccessDialogFragmentDirections.actionLoginSuccessDialogFragmentToMyTripsFragment())
//            },
//            1000)

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.MessageDialog)
    }


}