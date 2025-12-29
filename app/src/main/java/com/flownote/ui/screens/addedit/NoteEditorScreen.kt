package com.flownote.ui.screens.addedit

import android.Manifest
import android.content.pm.PackageManager
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.dimensionResource
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.flownote.R
import com.flownote.data.model.Category
import com.flownote.ui.components.TagInputField
import com.flownote.util.getAdaptiveColor
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.FormatUnderlined
import com.mohamedrejeb.richeditor.model.RichTextState
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditor
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditorDefaults
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AlarmOff
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.ui.text.TextRange
import com.flownote.data.model.NoteColor
import com.flownote.util.SpeechToTextManager
import java.util.Calendar
import java.util.Date
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.rememberTextMeasurer

/**
 * Enhanced Screen for adding or editing a note
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditorScreen(
    noteId: String,
    onNavigateBack: () -> Unit,
    viewModel: NoteEditorViewModel = hiltViewModel()
) {
    val title by viewModel.title.collectAsState()
    val content by viewModel.content.collectAsState()
    val noteColor by viewModel.noteColor.collectAsState()
    val category by viewModel.category.collectAsState()
    val isPinned by viewModel.isPinned.collectAsState()

    val reminderTime by viewModel.reminderTime.collectAsState()
    val lastEdited by viewModel.lastEdited.collectAsState()
    val isNoteSaved by viewModel.isNoteSaved.collectAsState()
    
    // Temporary note state
    val isTemporary by viewModel.isTemporary.collectAsState()
    val deleteAfter by viewModel.deleteAfter.collectAsState()
    
    val isRecordingAudio by viewModel.isRecordingAudio.collectAsState()
    val isPlayingAudio by viewModel.isPlayingAudio.collectAsState()
    val hasAudio by viewModel.hasAudio.collectAsState()
    val audioPath by viewModel.audioPath.collectAsState()
    val tags by viewModel.tags.collectAsState()
    val speechState by viewModel.speechState.collectAsState()
    
    val context = LocalContext.current
    val contentFocusRequester = remember { FocusRequester() }
    
    // Navigate back when saved
    LaunchedEffect(isNoteSaved) {
        if (isNoteSaved) {
            onNavigateBack()
        }
    }
    
    // Flush auto-save when leaving the screen
    DisposableEffect(Unit) {
        onDispose {
            viewModel.flushAutoSave()
        }
    }
    
    val surfaceColor = noteColor.getAdaptiveColor(isSystemInDarkTheme())
    
    // Determine content color based on background luminance
    val isDarkBackground = calculateLuminance(surfaceColor) < 0.5
    val contentColor = if (isDarkBackground) Color.White else Color.Black
    val iconColor = contentColor.copy(alpha = 0.7f)



    var showFindReplace by remember { mutableStateOf(false) }
    var findQuery by remember { mutableStateOf("") }
    var replaceQuery by remember { mutableStateOf("") }
    var matchCount by remember { mutableStateOf(0) }
    var currentMatchIndex by remember { mutableStateOf(-1) }
    var matches by remember { mutableStateOf<List<IntRange>>(emptyList()) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    // Initialize RichTextState BEFORE using it
    val state = rememberRichTextState()
    val isLoading by viewModel.isLoading.collectAsState()
    var isInitialized by remember { mutableStateOf(false) }
    
    var lastQuery by remember { mutableStateOf("") }

    // Update matches when find query or content text changes
    LaunchedEffect(findQuery, state.annotatedString.text) {
        val text = state.annotatedString.text
        
        // Calculate Matches
        if (findQuery.isNotEmpty()) {
            val foundMatches = mutableListOf<IntRange>()
            var startIndex = 0
            while (startIndex <= text.length) {
                val index = text.indexOf(findQuery, startIndex, ignoreCase = true)
                if (index >= 0) {
                    foundMatches.add(index until (index + findQuery.length))
                    startIndex = index + 1
                } else {
                    break
                }
            }
            matches = foundMatches
            matchCount = foundMatches.size
            if (currentMatchIndex >= matchCount) currentMatchIndex = 0
            if (currentMatchIndex < 0 && matchCount > 0) currentMatchIndex = 0
            
            // Jump to first match if query changed
            if (findQuery != lastQuery && matches.isNotEmpty()) {
                val range = matches[0]
                state.selection = TextRange(range.first, range.last + 1)
            }
        } else {
            matches = emptyList()
            matchCount = 0
            currentMatchIndex = -1
        }
        lastQuery = findQuery
    }

    // Initialize RichTextState when note is loaded
    LaunchedEffect(isLoading) {
        if(!isLoading && !isInitialized) {
             state.setHtml(content)
             isInitialized = true
        }
    }
    
    // Observe rich text content changes and trigger auto-save
    LaunchedEffect(state) {
        snapshotFlow { state.annotatedString.text }
            .collect {
                if (isInitialized) {
                    // Update ViewModel content
                    viewModel.onContentChange(state.toHtml())
                }
            }
    }
    


    // Sync content back to ViewModel on change
    LaunchedEffect(state.annotatedString) {
        if (isInitialized) {
            viewModel.onContentChange(state.toHtml())
        }
    }
    
    // Handle Speech Recognition Results
    LaunchedEffect(speechState) {
        if (speechState is SpeechToTextManager.SpeechState.Result) {
            val text = (speechState as SpeechToTextManager.SpeechState.Result).text
            // Append text (simplistic approach)
            // A better approach depends on cursor position, but RichTextState API is limited here.
            // We'll insert a space and then the text.
            val currentHtml = state.toHtml()
            state.setHtml(currentHtml + " " + text)
        }
    }

    // Permission Launcher
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { /* TODO: Handle result */ }
    )
    
    // Microphone Permission Launcher for Voice-to-Text
    val micPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Permission granted, start listening
            viewModel.speechManager.startListening()
        } else {
            // Permission denied - could show a message to the user
            // For now, do nothing
        }
    }

    // Reminder Dialogs State
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var pickerMode by remember { mutableStateOf("reminder") } // "reminder" or "expiration"
    val datePickerState = rememberDatePickerState()
    val timePickerState = rememberTimePickerState()
    
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    showDatePicker = false
                    showTimePicker = true
                }) {
                    Text("Next")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
    
    if (showTimePicker) {
         AlertDialog(
             onDismissRequest = { showTimePicker = false },
             confirmButton = {
                 TextButton(onClick = {
                     showTimePicker = false
                     datePickerState.selectedDateMillis?.let { dateMillis ->
                         val calendar = Calendar.getInstance().apply {
                             timeInMillis = dateMillis
                             set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                             set(Calendar.MINUTE, timePickerState.minute)
                             set(Calendar.SECOND, 0)
                         }
                         // Use local current date if dateMillis is UTC midnight?
                         // DatePicker usually returns UTC midnight.
                         // Need to adjust for timezone offset if strictly dealing with "Calendar Day".
                         // However, simplistic approach:
                         val utcCalendar = Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC"))
                         utcCalendar.timeInMillis = dateMillis
                         
                         val finalCalendar = Calendar.getInstance()
                         finalCalendar.set(utcCalendar.get(Calendar.YEAR), utcCalendar.get(Calendar.MONTH), utcCalendar.get(Calendar.DAY_OF_MONTH), timePickerState.hour, timePickerState.minute)
                         
                         // Set based on picker mode
                         if (pickerMode == "expiration") {
                             viewModel.setExpirationDate(finalCalendar.time)
                         } else {
                             viewModel.setReminder(finalCalendar.time)
                         }
                     }
                 }) {
                     Text("Confirm")
                 }
             },
             dismissButton = {
                 TextButton(onClick = { showTimePicker = false }) {
                     Text("Cancel")
                 }
             },
             text = {
                 TimePicker(state = timePickerState)
             }
         )
    }

    Scaffold(
        containerColor = surfaceColor,
        contentWindowInsets = WindowInsets.ime, // Handle keyboard
        topBar = {
            CenterAlignedTopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.action_cancel),
                            tint = contentColor
                        )
                    }
                },
                actions = {
                    // Menu (3-dots)
                    var showMenu by remember { mutableStateOf(false) }
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More Options",
                                tint = contentColor
                            )
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            // Pin
                             DropdownMenuItem(
                                text = { Text(if (isPinned) "Unpin" else "Pin") },
                                leadingIcon = { 
                                    Icon(
                                        if (isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin, 
                                        null,
                                        tint = if (isPinned) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface 
                                    ) 
                                },
                                onClick = {
                                    showMenu = false
                                    viewModel.togglePin()
                                }
                            )
                            
                            // Reminder
                            DropdownMenuItem(
                                text = { Text(if (reminderTime != null) "Edit Reminder" else "Set Reminder") },
                                leadingIcon = { Icon(Icons.Default.Alarm, null) },
                                onClick = {
                                    showMenu = false
                                    pickerMode = "reminder"
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    }
                                    showDatePicker = true
                                }
                            )
                            
                            // HIDDEN FOR MVP LAUNCH - Uncomment to enable Temporary Notes
                            /*
                            // Temporary Note
                            DropdownMenuItem(
                                text = { 
                                    Text(
                                        if (isTemporary) "Remove Expiration" else "Set as Temporary"
                                    ) 
                                },
                                leadingIcon = { 
                                    Icon(
                                        if (isTemporary) Icons.Default.AlarmOff else Icons.Default.Alarm,
                                        null,
                                        tint = if (isTemporary) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                                    ) 
                                },
                                onClick = {
                                    showMenu = false
                                    if (isTemporary) {
                                        viewModel.removeExpiration()
                                    } else {
                                        pickerMode = "expiration"
                                        showDatePicker = true
                                    }
                                }
                            )
                            */
                            
                            // Find & Replace
                             DropdownMenuItem(
                                text = { Text("Find & Replace") },
                                leadingIcon = { Icon(Icons.Default.Search, null) },
                                onClick = {
                                    showMenu = false
                                    showFindReplace = !showFindReplace
                                }
                            )

                            // Share / Export
                            DropdownMenuItem(
                                text = { Text("Share") },
                                leadingIcon = { Icon(Icons.Default.Share, null) },
                                onClick = {
                                    showMenu = false
                                    viewModel.exportAsText()
                                }
                            )
                            
                            // Delete
                            if (noteId != "new") {
                                DropdownMenuItem(
                                    text = { Text("Delete") },
                                    leadingIcon = { Icon(Icons.Default.Delete, null) },
                                    onClick = {
                                        showMenu = false
                                        showDeleteDialog = true
                                    }
                                )
                            }
                        }
                    }

                    // Done / Save
                    IconButton(onClick = { 
                        viewModel.onContentChange(state.toHtml())
                        viewModel.saveNote() 
                    }) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = stringResource(R.string.action_save),
                            tint = contentColor
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent
                ),
                windowInsets = WindowInsets(0, 0, 0, 0) // Minimal top bar height
            )
        },
        bottomBar = {
            BottomAppBar(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    contentPadding = PaddingValues(0.dp), // Remove padding for tighter layout
                    windowInsets = WindowInsets(0, 0, 0, 0) // Remove default insets
                ) {
                 // Formatting Toolbar
                 RichTextEditorToolbar(
                    state = state,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                 )
                 
                 // Mic / Voice Dictation with Permission Request
                 val isListening = speechState is SpeechToTextManager.SpeechState.Listening
                 val context = LocalContext.current
                 
                 // Check if permission is granted
                 val hasMicPermission = remember {
                     ContextCompat.checkSelfPermission(
                         context,
                         Manifest.permission.RECORD_AUDIO
                     ) == PackageManager.PERMISSION_GRANTED
                 }
                 
                 // Pulsing animation for active listening
                 val infiniteTransition = rememberInfiniteTransition(label = "mic_pulse")
                 val alpha by infiniteTransition.animateFloat(
                     initialValue = 0.3f,
                     targetValue = 0.8f,
                     animationSpec = infiniteRepeatable(
                         animation = tween(1000, easing = FastOutSlowInEasing),
                         repeatMode = RepeatMode.Reverse
                     ),
                     label = "alpha_animation"
                 )
                 
                 Box(
                     modifier = Modifier
                         .size(48.dp)
                         .background(
                             color = if (isListening) {
                                 MaterialTheme.colorScheme.primary.copy(alpha = alpha)
                             } else {
                                 Color.Transparent
                             },
                             shape = CircleShape
                         ),
                     contentAlignment = Alignment.Center
                 ) {
                     IconButton(
                         onClick = {
                             if (isListening) {
                                 // Stop listening
                                 viewModel.speechManager.stopListening()
                             } else {
                                 // Check permission first
                                 if (hasMicPermission) {
                                     viewModel.speechManager.startListening()
                                 } else {
                                     // Request permission
                                     micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                 }
                             }
                         }
                     ) {
                         Icon(
                             if (isListening) Icons.Default.MicOff else Icons.Default.Mic,
                             "Voice Dictation",
                             tint = if (isListening) {
                                 MaterialTheme.colorScheme.onPrimary
                             } else {
                                 MaterialTheme.colorScheme.onSurface
                             }
                         )
                     }
                 }

                 Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.spacing_xsmall)))
                 
                 // Category Chip with Dropdown
                 var showCategoryMenu by remember { mutableStateOf(false) }
                 
                 Box {
                     FilterChip(
                         selected = false,
                         onClick = { showCategoryMenu = true },
                         label = { Text(category.displayName) },
                         leadingIcon = { 
                             Icon(
                                 com.flownote.ui.screens.home.getCategoryIcon(category), 
                                 null, 
                                 modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size_small))
                             ) 
                         }, 
                         colors = FilterChipDefaults.filterChipColors(
                             containerColor = MaterialTheme.colorScheme.surface,
                             labelColor = MaterialTheme.colorScheme.onSurface
                         )
                     )

                     DropdownMenu(
                         expanded = showCategoryMenu,
                         onDismissRequest = { showCategoryMenu = false }
                     ) {
                         Category.values().forEach { cat ->
                             DropdownMenuItem(
                                 text = { Text(cat.displayName) },
                                 leadingIcon = {
                                     Icon(
                                         com.flownote.ui.screens.home.getCategoryIcon(cat),
                                         contentDescription = null,
                                         modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size_small)),
                                         tint = MaterialTheme.colorScheme.onSurface
                                     )
                                 },
                                 trailingIcon = if (category == cat) {
                                     { Icon(Icons.Default.Check, null) }
                                 } else null,
                                 onClick = {
                                     viewModel.onCategoryChange(cat)
                                     showCategoryMenu = false
                                 }
                             )
                         }
                     }
                 }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                // Remove verticalScroll here if using RichTextEditor inside?
                // RichTextEditor handles its own scrolling usually.
                // But we have Title + Editor.
                // We should make the column scrollable or just let them split space.
                // Note: RichTextEditor (MohamedRejeb) usually needs to be in a scrollable container OR handles it.
                // I will use `imePadding` on Scaffold.
        ) {
            // Find/Replace Bar (Conditional)
            AnimatedVisibility(visible = showFindReplace) {
                FindReplaceBar(
                    findQuery = findQuery,
                    onFindQueryChange = { findQuery = it },
                    replaceQuery = replaceQuery,
                    onReplaceQueryChange = { replaceQuery = it },
                    onFindNext = {
                        if (matches.isNotEmpty()) {
                            currentMatchIndex = (currentMatchIndex + 1) % matches.size
                            // Jump to match
                            val range = matches[currentMatchIndex]
                            state.selection = TextRange(range.first, range.last + 1)
                        }
                    },
                    onFindPrevious = {
                        if (matches.isNotEmpty()) {
                            currentMatchIndex = if (currentMatchIndex > 0) currentMatchIndex - 1 else matches.size - 1
                            // Jump to match
                            val range = matches[currentMatchIndex]
                            state.selection = TextRange(range.first, range.last + 1)
                        }
                    },
                    onReplace = {
                        if (matches.isNotEmpty() && currentMatchIndex >= 0 && findQuery.isNotEmpty()) {
                            // Get current HTML content
                            val currentHtml = state.toHtml()
                            // Replace first occurrence in HTML
                            val newHtml = currentHtml.replaceFirst(findQuery, replaceQuery, ignoreCase = true)
                            state.setHtml(newHtml)
                            // Update viewModel content
                            viewModel.onContentChange(newHtml)
                            // Reset search to update matches
                            val tempQuery = findQuery
                            findQuery = ""
                            findQuery = tempQuery
                        }
                    },
                    onReplaceAll = {
                        if (findQuery.isNotEmpty()) {
                            // Get current HTML content
                            val currentHtml = state.toHtml()
                            // Replace all occurrences in HTML
                            val newHtml = currentHtml.replace(findQuery, replaceQuery, ignoreCase = true)
                            state.setHtml(newHtml)
                            // Update viewModel content
                            viewModel.onContentChange(newHtml)
                            // Reset search
                            findQuery = ""
                        }
                    },
                    onClose = { 
                        showFindReplace = false
                        findQuery = ""
                        replaceQuery = ""
                    },
                    matchCount = matchCount,
                    currentMatchIndex = if (currentMatchIndex >= 0) currentMatchIndex + 1 else 0,
                    contentColor = contentColor
                )
            }

            // Delete Confirmation Dialog
            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text("Delete Note?") },
                    text = { Text("This note will be permanently deleted. Did you want to delete this note?") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showDeleteDialog = false
                                viewModel.deleteNote()
                            },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Delete")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }

            // Unified Title + Content Area (Google Keep style)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .clickable(indication = null, interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }) {
                        contentFocusRequester.requestFocus()
                    }
                    .padding(horizontal = 8.dp) // Minimal padding for wider content area
            ) {
                // Title Input (seamlessly integrated)
                BasicTextField(
                    value = title,
                    onValueChange = { viewModel.onTitleChange(it) },
                    textStyle = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = contentColor
                    ),
                    decorationBox = { innerTextField ->
                        if (title.isEmpty()) {
                            Text(
                                text = "Title",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = contentColor.copy(alpha = 0.4f)
                            )
                        }
                        innerTextField()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp), // Minimal spacing
                    cursorBrush = SolidColor(contentColor)
                )

                // Content Input (Rich Text) - flows naturally after title
                RichTextEditor(
                    state = state,
                    placeholder = {
                         Text(
                            text = "Note Content...",
                            style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 24.sp),
                            color = contentColor.copy(alpha = 0.5f)
                        )
                    },
                    textStyle = MaterialTheme.typography.bodyLarge.copy(lineHeight = 24.sp, color = contentColor),
                    colors = RichTextEditorDefaults.richTextEditorColors(
                        containerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = contentColor
                    ),
                    modifier = Modifier
                        .fillMaxSize()
                        .focusRequester(contentFocusRequester)
                )
                
                // Highlight Overlay
                // Highlight Overlay removed as per user request
                // Native selection is used instead
            }
                
            // Tags Input
            TagInputField(
                tags = tags,
                onAddTag = { tag -> viewModel.addTag(tag) },
                onRemoveTag = { tag -> viewModel.removeTag(tag) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun RichTextEditorToolbar(
    state: RichTextState,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.spacing_xsmall))
    ) {
        // Bold
        ToolbarButton(
            icon = Icons.Default.FormatBold,
            isSelected = state.currentSpanStyle.fontWeight == FontWeight.Bold,
            onClick = { state.toggleSpanStyle(androidx.compose.ui.text.SpanStyle(fontWeight = FontWeight.Bold)) },
            contentColor = contentColor
        )
        // Italic
        ToolbarButton(
            icon = Icons.Default.FormatItalic,
            isSelected = state.currentSpanStyle.fontStyle == androidx.compose.ui.text.font.FontStyle.Italic,
            onClick = { state.toggleSpanStyle(androidx.compose.ui.text.SpanStyle(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)) },
            contentColor = contentColor
        )
        // Underline
        ToolbarButton(
            icon = Icons.Default.FormatUnderlined,
            isSelected = state.currentSpanStyle.textDecoration == androidx.compose.ui.text.style.TextDecoration.Underline,
            onClick = { state.toggleSpanStyle(androidx.compose.ui.text.SpanStyle(textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline)) },
            contentColor = contentColor
        )
        // Bullet List
        ToolbarButton(
            icon = Icons.AutoMirrored.Filled.FormatListBulleted,
            isSelected = false, 
            onClick = { state.toggleUnorderedList() },
            contentColor = contentColor
        )
        // Ordered List
        ToolbarButton(
            icon = Icons.Default.FormatListNumbered,
            isSelected = false,
            onClick = { state.toggleOrderedList() },
            contentColor = contentColor
        )

    }
}

