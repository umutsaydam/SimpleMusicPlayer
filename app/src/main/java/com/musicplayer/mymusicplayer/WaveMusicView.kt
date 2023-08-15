package com.musicplayer.mymusicplayer

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat

class WaveMusicView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val paint = Paint()
    private var spike: FloatArray? = null
    private var averageMagnitude: Float? = null
    private var mediaPlayer = MediaPlayerInstance.getMediaPlayer()

    init {
        paint.color = Color.WHITE
        paint.strokeWidth = 6f
        VisualizerHelper.startVisualizer()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(ContextCompat.getColor(context, R.color.wave_bg))

        spike = VisualizerHelper.getSpike()
        setColor(VisualizerHelper.getAverageMagnitude())

        if (mediaPlayer.isPlaying && spike != null) {
            val widthPerColumn = width.toFloat() / spike!!.size
            var x = width.toFloat() / 2
            var rX = width.toFloat() / 2

            //Log.d("R/T", "cizildi.")
            for (i in spike!!.indices) {
                if (i == 0) {
                    canvas.drawLine(x, spike!![i], x + widthPerColumn, spike!![i + 1], paint)
                    canvas.drawLine(rX, spike!![i], rX - widthPerColumn, spike!![i + 1], paint)
                } else if (i + 1 < spike!!.size) {
                    canvas.drawLine(x, spike!![i], x + widthPerColumn, spike!![i + 1], paint)
                    canvas.drawLine(rX, spike!![i], rX - widthPerColumn, spike!![i + 1], paint)
                }
                x += widthPerColumn
                rX -= widthPerColumn
            }
        } else if (mediaPlayer.isPlaying && spike == null) {
           // Log.d("R/T", "calisiyor ama spike null")
        } else {
            Log.d("R/T", "player durdu!!!!!!")
            VisualizerHelper.stopVisualizer()
            spike = null
            canvas.drawColor(Color.TRANSPARENT)
        }
        postInvalidateOnAnimation()
    }

    private fun setColor(averageMagnitude: Float?) {
        paint.color = when(averageMagnitude!!){
            in 0f..4.80f-> ContextCompat.getColor(context, R.color.color1)
            in 4.81..5.10-> ContextCompat.getColor(context, R.color.color2)
            in 5.11..5.30-> ContextCompat.getColor(context, R.color.color3)
            in 5.31..5.80-> ContextCompat.getColor(context, R.color.color4)
            in 5.81..6.00-> ContextCompat.getColor(context, R.color.color5)
            in 6.01..7.00 -> ContextCompat.getColor(context, R.color.color6)
            else -> ContextCompat.getColor(context, R.color.color7)
        }
    }
}