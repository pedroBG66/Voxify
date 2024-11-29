package org.vosk.demo

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import android.widget.ToggleButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.voxify.R
import com.example.voxify.databinding.FragmentRecordingsBinding
import org.vosk.LibVosk
import org.vosk.LogLevel
import org.vosk.Recognizer
import org.vosk.android.SpeechService
import org.vosk.android.SpeechStreamService
import org.vosk.android.StorageService
import java.io.IOException

class RecordingsFragment : Fragment(), RecognitionListener {
    companion object {
        private const val STATE_START = 0
        private const val STATE_READY = 1
        private const val STATE_DONE = 2
        private const val STATE_FILE = 3
        private const val STATE_MIC = 4

        private const val PERMISSIONS_REQUEST_RECORD_AUDIO = 1
    }

    private var model: org.vosk.Model? = null
    private var speechService: SpeechService? = null
    private var speechStreamService: SpeechStreamService? = null
    private lateinit var binding: FragmentRecordingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRecordingsBinding.inflate(inflater, container, false)

        // Setup layout
        binding.resultText.movementMethod = ScrollingMovementMethod()
        setUiState(STATE_START)

        binding.recognizeFile.setOnClickListener {
            recognizeFile()
        }
        binding.recognizeMic.setOnClickListener {
            recognizeMicrophone()
        }
        binding.pause.setOnCheckedChangeListener { _, isChecked ->
            pause(isChecked)
        }

        LibVosk.setLogLevel(LogLevel.INFO)

        // Check if user has given permission to record audio, init the model after permission is granted
        val permissionCheck =
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO)
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(Manifest.permission.RECORD_AUDIO),
                PERMISSIONS_REQUEST_RECORD_AUDIO
            )
        } else {
            initModel()
        }

        return binding.root
    }

    private fun initModel() {
        StorageService.unpack(
            requireContext(), "model-en-us", "model",
            { model ->
                this.model = model
                setUiState(STATE_READY)
            },
            { exception -> setErrorState("Failed to unpack the model: ${exception.message}") }
        )
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // Permiso concedido
                Toast.makeText(requireContext(), "Permisos concedidos", Toast.LENGTH_SHORT).show()
            } else {
                // Permiso denegado
                Toast.makeText(
                    requireContext(),
                    "Permisos denegados, no puedes grabar",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    private fun requestPermissions() {
        // Solicitar el permiso de grabación
        requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
    }

    private fun checkPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroyView() {
        super.onDestroyView()

        speechService?.let {
            it.stop()
            it.shutdown()
        }

        speechStreamService?.let {
            it.stop()
        }
    }

    fun onResult(hypothesis: String) {
        binding.resultText.append(hypothesis + "\n")
    }

    fun onFinalResult(hypothesis: String) {
        binding.resultText.append(hypothesis + "\n")
        setUiState(STATE_DONE)
        speechStreamService = null
    }

    fun onPartialResult(hypothesis: String) {
        binding.resultText.append(hypothesis + "\n")
    }

    fun onError(e: Exception) {
        setErrorState(e.message)
    }

    fun onTimeout() {
        setUiState(STATE_DONE)
    }

    private fun setUiState(state: Int) {
        when (state) {
            STATE_START -> {
                binding.resultText.setText(R.string.preparing)
                binding.recognizeFile.isEnabled = false
                binding.recognizeMic.isEnabled = false
                binding.pause.isEnabled = false
            }

            STATE_READY -> {
                binding.resultText.setText(R.string.ready)
                binding.recognizeMic.setText(R.string.recognize_microphone)
                binding.recognizeFile.isEnabled = true
                binding.recognizeMic.isEnabled = true
                binding.pause.isEnabled = false
            }

            STATE_DONE -> {
                binding.recognizeFile.setText(R.string.recognize_file)
                binding.recognizeMic.setText(R.string.recognize_microphone)
                binding.recognizeFile.isEnabled = true
                binding.recognizeMic.isEnabled = true
                binding.pause.isEnabled = false
                binding.pause.isChecked = false
            }

            STATE_FILE -> {
                binding.recognizeFile.setText(R.string.stop_file)
                binding.resultText.text = getString(R.string.starting)
                binding.recognizeMic.isEnabled = false
                binding.recognizeFile.isEnabled = true
                binding.pause.isEnabled = false
            }

            STATE_MIC -> {
                binding.recognizeMic.setText(R.string.stop_microphone)
                binding.resultText.text = getString(R.string.say_something)
                binding.recognizeFile.isEnabled = false
                binding.recognizeMic.isEnabled = true
                binding.pause.isEnabled = true
            }

            else -> throw IllegalStateException("Unexpected value: $state")
        }
    }

    private fun setErrorState(message: String?) {
        binding.resultText.text = message
        binding.recognizeMic.setText(R.string.recognize_microphone)
        binding.recognizeFile.isEnabled = false
        binding.recognizeMic.isEnabled = false
    }

    private fun recognizeFile() {
        if (speechStreamService != null) {
            setUiState(STATE_DONE)
            speechStreamService?.stop()
            speechStreamService = null
        } else {
            setUiState(STATE_FILE)
            try {
                val rec = Recognizer(
                    model, 16000f, "[\"one zero zero zero one\", " +
                            "\"oh zero one two three four five six seven eight nine\", \"[unk]\"]"
                )

                val ais = assets.open("10001-90210-01803.wav")
                if (ais.skip(44) != 44L) throw IOException("File too short")

                speechStreamService = SpeechStreamService(rec, ais, 16000)
                speechStreamService?.start(requireContext()
            } catch (e: IOException) {
                setErrorState(e.message)
            }
        }
    }
}

private fun recognizeMicrophone() {
    if (speechService != null) {
        setUiState(STATE_DONE)
        speechService?.stop()
        speechService = null
    } else {
        setUiState(STATE_MIC)
        try {
            val rec = Recognizer(model, 16000.0f)
            speechService = SpeechService(rec, 16000.0f)
            speechService?.startListening(this)
        } catch (e: IOException) {
            setErrorState(e.message)
        }
    }
}

private fun pause(checked: Boolean) {
    speechService?.setPause(checked)
}



