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
            Log.d("R/T", "calisiyor ama spike null")
        } else {
            Log.d("R/T", "player durdu!!!!!!")
            VisualizerHelper.stopVisualizer()
            spike = null
            canvas.drawColor(Color.TRANSPARENT)
        }
        postInvalidateOnAnimation()
    }
}