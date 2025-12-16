package com.flownote.ui.screens.addedit

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp

/**
 * Editor for Checklist notes.
 * Converts newline-separated string into a list of checkable items.
 * 
 * Format convention for storage:
 * [x] Completed item
 * [ ] Incomplete item
 * Plain text lines are treated as incomplete items if they don't start with marker.
 */
@Composable
fun ChecklistEditor(
    content: String,
    onContentChange: (String) -> Unit,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    // Parse content into state
    // We maintain a local list state to allow smooth UI updates
    var items by remember(content) {
        mutableStateOf(parseChecklist(content))
    }
    
    // Function to serialize and notify change
    fun updateContent(newItems: List<ChecklistItem>) {
        items = newItems
        onContentChange(serializeChecklist(newItems))
    }

    Column(modifier = modifier) {
        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth()
        ) {
            itemsIndexed(items) { index, item ->
                ChecklistItemRow(
                    item = item,
                    contentColor = contentColor,
                    onCheckedChange = { isChecked ->
                        val newItems = items.toMutableList()
                        newItems[index] = item.copy(isChecked = isChecked)
                        updateContent(newItems)
                    },
                    onTextChange = { newText ->
                        val newItems = items.toMutableList()
                        newItems[index] = item.copy(text = newText)
                        updateContent(newItems)
                    },
                    onDelete = {
                        val newItems = items.toMutableList()
                        newItems.removeAt(index)
                        updateContent(newItems)
                    },
                    isLastItem = index == items.lastIndex,
                    onNext = {
                        // Add new item below
                        val newItems = items.toMutableList()
                        newItems.add(index + 1, ChecklistItem(false, ""))
                        updateContent(newItems)
                    }
                )
            }
            
            // Add Item Button at bottom
            item {
                TextButton(
                    onClick = {
                        val newItems = items.toMutableList()
                        newItems.add(ChecklistItem(false, ""))
                        updateContent(newItems)
                    },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = contentColor)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add Item", color = contentColor)
                }
            }
        }
    }
}

@Composable
fun ChecklistItemRow(
    item: ChecklistItem,
    contentColor: Color,
    onCheckedChange: (Boolean) -> Unit,
    onTextChange: (String) -> Unit,
    onDelete: () -> Unit,
    isLastItem: Boolean,
    onNext: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    ) {
        IconButton(onClick = { onCheckedChange(!item.isChecked) }) {
            Icon(
                imageVector = if (item.isChecked) Icons.Default.CheckBox else Icons.Default.CheckBoxOutlineBlank,
                contentDescription = null,
                tint = if (item.isChecked) contentColor.copy(alpha = 0.6f) else contentColor
            )
        }
        
        TextField(
            value = item.text,
            onValueChange = onTextChange,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = contentColor
            ),
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = if (item.isChecked) contentColor.copy(alpha = 0.5f) else contentColor,
                textDecoration = if (item.isChecked) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
            ),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = if (isLastItem) ImeAction.Default else ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { onNext() }
            ),
            modifier = Modifier.weight(1f)
        )
        
        IconButton(onClick = onDelete) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Delete item",
                tint = contentColor.copy(alpha = 0.4f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

data class ChecklistItem(
    val isChecked: Boolean,
    val text: String
)

fun parseChecklist(content: String): List<ChecklistItem> {
    if (content.isBlank()) return listOf(ChecklistItem(false, ""))
    
    // Strip HTML tags first
    val plainText = content
        .replace(Regex("<[^>]*>"), " ") // Remove all HTML tags
        .replace("&nbsp;", " ") // Replace HTML entities
        .replace("&lt;", "<")
        .replace("&gt;", ">")
        .replace("&amp;", "&")
        .trim()
    
    if (plainText.isBlank()) return listOf(ChecklistItem(false, ""))
    
    return plainText.lines()
        .filter { it.isNotBlank() } // Filter out empty lines
        .map { line ->
            val trimmedLine = line.trim()
            when {
                trimmedLine.startsWith("[x] ") -> ChecklistItem(true, trimmedLine.substring(4))
                trimmedLine.startsWith("[ ] ") -> ChecklistItem(false, trimmedLine.substring(4))
                else -> ChecklistItem(false, trimmedLine) // Default to unchecked if no prefix
            }
        }
        .ifEmpty { listOf(ChecklistItem(false, "")) } // Ensure at least one item
}

fun serializeChecklist(items: List<ChecklistItem>): String {
    return items.joinToString("\n") { item ->
        val prefix = if (item.isChecked) "[x] " else "[ ] "
        "$prefix${item.text}"
    }
}
