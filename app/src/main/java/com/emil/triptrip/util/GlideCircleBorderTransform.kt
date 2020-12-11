package com.emil.triptrip.util

import android.graphics.*
import com.bumptech.glide.load.Key
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.emil.triptrip.MainActivity
import com.emil.triptrip.R
import com.emil.triptrip.TripTripApplication
import java.security.MessageDigest


class GlideCircleBorderTransform(private val borderWidth: Float, private val borderColor: Int): BitmapTransformation() {

    private val ID = javaClass.name
    private var mBorderPaint: Paint? = null

    init {
        val paint = Paint()
        mBorderPaint = paint
        mBorderPaint?.let{
            it.apply {
                color = TripTripApplication.instance.resources.getColor(R.color.orange)
                style = Paint.Style.STROKE
                isAntiAlias = true
                strokeWidth = borderWidth
                isDither = true
            }
        }
    }

    private fun circleCrop(bitmapPool: BitmapPool, source: Bitmap): Bitmap {
        val size: Int = Math.min(source.width, source.height)
        val x = ( source.width - size ) / 2
        val y = ( source.height - size ) / 2
        val square = Bitmap.createBitmap(source, x, y, size, size)
        var result = bitmapPool.get(size, size, Bitmap.Config.ARGB_8888)
        result?.let {
           result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        }

        val canvas = Canvas(result)
        val paint = Paint()
        paint.shader = BitmapShader(square, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        paint.isAntiAlias = true

        val radius = size / 2f

        canvas.drawCircle(radius, radius, radius, paint)

        val borderRadius = radius - (borderWidth / 2)

        mBorderPaint?.let { canvas.drawCircle(radius, radius, borderRadius, it) }

        return result
    }


    override fun transform(
        pool: BitmapPool,
        toTransform: Bitmap,
        outWidth: Int,
        outHeight: Int
    ): Bitmap {
        return circleCrop(pool, toTransform)
    }



    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(ID.toByteArray(Key.CHARSET))
    }

    override fun equals(o: Any?): Boolean {
        return o is GlideCircleBorderTransform
    }

    override fun hashCode(): Int {
        return ID.hashCode()
    }

}