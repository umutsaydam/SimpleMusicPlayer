package com.musicplayer.mymusicplayer

import android.media.audiofx.Visualizer
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.hypot
import kotlin.math.log10

object VisualizerHelper {
    private var visualizer: Visualizer? = null
    private lateinit var magnitudesArray: FloatArray
    private val maxMagnitude = calculateMagnitude(128f, 128f)
    private var averageMagnitude: Float? = null
    private var spike: FloatArray? = null
    private var mediaPlayer = MediaPlayerInstance.getMediaPlayer()

    fun getVisualizer(): Visualizer? {
        return visualizer
    }

    fun startVisualizer() {
        visualizer = Visualizer(mediaPlayer.audioSessionId)
        getFFT()
    }

    fun stopVisualizer() {
        visualizer?.apply {
            release()
            visualizer = null
        }
    }

    private fun updateAverageMagnitude(average: Float) {
        averageMagnitude = average
    }

    fun getAverageMagnitude(): Float {
        if (averageMagnitude == null) {
            return 0f
        }
        return averageMagnitude as Float
    }

    private fun updateSpike(floatArray: FloatArray) {
        spike = floatArray
    }

    fun getSpike(): FloatArray? {
        return spike
    }

    private fun getFFT() {
        val captureSizeRange = Visualizer.getCaptureSizeRange()
        try {
            if (visualizer != null) {
                visualizer = Visualizer(mediaPlayer.audioSessionId)
                visualizer!!.captureSize
                visualizer?.apply {
                    captureSize = 256
                    measurementMode = Visualizer.MEASUREMENT_MODE_PEAK_RMS
                    setDataCaptureListener(
                        object : Visualizer.OnDataCaptureListener {
                            override fun onWaveFormDataCapture(
                                p0: Visualizer?,
                                fft: ByteArray?,
                                samplingRate: Int,
                            ) {
                            }

                            override fun onFftDataCapture(
                                p0: Visualizer?,
                                fft: ByteArray?,
                                p2: Int,
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
                                            hypot(
                                                fft[i].toDouble(),
                                                fft[i + 1].toDouble()
                                            ).toFloat()
                                        phases[k] =
                                            atan2(fft[i + 1].toDouble(), fft[i].toDouble())
                                                .toFloat()
                                    }

                                    magnitudes.map { it / maxMagnitude }
                                    updateAverageMagnitude(magnitudes.sum() / magnitudes.size)
                                    updateSpike(magnitudes)

                                }
                            }
                        },
                        Visualizer.getMaxCaptureRate() / 2,
                        false,
                        true
                    )
                    enabled = true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        magnitudesArray = FloatArray(captureSizeRange[1] / 2 + 1)
    }

    private fun calculateMagnitude(r: Float, i: Float) =
        if (i == 0f && r == 0f) 0f else 10 * log10(r * r + i * i)
}