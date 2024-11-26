package com.example.voxify.data.providers

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.voxify.data.entities.Transcriptions
import com.example.voxify.utils.DbHelper

class TranscriptionDAO(val context: Context) {

    private lateinit var db: SQLiteDatabase

    private fun open() {
        db = DbHelper(context).writableDatabase
    }

    private fun close() {
        db.close()
    }

    fun getContentValues(transcriptions: Transcriptions): ContentValues {
        return ContentValues().apply {
            put(Transcriptions.COLUMN_NAME_TEXT, transcriptions.text)
            put(Transcriptions.COLUMN_NAME_LANGUAGE, transcriptions.language)
            put(Transcriptions.COLUMN_GRAMMAR_CHECKED, transcriptions.grammarChecked)
            put(Transcriptions.COLUMN_GRAMMAR_ISSUES, transcriptions.grammarIssues)
            put(Transcriptions.COLUMN_DATE, transcriptions.createdAt)
            put(Transcriptions.COLUMN_NAME_RECORDING_ID, transcriptions.recordingId)
        }

    }

    fun cursorToEntity(cursor: Cursor): Transcriptions {
        val id = cursor.getLong(cursor.getColumnIndexOrThrow(Transcriptions.COLUMN_ID))
        val recordingId =
            cursor.getString(cursor.getColumnIndexOrThrow(Transcriptions.COLUMN_NAME_RECORDING_ID))
        val text = cursor.getString(cursor.getColumnIndexOrThrow(Transcriptions.COLUMN_NAME_TEXT))
        val language =
            cursor.getInt(cursor.getColumnIndexOrThrow(Transcriptions.COLUMN_NAME_LANGUAGE))
        val grammarChecked =
            cursor.getInt(cursor.getColumnIndexOrThrow(Transcriptions.COLUMN_GRAMMAR_CHECKED))
        val grammarIssues =
            cursor.getInt(cursor.getColumnIndexOrThrow(Transcriptions.COLUMN_GRAMMAR_ISSUES))
        val createdAt = cursor.getLong(cursor.getColumnIndexOrThrow(Transcriptions.COLUMN_DATE))


        return Transcriptions(
            id, recordingId, text,
            language.toString(), grammarChecked, grammarIssues.toString(), createdAt
        )
    }

    fun insert(transcriptions: Transcriptions) {
        open()
        // Create a new map of values, where column names are the keys
        val values = getContentValues(transcriptions)

        try {
            // Insert the new row, returning the primary key value of the new row
            val id = db.insert(Transcriptions.TABLE_NAME, null, values)
        } catch (e: Exception) {
            Log.e("DB", e.stackTraceToString())
        } finally {
            close()
        }
    }

    fun update(transcriptions: Transcriptions) {
        open()

        // Create a new map of values, where column names are the keys
        val values = getContentValues(transcriptions)

        try {
            // Update the existing rows, returning the number of affected rows
            val updatedRows = db.update(
                Transcriptions.TABLE_NAME,
                values,
                "${Transcriptions.COLUMN_ID} = ${transcriptions.id}",
                null
            )
        } catch (e: Exception) {
            Log.e("DB", e.stackTraceToString())
        } finally {
            close()
        }
    }

    fun delete(transcriptions: Transcriptions) {
        open()

        try {
            // Delete the existing row, returning the number of affected rows
            val deletedRows = db.delete(
                Transcriptions.TABLE_NAME,
                "${Transcriptions.COLUMN_ID} = ${transcriptions.id}",
                null
            )
        } catch (e: Exception) {
            Log.e("DB", e.stackTraceToString())
        } finally {
            close()
        }
    }

    fun findById(id: Long): Transcriptions? {
        open()

        try {
            val cursor = db.query(
                Transcriptions.TABLE_NAME,                    // The table to query
                Transcriptions.COLUMN_NAMES,                  // The array of columns to return (pass null to get all)
                "${Transcriptions.COLUMN_ID} = $id",  // The columns for the WHERE clause
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

    fun findAll(): List<Transcriptions> {
        open()

        var list: MutableList<Transcriptions> = mutableListOf()

        try {
            val cursor = db.query(
                Transcriptions.TABLE_NAME,                    // The table to query
                Transcriptions.COLUMN_NAMES,                  // The array of columns to return (pass null to get all)
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