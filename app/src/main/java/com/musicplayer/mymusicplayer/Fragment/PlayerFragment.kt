package com.musicplayer.mymusicplayer.Fragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.musicplayer.mymusicplayer.MediaPlayerInstance
import com.musicplayer.mymusicplayer.Model.Music
import com.musicplayer.mymusicplayer.R
import com.musicplayer.mymusicplayer.VisualizerHelper
import com.musicplayer.mymusicplayer.databinding.FragmentPlayerBinding

class PlayerFragment : Fragment(), Runnable {
    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!
    private var music: Music? = null
    private var seekBarDragTime = 0
    private val playerInstance = MediaPlayerInstance
    private val mediaPlayer = playerInstance.getMediaPlayer()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)

        music = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable("music", Music::class.java)!!
        } else {
            arguments?.getParcelable("music") as? Music
        }

        if (music == null) {
            Toast.makeText(context, "NULL", Toast.LENGTH_SHORT).show()
        } else {
            if (mediaPlayer.isPlaying && VisualizerHelper.getVisualizer() != null){
                VisualizerHelper.stopVisualizer()
                VisualizerHelper.startVisualizer()
            }
            setItems()
        }

        binding.prevImageView.setOnClickListener {
            playerInstance.setPrevMusic()
            startStopImgMusic()
            resetVisualizer()
            setItems()
        }

        binding.nextImageView.setOnClickListener {
            playerInstance.setNextMusic()
            startStopImgMusic()
            resetVisualizer()
            setItems()
        }
        return binding.root
    }

    private fun resetVisualizer() {
        VisualizerHelper.stopVisualizer()
        VisualizerHelper.startVisualizer()
    }

    private fun startStopImgMusic(){
        if (mediaPlayer.isPlaying) {
            binding.playPauseImageView.setImageResource(R.drawable.ic_pause)
        } else {
            binding.playPauseImageView.setImageResource(R.drawable.ic_play)
        }
    }

    private fun setItems() {
        music = playerInstance.getMusic()
        binding.audioName.text = music?.title
        binding.artistName.text = music?.artist

        val seekBar = binding.seekBar
        binding.seekBar.progress = 0
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, p2: Boolean) {
                seekBarDragTime = progress
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                mediaPlayer.seekTo(seekBarDragTime * 1000)
            }
        })

        setSeekBar()

        binding.msCurrPosition.text =
            playerInstance.formatTime(playerInstance.getMusicCurrPosition().toLong())
        binding.msDuration.text = playerInstance.formatTime(playerInstance.getMusicDuration())

        binding.playPauseLayout.setOnClickListener {
            MediaPlayerInstance.playStopMusic()
            startStopImgMusic()
            resetVisualizer()
            setSeekBar()
        }

    }

    private fun setSeekBar() {
       Thread(this).start()
    }

    override fun run() {
        val total = mediaPlayer.duration
        var currPosition = mediaPlayer.currentPosition / 1000
        binding.seekBar.max = total / 1000
        binding.seekBar.progress = currPosition

        while (mediaPlayer.isPlaying && mediaPlayer.currentPosition < total) {
            if (mediaPlayer.isPlaying && currPosition < total) {
                binding.seekBar.progress = currPosition
                currPosition = mediaPlayer.currentPosition / 1000
                activity?.runOnUiThread {
                    binding.msCurrPosition.text =
                        playerInstance.formatTime(playerInstance.getMusicCurrPosition().toLong())
                }
            }
            Thread.sleep(1000)
        }
        binding.playPauseImageView.setImageResource(R.drawable.ic_play)
    }

}