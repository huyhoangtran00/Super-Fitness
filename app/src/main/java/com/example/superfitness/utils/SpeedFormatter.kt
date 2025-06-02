package com.example.superfitness.utils

import java.util.Locale

object SpeedFormatter {
     fun getFormattedSpeedKmH(speedInKmH: Float): String {
         return if (speedInKmH >= 3.0f ) {
             val pace = 60f / speedInKmH
             val minutes = pace.toInt()
             val seconds = ((pace - minutes) * 60).toInt()
             String.format(Locale.US, "%d:%02d", minutes, seconds)
         } else  {
             "0:00"
         }
    }
}