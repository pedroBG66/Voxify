package com.example.voxify.data.entities

import com.google.gson.annotations.SerializedName
import java.util.Calendar


data class Transcriptions(
    @SerializedName("id") val id: Long,
    @SerializedName("recording_id") var recordingId: String,
    @SerializedName("text") var text: String = "",
    @SerializedName("language") var language: String,
    @SerializedName("grammar_checked") var grammarChecked: Int,
    @SerializedName("grammar_issues") var grammarIssues: String = "",
    @SerializedName("created_at") var createdAt: Long = Calendar.getInstance().timeInMillis
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