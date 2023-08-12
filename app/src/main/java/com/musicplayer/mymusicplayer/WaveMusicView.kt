package com.musicplayer.mymusicplayer

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.media.audiofx.Visualizer
import android.util.AttributeSet
import android.util.Log
import android.view.View
import java.util.Random
import kotlin.math.abs
import kotlin.math.log10

class WaveMusicView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val paint = Paint()
    private val spike = ArrayList<RectF>()
    private  var currentFreq = 0f
    private var mediaPlayer = MediaPlayerInstance.getMediaPlayer()
    private lateinit var magnitudesArray: FloatArray
    private val maxMagnitude = calculateMagnitude(128f, 128f)

    init {
        paint.color = Color.WHITE
      //  createSpikes()
        testRyhthm()
    }


    private fun updateSpikes() {
        // Spike'ları güncelleyin veya yeni ekleyin
        val spikeHeight = currentFreq * 10
        val newSpike = RectF(50f, 0f, 100f, spikeHeight)
        spike.clear()
        spike.add(newSpike)
    }

    fun updateFrequency(frequency: Float){
        currentFreq = frequency
        updateSpikes()
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.BLACK)
        Log.d("R/T", "tetiklendi!!!!!!!!!!!!!!!!!!")
        // Spike'ları çizin
        for (i in spike) {
            canvas.drawRect(i, paint)
        }


        // Çizimi güncelleyin
        postInvalidateOnAnimation()
    }

    fun testRyhthm() {
        if (mediaPlayer.isPlaying) {
            val visualizer: Visualizer
            val captureSizeRange = Visualizer.getCaptureSizeRange()
            try {
                visualizer = Visualizer(mediaPlayer.audioSessionId)
                visualizer.apply {
                    captureSize = Visualizer.getCaptureSizeRange()[1]
                    setDataCaptureListener(
                        object : Visualizer.OnDataCaptureListener {
                            override fun onWaveFormDataCapture(
                                p0: Visualizer?,
                                fft: ByteArray?,
                                samplingRate: Int
                            ) {

                            }

                            override fun onFftDataCapture(
                                p0: Visualizer?,
                                fft: ByteArray?,
                                p2: Int
                            ) {
                                if (fft != null) {
                                    val n: Int = fft.size
                                    val magnitudes = FloatArray(n / 2 + 1)
                                    val phases = FloatArray(n / 2 + 1)
                                    magnitudes[0] = abs(fft[0].toDouble()).toFloat() // DC
                                    magnitudes[n / 2] = abs(fft[1].toDouble()).toFloat() // Nyquist

                                    phases[0] = 0.also { phases[n / 2] = it.toFloat() }.toFloat()
                                    for (k in 1 until n / 2) {
                                        val i = k * 2
                                        magnitudes[k] =
                                            Math.hypot(
                                                fft.get(i).toDouble(),
                                                fft.get(i + 1).toDouble()
                                            ).toFloat()
                                        phases[k] =
                                            Math.atan2(fft[i + 1].toDouble(), fft[i].toDouble())
                                                .toFloat()
                                    }

                                    magnitudes.map { it / maxMagnitude }
                                    updateFrequency(magnitudes[23])
                                    Log.d("R/T", "updatedFreq"+magnitudes[23].toString())
                                } else {
                                    Log.d("R/T", "onWaveFormDataCapture null")
                                }
                            }
                        },
                        Visualizer.getMaxCaptureRate(),
                        true,
                        true
                    )
                    enabled = true
                }
            } catch (e: Exception) {
                Log.d("R/T", "!!!!" + e.message.toString())
            }

            magnitudesArray = FloatArray(captureSizeRange[1] / 2 + 1)

        }
    }


    private fun calculateMagnitude(r: Float, i: Float) =
        if (i == 0f && r == 0f) 0f else 10 * log10(r * r + i * i)

}