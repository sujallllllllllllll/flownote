package com.flownote.ui.screens.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import android.content.Intent
import com.flownote.R
import com.flownote.data.model.Category
import com.flownote.data.model.Note
import com.flownote.util.rememberWindowSizeClass
import com.flownote.util.scaledSp
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    onNoteClick: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val selectedTags by viewModel.selectedTags.collectAsState()
    val availableTags by viewModel.availableTags.collectAsState()
    val error by viewModel.error.collectAsState()
    
    val allNotes = uiState.allNotes
    val pinnedNotes = uiState.pinnedNotes
    val otherNotes = uiState.otherNotes
    
    // Snackbar host state
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Show error snackbar when error occurs
    LaunchedEffect(error) {
        error?.let {
            snackbarHostState.showSnackbar(
                message = it,
                duration = SnackbarDuration.Short
            )
            viewModel.clearError()
        }
    }
    
    // Window size for adaptive layouts
    val windowSizeClass = rememberWindowSizeClass()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // Header with FlowNotes title
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .height(24.dp)
                            .background(
                                MaterialTheme.colorScheme.primary,
                                RoundedCornerShape(2.dp)
                            )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.app_title),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = windowSizeClass.scaledSp(24)
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            // Search bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.onSearchQueryChange(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = {
                        Text(
                            stringResource(R.string.search_notes_hint),
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = stringResource(R.string.cd_search),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = stringResource(R.string.cd_clear_search),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    ),
                    singleLine = true
                )
            }

            // Category filter chips
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // All chip
                    item {
                        FilterChip(
                            selected = selectedCategory == null,
                            onClick = { viewModel.onCategorySelected(null) },
                            label = {
                                Text(
                                    stringResource(R.string.filter_all),
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = if (selectedCategory == null) FontWeight.SemiBold else FontWeight.Normal
                                    )
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Home,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            shape = RoundedCornerShape(20.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            border = null
                        )
                    }
                    
                    // Category chips with note counts
                    items(Category.values().toList()) { category ->
                        // Calculate note count for this category
                        val categoryNoteCount = remember(allNotes, category) {
                            allNotes.count { it.category == category }
                        }
                        
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { viewModel.onCategorySelected(category) },
                            label = {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        category.displayName,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = if (selectedCategory == category) FontWeight.SemiBold else FontWeight.Normal
                                        )
                                    )
                                    // Note count badge
                                    if (categoryNoteCount > 0) {
                                        Surface(
                                            color = if (selectedCategory == category) {
                                                MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)
                                            } else {
                                                MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                            },
                                            shape = CircleShape
                                        ) {
                                            Text(
                                                text = categoryNoteCount.toString(),
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold
                                                ),
                                                color = if (selectedCategory == category) {
                                                    MaterialTheme.colorScheme.onPrimary
                                                } else {
                                                    MaterialTheme.colorScheme.primary
                                                },
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                            },
                            leadingIcon = {
                                Icon(
                                    getCategoryIcon(category),
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            shape = RoundedCornerShape(20.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            border = if (selectedCategory == category) {
                                BorderStroke(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                            } else null
                        )
                    }
                }
            }

            // Tag filter chips
            if (availableTags.isNotEmpty()) {
                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(availableTags) { tag ->
                            FilterChip(
                                selected = selectedTags.contains(tag),
                                onClick = { viewModel.toggleTagFilter(tag) },
                                label = {
                                    Text(
                                        tag,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = if (selectedTags.contains(tag)) FontWeight.SemiBold else FontWeight.Normal
                                        )
                                    )
                                },
                                shape = RoundedCornerShape(24.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                border = null
                            )
                        }
                    }
                }
            }

            // Pinned section
            if (pinnedNotes.isNotEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.section_pinned),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        ),
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    )
                }

                // Pinned cards (horizontal scroll)
                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Show all pinned notes (horizontal scroll)
                        items(pinnedNotes) { note ->
                            PinnedNoteCard(note, onNoteClick)
                        }
                    }
                }
            }

            // Today section
            item {
                Text(
                    text = "Today",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }

            // Note list with swipe-to-delete and animations
            items(otherNotes, key = { it.id }) { note ->
                var showDeleteDialog by remember { mutableStateOf(false) }
                val haptic = LocalHapticFeedback.current
                
                // Calculate stagger delay based on index (max 500ms)
                val noteIndex = remember(otherNotes) { otherNotes.indexOf(note) }
                val animationDelay = remember(noteIndex) { (noteIndex * 50).coerceAtMost(500) }
                
                AnimatedVisibility(
                    visible = true,
                    enter = slideInVertically(
                        initialOffsetY = { it / 4 },
                        animationSpec = tween(
                            durationMillis = 300,
                            delayMillis = animationDelay,
                            easing = FastOutSlowInEasing
                        )
                    ) + fadeIn(
                        animationSpec = tween(
                            durationMillis = 300,
                            delayMillis = animationDelay
                        )
                    ),
                    exit = slideOutVertically(
                        targetOffsetY = { -it / 4 },
                        animationSpec = tween(200)
                    ) + fadeOut(animationSpec = tween(200))
                ) {
                    SwipeToDismissBox(
                        state = rememberSwipeToDismissBoxState(
                            confirmValueChange = {
                                if (it == SwipeToDismissBoxValue.EndToStart) {
                                    // Haptic feedback on swipe threshold
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    showDeleteDialog = true
                                    false // Don't dismiss yet, wait for confirmation
                                } else {
                                    false
                                }
                            }
                        ),
                        backgroundContent = {
                            // Empty background - no visual feedback needed
                            // Haptic feedback and dialog provide sufficient UX
                            Box(modifier = Modifier.fillMaxSize())
                        },
                        enableDismissFromStartToEnd = false
                    ) {
                        NoteListItem(note, onNoteClick)
                    }
                }
                
                // Delete confirmation dialog
                if (showDeleteDialog) {
                    AlertDialog(
                        onDismissRequest = { showDeleteDialog = false },
                        title = { Text(stringResource(R.string.delete_note_title)) },
                        text = { Text(stringResource(R.string.delete_note_message)) },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    showDeleteDialog = false
                                    viewModel.deleteNote(note)
                                },
                                colors = ButtonDefaults.textButtonColors(
                                    contentColor = MaterialTheme.colorScheme.error
                                )
                            ) {
                                Text(stringResource(R.string.confirm_delete))
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDeleteDialog = false }) {
                                Text(stringResource(R.string.action_cancel))
                            }
                        }
                    )
                }
            }

            // Contextual Empty state
            if (allNotes.isEmpty()) {
                item {
                    when {
                        // Search active but no results
                        searchQuery.isNotEmpty() -> EmptyStateContextual(
                            title = stringResource(R.string.empty_search_title),
                            subtitle = stringResource(R.string.empty_search_subtitle)
                        )
                        // Category filter active but no notes
                        selectedCategory != null -> EmptyStateContextual(
                            title = stringResource(R.string.empty_category_title),
                            subtitle = stringResource(R.string.empty_category_subtitle)
                        )
                        // Tag filter active but no notes
                        selectedTags.isNotEmpty() -> EmptyStateContextual(
                            title = stringResource(R.string.empty_tags_title),
                            subtitle = stringResource(R.string.empty_tags_subtitle)
                        )
                        // No notes at all
                        else -> EmptyStateSimple()
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PinnedNoteCard(
    note: Note,
    onNoteClick: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val isDarkMode = isSystemInDarkTheme()
    var showMenu by remember { mutableStateOf(false) }
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current
    
    // Optimized gradients for both light and dark modes
    val gradient = when (note.category) {
        Category.GENERAL -> Brush.linearGradient(
            colors = if (isDarkMode) {
                listOf(Color(0xFF4A5F9A), Color(0xFF5A3B72)) // Darker, muted
            } else {
                listOf(Color(0xFF667EEA), Color(0xFF764BA2))
            }
        )
        Category.MEETINGS -> Brush.linearGradient(
            colors = if (isDarkMode) {
                listOf(Color(0xFF2E5F8D), Color(0xFF1F4A6D)) // Darker blue
            } else {
                listOf(Color(0xFF4A90E2), Color(0xFF357ABD))
            }
        )
        Category.TASKS -> Brush.linearGradient(
            colors = if (isDarkMode) {
                listOf(Color(0xFF0A6B5F), Color(0xFF1F8A6D)) // Darker green
            } else {
                listOf(Color(0xFF11998E), Color(0xFF38EF7D))
            }
        )
        Category.RECIPES -> Brush.linearGradient(
            colors = if (isDarkMode) {
                listOf(Color(0xFFB85F54), Color(0xFFB84458)) // Darker pink/red
            } else {
                listOf(Color(0xFFFF9A8B), Color(0xFFFF6A88))
            }
        )
        Category.CODE -> Brush.linearGradient(
            colors = if (isDarkMode) {
                listOf(Color(0xFF5A1A9A), Color(0xFF3200A0)) // Darker purple
            } else {
                listOf(Color(0xFF8E2DE2), Color(0xFF4A00E0))
            }
        )
        Category.IDEAS -> Brush.linearGradient(
            colors = if (isDarkMode) {
                listOf(Color(0xFFB8945F), Color(0xFFB85454)) // Darker gold/pink
            } else {
                listOf(Color(0xFFFBD786), Color(0xFFF7797D))
            }
        )
        Category.STUDY -> Brush.linearGradient(
            colors = if (isDarkMode) {
                listOf(Color(0xFF4A0A8B), Color(0xFF1A4AAC)) // Darker blue/purple
            } else {
                listOf(Color(0xFF6A11CB), Color(0xFF2575FC))
            }
        )
    }

    Box(modifier = Modifier.wrapContentSize()) {
        Card(
            modifier = Modifier
                .width(140.dp)
                .height(120.dp)
                .combinedClickable(
                    onClick = { onNoteClick(note.id) },
                    onLongClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        showMenu = true
                    }
                ),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 3.dp,
                pressedElevation = 5.dp
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(gradient)
                    .padding(14.dp)
            ) {
                // Pin icon with subtle background
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.PushPin,
                        contentDescription = "Pinned",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }

                // Content
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = note.title.ifBlank { stringResource(R.string.untitled_note) },
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        ),
                        color = Color.White,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = note.getPlainTextContent(),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        ),
                        color = Color.White.copy(alpha = 0.85f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
        
        // Context menu for long press
        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false }
        ) {
            // Pin/Unpin option
            DropdownMenuItem(
                text = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            if (note.isPinned) Icons.Default.PushPin else Icons.Outlined.PushPin,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(if (note.isPinned) "Unpin Note" else "Pin Note")
                    }
                },
                onClick = {
                    viewModel.togglePin(note.id)
                    showMenu = false
                }
            )
            
            // Set Reminder option
            DropdownMenuItem(
                text = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Text("Set Reminder")
                    }
                },
                onClick = {
                    // Navigate to note editor to set reminder
                    onNoteClick(note.id)
                    showMenu = false
                }
            )
            
            // Share option
            DropdownMenuItem(
                text = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Text("Share Note")
                    }
                },
                onClick = {
                    // Share note using Android share intent
                    val shareText = buildString {
                        append(note.title)
                        append("\n\n")
                        append(note.getPlainTextContent())
                        if (note.tags.isNotEmpty()) {
                            append("\n\nTags: ")
                            append(note.tags.joinToString(", "))
                        }
                    }
                    
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, shareText)
                        type = "text/plain"
                    }
                    
                    val shareIntent = Intent.createChooser(sendIntent, "Share Note")
                    context.startActivity(shareIntent)
                    showMenu = false
                }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteListItem(
    note: Note,
    onNoteClick: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val categoryColor = getCategoryColor(note.category)
    var showMenu by remember { mutableStateOf(false) }
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current
    
    Box(modifier = Modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .combinedClickable(
                    onClick = { onNoteClick(note.id) },
                    onLongClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        showMenu = true
                    }
                ),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp,
                pressedElevation = 4.dp
            )
        ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            // Subtle gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                categoryColor.copy(alpha = 0.08f),
                                Color.Transparent
                            ),
                            startX = 0f,
                            endX = 400f
                        )
                    )
            )
            
            Row(modifier = Modifier.fillMaxWidth()) {
                // Colored left border
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(110.dp)
                        .background(categoryColor)
                )
                
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(14.dp)
                ) {
                    // Title
                    Text(
                        text = note.title.ifBlank { stringResource(R.string.untitled_note) },
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    // Preview text
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = note.getPlainTextContent(),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    // Tags
                    if (note.tags.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            note.tags.take(2).forEach { tag ->
                                Surface(
                                    color = categoryColor.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text(
                                        text = tag,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Medium
                                        ),
                                        color = categoryColor,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                            if (note.tags.size > 2) {
                                Text(
                                    text = "+${note.tags.size - 2}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                )
                            }
                        }
                    }
                }
                
                // Category icon and timestamp column
                Column(
                    modifier = Modifier.padding(10.dp),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Category icon in top right
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(categoryColor.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = getCategoryIcon(note.category),
                            contentDescription = note.category.displayName,
                            tint = categoryColor.copy(alpha = 0.7f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    
                    // Reminder icon if set
                    if (note.reminderTime != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        val isToday = remember(note.reminderTime) {
                            val today = java.util.Calendar.getInstance()
                            val reminderCal = java.util.Calendar.getInstance().apply {
                                time = note.reminderTime
                            }
                            today.get(java.util.Calendar.YEAR) == reminderCal.get(java.util.Calendar.YEAR) &&
                            today.get(java.util.Calendar.DAY_OF_YEAR) == reminderCal.get(java.util.Calendar.DAY_OF_YEAR)
                        }
                        
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Has reminder",
                            tint = if (isToday) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            },
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
                    // Timestamp in bottom right
                    Text(
                        text = getRelativeTime(note.updatedAt),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            }
        }
        
        // Context menu for long press
        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false }
        ) {
            // Pin/Unpin option
            DropdownMenuItem(
                text = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            if (note.isPinned) Icons.Default.PushPin else Icons.Outlined.PushPin,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(if (note.isPinned) "Unpin Note" else "Pin Note")
                    }
                },
                onClick = {
                    viewModel.togglePin(note.id)
                    showMenu = false
                }
            )
            
            // Set Reminder option
            DropdownMenuItem(
                text = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Text("Set Reminder")
                    }
                },
                onClick = {
                    // Navigate to note editor to set reminder
                    onNoteClick(note.id)
                    showMenu = false
                }
            )
            
            // Share option
            DropdownMenuItem(
                text = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Text("Share Note")
                    }
                },
                onClick = {
                    // Share note using Android share intent
                    val shareText = buildString {
                        append(note.title)
                        append("\n\n")
                        append(note.getPlainTextContent())
                        if (note.tags.isNotEmpty()) {
                            append("\n\nTags: ")
                            append(note.tags.joinToString(", "))
                        }
                    }
                    
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, shareText)
                        type = "text/plain"
                    }
                    
                    val shareIntent = Intent.createChooser(sendIntent, "Share Note")
                    context.startActivity(shareIntent)
                    showMenu = false
                }
            )
        }
    }
}
}

