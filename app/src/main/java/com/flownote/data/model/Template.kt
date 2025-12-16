package com.flownote.data.model

/**
 * Template for quick-start notes
 */
data class Template(
    val id: String,
    val name: String,
    val description: String,
    val content: String, // HTML content
    val category: Category = Category.GENERAL,
    val isDefault: Boolean = true
)
