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
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Find Row with prominent match counter
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = findQuery,
                    onValueChange = onFindQueryChange,
                    placeholder = { Text("Find...") },
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp)),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(8.dp))
                
                // Prominent match counter
                if (findQuery.isNotEmpty()) {
                    Surface(
                        color = if (matchCount > 0) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.errorContainer
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (matchCount > 0) "$currentMatchIndex/$matchCount" else "0",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = if (matchCount > 0) {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            } else {
                                MaterialTheme.colorScheme.onErrorContainer
                            },
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                }

                IconButton(
                    onClick = onFindPrevious,
                    enabled = matchCount > 0
                ) {
                    Icon(
                        Icons.Default.KeyboardArrowUp,
                        contentDescription = "Previous",
                        tint = if (matchCount > 0) contentColor else contentColor.copy(alpha = 0.3f)
                    )
                }
                IconButton(
                    onClick = onFindNext,
                    enabled = matchCount > 0
                ) {
                    Icon(
                        Icons.Default.KeyboardArrowDown,
                        contentDescription = "Next",
                        tint = if (matchCount > 0) contentColor else contentColor.copy(alpha = 0.3f)
                    )
                }
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = contentColor)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            
            // Divider for visual separation
            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            )
            
            Spacer(modifier = Modifier.height(8.dp))

            // Replace Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = replaceQuery,
                    onValueChange = onReplaceQueryChange,
                    placeholder = { Text("Replace with...") },
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp)),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = onReplace,
                    enabled = matchCount > 0,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Replace")
                }

                Spacer(modifier = Modifier.width(4.dp))

                TextButton(
                    onClick = onReplaceAll,
                    enabled = matchCount > 0
                ) {
                    Text(
                        "All",
                        color = if (matchCount > 0) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            contentColor.copy(alpha = 0.3f)
                        }
                    )
                }
            }
        }
    }
}
