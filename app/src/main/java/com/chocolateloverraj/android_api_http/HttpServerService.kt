package com.chocolateloverraj.android_api_http

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

class HttpServerService : Service() {
    companion object {
        var started = false
    }

    private val server by lazy {
        embeddedServer(Netty, port = 8080) {
            install(CORS) {
                anyHost()
            }

            install(createApplicationPlugin("Make sure it's called from same device") {
                onCall { call ->
                    val localAddress = call.request.local.localAddress
                    if (localAddress != "::1") {
                        call.respond(HttpStatusCode.Forbidden)
                    }
                }
            })

            routing {
                post("/requestPermission") {
                    Log.d(HttpServerService::class.java.name, call.request.headers["Origin"]!!)
                    call.respond(HttpStatusCode.NotImplemented)
                }

                get("/") {
                    call.respondText(
                        "Server is on ${call.request.headers["Origin"]}",
                        ContentType.Text.Plain
                    )
                }
            }
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        Log.d(this::class.java.name, "onBind")
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        started = true
        server.start(wait = false)
        Log.d(this::class.java.name, "onStartCommand")

        generateForegroundNotification()

        return START_REDELIVER_INTENT
    }

    override fun onDestroy() {
        started = false
        server.stop()
        Log.d(this::class.java.name, "stopService")
        stopForeground(true)
        super.onDestroy()
    }

    //Notififcation for ON-going
    private var notification: Notification? = null
    var mNotificationManager: NotificationManager? = null
    private val mNotificationId = 123
    private fun generateForegroundNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val intentMainLanding = Intent(this, MainActivity::class.java)
            val pendingIntent =
                PendingIntent.getActivity(this, 0, intentMainLanding, 0)
            if (mNotificationManager == null) {
                mNotificationManager =
                    this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                assert(mNotificationManager != null)
                val notificationChannel =
                    NotificationChannel(
                        "service_channel", "Service Notifications",
                        NotificationManager.IMPORTANCE_MIN
                    )
                notificationChannel.enableLights(false)
                mNotificationManager?.createNotificationChannel(notificationChannel)
            }
            val builder = NotificationCompat.Builder(this, "service_channel")

            builder.setContentTitle("Service is running")
                .setContentText("Touch to open")
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setOnlyAlertOnce(true)
                .setTicker("Ticker")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .addAction(
                    0, "Stop", PendingIntent.getBroadcast(
                        this,
                        0,
                        Intent(this, StopReceiver::class.java),
                        0
                    )
                )
            notification = builder.build()
            startForeground(mNotificationId, notification)
        }

    }
}