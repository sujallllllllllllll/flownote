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

        // Schedule the alarm with exact timing
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                // Android 12+ - Check if we can schedule exact alarms
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        time.time,
                        pendingIntent
                    )
                } else {
                    // Fallback to inexact alarm if permission not granted
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        time.time,
                        pendingIntent
                    )
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Android 6.0 to 11 - Use setExactAndAllowWhileIdle
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    time.time,
                    pendingIntent
                )
            } else {
                // Below Android 6.0 - Use setExact
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    time.time,
                    pendingIntent
                )
            }
        } catch (e: SecurityException) {
            // If exact alarms are not allowed, use inexact alarm
            alarmManager.setAndAllowWhileIdle(
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
