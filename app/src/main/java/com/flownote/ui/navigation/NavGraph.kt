package com.flownote.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.ui.Modifier
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
    modifier: Modifier = Modifier,
    startDestination: String = Screen.Home.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
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
            exitTransition = { fadeOut() },
            popEnterTransition = { fadeIn() }
        ) {
            HomeScreen(
                onNoteClick = { noteId ->
                    navController.navigate(Screen.NoteEditor.createRoute(noteId))
                }
            )
        }
        
        // Categories screen - REMOVED (functionality merged into Home screen)
        /*
        composable(
            route = Screen.Categories.route,
            enterTransition = { fadeIn() },
            exitTransition = { fadeOut() }
        ) {
            com.flownote.ui.screens.categories.CategoriesScreen(
                onNavigateBack = { navController.popBackStack() },
                onCategoryClick = { category ->
                    // Navigate to home and set category filter
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                    // TODO: Pass category to HomeScreen via savedStateHandle or ViewModel
                }
            )
        }
        */
          
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
        
        // Upcoming Features screen
        composable(
            route = Screen.UpcomingFeatures.route,
            exitTransition = { fadeOut() },
            popEnterTransition = { fadeIn() }
        ) {
            com.flownote.ui.screens.upcomingfeatures.UpcomingFeaturesScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Search screen
        composable(
            route = Screen.Search.route,
            enterTransition = { fadeIn() },
            exitTransition = { fadeOut() }
        ) {
            com.flownote.ui.screens.search.SearchScreen(
                onNavigateBack = { navController.popBackStack() },
                onNoteClick = { noteId ->
                    navController.navigate(Screen.NoteEditor.createRoute(noteId))
                }
            )
        }
        
        // HIDDEN FOR MVP - Template List Screen
        // Uncomment to enable templates
        /*
        composable(Screen.TemplateList.route) {
            TemplateListScreen(
                onNavigateBack = { navController.popBackStack() },
                onTemplateSelect = { templateId ->
                    navController.navigate(Screen.NoteEditor.createRoute("new?template=$templateId")) {
                        popUpTo(Screen.Home.route)
                    }
                }
            )
        }
        */
        
        // Upcoming Features
        composable(Screen.UpcomingFeatures.route) {
            com.flownote.ui.screens.info.UpcomingFeaturesScreen(
                onNavigateBack = { navController.popBackStack() }
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
            }
        ) {
            com.flownote.ui.screens.settings.SettingsScreen(
                onNavigateToContactUs = { navController.navigate(Screen.ContactUs.route) },
                onNavigateToPrivacyPolicy = { navController.navigate(Screen.PrivacyPolicy.route) },
                onNavigateToUpcomingFeatures = { navController.navigate(Screen.UpcomingFeatures.route) }
            )
        }
        
        // Privacy Policy Screen
        composable(
            route = Screen.PrivacyPolicy.route,
            enterTransition = {
                slideInHorizontally(initialOffsetX = { it }) + fadeIn()
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
