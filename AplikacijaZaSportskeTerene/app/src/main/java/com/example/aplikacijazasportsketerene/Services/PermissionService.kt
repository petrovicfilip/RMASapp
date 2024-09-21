package com.example.aplikacijazasportsketerene.Services

import android.app.Activity
import androidx.core.app.ActivityCompat
import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi

class PermissionService(
    val activity: Activity
) {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun getLocationAndNotificationsPermissions() {
        ActivityCompat.requestPermissions(activity, arrayOf(
            //Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.POST_NOTIFICATIONS
            ),0)

    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun getNotificationPermissions(){
        ActivityCompat.requestPermissions(activity, arrayOf(
            Manifest.permission.POST_NOTIFICATIONS
        ),0)
    }
}