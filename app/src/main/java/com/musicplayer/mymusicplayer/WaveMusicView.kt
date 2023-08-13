package com.musicplayer.mymusicplayer

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.media.audiofx.Visualizer
import android.util.AttributeSet
import android.util.Log
import android.view.View
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.hypot
import kotlin.math.log10

class WaveMusicView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val paint = Paint()
    private var spike: FloatArray? = null
    private var mediaPlayer = MediaPlayerInstance.getMediaPlayer()
    private lateinit var magnitudesArray: FloatArray
    private val maxMagnitude = calculateMagnitude(128f, 128f)
    private var visualizer: Visualizer? = null

    init {
        paint.color = Color.WHITE
        paint.strokeWidth = 6f
        startVisualizer()
    }

    private fun updateSpikes(floatArray: FloatArray) {
        spike = floatArray
        Log.d("R/T", "updated spike")
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.BLACK)

        if (mediaPlayer.isPlaying && spike != null) {
            val widthPerColumn = width.toFloat() / spike!!.size
            var x = width.toFloat() / 2
            var rX = width.toFloat() / 2
            val y = 0f
            Log.d("R/T", "cizildi.")
            for (i in spike!!.indices) {
                if (i == 0) {
                    canvas.drawLine(x, y, x + widthPerColumn, spike!![i], paint)
                    canvas.drawLine(rX, y, rX - widthPerColumn, spike!![i], paint)
                } else {
                    canvas.drawLine(x, spike!![i - 1], x + widthPerColumn, spike!![i], paint)
                    canvas.drawLine(rX, spike!![i - 1], rX - widthPerColumn, spike!![i], paint)
                }
                x += widthPerColumn
                rX -= widthPerColumn
            }
            //spike = null
        } else if (mediaPlayer.isPlaying && spike == null) {
            Log.d("R/T", "calisiyor ama spike null")
        }else{
            Log.d("R/T", "player durdu!!!!!!")
            stopVisualizer()
        }

        postInvalidateOnAnimation()
    }

    private fun startVisualizer() {
        visualizer = Visualizer(mediaPlayer.audioSessionId)
        getFFT()
    }

    private fun stopVisualizer() {
        visualizer?.apply {
            release()
            visualizer = null
        }
    }

    private fun getFFT() {
        val captureSizeRange = Visualizer.getCaptureSizeRange()
        try {
            if (visualizer != null) {
                Log.d("R/T", "getFFT")
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
                                samplingRate: Int
                            ) {
                                Log.d("R/T", "onWaveFormDataCapture")
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
                                            hypot(
                                                fft[i].toDouble(),
                                                fft[i + 1].toDouble()
                                            ).toFloat()
                                        phases[k] =
                                            atan2(fft[i + 1].toDouble(), fft[i].toDouble())
                                                .toFloat()
                                    }

                                    magnitudes.map { it / maxMagnitude }

                                    updateSpikes(magnitudes)
                                    Log.d("R/T", "updateSpikes")

                                } else {
                                    Log.d("R/T", "onWaveFormDataCapture null")
                                }
                            }
                        },
                        Visualizer.getMaxCaptureRate() / 2,
                        false,
                        true
                    )
                    enabled = true
                    Log.d("R/T", "end of try")
                }
            }
        } catch (e: Exception) {
            Log.d("R/T", "!!!!" + e.message.toString())
        }
        Log.d("R/T", "////////////////////////// ***** end of the functionn *///////////*/*/*/*/*")
        magnitudesArray = FloatArray(captureSizeRange[1] / 2 + 1)
    }


    private fun calculateMagnitude(r: Float, i: Float) =
        if (i == 0f && r == 0f) 0f else 10 * log10(r * r + i * i)

}