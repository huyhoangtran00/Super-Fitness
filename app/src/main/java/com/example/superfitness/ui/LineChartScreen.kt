package com.example.superfitness.ui

import android.graphics.Color
import android.graphics.Typeface
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.superfitness.R
import com.github.mikephil.charting.components.Legend
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

@Composable
fun LineChartScreen() {
    // Lấy ngày hiện tại và định dạng thành "Mar 23, 2025"
    val currentDate = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date())

    // Danh sách dữ liệu chạy theo thứ trong tuần
    val dataEntries = remember { generateWeeklyData() }

    Column(
        modifier = Modifier
            .fillMaxSize()  // Chiếm toàn bộ màn hình
            .background(color = androidx.compose.ui.graphics.Color.Black) // Đặt màu nền đen
            .padding(16.dp)  // Tạo khoảng cách để biểu đồ không bị sát lề
    ) {
        // Hiển thị ngày tháng phía trên biểu đồ
        Text(
            text = currentDate,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = androidx.compose.ui.graphics.Color.White
        )
        Spacer(modifier = Modifier.height(16.dp))

        // AndroidView để hiển thị biểu đồ MPAndroidChart trong Jetpack Compose
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
            factory = { context ->
                LineChart(context).apply {
                    description.isEnabled = false  // Tắt mô tả
                    setTouchEnabled(true)          // Cho phép chạm vào biểu đồ
                    setPinchZoom(true)             // Cho phép zoom
                    setScaleEnabled(true)          // Bật scale
                    setDrawGridBackground(false)   // Tắt nền lưới

                    // Cấu hình trục X (thứ trong tuần)
                    xAxis.apply {
                        position = XAxis.XAxisPosition.BOTTOM // Đưa trục X xuống dưới
                        setDrawGridLines(true)
                        gridLineWidth = 1f
                        gridColor = Color.GRAY
                        setDrawAxisLine(true)
                        axisLineColor = Color.WHITE
                        axisLineWidth = 2f
                        textSize = 12f
                        textColor = Color.WHITE  // Chữ ở trục X màu trắng
                        granularity = 1f  // Đảm bảo mỗi ngày có một khoảng riêng
                        setLabelCount(dataEntries.size, true)  // Tự động điều chỉnh theo số ngày
                        valueFormatter = WeekdayFormatter() // Hiển thị "Mon, Tue, ..."
                    }


                    // Cấu hình trục Y bên trái
                    axisLeft.apply {
                        setDrawGridLines(true)  // Hiển thị lưới ngang
                        gridColor = Color.GRAY
                        gridLineWidth = 1f

                        textSize = 12f
                        textColor = Color.WHITE
                        axisMinimum = 0f
                        axisMaximum = 10f
                        setLabelCount(6, true) // Chia mốc: 2, 4, 6, 8, 10 km
                        valueFormatter = KmValueFormatter()
                        setDrawAxisLine(true)  // Hiển thị trục Y
                        axisLineColor = Color.WHITE // Màu trắng cho trục Y
                        axisLineWidth = 2f
                    }

                    // Tắt trục Y bên phải
                    axisRight.isEnabled = false
                    // Set dữ liệu vào biểu đồ
                    data = LineData(LineDataSet(dataEntries, "Running Distance").apply {
                        color = Color.RED         // Đường màu đỏ
                        lineWidth = 2f            // Độ dày đường kẻ
                        setCircleColor(Color.RED) // Màu chấm tròn đỏ
                        circleRadius = 5f         // Kích thước chấm tròn
                        setDrawCircleHole(false)  // Tắt hiệu ứng lỗ tròn
                        setDrawValues(false)      // Không hiển thị số tại các chấm tròn
                        setDrawFilled(true)
                        fillDrawable = ContextCompat.getDrawable(context, R.drawable.gradient_fill)
                    })

                    invalidate() // Cập nhật lại biểu đồ
                }
            }
        )
    }
}

// Formatter để hiển thị thứ trong tuần (Mon, Tue, ..., Sun)
class WeekdayFormatter : com.github.mikephil.charting.formatter.ValueFormatter() {
    private val daysOfWeek = arrayOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    override fun getFormattedValue(value: Float): String {
        val index = (value).toInt()
        return if (index in daysOfWeek.indices) daysOfWeek[index] else ""
    }
}

class KmValueFormatter : com.github.mikephil.charting.formatter.ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        return "${value.toInt()} km"
    }
}

// Hàm tạo dữ liệu giả định theo ngày trong tuần
fun generateWeeklyData(): List<Entry> {
    return listOf(
        Entry(0f, 2f), // Thứ 2 - Giữa cột "Mon"
        Entry(1f, 3f), // Thứ 3 - Giữa cột "Tue"
        Entry(2f, 5f), // Thứ 4 - Giữa cột "Wed"
        Entry(3f, 1f), // Thứ 5 - Giữa cột "Thu"
        Entry(4f, 3f), // Thứ 3 - Giữa cột "Tue"
        Entry(5f, 5f), // Thứ 4 - Giữa cột "Wed"
        Entry(6f, 1f), // Thứ 5 - Giữa cột "Thu"
    )
}