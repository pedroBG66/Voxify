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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.voxify.R
import com.example.voxify.data.entities.Recordings
import com.example.voxify.data.providers.RecordingDAO
import com.example.voxify.databinding.FragmentHomeBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding
    lateinit var dao: RecordingDAO
    lateinit var recordingsAdapter: RecordingsAdapter
    private var mediaPlayer: MediaPlayer? = null
    private var currentPosition: Int = -1
    var recordingsList: MutableList<Recordings> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflamos la vista para el fragmento
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        /*ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }*/


        dao = RecordingDAO(requireContext())
        recordingsList = dao.findAll().toMutableList()

        recordingsAdapter = RecordingsAdapter(
            recordingsList, // Pasamos la lista de grabaciones obtenida
            onItemClick = { position ->
                // Manejo de clic en la tarjeta
                val recording = recordingsList[position]
                // Mostrar detalles, etc.
            },
            onPlayClick = { position ->
                // Manejo de clic en el botón de reproducción
                val recording = recordingsList[position]
                playRecording(recording, position)
            },
            {
                val recording = recordingsList[it]
                deleteItems(recording)
            }


        )

        recordingsAdapter.updateItems(recordingsList)


        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = recordingsAdapter
        }
        return binding.root
    }


    private fun deleteItems(recording: Recordings) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Borrar tarea")
            .setMessage("Estas seguro de que quieres borrar la tarea?")
            .setPositiveButton(android.R.string.ok) { dialog, which ->

                    dao.delete(recording)
                    recordingsList = recordingsList.minus(recording).toMutableList()
                    recordingsAdapter.updateItems(recordingsList)
                }
            .setNegativeButton(android.R.string.cancel, null)
            .setIcon(R.drawable.ic_delete)
            .show()
    }

    private fun playRecording(recording: Recordings, position: Int) {
        // Si ya hay una grabación sonando, detenerla
        if (currentPosition == position) {
            mediaPlayer?.apply {
                if (isPlaying) {
                    pause()  // Pausar la reproducción
                } else {
                    start()  // Reanudar si está pausada
                }
            }
            return
        }
        stopPlayback() //si esta sonando otra la paramos!!!
        mediaPlayer =
            MediaPlayer() // Creamos un nuevo MediaPlayer para reproducir la grabación seleccionada

        try {
            mediaPlayer?.setDataSource(recording.filePath)
            mediaPlayer?.prepare()
            mediaPlayer?.start()

            currentPosition = position //actualizamos la posicion actual!
            recordingsAdapter.setPlayingPosition(position)

            // Sincronizamos el progreso de la reproducción con la vista de onda
            val totalDuration = mediaPlayer?.duration ?: 0
            val handler = Handler(Looper.getMainLooper())
            val runnable = object : Runnable {
                override fun run() {
                    if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
                        val current = mediaPlayer?.currentPosition ?: 0
                        val progress = current.toFloat() / totalDuration
                        recordingsAdapter.updateWaveformProgress(
                            position,
                            progress
                        )  // Solo esta vista
                        handler.postDelayed(this, 100)
                    }
                }
            }
            handler.post(runnable)

            mediaPlayer?.setOnCompletionListener {
                stopPlayback()
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
        recordingsAdapter.setPlayingPosition(-1)   // Reseteamos la posición en el adaptador
        currentPosition = -1  // Restablecer la posición
    }

    override fun onStop() {
        super.onStop()
        stopPlayback()
    }


}
