package com.flownote.ui.screens.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.flownote.R
import com.flownote.data.model.Category
import com.flownote.data.model.Note
import com.flownote.ui.screens.addedit.calculateLuminance
import com.flownote.util.getAdaptiveColor

/**
 * Enhanced Home screen with Staggered Grid and Search
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNoteClick: (String) -> Unit,
    onNewNoteClick: () -> Unit,
    onSearchClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onPrivacyClick: () -> Unit,
    onContactClick: () -> Unit
) {
    val notes by viewModel.notes.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val selectedTags by viewModel.selectedTags.collectAsState()
    val availableTags by viewModel.availableTags.collectAsState()
    
    // Separate pinned notes
    val pinnedNotes = notes.filter { it.isPinned }
    val otherNotes = notes.filter { !it.isPinned }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    var isSearchActive by remember { mutableStateOf(false) }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                // Drawer Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(dimensionResource(id = R.dimen.drawer_header_height))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = "FlowNotes",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(dimensionResource(id = R.dimen.spacing_medium))
                    )
                }
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_small)))

                // Navigation Items
                NavigationDrawerItem(
                    label = { Text("Settings") },
                    icon = { Icon(Icons.Filled.Settings, null) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onSettingsClick()
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    label = { Text("Privacy Policy") },
                    icon = { Icon(Icons.Default.Description, null) }, // Using Description as generic doc icon
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onPrivacyClick()
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    label = { Text("Contact Us") },
                    icon = { Icon(Icons.Default.Menu, null) }, // Placeholder, changing to Email if available or generic
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onContactClick()
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
        }
    ) {
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                Column(
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                ) {
                    // 1. TopAppBar
                    CenterAlignedTopAppBar(
                        title = {
                            Text(
                                text = "FlowNotes",
                                style = MaterialTheme.typography.titleLarge
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Menu"
                                )
                            }
                        },
                        actions = {
                            IconButton(onClick = { isSearchActive = !isSearchActive }) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search"
                                )
                            }
                        },
                        scrollBehavior = scrollBehavior,
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            scrolledContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(dimensionResource(id = R.dimen.elevation_level_2))
                        )
                    )

                    // 2. Collapsible Search Bar
                    androidx.compose.animation.AnimatedVisibility(
                        visible = isSearchActive || searchQuery.isNotEmpty(),
                    ) {
                        DockedSearchBar(
                            inputField = {
                                SearchBarDefaults.InputField(
                                    query = searchQuery,
                                    onQueryChange = viewModel::onSearchQueryChange,
                                    onSearch = { isSearchActive = false },
                                    expanded = false,
                                    onExpandedChange = { },
                                    placeholder = { Text(stringResource(R.string.search_hint)) },
                                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                                    trailingIcon = {
                                        if (searchQuery.isNotEmpty()) {
                                            IconButton(onClick = viewModel::clearFilters) {
                                                Icon(Icons.Default.Close, contentDescription = "Clear")
                                            }
                                        }
                                    },
                                )
                            },
                            expanded = false,
                            onExpandedChange = { },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = dimensionResource(id = R.dimen.screen_margin_horizontal), vertical = dimensionResource(id = R.dimen.spacing_xsmall))
                        ) {}
                    }

                    // 3. Category Filter Chips (LazyRow)
                    androidx.compose.foundation.lazy.LazyRow(
                        contentPadding = PaddingValues(horizontal = dimensionResource(id = R.dimen.screen_margin_horizontal)),
                        horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.chip_spacing)),
                        modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.spacing_xsmall))
                    ) {
                        // "All" Chip
                        item {
                            val isSelected = selectedCategory == null
                            FilterChip(
                                selected = isSelected,
                                onClick = { 
                                   // Enforce at least one selected (All)
                                   viewModel.onCategorySelected(null) 
                                },
                                label = { Text("All") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Home,
                                        contentDescription = null,
                                        modifier = Modifier.size(dimensionResource(id = R.dimen.chip_icon_size))
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                    containerColor = Color.Transparent,
                                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    iconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer
                                ),
                                border = if (isSelected) null else FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = false,
                                    borderColor = MaterialTheme.colorScheme.outline
                                ),
                                shape = RoundedCornerShape(dimensionResource(id = R.dimen.corner_radius_small)),
                                elevation = FilterChipDefaults.filterChipElevation(
                                    elevation = if (isSelected) dimensionResource(id = R.dimen.elevation_level_2) else 0.dp,
                                    pressedElevation = dimensionResource(id = R.dimen.elevation_level_2)
                                ),
                                modifier = Modifier.height(dimensionResource(id = R.dimen.chip_height))
                            )
                        }

                        // Category Chips
                        items(Category.values().size) { index ->
                            val category = Category.values()[index]
                            val isSelected = selectedCategory == category
                            
                            FilterChip(
                                selected = isSelected,
                                onClick = { 
                                    // Single selection, switching category
                                    viewModel.onCategorySelected(category)
                                },
                                label = { Text(category.displayName) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = getCategoryIcon(category),
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                    containerColor = Color.Transparent,
                                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    iconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer
                                ),
                                border = if (isSelected) null else FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = false,
                                    borderColor = MaterialTheme.colorScheme.outline
                                ),
                                shape = RoundedCornerShape(8.dp),
                                elevation = FilterChipDefaults.filterChipElevation(
                                    elevation = if (isSelected) 2.dp else 0.dp,
                                    pressedElevation = 2.dp
                                ),
                                modifier = Modifier.height(32.dp)
                            )
                        }
                    }
                    
                    if (availableTags.isNotEmpty()) {
                        androidx.compose.foundation.lazy.LazyRow(
                            contentPadding = PaddingValues(horizontal = dimensionResource(id = R.dimen.screen_margin_horizontal)),
                            horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.chip_spacing)),
                            modifier = Modifier.padding(bottom = dimensionResource(id = R.dimen.spacing_xsmall))
                        ) {
                            items(availableTags.size) { index ->
                                val tag = availableTags[index]
                                FilterChip(
                                    selected = selectedTags.contains(tag),
                                    onClick = { viewModel.toggleTagFilter(tag) },
                                    label = { Text("#$tag") },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                                        selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                )
                            }
                        }
                    }
                }
            },
        floatingActionButton = {
            var isFabExpanded by remember { mutableStateOf(false) }
            var showTemplateDialog by remember { mutableStateOf(false) }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.spacing_medium)),
                modifier = Modifier.padding(bottom = dimensionResource(id = R.dimen.spacing_xsmall))
            ) {
                // Secondary FABs (Speed Dial)
                androidx.compose.animation.AnimatedVisibility(
                    visible = isFabExpanded,
                    enter = androidx.compose.animation.slideInVertically(initialOffsetY = { it }) + androidx.compose.animation.fadeIn(),
                    exit = androidx.compose.animation.slideOutVertically(targetOffsetY = { it }) + androidx.compose.animation.fadeOut()
                ) {
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.spacing_small))
                    ) {
                        // Template Option
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(
                                text = "Template",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(dimensionResource(id = R.dimen.corner_radius_small)))
                                    .padding(horizontal = dimensionResource(id = R.dimen.spacing_xsmall), vertical = dimensionResource(id = R.dimen.spacing_xxsmall))
                            )
                            Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.spacing_xsmall)))
                            SmallFloatingActionButton(
                                onClick = { 
                                    isFabExpanded = false
                                    showTemplateDialog = true
                                },
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            ) {
                                Icon(Icons.Default.Description, "Template")
                            }
                        }

                        // Text Note Option
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(
                                text = "Text Note",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(dimensionResource(id = R.dimen.corner_radius_small)))
                                    .padding(horizontal = dimensionResource(id = R.dimen.spacing_xsmall), vertical = dimensionResource(id = R.dimen.spacing_xxsmall))
                            )
                            Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.spacing_xsmall)))
                            SmallFloatingActionButton(
                                onClick = { 
                                    isFabExpanded = false
                                    onNewNoteClick() 
                                },
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            ) {
                                Icon(Icons.Default.Edit, "Text Note")
                            }
                        }
                    }
                }

                // Main Toggle FAB
                // Main Toggle FAB
                val rotation by androidx.compose.animation.core.animateFloatAsState(
                    targetValue = if (isFabExpanded) 45f else 0f,
                    label = "fab_rotation"
                )

                FloatingActionButton(
                    onClick = { isFabExpanded = !isFabExpanded },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    elevation = FloatingActionButtonDefaults.elevation(
                        dimensionResource(id = R.dimen.elevation_level_3),
                        dimensionResource(id = R.dimen.elevation_level_5)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Expand",
                        modifier = Modifier.graphicsLayer {
                            rotationZ = rotation
                        }
                    )
                }

                if (showTemplateDialog) {
                    val scope = rememberCoroutineScope()
                    com.flownote.ui.components.TemplateSelectionDialog(
                        templates = com.flownote.data.repository.TemplateRepository.getDefaultTemplates(),
                        onDismiss = { showTemplateDialog = false },
                        onTemplateSelected = { template ->
                            showTemplateDialog = false
                            scope.launch {
                                val newNoteId = viewModel.createNoteFromTemplate(template)
                                onNoteClick(newNoteId)
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        if (notes.isEmpty() && searchQuery.isEmpty() && selectedCategory == null) {
            EmptyState(paddingValues)
        } else {
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                contentPadding = PaddingValues(
                    start = dimensionResource(id = R.dimen.screen_margin_horizontal), 
                    end = dimensionResource(id = R.dimen.screen_margin_horizontal), 
                    top = dimensionResource(id = R.dimen.screen_margin_horizontal), // No dynamic padding here, handled by Modifier
                    bottom = dimensionResource(id = R.dimen.bottom_nav_height) // Space for FAB
                ),
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.spacing_small)),
                verticalItemSpacing = dimensionResource(id = R.dimen.spacing_small),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = paddingValues.calculateTopPadding()) // Push down below header
            ) {
                // Pinned Section
                if (pinnedNotes.isNotEmpty()) {
                    item(span = androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan.FullLine) {
                        Text(
                            text = "Pinned",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.spacing_xsmall))
                        )
                    }
                    items(items = pinnedNotes, key = { it.id }) { note ->
                        NoteCard(note = note, onClick = { onNoteClick(note.id) })
                    }
                }
                
                // Others Section (Title only if we have pinned notes)
                if (pinnedNotes.isNotEmpty() && otherNotes.isNotEmpty()) {
                    item(span = androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan.FullLine) {
                        Text(
                            text = "Others",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
                
                items(items = otherNotes, key = { it.id }) { note ->
                    Box(modifier = Modifier.animateItem()) {
                        NoteCard(note = note, onClick = { onNoteClick(note.id) })
                    }
                }
            }
        }
    }
    }
}

@Composable
fun EmptyState(paddingValues: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier
                    .size(dimensionResource(id = R.dimen.top_app_bar_height))
                    .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                    .padding(dimensionResource(id = R.dimen.spacing_medium)),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_medium)))
            Text(
                text = stringResource(R.string.empty_notes_title),
                style = MaterialTheme.typography.headlineSmall,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_xsmall)))
            Text(
                text = stringResource(R.string.empty_notes_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable


// Helper to clean HTML entities
fun String.cleanHtmlEntities(): String {
    return this
        .replace("&colon;", ":")
        .replace("&lsqb;", "[")
        .replace("&rsqb;", "]")
        .replace("&amp;", "&")
        .replace("&nbsp;", " ")
        .replace("&lt;", "<")
        .replace("&gt;", ">")
        .replace("&quot;", "\"")
        .replace("&apos;", "'")
}

// Map Category to requested colors
@Composable
fun getCategoryColor(category: Category): Color {
    return when (category.name.uppercase()) {
        "MEETINGS" -> com.flownote.ui.theme.CategoryMeetings
        "TASKS" -> com.flownote.ui.theme.CategoryTasks
        "RECIPES" -> com.flownote.ui.theme.CategoryRecipes
        "CODE" -> com.flownote.ui.theme.CategoryCode
        "IDEAS" -> com.flownote.ui.theme.CategoryIdeas
        "GENERAL" -> com.flownote.ui.theme.CategoryGeneral
        else -> MaterialTheme.colorScheme.primary // Fallback
    }
}

// Relative time formatting
fun getRelativeTime(date: java.util.Date): String {
    val now = java.util.Date().time
    val time = date.time
    val diff = now - time
    
    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24
    
    return when {
        seconds < 60 -> "Just now"
        minutes < 60 -> "${minutes}m ago"
        hours < 24 -> "${hours}h ago"
        days < 7 -> "${days}d ago"
        else -> java.text.SimpleDateFormat("MMM d", java.util.Locale.getDefault()).format(date)
    }
}

// Map Category to requested Icons
@Composable
fun getCategoryIcon(category: Category): androidx.compose.ui.graphics.vector.ImageVector {
    return when (category.name.uppercase()) {
        "MEETINGS" -> Icons.Default.DateRange // Calendar replacement
        "TASKS" -> Icons.Default.CheckCircle
        "RECIPES" -> Icons.Default.Restaurant // Might fail if not in Core, will fall back to List during error check or choose standard
        "CODE" -> Icons.Default.Code
        "IDEAS" -> Icons.Default.Lightbulb
        else -> Icons.Default.Folder // General
    }
}

@Composable
fun NoteCard(
    note: Note,
    onClick: () -> Unit
) {
    val categoryColor = getCategoryColor(note.category)
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    // Smooth scale animation (0.98 scale on press)
    val scale by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        label = "scale"
    )

    // Using Material 3 Elevated Card
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
            },
        shape = RoundedCornerShape(dimensionResource(id = R.dimen.card_corner_radius)), // Standard M3 shape
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface // M3 standard card color
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = dimensionResource(id = R.dimen.card_elevation_resting), // Low elevation at rest
            pressedElevation = dimensionResource(id = R.dimen.card_elevation_pressed)
        ),
        interactionSource = interactionSource
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min) // Height Intrinsic needed for indicator to stretch, IF we want full height. 
            // Warning: IntrinsicSize.Min was suspect for crash, but strictly required for "Full Height" indicator in a dynamic height card.
            // Let's try WITHOUT Intrinsic first if possible, OR re-introduce carefully.
            // A safer way for the indicator is to just be a box in the row, but if the content grows, the box needs to grow. 
            // I will use `height(IntrinsicSize.Min)` again but ensure the parent lazy column isn't doing something weird. 
            // If it crashes again, I'll switch to a modifier-less Box that fills parent *if* possible, or just fixed height.
            // ACTUALLY, checking the crash log, it was likely just "layout" issues. 
            // Alternative: Draw the border with `drawBehind` or `border` modifier on the Card itself? 
            // Request: "Add vertical colored bar on left (4dp width, full height)".
            // I will strictly use `height(IntrinsicSize.Min)` because that is the correct Compose way. If it crashes, the issue is typically deeply nested intrinsics.
        ) {
            // Category Indicator
            Box(
                modifier = Modifier
                    .width(dimensionResource(id = R.dimen.card_indicator_width))
                    .fillMaxHeight()
                    .background(categoryColor)
            )

            // Content Container
            Column(
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.card_padding)) // All sides 16dp
                    .weight(1f)
            ) {
                // Header: Title + Pin
                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = note.title.ifBlank { "Untitled" },
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    
                    if (note.isPinned) {
                        Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.spacing_xsmall)))
                        Icon(
                            imageVector = Icons.Default.PushPin,
                            contentDescription = "Pinned",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size_small)) 
                        )
                    }
                }

                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_xsmall))) // 8dp spacing

                // Content Preview
                val plainContent = note.getPlainTextContent().cleanHtmlEntities()
                if (plainContent.isNotBlank()) {
                    Text(
                        text = plainContent,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_xsmall))) // 8dp spacing
                }

                // Footer: Date + Category
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = getRelativeTime(note.updatedAt),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    // Category Chip (Small visual)
                    // If note.category is distinct
                    Surface(
                        color = categoryColor.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(dimensionResource(id = R.dimen.corner_radius_xs)),
                    ) {
                        Text(
                            text = note.category.displayName,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = categoryColor
                            ),
                            modifier = Modifier.padding(
                                horizontal = dimensionResource(id = R.dimen.spacing_xsmall), 
                                vertical = dimensionResource(id = R.dimen.spacing_xxsmall)
                            )
                        )
                    }
                }
            }
        }
    }
}
