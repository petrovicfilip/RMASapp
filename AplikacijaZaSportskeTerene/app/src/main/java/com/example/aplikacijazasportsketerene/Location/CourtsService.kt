package com.example.aplikacijazasportsketerene.Location

import android.app.ActivityManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.aplikacijazasportsketerene.MainActivity
import com.example.aplikacijazasportsketerene.Services.FirebaseDBService
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class CourtsService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private lateinit var currentUserLocation : CurrentUserLocation

    private val firebaseDBService = FirebaseDBService.getClassInstance()

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action){
            ACTION_START -> start()
            ACTION_STOP -> stop()
        }

        return START_REDELIVER_INTENT
        //return super.onStartCommand(intent, flags, startId)
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

        val notification = NotificationCompat.Builder(this, "location")
            .setContentTitle("Trazenje terena...")
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .setOngoing(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val nearbyCourtsChannel = NotificationChannel(
            NEARBY_COURTS_CHANNEL_ID, // ovo je bitno za NotificationCompat
            "Nearby courts",
            NotificationManager.IMPORTANCE_HIGH
        )

        val nearbyCourtsPermanentChannel = NotificationChannel(
            NEARBY_COURTS_PERMANENT_CHANNEL_ID, // ovo je bitno za NotificationCompat
            "Finding nearby courts",
            NotificationManager.IMPORTANCE_LOW
        )


        nearbyCourtsChannel.description = "Kanal za obavestenja i obilazak terena u blizini"
        notificationManager.createNotificationChannel(nearbyCourtsChannel)
        notificationManager.createNotificationChannel(nearbyCourtsPermanentChannel)
        notificationManager.notify(4,notification.build())

        startForeground(4, notification.build())

        serviceScope.launch(Dispatchers.IO) {
            while (true) { // proveriti da li postoji permission za lokaciju
                findNearbyCourts()
                delay(7500) // staviti na oko 7.5 sec, povecano zbog smanjenja firebase upisa
            }
        }

    }

    suspend fun findNearbyCourts(){
        if (currentUserLocation.location.value == null || Firebase.auth.currentUser == null)
            return
        Log.d("TRAZIM TERENE","TRAZIM TERENE...${ currentUserLocation.location.value!!.latitude}, ${ currentUserLocation.location.value!!.longitude}")
        FirebaseDBService.getClassInstance().findNearbyCourts(
            currentUserLocation.location.value!!.latitude,
            currentUserLocation.location.value!!.longitude // ovde je pisalo latitude isto xDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD, cu se utepam
        ) { nearbyCourts ->

/*            val newNearbyUsers =
                PersistedNearbyUsers.getClassInstance().filterAndUpdateList(nearbyUsers)*/

                val courtCount = nearbyCourts.count()
            Log.d("TRAZIM TERENE","NASAO SAM ${courtCount} komada!!!")
            if (nearbyCourts.isNotEmpty()) {


                val resultIntent = Intent(this@CourtsService, MainActivity::class.java)
                val stackBuilder = TaskStackBuilder.create(this@CourtsService)
                stackBuilder.addNextIntentWithParentStack(resultIntent)

                val resultPendingIntent = stackBuilder.getPendingIntent(
                    0,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                val nearbyUsersNotificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                val nearbyUsersNotification = NotificationCompat.Builder(
                    applicationContext,
                    LocationService.NEARBY_USERS_CHANNEL_ID
                )
                    .setContentTitle("Otkrio si novi teren!")
                    .setContentText("Broj otkrivenih terena: $courtCount\n" + "Dobio si ${firebaseDBService.foundCourtPoints * courtCount} poena!")
                    .setSmallIcon(android.R.drawable.star_off)
                    .setContentIntent(resultPendingIntent)
                    .setAutoCancel(true)
                    .setOngoing(false)

                nearbyUsersNotificationManager.notify(3, nearbyUsersNotification.build())
            }
            /*if(isAppInForeground()){
                withContext(Dispatchers.IO){
                    Toast.makeText(
                        applicationContext,
                        "Dobili ste ${firebaseDBService.foundCourtPoints * courtCount}" +
                                " poena i otkrili $courtCount terena ",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }*/
        }
    }

    private fun stop() {
        stopForeground(true)
        stopSelf()
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

    companion object{
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        const val NEARBY_COURTS_CHANNEL_ID = "NEARBY_COURTS_CHANNEL_ID"
        const val NEARBY_COURTS_PERMANENT_CHANNEL_ID = "NEARBY_COURTS_PERMANENT_CHANNEL_ID"
    }
}