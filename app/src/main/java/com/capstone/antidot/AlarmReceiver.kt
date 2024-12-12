package com.capstone.antidot

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.core.app.NotificationCompat
import com.capstone.antidot.R
import com.capstone.antidot.ui.Reminder.AddReminderByDatabaseActivity

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val time = intent.getStringExtra("TIME") ?: "Unknown Time"
        val medicationName = intent.getStringExtra("MEDICATION_NAME") ?: "Unknown Medication"

        showNotification(context, time, medicationName)
    }

    private fun showNotification(context: Context, time: String, medicationName: String) {
        val channelId = "reminder_channel"
        val channelName = "Reminder Notifications"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val soundUri = Uri.parse("android.resource://${context.packageName}/raw/notification_sound")
            var vibrationPattern = longArrayOf(0, 500, 250, 500)

            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Channel for medication reminders"
                setSound(soundUri, AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION).build())
                enableVibration(true)
                vibrationPattern = vibrationPattern
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notificationIntent = Intent(context, AddReminderByDatabaseActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle("Time to Take Your Medication!")
            .setContentText("It's time for your $medicationName at $time.")
            .setSmallIcon(R.drawable.riwayatobat)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        val notificationId = medicationName.hashCode()
        notificationManager.notify(notificationId, notification)

        val ringtone = RingtoneManager.getRingtone(context, Uri.parse("android.resource://${context.packageName}/raw/notification_sound"))
        ringtone.play()
    }
}
