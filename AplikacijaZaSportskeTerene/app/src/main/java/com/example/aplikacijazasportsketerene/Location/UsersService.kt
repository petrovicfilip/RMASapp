package com.example.aplikacijazasportsketerene.Location

import android.app.ActivityManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.aplikacijazasportsketerene.DataClasses.User
import com.example.aplikacijazasportsketerene.MainActivity
import com.example.aplikacijazasportsketerene.Services.FirebaseDBService
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class UsersService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private lateinit var currentUserLocation : CurrentUserLocation

    //private lateinit var locationClient: UsersClient

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action) {
            ACTION_START -> start()
            ACTION_STOP -> stop()
            MARK_AS_READ -> markAsRead()
        }
        //return super.onStartCommand(intent, flags, startId)
        return START_REDELIVER_INTENT
    }

    private fun markAsRead() {

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(2)
    }

    private fun isAppInForeground(): Boolean {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val appProcesses = activityManager.runningAppProcesses ?: return false
        val packageName = packageName
        for (appProcess in appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                && appProcess.processName == packageName) {
                return true
            }
        }
        return false
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        currentUserLocation = CurrentUserLocation.getClassInstance()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun start(){

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val nearbyUsersChannel = NotificationChannel(
            LocationService.NEARBY_USERS_CHANNEL_ID,
            "Nearby Users",
            NotificationManager.IMPORTANCE_HIGH
        )
        nearbyUsersChannel.description = "Kanal za obavestenja o korisnicima u blizini"
        notificationManager.createNotificationChannel(nearbyUsersChannel)

        // ...

        serviceScope.launch(Dispatchers.IO) {
            while (true) { // proveriti da li postoji permission za lokaciju
                checkNearbyUsers()
                delay(17500) // staviti na oko 7.5 sec, povecano zbog smanjenja firebase upisa
            }
        }

    }

    suspend fun checkNearbyUsers() {
        if (currentUserLocation.location.value == null || Firebase.auth.currentUser == null)
            return

        FirebaseDBService.getClassInstance().findNearbyUsers(
            currentUserLocation.location.value!!.latitude,
            currentUserLocation.location.value!!.latitude
        ) { nearbyUsers ->

            val newNearbyUsers =
                PersistedNearbyUsers.getClassInstance().filterAndUpdateList(nearbyUsers)
            //val newNearbyUsers = nearbyUsers

            if (newNearbyUsers.isNotEmpty() && !isAppInForeground()) {
                val userCount = newNearbyUsers.size


                val resultIntent = Intent(this@UsersService, MainActivity::class.java)
                val stackBuilder = TaskStackBuilder.create(this@UsersService)
                stackBuilder.addNextIntentWithParentStack(resultIntent)

                val resultPendingIntent = stackBuilder.getPendingIntent(
                    0,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )


                val markAsReadIntent = Intent(this@UsersService, LocationService::class.java)
                markAsReadIntent.action = MARK_AS_READ
                val markAsReadPendingIntent = PendingIntent.getService(
                    this@UsersService,
                    1,
                    markAsReadIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )


                val nearbyUsersNotificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                val nearbyUsersNotification = NotificationCompat.Builder(
                    applicationContext,
                    LocationService.NEARBY_USERS_CHANNEL_ID
                )
                    .setContentTitle("Novi korisnici u blizini!")
                    .setContentText("Broj novih korisnika u blizini: $userCount")
                    .setSmallIcon(android.R.drawable.ic_dialog_map)
                    .setContentIntent(resultPendingIntent)
                    .addAction(
                        android.R.drawable.ic_menu_close_clear_cancel,
                        "TO BE IMPLEMENTED",
                        markAsReadPendingIntent
                    )
                    .setAutoCancel(true)
                    .setOngoing(false)

                nearbyUsersNotificationManager.notify(2, nearbyUsersNotification.build())
            }
        }
    }

    private fun stop() {
        stopForeground(true)
        stopSelf()
    }

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        const val MARK_AS_READ = "MARK_AS_READ"
    }

}