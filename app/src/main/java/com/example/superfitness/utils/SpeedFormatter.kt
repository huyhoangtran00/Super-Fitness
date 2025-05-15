package com.example.superfitness.utils

import java.util.Locale

object SpeedFormatter {
     fun getFormattedSpeedKmH(speedInKmH: Float): String {
        return String.format(Locale.US, "%.2f", speedInKmH)
    }
}