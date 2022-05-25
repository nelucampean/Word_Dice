package ro.pontes.nelucampean.worddice

import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Typeface
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.speech.tts.TextToSpeech
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SeekBar
import androidx.annotation.RawRes
import androidx.constraintlayout.widget.ConstraintLayout
import ro.pontes.nelucampean.worddice.databinding.FragmentFirstBinding
import java.util.*

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    val tts by lazy {
        TextToSpeech(context) {
          //  generateRandom()
        }
    }
    private val binding get() = _binding!!
    private lateinit var fileContent : String
    private var sensorManager: SensorManager? = null
    private var acceleration = 0f
    private var currentAcceleration = 0f
    private var lastAcceleration = 0f
    private var nrOfDice = 5;
    private var currentText = "Aruncă zarurile!";

    private lateinit var settings: Settings
    var mediaPlayer: MediaPlayer? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        settings = Settings(activity)
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        sensorManager = context?.getSystemService(SENSOR_SERVICE) as SensorManager
        Objects.requireNonNull(sensorManager)!!.registerListener(sensorListener, sensorManager!!
            .getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL)
        acceleration = 10f
        currentAcceleration = SensorManager.GRAVITY_EARTH
        lastAcceleration = SensorManager.GRAVITY_EARTH
        fileContent = resources.getRawTextFile(R.raw.words)
        binding.seekBarDice.progress = nrOfDice;
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonFirst.setOnClickListener {
            generateRandom()
        //findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        nrOfDice = settings.loadPrefInt(R.string.prefNrOfDice, 5);
        binding.seekBarDice.progress = nrOfDice
        binding.buttonFirst.setText("Aruncă $nrOfDice zaruri!")
        binding.seekBarDice.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                nrOfDice = binding.seekBarDice.progress
                settings.savePrefInt(R.string.prefNrOfDice, nrOfDice)
                binding.buttonFirst.setText("Aruncă $nrOfDice zaruri!")
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // Do something
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // Do something

            }
        })
    }
    private val sensorListener: SensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            lastAcceleration = currentAcceleration
            currentAcceleration = kotlin.math.sqrt((x * x + y * y + z * z).toDouble()).toFloat()
            val delta: Float = currentAcceleration - lastAcceleration
            acceleration = acceleration * 0.9f + delta
            val sensitivity = settings.loadPrefString(R.string.prefShakeSensitivity, "8")?.toInt()
            if (acceleration > sensitivity!! && tts != null) {
                generateRandom()
            }
        }
        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    }
    override fun onResume() {
        if (settings.loadPrefBoolean(R.string.prefShake, true))
            sensorManager?.registerListener(sensorListener, sensorManager!!.getDefaultSensor(
                Sensor .TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL
            )
        super.onResume()
    }
    override fun onPause() {
        if (settings.loadPrefBoolean(R.string.prefShake, true))
            sensorManager!!.unregisterListener(sensorListener)
        super.onPause()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun generateRandom(){
        binding.layoutMain.removeAllViews()
        if (fileContent != null) {
            val list = fileContent.split("\n")
            val randomElements = list.asSequence().shuffled().take(nrOfDice).toList()
            randomElements.forEach{it-> createButton(it)}
            playDiceRoll()
            currentText = randomElements.joinToString()
            Handler().postDelayed(Runnable {
                tts.speak(currentText, TextToSpeech.QUEUE_FLUSH, null,"")
            }, 1000)
        }
    }

    fun createButton(text:String){
        val button = Button(context)
        button.layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, 150)
        button.text = text.lowercase().capitalize()
        button.textSize = 20f
        button.setOnClickListener {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
        }
        button.typeface = Typeface.DEFAULT_BOLD
        button.gravity =  Gravity.CENTER
        button.textAlignment = Button.TEXT_ALIGNMENT_CENTER
        button.setTextColor(Color.BLACK)
        button.background = context?.getDrawable(R.drawable.old_scroll)
        button.setOnLongClickListener {
            tts.speak("Zaruri jos: $currentText", TextToSpeech.QUEUE_FLUSH, null,"")
            return@setOnLongClickListener true
        }
        binding.layoutMain.addView(button)
    }
    fun getResourceAsText(path: String): String =
        this::class.java.getResource(path)?.readText().toString()
      //  object {}.javaClass.getResource(path)?.readText()
      fun Resources.getRawTextFile(@RawRes id: Int) =
          openRawResource(id).bufferedReader().use { it.readText() }
    fun playDiceRoll(){
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, R.raw.dice)
            mediaPlayer!!.isLooping = false
            mediaPlayer!!.start()
        } else mediaPlayer!!.start()
    }

}