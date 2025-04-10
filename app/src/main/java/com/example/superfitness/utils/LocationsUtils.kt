package com.example.superfitness.utils

import android.location.Location
import com.google.android.gms.maps.model.LatLng
import kotlin.math.roundToInt

object LocationsUtils {

    fun getDistanceBetweenPathPoints(
        pathPoint1: LatLng,
        pathPoint2: LatLng
    ): Int {
        val result = FloatArray(1)

        Location.distanceBetween(
            pathPoint1.latitude,
            pathPoint1.longitude,
            pathPoint2.latitude,
            pathPoint2.longitude,
            result
        )

        return result[0].roundToInt()
    }
}