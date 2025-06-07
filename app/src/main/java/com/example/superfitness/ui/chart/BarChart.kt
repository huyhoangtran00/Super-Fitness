package com.example.superfitness.ui.charts

import android.graphics.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.toColorInt
import com.example.superfitness.utils.BLUE
import com.example.superfitness.utils.GREEN
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.renderer.BarChartRenderer
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.time.LocalDate
import java.time.format.DateTimeFormatter

sealed interface ChartData {
    val date: String
    val value: Float
}

data class RunData(override val date: String, override val value: Float) : ChartData
data class WaterData(override val date: String, override val value: Float) : ChartData

@Composable
fun BarChart(
    runData: List<RunData>,
    waterData: List<WaterData>,
    onSelectionChange: (Boolean) -> Unit
) {
    val context = LocalContext.current
    var selectedData by remember { mutableStateOf<List<ChartData>>(runData) }
    var isRunDataSelected by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp),
                clip = true
            )
            .background(
                color = Color.White,
                shape = RoundedCornerShape(16.dp)
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 0.dp, top = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .clickable {
                        selectedData = runData
                        isRunDataSelected = true
                        onSelectionChange(true)
                    }
                    .padding(end = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.DirectionsRun,
                    contentDescription = "Run Data",
                    tint = if (isRunDataSelected) Color(GREEN.toColorInt()) else Color.Gray
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Chạy bộ",
                    color = if (isRunDataSelected) Color(GREEN.toColorInt()) else Color.Gray
                )
            }
            
            Row(
                modifier = Modifier
                    .clickable {
                        selectedData = waterData
                        isRunDataSelected = false
                        onSelectionChange(false)
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.WaterDrop,
                    contentDescription = "Water Data",
                    tint = if (!isRunDataSelected) Color(BLUE.toColorInt()) else Color.Gray
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Uống nước",
                    color = if (!isRunDataSelected) Color(BLUE.toColorInt()) else Color.Gray
                )
            }
        }
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            factory = { ctx ->
                BarChart(ctx).apply {
                    setupBarChart(this, selectedData, isRunDataSelected)
                }
            },
            update = { barChart ->
                setupBarChart(barChart, selectedData, isRunDataSelected)
            }
        )

        Text(
            text = "Các hoạt động",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(top = 8.dp, bottom = 8.dp)
        )
    }
}


fun setupBarChart(barChart: BarChart, dataList: List<ChartData>, isRunData: Boolean) {
    val entries = dataList.mapIndexed { index, data ->
        BarEntry(index.toFloat(), data.value)
    }

    val dataSet = BarDataSet(entries, if (isRunData) "Kilometers" else "Liters").apply {
        color = if (isRunData) android.graphics.Color.parseColor("#00C853") else android.graphics.Color.parseColor("#14b5ff")
        valueTextColor = if (isRunData) android.graphics.Color.parseColor("#00C853") else android.graphics.Color.parseColor("#14b5ff")
        setDrawValues(true)
        valueTextSize = 12f
        valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return String.format("%.0f", value)
            }
        }
    }

    val barData = BarData(dataSet).apply {
        barWidth = 0.6f
    }

    val dateFormatInput = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val dateFormatOutput = SimpleDateFormat("dd/MM", Locale.getDefault())
    val xAxisLabels = dataList.map { data ->
        try {
            val date = dateFormatInput.parse(data.date)
            dateFormatOutput.format(date!!)
        } catch (e: Exception) {
            // Handle timestamp format if needed
            val date = Date(data.date.toLongOrNull() ?: 0)
            dateFormatOutput.format(date)
        }
    }
    barChart.xAxis.apply {
        valueFormatter = IndexAxisValueFormatter(xAxisLabels)
        setDrawGridLines(false)
        setDrawAxisLine(false)
        textColor = android.graphics.Color.BLACK
        position = XAxis.XAxisPosition.BOTTOM
        granularity = 1f
        setLabelCount(xAxisLabels.size, false)
    }

    barChart.axisLeft.apply {
        setDrawGridLines(false)
        setDrawLabels(false)
        setDrawAxisLine(false)
        axisMinimum = 0f
    }
    barChart.axisRight.isEnabled = false

    barChart.renderer = RoundedBarChartRenderer(barChart, barChart.animator, barChart.viewPortHandler).apply {
        setRadius(15f)
    }

    barChart.apply {
        data = barData
        setFitBars(true)
        description.isEnabled = false
        legend.isEnabled = false
        setDrawGridBackground(false)
        setDrawBorders(false)
        setBackgroundColor(android.graphics.Color.TRANSPARENT)
        animateY(1000)
        invalidate()
    }
}

