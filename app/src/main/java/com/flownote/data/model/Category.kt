package com.flownote.data.model

/**
 * Category types for auto-categorization
 */
enum class Category(val displayName: String) {
    MEETINGS("Meetings"),
    TASKS("Tasks"),
    RECIPES("Recipes"),
    CODE("Code Snippets"),
    IDEAS("Ideas"),
    STUDY("Study Notes"),
    GENERAL("General");

    companion object {
        fun fromDisplayName(name: String): Category {
            return values().find { it.displayName == name } ?: GENERAL
        }
    }
}
