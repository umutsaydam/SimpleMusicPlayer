package com.musicplayer.mymusicplayer.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.musicplayer.mymusicplayer.MediaPlayerInstance
import com.musicplayer.mymusicplayer.Model.Music
import com.musicplayer.mymusicplayer.R

class MusicAdapter(private val context: Context, private val listener: MusicClickListener) :
    RecyclerView.Adapter<MusicAdapter.MusicViewHolder>() {
    private var musicsList = arrayListOf<Music>()
    private var lastSelectedMusicCardView: CardView? = null
    private val playerInstance = MediaPlayerInstance

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {
        return MusicViewHolder(
            LayoutInflater.from(context).inflate(R.layout.music_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return musicsList.size
    }

    override fun onBindViewHolder(holder: MusicViewHolder, position: Int) {
        val music = musicsList[position]
        holder.cardViewMusic.setOnClickListener {
            listener.onItemClickListener(music)
            holder.cardViewMusic.setCardBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.selected_music
                )
            )
            if (lastSelectedMusicCardView != null && lastSelectedMusicCardView != holder.cardViewMusic) {
                lastSelectedMusicCardView!!.setCardBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.listed_music
                    )
                )
            }

            if (playerInstance.getMediaPlayer().isPlaying){
                playerInstance.getMediaPlayer().stop()
            }
            playerInstance.setMusic(music)
            playerInstance.playOrStopMusic()

            lastSelectedMusicCardView = holder.cardViewMusic
        }
        holder.msMusic.text = music.title
        holder.msArtist.text = music.artist
        holder.msDuration.text = playerInstance.formatTime(music.duration)
    }

    fun setMusicList(musicList: ArrayList<Music>) {
        this.musicsList = musicList
        notifyDataSetChanged()
    }

    class MusicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardViewMusic: CardView = itemView.findViewById(R.id.cardViewMusic)
        val msMusic: TextView = itemView.findViewById(R.id.msName)
        val msArtist: TextView = itemView.findViewById(R.id.msArtist)
        val msDuration: TextView = itemView.findViewById(R.id.msDuration)
    }

    interface MusicClickListener {
        fun onItemClickListener(music: Music)
    }
}