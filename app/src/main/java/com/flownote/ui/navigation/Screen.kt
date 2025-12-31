package com.flownote.ui.navigation

/**
 * Navigation routes for the app
 */
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Settings : Screen("settings")
    object NoteEditor : Screen("note_editor/{noteId}") {
        fun createRoute(noteId: String) = "note_editor/$noteId"
    }
    object ContactUs : Screen("contact_us")
    object PrivacyPolicy : Screen("privacy_policy")
    object UpcomingFeatures : Screen("upcoming_features")
    object Help : Screen("help")
}
