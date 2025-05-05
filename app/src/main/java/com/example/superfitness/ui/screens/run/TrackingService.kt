package com.example.superfitness.ui.screens.run

import android.annotation.SuppressLint
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
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.superfitness.R
import com.example.superfitness.utils.CHANNEL_ID
import com.example.superfitness.utils.FATEST_LOCATION_INTERVAL
import com.example.superfitness.utils.LOCATION_UPDATE_INTERVAL
import com.example.superfitness.utils.LocationsUtils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.math.RoundingMode

class TrackingService : Service() {

    private val fusedLocationProviderClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }

    private val sensorManager: SensorManager by lazy {
        getSystemService(SENSOR_SERVICE) as SensorManager
    }

    private var isTimerEnabled = false
    private var startedTime = 0L
    private var lapTime = 0L
    private var timeRunInSeconds = MutableStateFlow<Long>(0L)
    private var lastSecondTimestamp = 0L

    private var initialSteps = -1L

    companion object {
        private const val TAG = "TrackingService"
        var startTime = 0L

        private val _locationUiState = MutableStateFlow(LocationUiState())
        val locationUiState = _locationUiState
    }
    private var serviceKilled = false

    private var isFirstRun = true
        set(value) {
            _locationUiState.update {
                it.copy(
                    isFirstRun = value
                )
            }
            field = value
        }
    private var isTracking = false
        set(value) {
            _locationUiState.update {
                it.copy(
                    isTracking = value
                )
            }
            field = value
        }
    private var timeRunInMillis = 0L
        set(value) {
            _locationUiState.update {
                it.copy(
                    durationTimerInMillis = value
                )
            }
            field = value
        }

    private var bearing = 0f
        set(value) {
            _locationUiState.update {
                it.copy(
                    bearing = bearing
                )
            }
            field = value
        }

    private var steps = 0L
        set(value) {
            _locationUiState.update {
                it.copy(
                    steps = steps
                )
            }
            field = value
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

    override fun onDestroy() {
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
        super.onDestroy()
    }

    private fun startOrResumeTracking() {
        if (isFirstRun) {
            isFirstRun = false
            isTracking = true
            startTime = System.currentTimeMillis()

            startForegroundService()
            registerStepCounter()
            requestLocationUpdates()
            startTime()
        } else {
            isTracking = true
            startTime()
            registerStepCounter()
        }
    }

    private fun pauseTracking() {
        isTracking = false
        isTimerEnabled = false
        initialSteps = -1L

        locationUiState.update {
            it.copy(
                speedInKmH = 0f
            )
        }
    }

    private fun startTime() {
        isTimerEnabled = true
        startedTime = System.currentTimeMillis() // Reset on resume

        CoroutineScope(Dispatchers.Main).launch {
            while (isTracking) {
                // Time difference between now and time when press Start or Resume
                lapTime = System.currentTimeMillis() - startedTime
                // New total time run
                timeRunInMillis += lapTime
                // Reset each loop
                startedTime = System.currentTimeMillis()

                if (timeRunInMillis >= lastSecondTimestamp + 1000L) {
                    timeRunInSeconds.value += 1
                    lastSecondTimestamp += 1000L
                }
                delay(50L)
            }

        }
    }

    private val sensorListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            if (event == null) return

            event.let {
                val stepsSinceReboot = event.values[0].toLong()

                if (initialSteps == -1L) {
                    initialSteps = stepsSinceReboot
                }

                steps += stepsSinceReboot - initialSteps
            }
        }

        override fun onAccuracyChanged(p0: Sensor?, p1: Int) {}
    }

    private fun registerStepCounter() {
        if (isTracking) {
            val stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

            if (stepCounterSensor == null) return

            sensorManager.registerListener(
                sensorListener,
                stepCounterSensor,
                SensorManager.SENSOR_DELAY_UI
            )
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationUpdates() {
        if (isTracking) {
            val locationRequest = LocationRequest
                .Builder(Priority.PRIORITY_HIGH_ACCURACY, LOCATION_UPDATE_INTERVAL)
                .setMinUpdateIntervalMillis(FATEST_LOCATION_INTERVAL)
                .setMaxUpdateDelayMillis(LOCATION_UPDATE_INTERVAL)
                .build()

            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)

            val location = result.lastLocation ?: return
            bearing = location.bearing

            if (isTracking) {
                result.locations.forEach { location ->
                    addPathPoints(location)
                }
            }
        }
    }

    private fun addPathPoints(location: Location?) = location?.let { location ->
        // Newest location
        val pos = LatLng(location.latitude, location.longitude)

        _locationUiState.update { state ->
            // Add new location to the path points
            val pathPoints = state.pathPoints + pos

            state.copy(
                currentLocation = pos,
                pathPoints = pathPoints,
                distanceInMeters = state.distanceInMeters.run {
                    var distance = this
                    if (pathPoints.size > 1) {
                        distance += LocationsUtils.getDistanceBetweenPathPoints(
                            pathPoint1 = pathPoints[pathPoints.size - 1],
                            pathPoint2 = pathPoints[pathPoints.size - 2]
                        )
                    }
                    distance
                },
                speedInKmH = (location.speed * 3.6f).toBigDecimal()
                    .setScale(2, RoundingMode.HALF_UP).toFloat()
            )
        }
    }

    private fun removeLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    private fun unregisterStepCounter() {
        sensorManager.unregisterListener(sensorListener)
        initialSteps = -1
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

    private fun stopTracking() {
        isTracking = false
        serviceKilled = true
        isFirstRun = false
        _locationUiState.update { LocationUiState() }
        removeLocationUpdates()
        unregisterStepCounter()

        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }
}