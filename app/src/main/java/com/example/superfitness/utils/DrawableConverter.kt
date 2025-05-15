package com.example.superfitness.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.createBitmap
import android.graphics.Canvas
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory

object DrawableConverter {
    fun bitmapDescriptorFromVector(
        context: Context,
        vectorResId: Int,
        tint: Int? = null,
        scale: Double,
    ): BitmapDescriptor {
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)!!
        tint?.let { vectorDrawable.setTint(it)}

        vectorDrawable.setBounds(
            0,
            0,
            (vectorDrawable.intrinsicWidth * scale).toInt(),
            (vectorDrawable.intrinsicHeight * scale).toInt()
        )

        val bitmap = createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)

        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}