package com.musicplayer.mymusicplayer.Fragment

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.musicplayer.mymusicplayer.Model.Music
import com.musicplayer.mymusicplayer.databinding.FragmentPlayerBinding

class PlayerFragment : Fragment() {
    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!
    private var music: Music? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)


        music = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable("music", Music::class.java)!!
        } else {
            arguments?.getParcelable("music") as? Music
        }

        if (music == null) {
            Toast.makeText(context, "NULL", Toast.LENGTH_SHORT).show()
        } else {
            setItems()
        }

        return binding.root
    }

    private fun setItems() {
        binding.audioName.text = music?.title
        binding.artistName.text = music?.artist
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}