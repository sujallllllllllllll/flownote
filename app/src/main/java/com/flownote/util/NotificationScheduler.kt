package com.flownote.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.util.Date

class NotificationScheduler {

    /**
     * Schedule a reminder notification for a note
     * @param context Application context
     * @param noteId Unique ID of the note
     * @param noteTitle Title of the note
     * @param time Date/time when the reminder should trigger
     */
    fun scheduleReminder(
        context: Context,
        noteId: String,
        noteTitle: String,
        time: Date
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            action = NotificationReceiver.ACTION_SHOW_REMINDER
            putExtra(NotificationReceiver.EXTRA_NOTE_ID, noteId)
            putExtra(NotificationReceiver.EXTRA_NOTE_TITLE, noteTitle)
            // Removed EXTRA_NOTE_CONTENT - only showing title
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            noteId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Schedule the alarm
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Use setExactAndAllowWhileIdle for API 23+
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                time.time,
                pendingIntent
            )
        } else {
            // Use setExact for older versions
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                time.time,
                pendingIntent
            )
        }
    }

    /**
     * Cancel a scheduled reminder for a note
     * @param context Application context
     * @param noteId Unique ID of the note
     */
    fun cancelReminder(context: Context, noteId: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            action = NotificationReceiver.ACTION_SHOW_REMINDER
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            noteId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }
}