class RoundedBarChartRenderer(
    chart: BarChart,
    animator: com.github.mikephil.charting.animation.ChartAnimator,
    viewPortHandler: ViewPortHandler
) : BarChartRenderer(chart, animator, viewPortHandler) {
    private var mRadius: Float = 0f

    fun setRadius(radius: Float) {
        mRadius = radius
    }

    override fun drawDataSet(c: Canvas, dataSet: IBarDataSet, index: Int) {
        val trans: Transformer = mChart.getTransformer(dataSet.axisDependency)
        val paint = mRenderPaint
        val borderPaint = mBarBorderPaint

        borderPaint.color = dataSet.barBorderColor
        borderPaint.strokeWidth = Utils.convertDpToPixel(dataSet.barBorderWidth)

        val drawBorder = dataSet.barBorderWidth > 0f
        val phaseX = mAnimator.phaseX
        val phaseY = mAnimator.phaseY

        if (mChart.isDrawBarShadowEnabled) {
            mShadowPaint.color = dataSet.barShadowColor
            val barData = mChart.barData
            val barWidth = barData.barWidth
            val barWidthHalf = barWidth / 2.0f

            for (i in 0 until Math.min(
                Math.ceil((dataSet.entryCount.toFloat() * phaseX).toDouble()),
                dataSet.entryCount.toDouble()
            ).toInt()) {
                val e = dataSet.getEntryForIndex(i)
                val x = e.x
                mBarRect.set(
                    x - barWidthHalf,
                    mViewPortHandler.contentTop(),
                    x + barWidthHalf,
                    mViewPortHandler.contentBottom()
                )
                trans.rectToPixelPhase(mBarRect, phaseY)

                if (!mViewPortHandler.isInBoundsLeft(mBarRect.right)) continue
                if (!mViewPortHandler.isInBoundsRight(mBarRect.left)) break

                c.drawRoundRect(mBarRect, mRadius, mRadius, mShadowPaint)
            }
        }

        val buffer = mBarBuffers[index]
        buffer.setPhases(phaseX, phaseY)
        buffer.setDataSet(index)
        buffer.setInverted(mChart.isInverted(dataSet.axisDependency))
        buffer.setBarWidth(mChart.barData.barWidth)

        buffer.feed(dataSet)
        trans.pointValuesToPixel(buffer.buffer)
        paint.color = dataSet.color

        for (j in 0 until buffer.size() step 4) {
            if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[j + 2])) continue
            if (!mViewPortHandler.isInBoundsRight(buffer.buffer[j])) break
            mBarRect.set(
                buffer.buffer[j],
                buffer.buffer[j + 1],
                buffer.buffer[j + 2],
                buffer.buffer[j + 3]
            )

            c.drawRoundRect(mBarRect, mRadius, mRadius, paint)

            if (drawBorder) {
                c.drawRoundRect(mBarRect, mRadius, mRadius, borderPaint)
            }
        }
    }

    override fun drawValues(c: Canvas) {
        if (!isDrawingValuesAllowed(mChart)) return

        val dataSets = mChart.barData.dataSets
        val valueOffsetPlus = Utils.convertDpToPixel(4.5f)
        val drawValueAboveBar = mChart.isDrawValueAboveBarEnabled

        for (i in 0 until mChart.barData.dataSetCount) {
            val dataSet = dataSets[i]

            if (!shouldDrawValues(dataSet)) continue

            applyValueTextStyle(dataSet)

            val isInverted = mChart.isInverted(dataSet.axisDependency)
            val valueTextHeight = Utils.calcTextHeight(mValuePaint, "8").toFloat()
            val posOffset = if (drawValueAboveBar) -valueOffsetPlus else valueTextHeight + valueOffsetPlus
            val negOffset = if (drawValueAboveBar) valueTextHeight + valueOffsetPlus else -valueOffsetPlus

            val updatedPosOffset = if (isInverted) -posOffset - valueTextHeight else posOffset
            val updatedNegOffset = if (isInverted) -negOffset - valueTextHeight else negOffset

            val buffer = mBarBuffers[i]

            val iconsOffset = MPPointF.getInstance(dataSet.iconsOffset).apply {
                x = Utils.convertDpToPixel(x)
                y = Utils.convertDpToPixel(y)
            }

            for (j in 0 until buffer.buffer.size step 4) {
                val left = buffer.buffer[j]
                val top = buffer.buffer[j + 1]
                val right = buffer.buffer[j + 2]
                val bottom = buffer.buffer[j + 3]

                val x = (left + right) / 2f

                if (!mViewPortHandler.isInBoundsRight(x)) break
                if (!mViewPortHandler.isInBoundsY(top) || !mViewPortHandler.isInBoundsLeft(x)) continue

                val entry = dataSet.getEntryForIndex(j / 4)
                val value = entry.y

                if (dataSet.isDrawValuesEnabled) {
                    val formattedValue = dataSet.valueFormatter.getFormattedValue(value)
                    drawValue(
                        c,
                        formattedValue,
                        x,
                        if (value >= 0) top + updatedPosOffset else bottom + updatedNegOffset,
                        dataSet.getValueTextColor(j / 4)
                    )
                }
            }

            MPPointF.recycleInstance(iconsOffset)
        }
    }

    override fun drawValue(c: Canvas, valueText: String, x: Float, y: Float, color: Int) {
        mValuePaint.color = color
        c.drawText(valueText, x, y, mValuePaint)
    }
}