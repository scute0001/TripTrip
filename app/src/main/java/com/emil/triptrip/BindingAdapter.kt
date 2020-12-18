package com.emil.triptrip

import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.emil.triptrip.database.NotificationAddTrip
import java.text.SimpleDateFormat
import java.util.*

/**
 * Uses the Glide library to load an image by URL into an [ImageView]
 */
@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
//        imgView.setBackgroundResource(R.drawable.shape_circle_btn)



        Glide.with(imgView.context)
            .load(imgUrl)
            .placeholder(R.drawable.ic_placeholder)
            .error(R.drawable.ic_placeholder)
            .apply(
                RequestOptions().transform(CenterCrop(), RoundedCorners(50))
//                RequestOptions.bitmapTransform(RoundedCorners(20))
            )
            .into(imgView)
    }
}


@BindingAdapter("imagePathUrl")
fun bindSelectImage(imgView: ImageView, imgUrl: String?) {
    if (imgUrl == "" || imgUrl == null) {

    } else {
        Glide.with(imgView.context)
            .load(imgUrl)
            .placeholder(R.drawable.ic_placeholder)
            .error(R.drawable.ic_placeholder)
            .apply(
                RequestOptions().transform(CenterCrop(), RoundedCorners(50))
            )
            .into(imgView)
    }
}


@BindingAdapter("timeTransfer")
fun convertLongToDateString(textView: TextView, systemTime: Long){
    textView.text = SimpleDateFormat("yyyy.MM.dd")
        .format(systemTime)
}

@BindingAdapter("timeTransferFull")
fun convertLongToDateStringFull(textView: TextView, systemTime: Long){
    textView.text = SimpleDateFormat("yyyy.MM.dd HH:mm")
        .format(systemTime)
}

@BindingAdapter("timeTransferHHmm")
fun convertTimeHHmmToString(textView: TextView, systemTime: Long){
    textView.text = SimpleDateFormat("HH:mm")
        .format(systemTime).toString()
}

@BindingAdapter("timeTransferMMddHHmm")
fun convertTimeMMddHHmmToString(textView: TextView, systemTime: Long){
    textView.text = SimpleDateFormat("MM/dd HH:mm")
        .format(systemTime).toString()
}

@BindingAdapter("timeTransferHHmmGMT")
fun convertTimeHHmmToStringGMT(textView: TextView, systemTime: Long){
    val sdf = SimpleDateFormat("HH:mm")
    sdf.setTimeZone(TimeZone.getTimeZone("GMT"))
    textView.text = sdf.format(systemTime).toString()
}

@BindingAdapter("notificationContent")
fun setNotificationContent(textView: TextView, data: NotificationAddTrip) {
    textView.text = "${data.inviterName} 已將你加入 ${data.tripTitle} 的夥伴，一起來規劃行程吧!!"
}
