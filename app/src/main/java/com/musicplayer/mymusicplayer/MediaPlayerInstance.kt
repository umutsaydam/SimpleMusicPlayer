package com.musicplayer.mymusicplayer

import android.media.MediaPlayer

object MediaPlayerInstance {
    private val mediaPlayer: MediaPlayer = MediaPlayer()

    fun getMediaPlayer(): MediaPlayer {
        return mediaPlayer
    }
}