@Composable
fun ToolbarButton(
    icon:  androidx.compose.ui.graphics.vector.ImageVector? = null,
    text: String? = null,
    isSelected: Boolean,
    onClick: () -> Unit,
    contentColor: Color
) {
    Box(
        modifier = Modifier
            .size(dimensionResource(id = R.dimen.chip_height))
            .background(
                color = if (isSelected) contentColor.copy(alpha = 0.2f) else Color.Transparent,
                shape = RoundedCornerShape(dimensionResource(id = R.dimen.corner_radius_xs))
            )
            .border(
                 width = dimensionResource(id = R.dimen.border_width_thin),
                 color = if (isSelected) contentColor else Color.Transparent,
                 shape = RoundedCornerShape(dimensionResource(id = R.dimen.corner_radius_xs))
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size_small))
            )
        } else if (text != null) {
             Text(
                 text = text,
                 style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                 color = contentColor
             )
        }
    }
}
@Composable
fun BottomOptionsBar(
    selectedColor: NoteColor,
    selectedCategory: Category,
    onColorChange: (NoteColor) -> Unit,
    onCategoryChange: (Category) -> Unit,
    contentColor: Color
) {
    var showColorPicker by remember { mutableStateOf(false) }
    var showCategoryPicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .padding(dimensionResource(id = R.dimen.spacing_medium))
    ) {
        // Expandable Color Picker
        AnimatedVisibility(visible = showColorPicker) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = dimensionResource(id = R.dimen.spacing_medium))
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.spacing_xsmall))
            ) {
                NoteColor.values().forEach { colorOption ->
                    val colorInt = colorOption.getAdaptiveColor(isSystemInDarkTheme())
                    val isSelected = selectedColor == colorOption
                    Box(
                        modifier = Modifier
                            .size(dimensionResource(id = R.dimen.fab_mini_size))
                            .border(
                                width = if (isSelected) dimensionResource(id = R.dimen.border_width_thick) else dimensionResource(id = R.dimen.border_width_thin),
                                color = if (isSelected) contentColor else contentColor.copy(alpha = 0.2f),
                                shape = CircleShape
                            )
                            .background(colorInt, CircleShape)
                            .clickable { onColorChange(colorOption) },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = if (calculateLuminance(colorInt) < 0.5) Color.White else Color.Black,
                                modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size_small))
                            )
                        }
                    }
                }
            }
        }
        
        // Expandable Category Picker
        AnimatedVisibility(visible = showCategoryPicker) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = dimensionResource(id = R.dimen.spacing_medium))
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.spacing_xsmall))
            ) {
                Category.values().forEach { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { onCategoryChange(category) },
                        label = { Text(category.displayName) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = contentColor.copy(alpha = 0.1f),
                            labelColor = contentColor
                        )
                    )
                }
            }
        }

        // Bottom Action Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selectedCategory.displayName,
                style = MaterialTheme.typography.bodyMedium,
                color = contentColor.copy(alpha = 0.6f),
                modifier = Modifier
                    .clickable { showCategoryPicker = !showCategoryPicker }
                    .padding(dimensionResource(id = R.dimen.spacing_xsmall))
            )

            IconButton(onClick = { showColorPicker = !showColorPicker }) {
                Box(
                    modifier = Modifier
                        .size(dimensionResource(id = R.dimen.icon_size_default))
                        .background(
                            Color(android.graphics.Color.parseColor(selectedColor.hexValue)),
                            CircleShape
                        )
                        .border(dimensionResource(id = R.dimen.border_width_thin), contentColor, CircleShape)
                )
            }
        }
    }
}


