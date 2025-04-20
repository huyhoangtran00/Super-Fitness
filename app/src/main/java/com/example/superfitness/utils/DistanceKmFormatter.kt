package com.example.superfitness.utils

import java.util.Locale

object DistanceKmFormatter {
    fun metersToKm(meters: Int): String {
        return String.format(Locale.US, "%.2f", meters / 1000f)
    }

    fun calculateAveragePace(ms: Long, distance: Int): String {
        if (distance == 0) return "0:00"

        val minutesPerKm = (ms / 1000f / 60f) / (distance / 1000f)
        val minutes = minutesPerKm.toInt()
        val seconds = ((minutesPerKm - minutes) * 60).toInt()

        return String.format(Locale.US, "%d:%02d", minutes, seconds)
    }
}