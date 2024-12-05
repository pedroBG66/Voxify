package com.example.voxify.activities.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.voxify.databinding.FragmentTextToAudioBinding
import java.util.Locale

class TextToAudioFragment : Fragment() {

    private lateinit var binding: FragmentTextToAudioBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTextToAudioBinding.inflate(inflater, container, false)

        // Configura el TextView para desplazarse
        binding.resultText.movementMethod = ScrollingMovementMethod()

        // Reconocer desde el micrófono
        binding.recognizeMic.setOnClickListener {
            recognizeFromMicrophone()
        }

        return binding.root
    }

    // Inicia el reconocimiento de voz desde el micrófono
    private fun recognizeFromMicrophone() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Habla ahora...")
        }
        speechRecognizerLauncher.launch(intent)
    }

    // Procesar el resultado del reconocimiento de voz
    private val speechRecognizerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val spokenText = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            binding.resultText.text = spokenText?.get(0) ?: "No se reconoció texto"
        } else {
            Toast.makeText(requireContext(), "Error en el reconocimiento de voz", Toast.LENGTH_SHORT).show()
        }
    }
}
