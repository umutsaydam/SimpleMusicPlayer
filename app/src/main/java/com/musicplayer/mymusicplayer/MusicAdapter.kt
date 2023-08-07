package com.musicplayer.mymusicplayer

import android.content.Context
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import java.lang.Exception

class MusicAdapter(private val context: Context) :
    RecyclerView.Adapter<MusicAdapter.MusicViewHolder>() {
    private lateinit var musicsList: List<Music>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {
        return MusicViewHolder(LayoutInflater.from(context).inflate(R.layout.music_item, parent, false))
    }

    override fun getItemCount(): Int {
        return musicsList.size
    }

    override fun onBindViewHolder(holder: MusicViewHolder, position: Int) {
        val music = musicsList[position]
        holder.cardViewMusic.setOnClickListener {
            try {
                val mediaPlayer = MediaPlayer()
                mediaPlayer.reset()
                mediaPlayer.setDataSource(music.path)
                mediaPlayer.prepare()
                mediaPlayer.start()
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
        holder.msMusic.text = music.title
    }

    fun setMusicList(musicList: List<Music>) {
        this.musicsList = musicList
        notifyDataSetChanged()
    }
    class MusicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardViewMusic: CardView = itemView.findViewById(R.id.cardViewMusic)
        val msMusic: TextView = itemView.findViewById(R.id.msName)
    }
}