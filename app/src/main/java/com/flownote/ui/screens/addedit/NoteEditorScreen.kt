package com.flownote.ui.screens.addedit

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.flownote.R
import com.flownote.data.model.Category
import com.flownote.ui.components.TagInputField
import com.flownote.util.getAdaptiveColor
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatListBulleted
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
import androidx.compose.ui.text.TextRange
import com.flownote.data.model.NoteColor
import com.flownote.util.SpeechToTextManager
import java.util.Calendar
import java.util.Date
import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.foundation.BorderStroke

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
    
    val isRecordingAudio by viewModel.isRecordingAudio.collectAsState()
    val isPlayingAudio by viewModel.isPlayingAudio.collectAsState()
    val hasAudio by viewModel.hasAudio.collectAsState()
    val audioPath by viewModel.audioPath.collectAsState()
    val tags by viewModel.tags.collectAsState()
    val speechState by viewModel.speechState.collectAsState()
    
    val context = LocalContext.current
    
    // Navigate back when saved
    LaunchedEffect(isNoteSaved) {
        if (isNoteSaved) {
            onNavigateBack()
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

    val state = rememberRichTextState()
    val isLoading by viewModel.isLoading.collectAsState()
    var isInitialized by remember { mutableStateOf(false) }

    // Initialize RichTextState when note is loaded
    LaunchedEffect(isLoading) {
        if(!isLoading && !isInitialized) {
             state.setHtml(content)
             isInitialized = true
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

    // Reminder Dialogs State
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
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
                         
                         viewModel.setReminder(finalCalendar.time)
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
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.action_cancel),
                            tint = contentColor
                        )
                    }
                },
                actions = {
                    // Pin Action
                    IconButton(onClick = { viewModel.togglePin() }) {
                        Icon(
                            imageVector = if (isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                            contentDescription = stringResource(R.string.action_pin),
                            tint = if (isPinned) MaterialTheme.colorScheme.primary else iconColor
                        )
                    }
                    


                    // Reminder Action
                    IconButton(onClick = {
                        if (reminderTime != null) {
                            // Clear Reminder
                            viewModel.setReminder(null)
                        } else {
                            // Check Permissions
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                            // Show Date Picker
                            showDatePicker = true
                        }
                    }) {
                        Icon(
                            imageVector = if (reminderTime != null) Icons.Default.AlarmOff else Icons.Default.Alarm,
                            contentDescription = "Set Reminder",
                            tint = if (reminderTime != null) MaterialTheme.colorScheme.primary else iconColor
                        )
                    }
                    
                    // Dictation Action
                    val isListening = speechState is SpeechToTextManager.SpeechState.Listening || speechState is SpeechToTextManager.SpeechState.Speaking
                    IconButton(onClick = { 
                        if (isListening) viewModel.speechManager.stopListening() else viewModel.speechManager.startListening() 
                    }) {
                        Icon(
                            imageVector = if (isListening) Icons.Default.Mic else Icons.Default.MicOff,
                            contentDescription = "Dictation",
                            tint = if (isListening) MaterialTheme.colorScheme.error else iconColor
                        )
                    }

                    // Export/Share Menu
                    var showMenu by remember { mutableStateOf(false) }
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More Options",
                                tint = iconColor
                            )
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Share Text") },
                                onClick = {
                                    showMenu = false
                                    viewModel.exportAsText()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Export as PDF") },
                                onClick = {
                                    showMenu = false
                                    viewModel.exportAsPdf()
                                }
                            )
                        }
                    }
                    
                    // Find Action
                    IconButton(onClick = { showFindReplace = !showFindReplace }) {
                         Icon(
                             imageVector = Icons.Default.Search,
                             contentDescription = "Find",
                             tint = if (showFindReplace) MaterialTheme.colorScheme.primary else iconColor
                         )
                    }

                    // Delete Action (only if editing existing note)
                    if (noteId != "new") {
                         IconButton(onClick = { viewModel.deleteNote() }) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = stringResource(R.string.action_delete),
                                tint = iconColor
                            )
                        }
                    }

                    // Save Action
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent, // Transparent to blend with note color
                    scrolledContainerColor = Color.Transparent
                )
            )
        },
        bottomBar = {
            Column {
                // Rich Text Toolbar
                    RichTextEditorToolbar(
                        state = state,
                        contentColor = contentColor,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Transparent)
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                
                BottomOptionsBar(
                    selectedColor = noteColor,
                    selectedCategory = category,
                    onColorChange = viewModel::onColorChange,
                    onCategoryChange = viewModel::onCategoryChange,
                    contentColor = contentColor
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // ... (keep existing content logic, this is just to context match the start)
            
            // Last Edited Caption
            lastEdited?.let {
                Text(
                    text = "Edited $it",
                    style = MaterialTheme.typography.labelSmall,
                    color = contentColor.copy(alpha = 0.4f),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            
            // Title Input
            TextField(
                value = title,
                onValueChange = { viewModel.onTitleChange(it) },
                placeholder = {
                    Text(
                        text = stringResource(R.string.title_hint),
                        style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                        color = contentColor.copy(alpha = 0.5f)
                    )
                },
                textStyle = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold, color = contentColor),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = contentColor
                ),
                modifier = Modifier.fillMaxWidth()
            )
            
            // Tag Input
            TagInputField(
                tags = tags,
                onAddTag = viewModel::addTag,
                onRemoveTag = viewModel::removeTag,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Audio Recording / Player Section
            if (hasAudio || isRecordingAudio) {
                Card(
                     colors = CardDefaults.cardColors(containerColor = contentColor.copy(alpha = 0.05f)),
                     modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.padding(12.dp).fillMaxWidth()
                    ) {
                         if (isRecordingAudio) {
                             Text(
                                 text = "Recording... (Tap to stop)",
                                 color = MaterialTheme.colorScheme.error,
                                 style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                             )
                             IconButton(onClick = { viewModel.toggleAudioRecording() }) {
                                 Icon(Icons.Default.Stop, "Stop Recording", tint = MaterialTheme.colorScheme.error)
                             }
                         } else {
                             Row(verticalAlignment = Alignment.CenterVertically) {
                                 Icon(Icons.Default.GraphicEq, null, tint = contentColor)
                                 Spacer(modifier = Modifier.width(8.dp))
                                 Text("Voice Note", color = contentColor)
                             }
                             Row {
                                 IconButton(onClick = { viewModel.toggleAudioPlayback() }) {
                                     Icon(
                                         imageVector = if (isPlayingAudio) Icons.Default.Stop else Icons.Default.PlayArrow,
                                         contentDescription = "Play/Stop",
                                         tint = contentColor
                                     )
                                 }
                                 IconButton(onClick = { viewModel.deleteAudio() }) {
                                     Icon(Icons.Default.Delete, "Delete Audio", tint = contentColor)
                                 }
                             }
                         }
                    }
                }
            } else {
                 // Option to add audio
                 // We can make this a small button or chip or just rely on toolbar
                 // For visibility, let's add a small 'Add Voice Note' button here
                 OutlinedButton(
                     onClick = { viewModel.toggleAudioRecording() },
                     modifier = Modifier.fillMaxWidth(),
                     border = BorderStroke(1.dp, contentColor.copy(alpha = 0.2f))
                 ) {
                     Icon(Icons.Default.GraphicEq, null, tint = contentColor, modifier = Modifier.size(16.dp))
                     Spacer(modifier = Modifier.width(8.dp))
                     Text("Add Voice Note", color = contentColor)
                 }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Find/Replace Bar
            AnimatedVisibility(visible = showFindReplace) {
                FindReplaceBar(
                    findQuery = findQuery,
                    onFindQueryChange = { query ->
                        findQuery = query
                        // Reset search
                        currentMatchIndex = -1
                        matchCount = 0
                        // Perform search logic if query not empty
                        if (query.isNotEmpty()) {
                            val text = state.annotatedString.text
                            // Count matches (simple case-insensitive)
                            matchCount = text.split(query, ignoreCase = true).size - 1
                            if (matchCount > 0) {
                                // Find first match
                                val index = text.indexOf(query, ignoreCase = true)
                                if (index != -1) {
                                    state.selection = TextRange(index, index + query.length)
                                    currentMatchIndex = 0
                                }
                            }
                        }
                    },
                    replaceQuery = replaceQuery,
                    onReplaceQueryChange = { replaceQuery = it },
                    onFindNext = {
                        if (findQuery.isNotEmpty() && matchCount > 0) {
                             val text = state.annotatedString.text
                             // Search after current selection
                             val startSearch = state.selection.end
                             var index = text.indexOf(findQuery, startIndex = startSearch, ignoreCase = true)
                             if (index == -1) {
                                 // Wrap around
                                 index = text.indexOf(findQuery, ignoreCase = true)
                             }
                             
                             if (index != -1) {
                                 state.selection = TextRange(index, index + findQuery.length)
                                 // Calculate match index
                                 // This is expensive for large text to recalculate every time, but fine for MVP
                                 // TODO: Optimize match index tracking
                                 // For now just increment purely based on wrap logic is hard without list
                                 // Just rely on visualization selection
                             }
                        }
                    },
                    onFindPrevious = {
                         // Similar logic but backwards (lastIndexOf)
                         if (findQuery.isNotEmpty() && matchCount > 0) {
                             val text = state.annotatedString.text
                             val startSearch = state.selection.start
                             var index = text.lastIndexOf(findQuery, startIndex = startSearch - 1, ignoreCase = true)
                             if (index == -1) {
                                 // Wrap around
                                 index = text.lastIndexOf(findQuery, ignoreCase = true)
                             }
                             if (index != -1) {
                                 state.selection = TextRange(index, index + findQuery.length)
                             }
                         }
                    },
                    onReplace = {
                        // Replace current selection if it matches findQuery
                        if (findQuery.isNotEmpty() && replaceQuery != null) {
                            val currentHtml = state.toHtml()
                            val plainText = state.annotatedString.text
                            
                            // Check if current selection matches the find query
                            if (state.selection.length > 0) {
                                val selectedText = plainText.substring(
                                    state.selection.start,
                                    state.selection.end
                                )
                                
                                if (selectedText.equals(findQuery, ignoreCase = true)) {
                                    // Find the HTML position corresponding to the selection
                                    // Strategy: Replace first occurrence in HTML that matches
                                    val regex = if (findQuery.contains("<") || findQuery.contains(">")) {
                                        // If query contains HTML chars, escape them
                                        Regex.escape(findQuery).toRegex(RegexOption.IGNORE_CASE)
                                    } else {
                                        // Match the query in text content (not in tags)
                                        Regex("(?<![<>])${Regex.escape(findQuery)}(?![<>])", RegexOption.IGNORE_CASE)
                                    }
                                    
                                    val newHtml = currentHtml.replaceFirst(regex, replaceQuery)
                                    state.setHtml(newHtml)
                                    
                                    // Update match count after replacement
                                    val updatedText = state.annotatedString.text
                                    matchCount = updatedText.split(findQuery, ignoreCase = true).size - 1
                                    
                                    // Find next match
                                    if (matchCount > 0) {
                                        val nextIndex = updatedText.indexOf(findQuery, ignoreCase = true)
                                        if (nextIndex != -1) {
                                            state.selection = TextRange(nextIndex, nextIndex + findQuery.length)
                                            currentMatchIndex = 0
                                        }
                                    } else {
                                        currentMatchIndex = -1
                                    }
                                }
                            }
                        }
                    },
                    onReplaceAll = {
                        // Replace all occurrences
                        if (findQuery.isNotEmpty() && replaceQuery != null) {
                            val currentHtml = state.toHtml()
                            
                            // Use regex to replace all occurrences while preserving HTML structure
                            val regex = if (findQuery.contains("<") || findQuery.contains(">")) {
                                Regex.escape(findQuery).toRegex(RegexOption.IGNORE_CASE)
                            } else {
                                // Match text content only (not inside tags)
                                Regex("(?<![<>])${Regex.escape(findQuery)}(?![<>])", RegexOption.IGNORE_CASE)
                            }
                            
                            val newHtml = currentHtml.replace(regex, replaceQuery)
                            state.setHtml(newHtml)
                            
                            // Reset search state
                            matchCount = 0
                            currentMatchIndex = -1
                            findQuery = ""
                        }
                    },
                    onClose = { showFindReplace = false },
                    matchCount = matchCount,
                    currentMatchIndex = currentMatchIndex,
                    contentColor = contentColor
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Content Input (Rich Text)
            RichTextEditor(
                    state = state,
                    placeholder = {
                        Text(
                            text = stringResource(R.string.content_hint),
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
                    modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 400.dp)
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
        horizontalArrangement = Arrangement.spacedBy(8.dp)
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
            icon = Icons.Default.FormatListBulleted,
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
            .size(32.dp)
            .background(
                color = if (isSelected) contentColor.copy(alpha = 0.2f) else Color.Transparent,
                shape = RoundedCornerShape(4.dp)
            )
            .border(
                 width = 1.dp,
                 color = if (isSelected) contentColor else Color.Transparent,
                 shape = RoundedCornerShape(4.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(20.dp)
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
            .padding(16.dp)
    ) {
        // Expandable Color Picker
        AnimatedVisibility(visible = showColorPicker) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NoteColor.values().forEach { colorOption ->
                    val colorInt = colorOption.getAdaptiveColor(isSystemInDarkTheme())
                    val isSelected = selectedColor == colorOption
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
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
                                modifier = Modifier.size(20.dp)
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
                    .padding(bottom = 16.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
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
                    .padding(8.dp)
            )

            IconButton(onClick = { showColorPicker = !showColorPicker }) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(
                            Color(android.graphics.Color.parseColor(selectedColor.hexValue)),
                            CircleShape
                        )
                        .border(1.dp, contentColor, CircleShape)
                )
            }
        }
    }
}

// Helper to determine brightness
fun calculateLuminance(color: Color): Double {
    return (0.2126 * color.red + 0.7152 * color.green + 0.0722 * color.blue)
}