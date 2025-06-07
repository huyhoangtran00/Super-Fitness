package com.example.superfitness.utils

import android.icu.util.Calendar
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

object TimeUtilFormatter {

    fun getStopWatchTime(ms: Long): String {
        var milliseconds = ms
        val hours = TimeUnit.MILLISECONDS.toHours(milliseconds)
        milliseconds -= TimeUnit.HOURS.toMillis(hours)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds)
        milliseconds -= TimeUnit.MINUTES.toMillis(minutes)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds)

        return "${if(hours < 10) "0" else ""}$hours:" +
                "${if(minutes < 10) "0" else ""}$minutes:" +
                "${if(seconds < 10) "0" else ""}$seconds"
    }

    fun getTime(ms: Long): String {
        var milliseconds = ms
        val hours = TimeUnit.MILLISECONDS.toHours(milliseconds)
        milliseconds -= TimeUnit.HOURS.toMillis(hours)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds)
        milliseconds -= TimeUnit.MINUTES.toMillis(minutes)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds)

        return if (hours > 0)
            "${hours}h ${minutes}m ${seconds}s"
        else if (minutes > 0)
            "${minutes}m${seconds}s"
        else
            "${seconds}s"
    }

    fun getDate(ms: Long): String {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = ms
        }

        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val suffix = getDayOfMonthSuffix(day)

        val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.US)
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.US)

        val date = "$day$suffix ${dateFormat.format(calendar.time)}"
        val time = timeFormat.format(calendar.time)

        return "$date at $time"
    }
    fun getTimeOfTheDay(ms: Long): String {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = ms
        }

        val hour = calendar.get(Calendar.HOUR_OF_DAY)

        return when (hour) {
            in 4..11 -> "MORNING"
            in 12..16 -> "AFTERNOON"
            in 17..21 -> "EVENING"
            else ->  "NIGHT"
        }
    }

    private fun getDayOfMonthSuffix(n: Int): String {
        return if (n in 11..13) "th" else when (n % 10) {
            1 -> "st"
            2 -> "nd"
            3 -> "rd"
            else -> "th"
        }
    }
}