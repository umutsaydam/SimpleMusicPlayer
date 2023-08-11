package com.musicplayer.mymusicplayer


import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.musicplayer.mymusicplayer.Adapter.MusicAdapter
import com.musicplayer.mymusicplayer.Fragment.PlayerFragment
import com.musicplayer.mymusicplayer.Model.Music
import com.musicplayer.mymusicplayer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), MusicAdapter.MusicClickListener {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkPermission()

    }

    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.MODIFY_AUDIO_SETTINGS
            )
            == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            listMusics()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.MODIFY_AUDIO_SETTINGS,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                1
            )
        }
    }

    private fun listMusics() {
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA
        )

        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"

        val cursor = contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            null
        )

        val musicList = arrayListOf<Music>()

        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getLong(it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID))
                val title =
                    it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME))
                val artist = it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST))
                val duration = it.getLong(it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))
                val path = it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))

                val music = Music(id, title, artist, duration, path)
                musicList.add(music)
            }
            val adapter = MusicAdapter(this, this)
            adapter.setMusicList(musicList)
            binding.recycler.setHasFixedSize(true)
            binding.recycler.adapter = adapter
        }
    }

    override fun onItemClickListener(music: Music) {
        val playerFragment = PlayerFragment()
        val bundle = Bundle().apply {
            putParcelable("music", music)
        }
        playerFragment.arguments = bundle
        supportFragmentManager.beginTransaction().add(R.id.fragmentContainer, playerFragment)
            .commit()

    }
}