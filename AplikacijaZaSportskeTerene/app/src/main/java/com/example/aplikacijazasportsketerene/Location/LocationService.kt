package com.example.aplikacijazasportsketerene.Location

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
import com.example.aplikacijazasportsketerene.MainActivity
import com.example.aplikacijazasportsketerene.Services.FirebaseDBService
import com.google.android.gms.location.LocationServices
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class LocationService: Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var locationClient: LocationClient

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        locationClient = LocationClient(
            applicationContext,
            LocationServices.getFusedLocationProviderClient(applicationContext)
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action) {
            ACTION_START -> start()
            ACTION_STOP -> stop()
        }
        //return super.onStartCommand(intent, flags, startId)
        return START_REDELIVER_INTENT
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun start() {
        val notification = NotificationCompat.Builder(this, "location")
            .setContentTitle("Pracenje lokacije...")
            .setContentText("Lokacija: -")
            .setSmallIcon(android.R.drawable.ic_menu_mapmode)
            .setOngoing(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//        val nearbyUsersChannel = NotificationChannel(
//            NEARBY_USERS_CHANNEL_ID,
//            "Nearby Users",
//            NotificationManager.IMPORTANCE_HIGH
//        )
//        nearbyUsersChannel.description = "Kanal za obavestenja o korisnicima u blizini"
//        notificationManager.createNotificationChannel(nearbyUsersChannel)

        locationClient
            .getLocationUpdates(1000L)
            .catch { e -> e.printStackTrace() }
            .onEach { location ->
                CurrentUserLocation.getClassInstance().previousLocation.value = CurrentUserLocation.getClassInstance().location.value
                CurrentUserLocation.getClassInstance().location.value = location
                val lat = location.latitude.toString()
                val long = location.longitude.toString()
                val updatedNotification = notification.setContentText(
                    "Lokacija: ($lat, $long)"
                )
                val geopoint = GeoPoint(location.latitude, location.longitude)
                val currentUserId = Firebase.auth.currentUser?.uid
                if(currentUserId != null) {
                    GlobalScope.launch(Dispatchers.IO) {
                        FirebaseDBService.getClassInstance().updateUserLocation(
                            currentUserId,
                            geopoint
                        )
                    }
                }

                ///////////////////////
//                FirebaseDBService().findNearbyUsers(location.latitude, location.longitude) { nearbyUsers ->
//                    if (nearbyUsers.isNotEmpty()) {
//                        val userCount = nearbyUsers.size
//
//                        // Pending intent for opening the app
//                        val resultIntent = Intent(this@LocationService, MainActivity::class.java)
//                        val stackBuilder = TaskStackBuilder.create(this@LocationService)
//                        stackBuilder.addNextIntentWithParentStack(resultIntent)
//
//                        val resultPendingIntent = stackBuilder.getPendingIntent(
//                            0,
//                            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//                        )
//
//                        // Pending intent for marking notification as read
//                        val markAsReadIntent = Intent(this@LocationService, LocationService::class.java)
//                        markAsReadIntent.action = ACTION_MARK_AS_READ
//                        val markAsReadPendingIntent = PendingIntent.getService(
//                            this@LocationService,
//                            1,
//                            markAsReadIntent,
//                            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//                        )
//
//                        val nearbyUsersNotificationManager =
//                            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//                        // Build notification for nearby users
//                        val nearbyUsersNotification = NotificationCompat.Builder(applicationContext, NEARBY_USERS_CHANNEL_ID)
//                            .setContentTitle("Novi korisnici u blizini!")
//                            .setContentText("Broj novih korisnika u blizini: $userCount")
//                            .setSmallIcon(android.R.drawable.ic_dialog_map)
//                            .setContentIntent(resultPendingIntent)
//                            .addAction(
//                                android.R.drawable.ic_menu_close_clear_cancel,
//                                "Oznaci kao procitano",
//                                markAsReadPendingIntent
//                            )
//                            .setAutoCancel(true)
//                            .setOngoing(false)
//
//                        // Show the nearby users notification (without startForeground)
//                        nearbyUsersNotificationManager.notify(2, nearbyUsersNotification.build())
//                    }
//                }
                //////////////////////////////////


                notificationManager.notify(1, updatedNotification.build())
            }
            .launchIn(serviceScope)

        startForeground(1, notification.build())
    }

    private fun stop() {
        stopForeground(true)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        var locationUpdates = MutableStateFlow<Location?>(null)

        const val ACTION_MARK_AS_READ = "ACTION_MARK_AS_READ"
        //const val LOCATION_CHANNEL_ID = "location_channel" - TBD
        const val NEARBY_USERS_CHANNEL_ID = "nearby_users_channel"
        const val LOCATION_NOTIFICATION_ID = 1
        const val NEARBY_USERS_NOTIFICATION_ID = 2
    }

}