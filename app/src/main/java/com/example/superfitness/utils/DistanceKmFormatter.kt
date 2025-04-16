package com.example.superfitness.utils

import java.util.Locale

object DistanceKmFormatter {
    fun metersToKm(meters: Int): String {
        return String.format(Locale.US, "%.2f", meters / 1000f)
    }
}