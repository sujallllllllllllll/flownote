package com.flownote.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.flownote.MainActivity
import com.flownote.R

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_SHOW_REMINDER -> {
                val noteId = intent.getStringExtra(EXTRA_NOTE_ID) ?: return
                val noteTitle = intent.getStringExtra(EXTRA_NOTE_TITLE) ?: "Note Reminder"
                val noteContent = intent.getStringExtra(EXTRA_NOTE_CONTENT) ?: ""
                
                showNotification(context, noteId, noteTitle, noteContent)
            }
            Intent.ACTION_BOOT_COMPLETED -> {
                // TODO: Reschedule all pending reminders after device reboot
                // This would require reading all notes with reminders from the database
                // and calling NotificationScheduler.scheduleReminder for each
            }
        }
    }

    private fun showNotification(
        context: Context,
        noteId: String,
        title: String,
        content: String
    ) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) 
            as NotificationManager

        // Create notification channel for Android O and above
        createNotificationChannel(notificationManager, context)

        // Create intent to open MainActivity when notification is clicked
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(EXTRA_NOTE_ID, noteId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            noteId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Build notification
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(content)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        // Show notification
        notificationManager.notify(noteId.hashCode(), notification)
    }

    private fun createNotificationChannel(
        notificationManager: NotificationManager,
        context: Context
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Note Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminders for notes"
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID = "note_reminders"
        const val ACTION_SHOW_REMINDER = "com.flownote.ACTION_SHOW_REMINDER"
        const val EXTRA_NOTE_ID = "note_id"
        const val EXTRA_NOTE_TITLE = "note_title"
        const val EXTRA_NOTE_CONTENT = "note_content"
    }
}
