package com.musicplayer.mymusicplayer

data class Music(
    val id: Long,
    val title: String,
    val artist: String,
    val duration: Long,
    val path: String
)
