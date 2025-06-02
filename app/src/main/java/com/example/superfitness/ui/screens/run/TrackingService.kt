package com.example.superfitness.ui.screens.run

import android.app.Notification
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.superfitness.R
import com.example.superfitness.SuperFitnessApplication
import com.example.superfitness.location.LocationManager
import com.example.superfitness.utils.CHANNEL_ID
import com.example.superfitness.utils.LocationsUtils
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.RoundingMode

class TrackingService : Service() {

    private lateinit var locationManager: LocationManager
    private lateinit var sensorManager: SensorManager

    private var locationTrackingJob: Job? = null

    private var isTimerEnabled = false
    private var startedTime = 0L
    private var lapTime = 0L

    private var initialSteps = -1L
    private var totalSteps = 0L

    // Service lifecycle
    override fun onCreate() {
        super.onCreate()

        // MANUALLY RETRIEVE DEPENDENCIES from SuperFitnessApplication's AppContainer
        val app = application as SuperFitnessApplication
        locationManager = app.container.locationManager
        sensorManager = app.container.sensorManager

    }

    override fun onDestroy() {
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopTracking(stopSelf = false)
        super.onDestroy()
    }

    companion object {
        private const val TAG = "TrackingService"
        var startTime = 0L

        private val _locationUiState = MutableStateFlow(LocationUiState())
        val locationUiState = _locationUiState.asStateFlow()
    }

    // We don't bind service on anything
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action) {
            Actions.START_OR_RESUME.name -> startOrResumeTracking()
            Actions.PAUSE.name -> pauseTracking()
            Actions.STOP.name -> stopTracking()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startOrResumeTracking() {
        _locationUiState.update { state ->
            if (state.isFirstRun) {
                // First run of a new tracking session
                startTime = System.currentTimeMillis() // Set initial start time for the run
                startForegroundService()
                registerStepCounter()
                startLocationTracking()
                startTimeCounting()
                state.copy(isFirstRun = false, isTracking = true)
            } else {
                // Resuming a paused tracking session
                startTimeCounting() // Resume timer
                registerStepCounter() // Re-register
                startLocationTracking() // Resume location updates
                state.copy(isTracking = true)
            }
        }
    }

    private fun pauseTracking() {
        _locationUiState.update {
            it.copy(isTracking = false, speedInKmH = 0f)
        }
        isTimerEnabled = false
        unregisterStepCounter()
        stopLocationTracking()
    }

    private fun startLocationTracking() {
        locationTrackingJob?.cancel()
        locationTrackingJob = CoroutineScope(Dispatchers.Default).launch {
            locationManager.locationUpdates.collect { location ->
                addPathPoints(location)
            }
        }
    }

    private fun stopLocationTracking() {
        locationTrackingJob?.cancel()
        locationTrackingJob = null
    }

    private fun startTimeCounting() {
        isTimerEnabled = true

        CoroutineScope(Dispatchers.Main).launch {
            startedTime = System.currentTimeMillis() // Reset on resume
            while (_locationUiState.value.isTracking) {
                // Time difference between now and time when press Start or Resume
                lapTime = System.currentTimeMillis() - startedTime
                // New total time run
                _locationUiState.update { it.copy(durationTimerInMillis = it.durationTimerInMillis + lapTime) }
                // Reset for next lap calculation
                startedTime = System.currentTimeMillis()

                delay(50L)
            }

        }
    }

    private val sensorListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            if (event == null) return

            event.let {
                val stepsSinceReboot = it.values[0].toLong()

                if (initialSteps == -1L) {
                    initialSteps = stepsSinceReboot
                }

                totalSteps = stepsSinceReboot - initialSteps
                // Update steps
                _locationUiState.update { it.copy(steps = totalSteps) }
            }
        }

        override fun onAccuracyChanged(p0: Sensor?, p1: Int) {}
    }

    private fun registerStepCounter() {

        val stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (stepCounterSensor == null) return

        sensorManager.registerListener(
            sensorListener,
            stepCounterSensor,
            SensorManager.SENSOR_DELAY_UI
        )
    }

    private fun addPathPoints(location: Location) {

        _locationUiState.update { state ->
            // Add new location to the path points
            val pos = LatLng(location.latitude, location.longitude)
            val newPathPoints = state.pathPoints + pos
            val newDistance = state.distanceInMeters.run {
                var distance = this
                if (newPathPoints.size > 1) {
                    distance += LocationsUtils.getDistanceBetweenPathPoints(
                        pathPoint1 = newPathPoints[newPathPoints.size - 1],
                        pathPoint2 = newPathPoints[newPathPoints.size - 2]
                    )
                }
                distance
            }
            val newSpeed = (location.speed * 3.6f).toBigDecimal()
                .setScale(2, RoundingMode.HALF_UP).toFloat()

            state.copy(
                bearing = location.bearing,
                currentLocation = pos,
                pathPoints = newPathPoints,
                distanceInMeters = newDistance,
                speedInKmH = newSpeed
            )
        }
    }

    private fun unregisterStepCounter() {
        sensorManager.unregisterListener(sensorListener)
        initialSteps = -1L
    }

    private fun startForegroundService() {
        startForeground(123, createNotification(this, "Tracking current location.."))
    }

    private fun createNotification(context: Context, msg: String): Notification {
        return NotificationCompat
            .Builder(context, CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.baseline_pause_24)
            .setContentTitle("GPS Tracker")
            .setContentText(msg)
            .build()
    }

    private fun stopTracking(stopSelf: Boolean = true) {
        _locationUiState.update { LocationUiState() }
        stopLocationTracking()
        unregisterStepCounter()

        stopForeground(STOP_FOREGROUND_REMOVE)
        if (stopSelf) {
            stopSelf()
        }
    }
}