package com.example.voxify.data.providers

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.voxify.data.entities.Translations
import com.example.voxify.utils.DbHelper

class TranslationDAO(val context: Context) {

    private lateinit var db: SQLiteDatabase

    private fun open() {
        db = DbHelper(context).writableDatabase
    }

    private fun close() {
        db.close()
    }

    fun getContentValues(translations: Translations): ContentValues {
        return ContentValues().apply {
            put(Translations.COLUMN_NAME_TRANSCRIPTION_ID, translations.transcriptionId)
            put(Translations.COLUMN_NAME_TRANSLATED_TEXT, translations.translatedText)
            put(Translations.COLUMN_NAME_TARGET_LANGUAGE, translations.targetLanguage)
            put(Translations.COLUMN_DATE, translations.createdAt)
        }
    }

    fun cursorToEntity(cursor: Cursor): Translations {
        val id = cursor.getLong(cursor.getColumnIndexOrThrow(Translations.COLUMN_ID))
        val transcriptionId = cursor.getString(cursor.getColumnIndexOrThrow(Translations.COLUMN_NAME_TRANSCRIPTION_ID))
        val translatedText = cursor.getString(cursor.getColumnIndexOrThrow(Translations.COLUMN_NAME_TRANSLATED_TEXT))
        val targetLanguage = cursor.getInt(cursor.getColumnIndexOrThrow(Translations.COLUMN_NAME_TARGET_LANGUAGE))
        val createdAt = cursor.getLong(cursor.getColumnIndexOrThrow(Translations.COLUMN_DATE))

        return Translations(
            id, transcriptionId, translatedText,
            targetLanguage.toString(), createdAt
        )
    }

    fun insert(translations: Translations) {
        open()
        // Create a new map of values, where column names are the keys
        val values = getContentValues(translations)

        try {
            // Insert the new row, returning the primary key value of the new row
            val id = db.insert(Translations.TABLE_NAME, null, values)
        } catch (e: Exception) {
            Log.e("DB", e.stackTraceToString())
        } finally {
            close()
        }
    }

    fun update(translations: Translations) {
        open()

        // Create a new map of values, where column names are the keys
        val values = getContentValues(translations)

        try {
            // Update the existing rows, returning the number of affected rows
            val updatedRows = db.update(
                Translations.TABLE_NAME,
                values,
                "${Translations.COLUMN_ID} = ${translations.id}",
                null
            )
        } catch (e: Exception) {
            Log.e("DB", e.stackTraceToString())
        } finally {
            close()
        }
    }

    fun delete(translations: Translations) {
        open()

        try {
            // Delete the existing row, returning the number of affected rows
            val deletedRows = db.delete(
                Translations.TABLE_NAME,
                "${Translations.COLUMN_ID} = ${translations.id}",
                null
            )
        } catch (e: Exception) {
            Log.e("DB", e.stackTraceToString())
        } finally {
            close()
        }
    }

    fun findById(id: Long): Translations? {
        open()

        try {
            val cursor = db.query(
                Translations.TABLE_NAME,                    // The table to query
                Translations.COLUMN_NAMES,                  // The array of columns to return (pass null to get all)
                "${Translations.COLUMN_ID} = $id",  // The columns for the WHERE clause
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

    fun findAll(): List<Translations> {
        open()

        var list: MutableList<Translations> = mutableListOf()

        try {
            val cursor = db.query(
                Translations.TABLE_NAME,                    // The table to query
                Translations.COLUMN_NAMES,                  // The array of columns to return (pass null to get all)
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