@Composable
fun EmptyStateSimple() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Description,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                stringResource(R.string.empty_notes_title),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                stringResource(R.string.empty_notes_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}

// Helper functions
fun getCategoryColor(category: Category): Color {
    return when (category) {
        Category.GENERAL -> Color(0xFF6B7280)
        Category.MEETINGS -> Color(0xFF3B82F6)
        Category.TASKS -> Color(0xFF10B981)
        Category.RECIPES -> Color(0xFFF59E0B)
        Category.CODE -> Color(0xFFA855F7)
        Category.IDEAS -> Color(0xFFFBBF24)
        Category.STUDY -> Color(0xFF8B5CF6) // Purple for study
    }
}

fun getCategoryIcon(category: Category): ImageVector {
    return when (category) {
        Category.GENERAL -> Icons.Default.Folder
        Category.MEETINGS -> Icons.Default.DateRange
        Category.TASKS -> Icons.Default.CheckCircle
        Category.RECIPES -> Icons.Default.Restaurant
        Category.CODE -> Icons.Default.Code
        Category.IDEAS -> Icons.Default.Lightbulb
        Category.STUDY -> Icons.Default.School // Book/School icon for study
    }
}


@Composable
fun getRelativeTime(date: Date): String {
    val now = Date()
    val diff = now.time - date.time
    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24

    return when {
        minutes < 1 -> stringResource(R.string.time_just_now)
        minutes < 60 -> stringResource(R.string.time_minutes_ago, minutes)
        hours < 24 -> stringResource(R.string.time_hours_ago, hours)
        days < 7 -> stringResource(R.string.time_days_ago, days)
        else -> SimpleDateFormat("MMM dd", Locale.getDefault()).format(date)
    }
}

@Composable
fun EmptyStateContextual(
    title: String,
    subtitle: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 64.dp, horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.Search,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
    }
}
