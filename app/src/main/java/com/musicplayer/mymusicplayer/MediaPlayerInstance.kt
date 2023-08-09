package com.musicplayer.mymusicplayer

import android.media.MediaPlayer
import com.musicplayer.mymusicplayer.Model.Music
import java.lang.Exception

object MediaPlayerInstance {
    private val mediaPlayer: MediaPlayer = MediaPlayer()
    private lateinit var music: Music

    fun getMediaPlayer(): MediaPlayer {
        return mediaPlayer
    }

    fun setMusic(music: Music){
        this.music = music
    }

    fun playOrStopMusic(){
        try {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
            }
            mediaPlayer.reset()
            mediaPlayer.setDataSource(music.path)
            mediaPlayer.prepare()
            mediaPlayer.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun playStopMusic() {
        if (mediaPlayer.isPlaying){
            mediaPlayer.pause()
        }else{
            mediaPlayer.start()
        }
    }

}