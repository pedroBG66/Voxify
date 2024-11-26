package com.example.voxify.activities

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.voxify.R
import com.example.voxify.data.entities.Recordings
import com.example.voxify.data.providers.RecordingDAO

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

    testDatabase()

    }
    private fun testDatabase() {
        val dao = RecordingDAO(this)

        // Crear un nuevo registro
        val recording = Recordings(
            id = -1, // El ID será generado automáticamente por la base de datos si es autoincremental
            title = "Test Recording",
            description = "Description for test recording",
            file_path = "/path/to/test/file",
            recording_duration = 180,
            created_at = System.currentTimeMillis()
        )

        // Insertar el registro
        dao.insert(recording)
        Log.d("DatabaseTest", "Registro insertado: $recording")

        // Obtener todos los registros
        val recordingsList = dao.findAll()
        Log.d("DatabaseTest", "Todos los registros: $recordingsList")

        // Buscar un registro específico (por ejemplo, el primer ID)
        if (recordingsList.isNotEmpty()) {
            val firstRecording = dao.findById(recordingsList.first().id)
            Log.d("DatabaseTest", "Primer registro encontrado: $firstRecording")
        } else {
            Log.d("DatabaseTest", "No se encontraron registros en la base de datos.")
        }
    }
}