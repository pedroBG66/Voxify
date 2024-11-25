package com.example.voxify.data.entities

import java.util.Calendar

data class Recordings (
    val id: Long,
    var title: String,
    var description: String = "",
    var file_path: String,
    var recording_duration: Int,
    var created_at: Long =Calendar.getInstance().timeInMillis
) {
    init {
        require(recording_duration >= 0) { "La duración de la grabación no puede ser negativa" }
        require(file_path.isNotBlank()) { "El path del archivo no puede estar vacío" }
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