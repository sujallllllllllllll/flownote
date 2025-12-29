package com.flownote.ui.screens.addedit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun FindReplaceBar(
    findQuery: String,
    onFindQueryChange: (String) -> Unit,
    replaceQuery: String,
    onReplaceQueryChange: (String) -> Unit,
    onFindNext: () -> Unit,
    onFindPrevious: () -> Unit,
    onReplace: () -> Unit,
    onReplaceAll: () -> Unit,
    onClose: () -> Unit,
    matchCount: Int,
    currentMatchIndex: Int,
    contentColor: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(8.dp)
    ) {
        // Find Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = findQuery,
                onValueChange = onFindQueryChange,
                placeholder = { Text("Find...", color = contentColor.copy(alpha = 0.5f)) },
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp)),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = contentColor
                ),
                singleLine = true,
                trailingIcon = {
                    if (findQuery.isNotEmpty()) {
                        if (matchCount > 0) {
                            Text(
                                text = "$currentMatchIndex/$matchCount",
                                style = MaterialTheme.typography.labelSmall,
                                color = contentColor.copy(alpha = 0.6f),
                                modifier = Modifier.padding(end = 8.dp)
                            )
                        } else {
                            Text(
                                text = "No results",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(onClick = onFindPrevious) {
                Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Previous", tint = contentColor)
            }
            IconButton(onClick = onFindNext) {
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Next", tint = contentColor)
            }
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = contentColor)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Replace Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = replaceQuery,
                onValueChange = onReplaceQueryChange,
                placeholder = { Text("Replace with...", color = contentColor.copy(alpha = 0.5f)) },
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp)),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = contentColor
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onReplace,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Replace")
            }

            Spacer(modifier = Modifier.width(4.dp))

            TextButton(onClick = onReplaceAll) {
                Text("All", color = contentColor)
            }
        }
    }
}
