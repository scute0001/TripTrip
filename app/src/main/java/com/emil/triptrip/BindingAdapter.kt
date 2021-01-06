package com.emil.triptrip

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
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
        Glide.with(imgView.context)
            .load(imgUrl)
            .placeholder(R.drawable.ic_placeholder_big)
            .error(R.drawable.ic_placeholder_big)
            .apply(
                RequestOptions().transform(CenterCrop(), RoundedCorners(50))
            )
            .into(imgView)
    }
}

// check isSelect image and show on UI
@BindingAdapter("imagePathUrl")
fun bindSelectImage(imgView: ImageView, imgUrl: String?) {
    if (imgUrl == "" || imgUrl == null) {

    } else {
        Glide.with(imgView.context)
            .load(imgUrl)
            .placeholder(R.drawable.ic_placeholder_big)
            .error(R.drawable.ic_placeholder_big)
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
    var notificationStringFirst = TripTripApplication.instance.applicationContext.resources.getText(R.string.notification_added)
    var notificationStringSec = TripTripApplication.instance.applicationContext.resources.getText(R.string.notification_member)
    textView.text = "${data.inviterName} $notificationStringFirst ${data.tripTitle} $notificationStringSec "
}
