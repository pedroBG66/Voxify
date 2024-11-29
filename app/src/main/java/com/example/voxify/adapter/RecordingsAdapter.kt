import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.voxify.R
import com.example.voxify.data.entities.Recordings
import com.example.voxify.databinding.CardViewBinding
import com.example.voxify.utils.WaveFormView
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.channels.FileChannel

class RecordingsAdapter(
    var items: List<Recordings>,
    private val onItemClick: (Int) -> Unit,        // Acción al hacer clic en la tarjeta
    private val onPlayClick: (Int) -> Unit,
    private val onItemDelete: (Int) -> Unit,
    // Acción al hacer clic en el botón de reproducción
) : RecyclerView.Adapter<RecordingViewHolder>() {

    private var playingPosition: Int = -1
    private val waveformProgressMap = mutableMapOf<Int, Float>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordingViewHolder {
        val binding = CardViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecordingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecordingViewHolder, position: Int) {
        val recording = items[position]
        holder.render(recording)

        holder.binding.playButton.setImageResource(
            if (position == playingPosition) R.drawable.ic_pause else R.drawable.ic_play_normal
        )

        val progress = waveformProgressMap[position] ?: 0f
        holder.updateWaveform(position == playingPosition, progress)

        // Acciones al interactuar con los elementos
        holder.itemView.setOnClickListener {
            onItemClick(position)
        }
        holder.binding.playButton.setOnClickListener {
            onPlayClick(position)
        }
        holder.binding.deleteButton.setOnClickListener{
            onItemDelete(position)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    // Método para actualizar los datos
    fun updateItems(newItems: List<Recordings>) {
        this.items = newItems
        notifyDataSetChanged()
    }

    fun setPlayingPosition(position: Int) {
        val previousPosition = playingPosition
        playingPosition = position
        notifyItemChanged(previousPosition) // Actualiza el ítem anterior
        notifyItemChanged(playingPosition) // Actualiza el nuevo ítem en reproducción
    }
    fun updateWaveformProgress(position: Int, progress: Float) {
        waveformProgressMap[position] = progress
        notifyItemChanged(position)  // Solo actualiza la vista específica
    }
}

class RecordingViewHolder(val binding: CardViewBinding) : RecyclerView.ViewHolder(binding.root) {

    private var progress: Float = 0f
    private var isPlaying: Boolean = false

    fun render(recording: Recordings) {
        //binding.titleText.text = recording.title
        //binding.descriptionText.text = recording.description
        binding.durationText.text = recording.recordingDuration.toString()
        binding.createdAtText.text = recording.createdAt.toString()
        val audioData = getAudioBytes(recording.filePath)
        if (audioData != null) {
           binding.wave.setRawData(audioData)  // Establece la onda en la vista
        }
    }


    fun updateWaveform(isPlaying: Boolean, progress: Float) {
        (binding.wave as? WaveFormView)?.setPlaying(isPlaying)
        (binding.wave as? WaveFormView)?.setProgress(progress)// Actualiza el progreso en la vista de onda

    }
    fun getAudioBytes(filePath: String): ByteArray? {
        try {
            val file = File(filePath)
            val fileInputStream = FileInputStream(file)
            val fileChannel: FileChannel = fileInputStream.channel
            val buffer = ByteBuffer.allocate(fileChannel.size().toInt())

            fileChannel.read(buffer)
            buffer.flip()
            return buffer.array()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }
}
