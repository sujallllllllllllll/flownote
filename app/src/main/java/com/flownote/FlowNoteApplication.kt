package com.flownote

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class for FlowNote
 * Annotated with @HiltAndroidApp to enable Hilt dependency injection
 */
@HiltAndroidApp
class FlowNoteApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        // Initialize app-level components here if needed
    }
}
