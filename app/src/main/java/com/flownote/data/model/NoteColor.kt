package com.flownote.data.model

/**
 * Note color options for visual organization
 */
enum class NoteColor(val hexValue: String) {
    DEFAULT("#FFFFFF"),
    YELLOW("#FFF9C4"),
    GREEN("#C8E6C9"),
    BLUE("#BBDEFB"),
    PURPLE("#E1BEE7"),
    PINK("#F8BBD0"),
    ORANGE("#FFE0B2");

    companion object {
        fun fromHex(hex: String): NoteColor {
            return values().find { it.hexValue == hex } ?: DEFAULT
        }
    }
}
