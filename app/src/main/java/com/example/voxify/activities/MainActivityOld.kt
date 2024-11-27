package com.example.voxify.activities

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.voxify.R
import com.example.voxify.databinding.ActivityMainBinding
import com.example.voxify.databinding.ActivityMainOldBinding
import java.io.File
import java.io.IOException

class MainActivityOld : AppCompatActivity() {

    private lateinit var binding: ActivityMainOldBinding
    private var mediaRecorder: MediaRecorder? = null
    private var isRecording: Boolean = false
    private var audioFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainOldBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Configurar botón de grabación
        binding.startRecordingButton.setOnClickListener {
            if (!isRecording) {
                if (checkPermissions()) {
                    startRecording()
                } else {
                    requestPermissions()
                }
            } else {
                stopRecording()
            }
        }
    }
        //
    private fun startRecording() {
        val recordingsDir = File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), "recordings")
        if (!recordingsDir.exists()) recordingsDir.mkdirs()

        audioFile = File(recordingsDir, "voxify_recording_${System.currentTimeMillis()}.mp3")
        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(audioFile?.absolutePath)
            try {
                prepare()
                start()
                isRecording = true
                updateUI(isRecording = true)
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(this@MainActivityOld, "Error al iniciar la grabación", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun stopRecording() {
        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null
            isRecording = false
            updateUI(isRecording = false)
            Toast.makeText(this, "Grabación guardada: ${audioFile?.absolutePath}", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error al detener la grabación", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(isRecording: Boolean) {
        binding.startRecordingButton.text = if (isRecording) "STOP" else "START"

        if (isRecording) {
            binding.microphoneImageView.setImageResource(R.drawable.ic_pause) // Cambia al ícono de pausa
            binding.microphoneImageView.setBackgroundResource(R.drawable.im_circles_listening_foreground) // Fondo si lo necesitas
        } else {
            binding.microphoneImageView.setImageResource(R.drawable.ic_microphone)
        }
    }

    private fun checkPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED

    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.RECORD_AUDIO),
            1001
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permisos concedidos", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permisos denegados, no puedes grabar", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaRecorder?.release()
        mediaRecorder = null
    }
}