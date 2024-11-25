package com.example.voxify.data.entities

import java.util.Calendar

data class Translations (
    val id: Long,
    var transcription_id: Long,
    var translated_text: String = "",
    var target_language: String,
    var created_at: Long = Calendar.getInstance().timeInMillis
) {

    companion object {
        const val TABLE_NAME = "Translations"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME_TRANSCRIPTION_ID = "transcription_id"
        const val COLUMN_NAME_TRANSLATED_TEXT = "translated_text"
        const val COLUMN_NAME_TARGET_LANGUAGE = "target_language"
        const val COLUMN_DATE = "created_at"

        val COLUMN_NAMES = arrayOf(
            COLUMN_ID,
            COLUMN_NAME_TRANSCRIPTION_ID,
            COLUMN_NAME_TRANSLATED_TEXT,
            COLUMN_NAME_TARGET_LANGUAGE,
            COLUMN_DATE,
        )
    }
}