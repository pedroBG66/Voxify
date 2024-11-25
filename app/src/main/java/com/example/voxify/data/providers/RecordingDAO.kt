package com.example.voxify.data.providers

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.example.voxify.data.entities.Recordings
import com.example.voxify.utils.DbHelper

class RecordingDAO(val context: Context) {

    private lateinit var db: SQLiteDatabase

    private fun open() {
        db = DbHelper(context).writableDatabase
    }

    private fun close() {
        db.close()
    }

    fun getContentValues(task: Recordings): ContentValues {
        return ContentValues().apply {
            put(Recordings.COLUMN_NAME_TITLE, task.title)
            put(Recordings.COLUMN_NAME_DESCRIPTION, task.description)
            put(Recordings.COLUMN_NAME_FILE_PATH, task.file_path)
            put(Recordings.COLUMN_NAME_RECORDING_DURATION, task.recording_duration)
            put(Recordings.COLUMN_NAME_DATE, task.created_at)
        }

    }

    fun cursorToEntity(cursor: Cursor): Recordings {
        val id = cursor.getLong(cursor.getColumnIndexOrThrow(Recordings.COLUMN_ID))
        val title = cursor.getString(cursor.getColumnIndexOrThrow(Recordings.COLUMN_NAME_TITLE))
        val description = cursor.getString(cursor.getColumnIndexOrThrow(Recordings.COLUMN_NAME_DESCRIPTION))
        val file_path = cursor.getInt(cursor.getColumnIndexOrThrow(Recordings.COLUMN_NAME_FILE_PATH))
        val recording_duration = cursor.getInt(cursor.getColumnIndexOrThrow(Recordings.COLUMN_NAME_RECORDING_DURATION))
        val date = cursor.getLong(cursor.getColumnIndexOrThrow(Recordings.COLUMN_NAME_DATE))


        return Recordings(id, title, description, file_path.toString(), recording_duration,date)
    }
}