package com.example.voxify.data.entities

import java.util.Calendar

data class Transcriptions(
    val id: Long,
    var recording_id: Long,
    var text: String = "",
    var language: String,
    var grammar_checked: Boolean = false,
    var grammar_issues: String = "",
    var created_at: Long =Calendar.getInstance().timeInMillis
) {


    companion object {
        const val TABLE_NAME = "Transcriptions"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME_RECORDING_ID = "recording_id"
        const val COLUMN_NAME_TEXT = "text"
        const val COLUMN_NAME_LANGUAGE = "language"
        const val COLUMN_GRAMMAR_CHECKED = "grammar_checked"
        const val COLUMN_GRAMMAR_ISSUES = "grammar_issues"
        const val COLUMN_DATE = "created_at"

        val COLUMN_NAMES = arrayOf(
            COLUMN_ID,
            COLUMN_NAME_RECORDING_ID,
            COLUMN_NAME_TEXT,
            COLUMN_NAME_LANGUAGE,
            COLUMN_GRAMMAR_CHECKED,
            COLUMN_GRAMMAR_ISSUES,
            COLUMN_DATE,
        )
    }
}