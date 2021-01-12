package com.emil.triptrip.ui.dialog

import android.animation.Animator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import com.emil.triptrip.R
import com.emil.triptrip.databinding.FragmentConfirmDialogBinding
import com.emil.triptrip.databinding.TripDetailFragmentBinding
import com.emil.triptrip.ui.tripdetail.TripDetailViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior


class ConfirmDialog(val viewModel: TripDetailViewModel,
                    private val bindingBottomSheet: TripDetailFragmentBinding,
                    private val bottomBehavior: BottomSheetBehavior<ConstraintLayout>
): DialogFragment() {

    override fun onStart() {
        super.onStart()
        dialog?.setCanceledOnTouchOutside(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentConfirmDialogBinding.inflate(inflater, container, false)

        binding.buttonOk.setOnClickListener {
            // delete data
            viewModel.delSpotData()

            // close bottom sheet and edit mode
            closeEdit(bindingBottomSheet)
            bottomBehavior.state = BottomSheetBehavior.STATE_HIDDEN

            // show success animation
            binding.constraintMain.visibility = View.INVISIBLE
            binding.constraintSuccess.visibility = View.VISIBLE
            binding.dialogLottie.playAnimation()
        }

        binding.buttonCancel.setOnClickListener {
            dismiss()
        }

        // lottile setting
        binding.dialogLottie.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }
            override fun onAnimationEnd(animation: Animator?) {
                dismiss()
            }
            override fun onAnimationCancel(animation: Animator?) {
            }
            override fun onAnimationStart(animation: Animator?) {
            }
        })

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.MessageDialog)
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
}