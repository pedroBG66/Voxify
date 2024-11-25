package com.example.voxify.data

import java.util.Calendar

data class Recordings (
    val id: Long,
    var title: String,
    var description: String = "",
    var file_path: String,
    var recording_duration: Int,
    var created_at: Long
) {

    init {
        created_at = Calendar.getInstance().timeInMillis
    }
    companion object {
        const val TABLE_NAME = "Recordings"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME_TITLE = "title"
        const val COLUMN_NAME_DESCRIPTION = "description"
        const val COLUMN_NAME_FILE_PATH = "file_path"
        const val COLUMN_RECORDING_DURATION = "recording_duration"
        const val COLUMN_NAME_DATE = "created_at"

        val COLUMN_NAMES = arrayOf(
            COLUMN_ID,
            COLUMN_NAME_TITLE,
            COLUMN_NAME_DESCRIPTION,
            COLUMN_NAME_FILE_PATH,
            COLUMN_RECORDING_DURATION,
            COLUMN_NAME_DATE,
        )
    }
}