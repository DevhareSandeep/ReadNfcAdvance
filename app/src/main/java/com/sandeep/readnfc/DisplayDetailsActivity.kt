package com.sandeep.readnfc

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.sandeep.readnfc.Constants.GPS_INTENT_KEY
import com.sandeep.readnfc.Constants.LAT_KEY
import com.sandeep.readnfc.Constants.LOCATION_INTENT_ACTION_START
import com.sandeep.readnfc.Constants.LOCATION_INTENT_ACTION_STOP
import com.sandeep.readnfc.Constants.LONG_KEY
import com.sandeep.readnfc.Constants.PAN_ID_KEY
import com.sandeep.readnfc.Constants.TAG_KEY
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.*

private const val TAG = "DisplayDetailsActivity"
private const val PERMISSION_REQUEST_ACCESS_LOCATION = 1

class DisplayDetailsActivity : AppCompatActivity() {
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var tvLatitude: TextView
    private lateinit var tvlongitude: TextView
    private var locationPermission = false
    private lateinit var permissionlauncher: ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)
        val tv = findViewById<TextView>(R.id.panid)
        val tv2 = findViewById<TextView>(R.id.tag)
        val receivedPanId = intent.getStringExtra(PAN_ID_KEY).string()
        tv.text = "Pan Id $receivedPanId"
        val tag = intent.getStringExtra(TAG_KEY).string()
        tv2.text = "Tag $tag"
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        tvlongitude = findViewById(R.id.tv_longitude)
        tvLatitude = findViewById(R.id.tv_latitude)

        LocalBroadcastManager.getInstance(this).registerReceiver(
            mMessageReceiver, IntentFilter(GPS_INTENT_KEY)
        )
        permissionlauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permission ->
                locationPermission = permission[android.Manifest.permission.ACCESS_FINE_LOCATION] ?: locationPermission
            }
        checkPermission()

    }

    private fun isLocationServiceRunning(): Boolean {
        val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        return manager.getRunningServices(Int.MAX_VALUE)
            .any { service -> DisplayDetailsActivity::class.java.name == service.service.className }
    }

    private fun startLocationService() {
        if (!isLocationServiceRunning()) {
            val intent = Intent(this, LocationService::class.java)
            intent.action = LOCATION_INTENT_ACTION_START
            startService(intent)
            Toast.makeText(applicationContext, "Location service started", Toast.LENGTH_SHORT).show()
        }
    }

    private fun stopLocationService() {
        val intent = Intent(this, LocationService::class.java)
        intent.action = LOCATION_INTENT_ACTION_STOP
        startService(intent)
        Toast.makeText(applicationContext, "Location service stopped", Toast.LENGTH_SHORT).show()
    }

    private fun checkPermission() {

        locationPermission = ContextCompat.checkSelfPermission(
            this, android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val permissionRequest: MutableList<String> = ArrayList()

        if (!locationPermission) {

            permissionRequest.add(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }

        if (permissionRequest.isNotEmpty()) {
            permissionlauncher.launch(permissionRequest.toTypedArray())
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_ACCESS_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(applicationContext, "Granted", Toast.LENGTH_SHORT).show()
                startLocationService()
            } else {
                Toast.makeText(applicationContext, "Denied", Toast.LENGTH_SHORT).show()

            }
        }
    }

    @SuppressLint("MissingPermission")
    fun clickMe(view: View) {
        startLocationService()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopLocationService()
    }

    private val mMessageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val lat = intent.getDoubleExtra(LAT_KEY, 0.0)
            val long = intent.getDoubleExtra(LONG_KEY, 0.0)
            println(lat + long)
            tvLatitude.text = "Northing here = $lat"
            tvlongitude.text = "Easting here = $long"
        }
    }
}
