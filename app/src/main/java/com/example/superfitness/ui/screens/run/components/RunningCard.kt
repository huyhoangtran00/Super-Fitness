package com.example.superfitness.ui.screens.run.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.superfitness.R
import com.example.superfitness.utils.TimeUtilFormatter

@Composable
fun RunningCard(
    modifier: Modifier = Modifier,
    isTracking: Boolean,
    durationTimerInMillis: Long,
    distanceInMeters: Int,
    speedInKmH: Float,
    onPlayClicked: () -> Unit,
    onFinishClicked: () -> Unit,
) {

    val formattedTime = TimeUtilFormatter.getFormattedStopWatchTime(durationTimerInMillis)

    ElevatedCard(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        RunningCardTime(
            durationTimer = formattedTime,
            isTracking = isTracking,
            onPlayClicked = onPlayClicked,
            onFinishedClicked = onFinishClicked,
            modifier = Modifier
                .padding(vertical = 8.dp, horizontal = 16.dp)
                .fillMaxWidth()
        )

        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .padding(bottom = 4.dp)
                .height(IntrinsicSize.Min)
                .fillMaxWidth()
        ) {
            RunningStatusItem(
                painter = painterResource(R.drawable.ic_action_name),
                unit = "km",
                value = String.format("%.2f", distanceInMeters / 1000f),
                modifier = modifier,
            )

            RunningStatusItem(
                painter = painterResource(R.drawable.ic_action_name),
                unit = "km/h",
                value = String.format("%.2f", speedInKmH),
                modifier = modifier,
            )
        }
    }
}

@Composable
fun RunningCardTime(
    modifier: Modifier = Modifier,
    durationTimer: String,
    isTracking: Boolean,
    onPlayClicked: () -> Unit,
    onFinishedClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Running time",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
            Text(
                text = durationTimer,
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }

        IconButton(
            onClick = onPlayClicked,
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = MaterialTheme.shapes.medium
                )
                .align(Alignment.CenterVertically)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(
                    id = if (isTracking) R.drawable.baseline_pause_24 else R.drawable.baseline_play_arrow_24
                ),
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
fun RunningStatusItem(
    modifier: Modifier = Modifier,
    painter: Painter,
    unit: String,
    value: String
) {
    Row(modifier = Modifier.padding(horizontal = 4.dp)) {
        Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier
                .padding(top =  4.dp)
                .size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = unit,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}