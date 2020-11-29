package com.emil.triptrip

import android.graphics.drawable.BitmapDrawable
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import java.text.SimpleDateFormat

/**
 * Uses the Glide library to load an image by URL into an [ImageView]
 */
@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        val imgUri = it.toUri().buildUpon().build()
        Glide.with(imgView.context)
            .load(imgUri)
            .apply(
                RequestOptions.bitmapTransform(RoundedCorners(20))

//                    .placeholder(R.drawable.ic_placeholder)
//                    .error(R.drawable.ic_placeholder)

//                    (RequestOptions.bitmapTransform(new RoundedCorners(20)))
                    )
            .into(imgView)
    }
}


@BindingAdapter("timeTransfer")
fun convertLongToDateString(textView: TextView, systemTime: Long){
    textView.text = SimpleDateFormat("yyyy.MM.dd")
        .format(systemTime)
}