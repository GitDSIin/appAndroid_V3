package com.apptomatico.app_movil_kotlin_v3

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*

class GpsUtil(private val context: Context) {

    private val PERMISSION_ID = 42
    private var fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)



    interface LocationCallbackInterface {
        fun onLocationResult(latitude: String, longitude: String)
        fun onLocationFailure()
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun getLastLocation(activity: Activity, callback: LocationCallbackInterface) {
        if (checkPermissions()) {
            fusedLocationClient.lastLocation.addOnSuccessListener @RequiresPermission(
                allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION]
            ) { location: Location? ->
                if (location != null) {
                    callback.onLocationResult(
                        location.latitude.toString(),
                        location.longitude.toString()
                    )
                } else {
                    requestNewLocationData(callback)
                }
            }.addOnFailureListener {
                callback.onLocationFailure()
            }
        } else {
            requestPermissions(activity)
        }
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun requestNewLocationData(callback: LocationCallbackInterface) {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 0
        ).apply {
            setWaitForAccurateLocation(true)
            setMinUpdateIntervalMillis(0)
            setMaxUpdateDelayMillis(0)
            setMaxUpdates(1)
        }.build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location = locationResult.lastLocation
                if (location != null) {
                    callback.onLocationResult(
                        location.latitude.toString(),
                        location.longitude.toString()
                    )
                } else {
                    callback.onLocationFailure()
                }
                fusedLocationClient.removeLocationUpdates(this)
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    context, Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    fun requestPermissions(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            PERMISSION_ID
        )
    }

    fun isLocationPermissionGranted(): Boolean {
        return checkPermissions()
    }
}
