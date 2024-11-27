package com.example.voxify.activities.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.voxify.R
import com.example.voxify.databinding.FragmentRecordingBinding
import java.io.File
import java.io.IOException

class RecordingFragment : Fragment() {

    private lateinit var binding: FragmentRecordingBinding
    private var mediaRecorder: MediaRecorder? = null
    private var isRecording: Boolean = false
    private var audioFile: File? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflamos la vista para el fragmento
        binding = FragmentRecordingBinding.inflate(inflater, container, false)

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

        // Aplicar insets para manejar el recorte de la UI en dispositivos con pantallas edge-to-edge (opcional)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        return binding.root
    }

    private fun startRecording() {
        val recordingsDir = File(requireContext().getExternalFilesDir(Environment.DIRECTORY_MUSIC), "recordings")
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
                Toast.makeText(requireContext(), "Error al iniciar la grabación", Toast.LENGTH_SHORT).show()
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
            Toast.makeText(requireContext(), "Grabación guardada: ${audioFile?.absolutePath}", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Error al detener la grabación", Toast.LENGTH_SHORT).show()
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

    // Variable para el lanzador de permisos
    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            // Permiso concedido
            Toast.makeText(requireContext(), "Permisos concedidos", Toast.LENGTH_SHORT).show()
        } else {
            // Permiso denegado
            Toast.makeText(requireContext(), "Permisos denegados, no puedes grabar", Toast.LENGTH_SHORT).show()
        }
    }

    private fun requestPermissions() {
        // Solicitar el permiso de grabación
        requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
    }

    private fun checkPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mediaRecorder?.release()
        mediaRecorder = null
    }
}
