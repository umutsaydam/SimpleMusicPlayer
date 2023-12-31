package com.musicplayer.mymusicplayer

import android.media.MediaPlayer
import com.musicplayer.mymusicplayer.Model.Music
import java.lang.Exception
import java.util.concurrent.TimeUnit

object MediaPlayerInstance {
    private val mediaPlayer: MediaPlayer = MediaPlayer()
    private lateinit var music: Music
    private var musicList = arrayListOf<Music>()

    fun getMusic(): Music{
        return music
    }

    fun setMusicList(musics: ArrayList<Music>) {
        musicList = musics
    }

    fun getMediaPlayer(): MediaPlayer {
        return mediaPlayer
    }

    fun setMusic(music: Music) {
        this.music = music
    }

    fun playOrStopMusic() {
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
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        } else {
            mediaPlayer.start()
        }
    }

    fun getMusicDuration(): Long {
        return music.duration
    }

    fun getMusicCurrPosition(): Int {
        return mediaPlayer.currentPosition
    }

    fun setPrevMusic() {
        val index = musicList.indexOf(music) - 1
        if (index >= 0) {
            mediaPlayer.pause()
            music = musicList[index]
        }
        playOrStopMusic()
    }

    fun setNextMusic() {
        val index = musicList.indexOf(music) + 1
        if (index < musicList.size) {
            mediaPlayer.pause()
            music = musicList[index]
        }
        playOrStopMusic()
    }

    fun formatTime(duration: Long): String {
        return String.format(
            "%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(duration) % TimeUnit.HOURS.toMinutes(1),
            TimeUnit.MILLISECONDS.toSeconds(duration) % TimeUnit.MINUTES.toSeconds(1)
        )
    }

}