package com.sandeep.readnfc

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.sandeep.readnfc.Constants.GPS_INTENT_KEY
import com.sandeep.readnfc.Constants.LAT_KEY
import com.sandeep.readnfc.Constants.LOCATION_INTENT_ACTION_START
import com.sandeep.readnfc.Constants.LOCATION_INTENT_ACTION_STOP
import com.sandeep.readnfc.Constants.LONG_KEY
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

class LocationService : Service() {

    private var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            if (result.lastLocation != null) {
                val lat = result.lastLocation?.latitude.default()
                val long = result.lastLocation?.longitude.default()
                sendMessageToActivity(lat, long)
            }
        }
    }

    private fun sendMessageToActivity(lat: Double, long: Double) {
        val intent = Intent(GPS_INTENT_KEY)
        intent.putExtra(LAT_KEY, lat)
        intent.putExtra(LONG_KEY, long)
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
    }

    override fun onBind(p0: Intent?): IBinder? {
        throw java.lang.UnsupportedOperationException("not yet implemented")
    }

    private fun startLocationService() {

        val locaionRequest = LocationRequest()
        locaionRequest.setInterval(4000)
        locaionRequest.setFastestInterval(2000)

        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        LocationServices.getFusedLocationProviderClient(this)
            .requestLocationUpdates(locaionRequest, locationCallback, Looper.getMainLooper())
        // startForeground(175, builder.build())
    }

    private fun stopLocationService() {
        LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(locationCallback)
        stopForeground(true)
        stopSelf()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.action?.let { action ->
            when (action) {
                LOCATION_INTENT_ACTION_START -> startLocationService()
                LOCATION_INTENT_ACTION_STOP -> stopLocationService()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

}