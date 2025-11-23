package apps.jizzu.simpletodo.service.alarm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import apps.jizzu.simpletodo.R
import apps.jizzu.simpletodo.ui.view.MainActivity

class ReminderService : Service() {

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val title = intent?.getStringExtra("title") ?: return START_NOT_STICKY
        val timeStamp = intent.getLongExtra("time_stamp", 0).toInt()

        // Intent to launch the application when you click on notification
        val resultIntent = Intent(this, MainActivity::class.java)
        resultIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        val flagsPending = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        val pendingIntent = PendingIntent.getActivity(this, timeStamp, resultIntent, flagsPending)

        // Dismiss action
        val dismissIntent = Intent(this, DismissReceiver::class.java).apply {
            putExtra("notification_id", timeStamp)
        }
        val dismissPendingIntent = PendingIntent.getBroadcast(this, timeStamp, dismissIntent, flagsPending)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Set NotificationChannel for Android Oreo
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = notificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_ID)

            if (notificationChannel == null) {
                val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, getString(R.string.notification_channel), NotificationManager.IMPORTANCE_DEFAULT).apply {
                    enableLights(true)
                    lightColor = Color.BLUE
                    enableVibration(true)
                    setSound(android.media.RingtoneManager.getDefaultUri(android.media.RingtoneManager.TYPE_NOTIFICATION), null)
                }
                notificationManager.createNotificationChannel(channel)
            }
        }

        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID).apply {
            setContentTitle(getString(R.string.reminder_text))
            setContentText(title)
            setStyle(NotificationCompat.BigTextStyle().bigText(title))
            color = ContextCompat.getColor(this@ReminderService, R.color.blue)
            setSmallIcon(R.drawable.ic_check_circle_white_24dp)
            setDefaults(Notification.DEFAULT_SOUND or Notification.DEFAULT_VIBRATE or Notification.DEFAULT_LIGHTS)
            setContentIntent(pendingIntent)
            setAutoCancel(false)
            setTimeoutAfter(0)
            addAction(R.drawable.round_close_black_24, getString(R.string.dismiss), dismissPendingIntent)
        }

       startForeground(timeStamp, notification.build())

        return START_NOT_STICKY
    }

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "1"
    }
}