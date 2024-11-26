package com.example.voxify.data.entities

import com.google.gson.annotations.SerializedName
import java.util.Calendar

data class Translations(
    @SerializedName("id") val id: Long,
    @SerializedName("transcription_id") var transcriptionId: String,
    @SerializedName("translated_text") var translatedText: String = "",
    @SerializedName("target_language") var targetLanguage: String,
    @SerializedName("created_at") var createdAt: Long = Calendar.getInstance().timeInMillis
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