package com.example.voxify.utils

import android.content.Context
import android.util.AttributeSet
import android.view.View

class WaveFormView
@JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {


    private var progress: Float = 0f // Progreso actual (0.0f a 1.0f)
    private var isPlaying: Boolean = false // Estado de reproducción

    fun setProgress(progress: Float) {
        this.progress = progress.coerceIn(0f, 1f) // Asegurar que esté en el rango 0.0 a 1.0
        invalidate() // Redibujar la vista
    }

    fun setPlaying(isPlaying: Boolean) {
        this.isPlaying = isPlaying
        invalidate() // Redibujar la vista
    }

}