// Helper to determine brightness
fun calculateLuminance(color: Color): Double {
    return (0.2126 * color.red + 0.7152 * color.green + 0.0722 * color.blue)
}

/**
 * Overlay composable that draws highlight rectangles over matched text
 */
@Composable
fun SearchHighlightOverlay(
    text: String,
    matches: List<IntRange>,
    currentMatchIndex: Int,
    highlightColor: Color,
    textStyle: androidx.compose.ui.text.TextStyle,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()
    
    Canvas(modifier = modifier.fillMaxSize()) {
        if (matches.isEmpty()) return@Canvas
        
        // Measure the text to get character positions
        val measuredText = textMeasurer.measure(
            text = text,
            style = textStyle,
            constraints = androidx.compose.ui.unit.Constraints(
                maxWidth = size.width.toInt()
            )
        )
        
        // Draw highlights for each match. We compute per-character bounding boxes
        // and merge them per line to correctly handle multi-line matches.
        matches.forEachIndexed { index, range ->
            try {
                val start = range.first.coerceIn(0, text.length)
                val endExclusive = (range.last + 1).coerceIn(0, text.length)
                if (start >= endExclusive) return@forEachIndexed

                // Collect bounding boxes for each character in the match
                val charBoxes = mutableListOf<androidx.compose.ui.geometry.Rect>()
                for (i in start until endExclusive) {
                    try {
                        val box = measuredText.getBoundingBox(i)
                        charBoxes.add(box)
                    } catch (_: Exception) {
                        // Skip chars that can't be measured
                    }
                }

                if (charBoxes.isEmpty()) return@forEachIndexed

                // Group boxes by line (use top as key with some tolerance)
                val groups = charBoxes.groupBy { (it.top / 2).toInt() }

                val color = if (index == currentMatchIndex) highlightColor.copy(alpha = 0.6f) else highlightColor.copy(alpha = 0.3f)

                groups.values.forEach { boxesOnLine ->
                    val left = boxesOnLine.minOf { it.left }
                    val right = boxesOnLine.maxOf { it.right }
                    val top = boxesOnLine.minOf { it.top }
                    val bottom = boxesOnLine.maxOf { it.bottom }

                    drawRect(
                        color = color,
                        topLeft = androidx.compose.ui.geometry.Offset(left, top),
                        size = androidx.compose.ui.geometry.Size(right - left, bottom - top)
                    )
                }
            } catch (e: Exception) {
                // Ignore measurement errors
            }
        }
    }
}