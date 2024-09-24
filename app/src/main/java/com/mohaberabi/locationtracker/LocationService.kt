package com.mohaberabi.locationtracker

import android.Manifest
import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.Notification
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

class LocationService : Service() {

    companion object {

        private const val INTERVAL = 1000L
        private const val PRIORITY = Priority.PRIORITY_HIGH_ACCURACY
    }

    private val locationRequest by lazy {
        LocationRequest.Builder(
            PRIORITY,
            INTERVAL,
        ).setIntervalMillis(INTERVAL).build()
    }

    private val locationCallBack by lazy {

        object : LocationCallback() {

            override fun onLocationResult(result: LocationResult) {

                val lat = result.lastLocation?.latitude.toString()
                val lng = result.lastLocation?.longitude.toString()

                Log.d("location", lat)
                startLocationService(lat, lng)

            }
        }
    }

    override fun onBind(p0: Intent?): IBinder? = null


    override fun onStartCommand(
        intent: Intent?, flags: Int,
        startId: Int
    ): Int {
        locationUpdates()
        return START_STICKY
    }


    private fun locationUpdates() {
        val fusedClient = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        } else {
            fusedClient.requestLocationUpdates(
                locationRequest,
                locationCallBack,
                null,
            )
        }
    }

    private fun startLocationService(
        lat: String,
        lng: String
    ) {
        startForeground(1, createNotification(lat, lng))
    }

    private fun createNotification(lat: String, lng: String): Notification {
        val notification = NotificationCompat.Builder(this, LocationTrackerApp.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Location Updates")
            .setContentText(" Lat:$lat- Lng:$lng ")
            .build()
        return notification
    }


}

