package com.example.voxify.data.entities

import com.google.gson.annotations.SerializedName
import java.util.Calendar

data class Recordings(
    @SerializedName("id") val id: Long,
    @SerializedName("title") var title: String,
    @SerializedName("description") var description: String = "",
    @SerializedName("file_path") var filePath: String,
    @SerializedName("recording_duration") var recordingDuration: String,
    @SerializedName("created_at") var createdAt: String = Calendar.getInstance().timeInMillis.toString()
) {
    init {
        require(recordingDuration >= 0.toString()) { "La duración de la grabación no puede ser negativa" }
        require(filePath.isNotBlank()) { "El path del archivo no puede estar vacío" }
    }
    companion object {
        const val TABLE_NAME = "Recordings"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME_TITLE = "title"
        const val COLUMN_NAME_DESCRIPTION = "description"
        const val COLUMN_NAME_FILE_PATH = "file_path"
        const val COLUMN_NAME_RECORDING_DURATION = "recording_duration"
        const val COLUMN_NAME_DATE = "created_at"

        val COLUMN_NAMES = arrayOf(
            COLUMN_ID,
            COLUMN_NAME_TITLE,
            COLUMN_NAME_DESCRIPTION,
            COLUMN_NAME_FILE_PATH,
            COLUMN_NAME_RECORDING_DURATION,
            COLUMN_NAME_DATE,
        )
    }
}