package com.example.voxify.activities.fragments

import RecordingsAdapter
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.voxify.data.entities.Recordings
import com.example.voxify.data.providers.RecordingDAO
import com.example.voxify.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding
    lateinit var dao: RecordingDAO
    lateinit var recordingsAdapter: RecordingsAdapter
    private var mediaPlayer: MediaPlayer? = null
    private var currentPosition: Int = -1  // Para llevar un control de la posición de la grabación que se está reproduciendo

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflamos la vista para el fragmento
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dao = RecordingDAO(requireContext())
        val recordingsList = dao.findAll()  // Aquí obtienes tus grabaciones desde la base de datos o fuente

        setupRecyclerView(recordingsList)

        return binding.root
    }

    private fun setupRecyclerView(recordingsList: List<Recordings>) {
        recordingsAdapter = RecordingsAdapter(
            items = recordingsList, // Pasamos la lista de grabaciones obtenida
            onItemClick = { position ->
                // Manejo de clic en la tarjeta
                val recording = recordingsList[position]
                // Mostrar detalles, etc.
            },
            onPlayClick = { position ->
                // Manejo de clic en el botón de reproducción
                val recording = recordingsList[position]
                playRecording(recording, position)
            }
        )

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = recordingsAdapter
        }
    }

    private fun playRecording(recording: Recordings, position: Int) {
        // Si ya hay una grabación sonando, detenerla
        mediaPlayer?.apply {
            if (isPlaying) {
                stop()  // Detener la reproducción si está en curso
                release()  // Liberar los recursos
            }
        }

        mediaPlayer = MediaPlayer() // Creamos un nuevo MediaPlayer para reproducir la grabación seleccionada

        try {
            mediaPlayer?.setDataSource(recording.filePath)
            mediaPlayer?.prepare()
            mediaPlayer?.start()

            // Actualizar la vista de onda y la posición de reproducción en el adaptador
            recordingsAdapter.setPlayingPosition(position)

            // Sincronizamos el progreso de la reproducción con la vista de onda
            val totalDuration = mediaPlayer?.duration ?: 0
            val handler = Handler(Looper.getMainLooper())
            val runnable = object : Runnable {
                override fun run() {
                    val currentPosition = mediaPlayer?.currentPosition ?: 0
                    val progress = currentPosition.toFloat() / totalDuration
                    // Actualiza la vista de onda en el adaptador
                    recordingsAdapter.notifyDataSetChanged()

                    // Sigue actualizando el progreso
                    handler.postDelayed(this, 100)  // Actualización cada 100 ms
                }
            }
            handler.post(runnable)

            mediaPlayer?.setOnCompletionListener {
                Log.d("PlayRecording", "La grabación ha terminado.")
                stopPlayback()  // Detener la reproducción cuando finalice
            }

        } catch (e: Exception) {
            Log.e("PlayRecording", "Error al intentar reproducir la grabación", e)
        }
    }

    private fun stopPlayback() {
        mediaPlayer?.apply {
            stop()
            release()
        }
        mediaPlayer = null
        currentPosition = -1  // Restablecer la posición
        recordingsAdapter.setPlayingPosition(-1)   // Reseteamos la posición en el adaptador
    }

    override fun onStop() {
        super.onStop()
        stopPlayback()
    }
}
