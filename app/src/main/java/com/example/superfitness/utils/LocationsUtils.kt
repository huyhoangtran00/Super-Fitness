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

    fun pathPointsToString(list: List<LatLng>): String {
        val string = StringBuilder()

        list.forEach {
            string.append("${it.latitude},${it.longitude}/")
        }

        return string.toString()
    }

    fun stringToPathPoints(pointsString: String) : List<LatLng> {
        val pathPoints = mutableListOf<LatLng>()
        val tempList = pointsString.split("/")

        tempList.forEach {
            // return for lambda only use return@forEach
            if(it.isEmpty()) return@forEach
            val points = it.split(",")

            pathPoints.add(
                LatLng(
                    points[0].toDouble(),
                    points[1].toDouble()
                )
            )
        }

        return pathPoints
    }
}