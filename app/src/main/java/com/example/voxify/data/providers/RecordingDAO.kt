package com.example.voxify.data.providers

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
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

    fun insert(recordings: Recordings) {
        open()
        // Create a new map of values, where column names are the keys
        val values = getContentValues(recordings)

        try {
            // Insert the new row, returning the primary key value of the new row
            val id = db.insert(Recordings.TABLE_NAME, null, values)
        } catch (e: Exception) {
            Log.e("DB", e.stackTraceToString())
        } finally {
            close()
        }
    }

    fun update(recordings: Recordings) {
        open()

        // Create a new map of values, where column names are the keys
        val values = getContentValues(recordings)

        try {
            // Update the existing rows, returning the number of affected rows
            val updatedRows = db.update(Recordings.TABLE_NAME, values, "${Recordings.COLUMN_ID} = ${recordings.id}", null)
        } catch (e: Exception) {
            Log.e("DB", e.stackTraceToString())
        } finally {
            close()
        }
    }
    fun delete(recordings: Recordings) {
        open()

        try {
            // Delete the existing row, returning the number of affected rows
            val deletedRows = db.delete(Recordings.TABLE_NAME, "${Recordings.COLUMN_ID} = ${recordings.id}", null)
        } catch (e: Exception) {
            Log.e("DB", e.stackTraceToString())
        } finally {
            close()
        }
    }

    fun findById(id: Long) : Recordings? {
        open()

        try {
            val cursor = db.query(
                Recordings.TABLE_NAME,                    // The table to query
                Recordings.COLUMN_NAMES,                  // The array of columns to return (pass null to get all)
                "${Recordings.COLUMN_ID} = $id",  // The columns for the WHERE clause
                null,                   // The values for the WHERE clause
                null,                       // don't group the rows
                null,                         // don't filter by row groups
                null                         // The sort order
            )

            if (cursor.moveToNext()) {
                return cursorToEntity(cursor)
            }
        } catch (e: Exception) {
            Log.e("DB", e.stackTraceToString())
        } finally {
            close()
        }
        return null
    }
    fun findAll() : List<Recordings> {
        open()

        var list: MutableList<Recordings> = mutableListOf()

        try {
            val cursor = db.query(
                Recordings.TABLE_NAME,                    // The table to query
                Recordings.COLUMN_NAMES,                  // The array of columns to return (pass null to get all)
                null,                       // The columns for the WHERE clause
                null,                   // The values for the WHERE clause
                null,                       // don't group the rows
                null,                         // don't filter by row groups
                null                         // The sort order
            )

            while (cursor.moveToNext()) {
                val task = cursorToEntity(cursor)
                list.add(task)
            }
        } catch (e: Exception) {
            Log.e("DB", e.stackTraceToString())
        } finally {
            close()
        }
        return list
    }

}