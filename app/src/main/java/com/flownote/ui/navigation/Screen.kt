package com.flownote.ui.navigation

/**
 * Navigation routes for the app
 */
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object NoteEditor : Screen("note_editor/{noteId}") {
        fun createRoute(noteId: String = "new") = "note_editor/$noteId"
    }
    object Categories : Screen("categories")
    object Search : Screen("search")
    object Settings : Screen("settings")
    object PrivacyPolicy : Screen("privacy_policy")
    object ContactUs : Screen("contact_us")
}
