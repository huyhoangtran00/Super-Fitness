package com.example.superfitness.ui.screens.runningdetails.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.superfitness.data.local.db.entity.RunEntity
import com.example.superfitness.utils.DistanceKmFormatter
import com.example.superfitness.utils.LocationsUtils
import com.example.superfitness.utils.TimeUtilFormatter
import com.example.superfitness.viewmodel.RunDetails
import com.example.superfitness.viewmodel.toRunEntity
import java.nio.file.WatchEvent

@Composable
fun BottomSheetContent(
    modifier: Modifier = Modifier,
    onDeleteClick: (RunEntity) -> Unit,
    onBackClick: () -> Unit,
    runItem: RunDetails
) {
    val dateAndTime = TimeUtilFormatter.getDate(runItem.timeStamp)
    val timeOfDay = TimeUtilFormatter.getTimeOfTheDay(runItem.timeStamp)
    val duration = TimeUtilFormatter.getTime(runItem.duration)
    val distance = DistanceKmFormatter.metersToKm(runItem.distance)
    val averagePace = DistanceKmFormatter.calculateAveragePace(runItem.duration, runItem.distance)
    val steps = runItem.steps.toString()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp)
    ) {
        // Date and time of the run
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
        ) {
            Text(
                text = dateAndTime,
                style = MaterialTheme.typography.labelMedium.copy(
                    color = Color.Gray,
                )
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        // Time of the day of the run
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)) {
            Text(
                text = "$timeOfDay RUN",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = Color.Black,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }

        // Statistics of the run
        BottomSheetRunningStatistics(
            modifier = Modifier.padding(vertical = 24.dp),
            duration = duration,
            distance = distance,
            averagePace = averagePace,
            steps = steps,
        )

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(
            onClick = {
                onDeleteClick(runItem.toRunEntity())
                onBackClick()
            },
            border = BorderStroke(2.dp, Color.Red),
            modifier = Modifier.align(Alignment.CenterHorizontally).fillMaxWidth().padding(horizontal = 32.dp)
        ) {
            Text(
                text = "Delete activity",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color.Red
                )
            )
        }
    }
}

@Composable
fun BottomSheetRunningStatistics(
    modifier: Modifier = Modifier,
    duration: String,
    distance: String,
    averagePace: String,
    steps: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Distance",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "$distance km",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Avg Pace",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "$averagePace/km",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Moving Time",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = duration,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Steps",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = steps,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}