package com.musicplayer.mymusicplayer.Model

data class Music(
    val id: Long,
    val title: String,
    val artist: String,
    val duration: Long,
    val path: String
)
