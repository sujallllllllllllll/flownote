package com.flownote.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
        startDestination = startDestination,
        enterTransition = {
            slideInHorizontally(initialOffsetX = { it }) + fadeIn()
        },
        exitTransition = {
            slideOutHorizontally(targetOffsetX = { -it / 5 }) + fadeOut()
        },
        popEnterTransition = {
            slideInHorizontally(initialOffsetX = { -it / 5 }) + fadeIn()
        },
        popExitTransition = {
            slideOutHorizontally(targetOffsetX = { it }) + fadeOut()
        }
    ) {
        // Home screen (notes list)
        composable(
            route = Screen.Home.route,
            exitTransition = {
                 fadeOut()
            },
            popEnterTransition = {
                 fadeIn()
            }
        ) {
            HomeScreen(
                onNoteClick = { noteId ->
                    navController.navigate(Screen.NoteEditor.createRoute(noteId))
                },
                onNewNoteClick = {
                    navController.navigate(Screen.NoteEditor.createRoute())
                },
                onSearchClick = {
                    navController.navigate(Screen.Search.route)
                },
                onSettingsClick = {
                    navController.navigate(Screen.Settings.route)
                },
                onPrivacyClick = {
                    navController.navigate(Screen.PrivacyPolicy.route)
                },
                onContactClick = {
                    navController.navigate(Screen.ContactUs.route)
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
            ),
            enterTransition = {
                slideInHorizontally(initialOffsetX = { it }) + fadeIn()
            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { it }) + fadeOut()
            }
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId") ?: "new"
            com.flownote.ui.screens.addedit.NoteEditorScreen(
                noteId = noteId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Categories screen
        composable(route = Screen.Categories.route) {
            // CategoriesScreen will be implemented in next phase
        }
        
        // Search screen
        composable(
            route = Screen.Search.route,
            enterTransition = {
                fadeIn()
            },
            exitTransition = {
                fadeOut()
            }
        ) {
            com.flownote.ui.screens.search.SearchScreen(
                onNavigateBack = { navController.popBackStack() },
                onNoteClick = { noteId ->
                    navController.navigate(Screen.NoteEditor.createRoute(noteId))
                }
            )
        }
        
        // Settings screen
        composable(
            route = Screen.Settings.route,
            enterTransition = {
                slideInHorizontally(initialOffsetX = { it }) + fadeIn()
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { -it }) + fadeOut()
            },
            popEnterTransition = {
                slideInHorizontally(initialOffsetX = { -it }) + fadeIn()
            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { it }) + fadeOut()
            }
        ) {
            com.flownote.ui.screens.settings.SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Privacy Policy Screen
        composable(
            route = Screen.PrivacyPolicy.route,
            enterTransition = {
                slideInHorizontally(initialOffsetX = { it }) + fadeIn()
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { -it }) + fadeOut()
            },
            popEnterTransition = {
                slideInHorizontally(initialOffsetX = { -it }) + fadeIn()
            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { it }) + fadeOut()
            }
        ) {
             com.flownote.ui.screens.info.PrivacyPolicyScreen(
                 onNavigateBack = { navController.popBackStack() }
             )
        }

        // Contact Us Screen
        composable(
            route = Screen.ContactUs.route,
            enterTransition = {
                slideInHorizontally(initialOffsetX = { it }) + fadeIn()
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { -it }) + fadeOut()
            },
            popEnterTransition = {
                slideInHorizontally(initialOffsetX = { -it }) + fadeIn()
            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { it }) + fadeOut()
            }
        ) {
             com.flownote.ui.screens.info.ContactUsScreen(
                 onNavigateBack = { navController.popBackStack() }
             )
        }
    }
}
