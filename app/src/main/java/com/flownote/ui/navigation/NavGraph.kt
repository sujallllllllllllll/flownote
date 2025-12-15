package com.flownote.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.flownote.ui.screens.home.HomeScreen

/**
 * Navigation graph for the app
 */
@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Home.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Home screen (notes list)
        composable(route = Screen.Home.route) {
            HomeScreen(
                onNoteClick = { noteId ->
                    navController.navigate(Screen.NoteEditor.createRoute(noteId))
                },
                onNewNoteClick = {
                    navController.navigate(Screen.NoteEditor.createRoute())
                }
            )
        }
        
        // Note editor screen
        composable(
            route = Screen.NoteEditor.route,
            arguments = listOf(
                navArgument("noteId") {
                    type = NavType.StringType
                    defaultValue = "new"
                }
            )
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId") ?: "new"
            // NoteEditorScreen will be implemented in next phase
            // NoteEditorScreen(
            //     noteId = noteId,
            //     onNavigateBack = { navController.popBackStack() }
            // )
        }
        
        // Categories screen
        composable(route = Screen.Categories.route) {
            // CategoriesScreen will be implemented in next phase
        }
        
        // Search screen
        composable(route = Screen.Search.route) {
            // SearchScreen will be implemented in next phase
        }
        
        // Settings screen
        composable(route = Screen.Settings.route) {
            // SettingsScreen will be implemented in next phase
        }
    }
}
