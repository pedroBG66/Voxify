package com.example.voxify.utils

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.voxify.data.entities.Recordings
import com.example.voxify.data.entities.Transcriptions
import com.example.voxify.data.entities.Translations
import org.intellij.lang.annotations.Language

class DbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "VoxiFyDatabase.db"

        // Tabla Recordings
        @Language("SQL")
        private const val SQL_CREATE_TABLE_RECORDINGS =
            """
            CREATE TABLE ${Recordings.TABLE_NAME} (
                ${Recordings.COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                 ${Recordings.COLUMN_NAME_TITLE} TEXT,
                 ${Recordings.COLUMN_NAME_DESCRIPTION} TEXT,
                ${Recordings.COLUMN_NAME_FILE_PATH} TEXT,
                 ${Recordings.COLUMN_NAME_RECORDING_DURATION} INTEGER,
                 ${Recordings.COLUMN_NAME_DATE} TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
            """

        private const val SQL_DELETE_TABLE_RECORDINGS = "DROP TABLE IF EXISTS ${Recordings.TABLE_NAME}"

        // Tabla Transcriptions
        @Language("SQL")
        private const val SQL_CREATE_TABLE_TRANSCRIPTIONS =
            """
            CREATE TABLE ${Transcriptions.TABLE_NAME} (
                ${Transcriptions.COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${Transcriptions.COLUMN_NAME_RECORDING_ID} INTEGER,
                ${Transcriptions.COLUMN_NAME_TEXT} TEXT,
                ${Transcriptions.COLUMN_NAME_LANGUAGE} TEXT,
                ${Transcriptions.COLUMN_GRAMMAR_CHECKED} BOOLEAN DEFAULT 0,
                ${Transcriptions.COLUMN_GRAMMAR_ISSUES} TEXT,
                ${Transcriptions.COLUMN_DATE} TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (${Transcriptions.COLUMN_NAME_RECORDING_ID}) REFERENCES ${Recordings.TABLE_NAME}(${Recordings.COLUMN_ID})
            )
            """

        private const val SQL_DELETE_TABLE_TRANSCRIPTIONS = "DROP TABLE IF EXISTS ${Transcriptions.TABLE_NAME}"

        // Tabla Translations
        @Language("SQL")
        private const val SQL_CREATE_TABLE_TRANSLATIONS =
            """
            CREATE TABLE ${Translations.TABLE_NAME} (
                ${Translations.COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${Translations.COLUMN_NAME_TRANSCRIPTION_ID} INTEGER,
                ${Translations.COLUMN_NAME_TRANSLATED_TEXT} TEXT,
                ${Translations.COLUMN_NAME_TARGET_LANGUAGE} TEXT,
                ${Translations.COLUMN_DATE} TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (${Translations.COLUMN_NAME_TRANSCRIPTION_ID}) REFERENCES ${Transcriptions.TABLE_NAME}(${Transcriptions.COLUMN_ID})
            )
            """

        private const val SQL_DELETE_TABLE_TRANSLATIONS = "DROP TABLE IF EXISTS ${Translations.TABLE_NAME}"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Crear todas las tablas
        db.execSQL(SQL_CREATE_TABLE_RECORDINGS)
        db.execSQL(SQL_CREATE_TABLE_TRANSCRIPTIONS)
        db.execSQL(SQL_CREATE_TABLE_TRANSLATIONS)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Actualizar la base de datos
        db.execSQL(SQL_DELETE_TABLE_TRANSLATIONS)
        db.execSQL(SQL_DELETE_TABLE_TRANSCRIPTIONS)
        db.execSQL(SQL_DELETE_TABLE_RECORDINGS)
        onCreate(db)
    }
}
