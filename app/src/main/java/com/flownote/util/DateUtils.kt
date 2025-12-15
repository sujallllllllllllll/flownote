package com.flownote.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * Date and time utility functions
 */
object DateUtils {
    
    private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    private val dateTimeFormat = SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault())
    
    /**
     * Format date as "Jan 15, 2024"
     */
    fun formatDate(date: Date): String {
        return dateFormat.format(date)
    }
    
    /**
     * Format time as "03:45 PM"
     */
    fun formatTime(date: Date): String {
        return timeFormat.format(date)
    }
    
    /**
     * Format date and time as "Jan 15, 2024 03:45 PM"
     */
    fun formatDateTime(date: Date): String {
        return dateTimeFormat.format(date)
    }
    
    /**
     * Get relative time string (e.g., "2 hours ago", "Yesterday")
     */
    fun getRelativeTime(date: Date): String {
        val now = Date()
        val diff = now.time - date.time
        
        return when {
            diff < TimeUnit.MINUTES.toMillis(1) -> "Just now"
            diff < TimeUnit.HOURS.toMillis(1) -> {
                val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
                "$minutes ${if (minutes == 1L) "minute" else "minutes"} ago"
            }
            diff < TimeUnit.DAYS.toMillis(1) -> {
                val hours = TimeUnit.MILLISECONDS.toHours(diff)
                "$hours ${if (hours == 1L) "hour" else "hours"} ago"
            }
            diff < TimeUnit.DAYS.toMillis(2) -> "Yesterday"
            diff < TimeUnit.DAYS.toMillis(7) -> {
                val days = TimeUnit.MILLISECONDS.toDays(diff)
                "$days days ago"
            }
            else -> formatDate(date)
        }
    }
    
    /**
     * Add days to a date
     */
    fun addDays(date: Date, days: Int): Date {
        return Date(date.time + TimeUnit.DAYS.toMillis(days.toLong()))
    }
    
    /**
     * Check if date is today
     */
    fun isToday(date: Date): Boolean {
        val today = Date()
        return formatDate(date) == formatDate(today)
    }
    
    /**
     * Check if date is in the past
     */
    fun isPast(date: Date): Boolean {
        return date.before(Date())
    }
}
