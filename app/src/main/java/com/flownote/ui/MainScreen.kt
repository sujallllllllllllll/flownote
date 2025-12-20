package com.flownote.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
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
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Logic to show/hide bottom bar
    val isBottomBarVisible = currentDestination?.route in listOf(
        Screen.Home.route,
        Screen.Settings.route
    )

    // FAB Menu State
    var isFabExpanded by remember { mutableStateOf(false) }
    var showTemplateDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // FAB Rotation Animation
    val rotation by animateFloatAsState(
        targetValue = if (isFabExpanded) 45f else 0f,
        label = "fab_rotation",
        animationSpec = androidx.compose.animation.core.tween(durationMillis = 200)
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            bottomBar = {
                AnimatedVisibility(
                    visible = isBottomBarVisible,
                    enter = slideInVertically { it },
                    exit = slideOutVertically { it }
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer,
                            tonalElevation = dimensionResource(id = R.dimen.elevation_level_2)
                        ) {
                            // Home
                            NavigationBarItem(
                                selected = currentDestination?.hierarchy?.any { it.route == Screen.Home.route } == true,
                                onClick = {
                                    if (isFabExpanded) isFabExpanded = false // Close FAB if open
                                    navController.navigate(Screen.Home.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                                label = { Text("Notes") }
                            )

                            // Placeholder for FAB
                            NavigationBarItem(
                                selected = false,
                                onClick = { },
                                icon = { },
                                enabled = false
                            )

                            // Settings
                            NavigationBarItem(
                                selected = currentDestination?.hierarchy?.any { it.route == Screen.Settings.route } == true,
                                onClick = {
                                    if (isFabExpanded) isFabExpanded = false // Close FAB if open
                                    navController.navigate(Screen.Settings.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                                label = { Text("Settings") }
                            )
                        }

                        // Floating Action Button
                        FloatingActionButton(
                            onClick = { isFabExpanded = !isFabExpanded },
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            shape = CircleShape,
                            elevation = FloatingActionButtonDefaults.elevation(
                                defaultElevation = 6.dp, // Slightly higher to pop
                                pressedElevation = 6.dp
                            ),
                            modifier = Modifier
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Create",
                                modifier = Modifier.rotate(rotation)
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            NavGraph(
                navController = navController,
                modifier = Modifier.padding(innerPadding)
            )
        }

        // Overlay for FAB Menu
        if (isFabExpanded) {
            // Dimmed Background
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { isFabExpanded = false } // Close on outside tap
            )

            // FAB Menu Items
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 130.dp), // Increased spacing from bottom
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp) // Increased separation
            ) {
                // Template Note (Secondary)
                FabMenuItem(
                    text = "Template Note",
                    icon = Icons.Default.Description,
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ) {
                    isFabExpanded = false
                    showTemplateDialog = true
                }

                // Blank Note (Primary)
                FabMenuItem(
                    text = "Blank Note",
                    icon = Icons.Default.Edit,
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    isPrimary = true
                ) {
                    isFabExpanded = false
                    navController.navigate(Screen.NoteEditor.createRoute())
                }
            }
        }
        
        // Template Dialog
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
    }
}

@Composable

fun FabMenuItem(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    containerColor: Color,
    contentColor: Color,
    isPrimary: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End,
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = if (isPrimary) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Normal
            ),
            color = Color.White, // Always white on dim background for contrast
            modifier = Modifier
                .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        SmallFloatingActionButton(
            onClick = onClick,
            containerColor = containerColor,
            contentColor = contentColor,
            shape = CircleShape
        ) {
            Icon(imageVector = icon, contentDescription = null)
        }
    }
}
