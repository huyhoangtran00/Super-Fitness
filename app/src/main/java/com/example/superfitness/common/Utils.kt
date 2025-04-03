package com.example.superfitness.common

import android.annotation.SuppressLint
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object Utils {

    fun suggestActivityByCode(weatherCode: Int) : String {
        val activity = when (weatherCode) {
            0 -> "Trời quang, lý tưởng để chạy bộ hoặc đi dạo!"
            in 1..3 -> "Trời mây nhẹ, thích hợp đi dã ngoại hoặc chụp ảnh."
            45, 48 -> "Sương mù, thử yoga hoặc tập trong nhà."
            in 51..55 -> "Mưa phùn, tập nhẹ nhàng trong nhà."
            in 56..57 -> "Mưa phùn đóng băng, tránh ra ngoài."
            in 61..65 -> "Mưa, tập aerobic hoặc tăng cường sức khỏe."
            in 66..67 -> "Mưa đóng băng, giữ ấm và hạn chế ra ngoài."
            in 71..75 -> "Tuyết, thử trượt tuyết hoặc ngắm cảnh."
            77 -> "Tuyết hạt, tập tại nhà hoặc thư giãn."
            in 80..82 -> "Mưa rào, chạy bộ trong nhà hoặc tập gym."
            in 85..86 -> "Tuyết rào, thử yoga hoặc nhảy trong nhà."
            95 -> "Giông bão, ở nhà và tập cá nhân."
            in 96..99 -> "Giông kèm mưa đá, giữ an toàn trong nhà."
            else -> "Thời tiết không rõ, thử thiền hoặc đọc sách."
        }
        return activity
    }
    @SuppressLint("NewApi")
    fun getDayOfWeekFromDate(dateString: String): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val date = LocalDate.parse(dateString, formatter)
        val dayOfWeek = date.dayOfWeek
        return when (dayOfWeek) {
            DayOfWeek.MONDAY -> "Thứ 2"
            DayOfWeek.TUESDAY -> "Thứ 3"
            DayOfWeek.WEDNESDAY -> "Thứ 4"
            DayOfWeek.THURSDAY -> "Thứ 5"
            DayOfWeek.FRIDAY -> "Thứ 6"
            DayOfWeek.SATURDAY -> "Thứ 7"
            DayOfWeek.SUNDAY -> "CN"
        }
    }

}