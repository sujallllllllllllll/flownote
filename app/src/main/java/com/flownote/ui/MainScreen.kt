package com.flownote.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.flownote.R
import com.flownote.ui.navigation.NavGraph
import com.flownote.ui.navigation.Screen
import kotlinx.coroutines.launch

@Composable
fun MainScreen(initialNoteId: String? = null) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Handle deep link from notification
    LaunchedEffect(initialNoteId) {
        if (initialNoteId != null) {
            // Navigate to the specific note
            navController.navigate(Screen.NoteEditor.createRoute(initialNoteId))
        }
    }

    // Logic to show/hide bottom bar
    val isBottomBarVisible = currentDestination?.route in listOf(
        Screen.Home.route,
        Screen.Settings.route
    )

    // HIDDEN FOR MVP - Uncomment to enable expandable FAB menu with templates
    // var isFabExpanded by remember { mutableStateOf(false) }
    
    // FAB Menu State
    var showTemplateDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // HIDDEN FOR MVP - FAB rotation animation
    // val rotation by animateFloatAsState(
    //     targetValue = if (isFabExpanded) 45f else 0f,
    //     animationSpec = tween(200), label = ""
    // )

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.imePadding(), // Push bottom bar above keyboard
            bottomBar = {
                AnimatedVisibility(
                    visible = isBottomBarVisible,
                    enter = slideInVertically { it },
                    exit = slideOutVertically { it }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(96.dp) // Taller to accommodate elevated FAB and taller nav bar
                    ) {
                        // Background Navigation Bar
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer,
                            tonalElevation = dimensionResource(id = R.dimen.elevation_level_2),
                            windowInsets = WindowInsets(0, 0, 0, 0),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(dimensionResource(id = R.dimen.bottom_nav_height))
                                .align(Alignment.BottomCenter)
                        ) {
                            // Home/Notes - takes up left side
                            NavigationBarItem(
                                selected = currentDestination?.hierarchy?.any { it.route == Screen.Home.route } == true,
                                onClick = {
                                    navController.navigate(Screen.Home.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                                label = { Text("Notes") },
                                modifier = Modifier.weight(1f) // Take up half of available space
                            )

                            // Empty space for FAB - fixed width to prevent pushing items too far
                            Spacer(modifier = Modifier.width(80.dp))

                            // Settings - takes up right side
                            NavigationBarItem(
                                selected = currentDestination?.hierarchy?.any { it.route == Screen.Settings.route } == true,
                                onClick = {
                                    navController.navigate(Screen.Settings.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                                label = { Text("Settings") },
                                modifier = Modifier.weight(1f) // Take up half of available space
                            )
                        }
                        
                        // Elevated FAB - positioned above the nav bar
                        FloatingActionButton(
                            onClick = {
                                navController.navigate(Screen.NoteEditor.createRoute("new"))
                            },
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            shape = CircleShape,
                            elevation = FloatingActionButtonDefaults.elevation(
                                defaultElevation = 8.dp,
                                pressedElevation = 12.dp,
                                hoveredElevation = 10.dp
                            ),
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .offset(y = 8.dp) // Slight offset from top for better visual balance
                                .size(64.dp) // Larger, more prominent FAB
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Create Note",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        ) { paddingValues ->
            NavGraph(
                navController = navController,
                modifier = Modifier.padding(paddingValues)
            )
        }

        // HIDDEN FOR MVP - FAB Expandable Menu Overlay
        // Uncomment this entire block to enable template selection
        /*
        if (isFabExpanded) {
            // Dimmed Background
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { isFabExpanded = false }
            )

            // FAB Menu Items
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 130.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Template Note
                FABMenuItem(
                    icon = Icons.AutoMirrored.Filled.Label,
                    label = "From Template",
                    onClick = {
                        isFabExpanded = false
                        showTemplateDialog = true
                    }
                )
                
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_small)))

                // Blank Note
                FABMenuItem(
                    icon = Icons.Default.Edit,
                    label = "Blank Note",
                    onClick = {
                        isFabExpanded = false
                        navController.navigate(Screen.NoteEditor.createRoute("new"))
                    }
                )
            }
        }
        */
        
        // HIDDEN FOR MVP - Template Selection Dialog
        // Uncomment to enable template feature
        /*
        if (showTemplateDialog) {
            com.flownote.ui.components.TemplateSelectionDialog(
                templates = com.flownote.data.repository.TemplateRepository.getDefaultTemplates(),
                onDismiss = { showTemplateDialog = false },
                onTemplateSelected = { template ->
                    showTemplateDialog = false
                    scope.launch {
                        val newNoteId = viewModel.createNoteFromTemplate(template)
                        navController.navigate(Screen.NoteEditor.createRoute(newNoteId))
                    }
                }
            )
        }
        */
    }
}

// HIDDEN FOR MVP - FAB Menu Item Component
// Uncomment to enable template feature
/*
@Composable
fun FABMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontal Alignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SmallFloatingActionButton(
            onClick = onClick,
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ) {
            Icon(imageVector = icon, contentDescription = null)
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
*/
