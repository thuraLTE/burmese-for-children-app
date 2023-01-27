package com.example.burmeseforkids.ui

import android.content.Context
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.burmeseforkids.R
import com.example.burmeseforkids.adapters.WordAdapter
import com.example.burmeseforkids.databinding.FragmentColorBinding
import com.example.burmeseforkids.models.Word

class ColorFragment : Fragment() {

    private val TAG = "ColorFragment"
    private var _binding: FragmentColorBinding? = null
    private val binding get() = _binding!!
    private var mMediaPlayer: MediaPlayer? = null
    private lateinit var mAudioManager: AudioManager
    private lateinit var mFocusRequest: AudioFocusRequest
    private lateinit var currentWord: Word

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentColorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        createWordAdapter()
    }

    private fun createWordAdapter() {
        val words = ArrayList<Word>()
        words.apply {
            add(Word(getString(R.string.red_mm), getString(R.string.red_eng), ContextCompat.getDrawable(requireContext(), R.drawable.color_red)!!, R.raw.color_red))
            add(Word(getString(R.string.blue_mm), getString(R.string.blue_eng), ContextCompat.getDrawable(requireContext(), R.drawable.color_blue_2)!!, R.raw.color_blue))
            add(Word(getString(R.string.green_mm), getString(R.string.green_eng), ContextCompat.getDrawable(requireContext(), R.drawable.color_green)!!, R.raw.color_green))
            add(Word(getString(R.string.yellow_mm), getString(R.string.yellow_eng), ContextCompat.getDrawable(requireContext(), R.drawable.color_yellow)!!, R.raw.color_yellow))
            add(Word(getString(R.string.pink_mm), getString(R.string.pink_eng), ContextCompat.getDrawable(requireContext(), R.drawable.color_pink_2)!!, R.raw.color_pink))
            add(Word(getString(R.string.black_mm), getString(R.string.black_eng), ContextCompat.getDrawable(requireContext(), R.drawable.color_black)!!, R.raw.color_black))
            add(Word(getString(R.string.white_mm), getString(R.string.white_eng), ContextCompat.getDrawable(requireContext(), R.drawable.color_white)!!, R.raw.color_white))
            add(Word(getString(R.string.brown_mm), getString(R.string.brown_eng), ContextCompat.getDrawable(requireContext(), R.drawable.color_brown)!!, R.raw.color_brown))
            add(Word(getString(R.string.purple_mm), getString(R.string.purple_eng), ContextCompat.getDrawable(requireContext(), R.drawable.color_purple_2)!!, R.raw.color_purple))
            add(Word(getString(R.string.gray_mm), getString(R.string.gray_eng), ContextCompat.getDrawable(requireContext(), R.drawable.color_gray)!!, R.raw.color_gray))
        }

        for (i in words.indices) {
            Log.v(TAG, "Word at index ${i}: ${words[i]}")
        }

        val wordAdapter =
            WordAdapter(requireContext(), 0, words, ContextCompat.getColor(requireContext(), R.color.category_colors))

        binding.colorListView.apply {
            adapter = wordAdapter
            setOnItemClickListener { adapterView, view, position, id ->
                currentWord = words[position]
                // Call the toString() method from the word class for debugging purposes
                Log.v(TAG, "Current word: $currentWord")
                // Release any leftover media player resources associated with old files
                releaseMediaPlayer()
                createAudioFocusRequest()
            }
        }
    }

    // Release memory resources associated with current media player object
    private fun releaseMediaPlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer!!.release()
            mMediaPlayer = null
            mAudioManager.abandonAudioFocusRequest(mFocusRequest)
        }
    }

    private fun createAudioFocusRequest() {
        val onAudioFocusChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
            when (focusChange) {
                AudioManager.AUDIOFOCUS_GAIN -> mMediaPlayer?.start()
                AudioManager.AUDIOFOCUS_LOSS -> releaseMediaPlayer()
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                    mMediaPlayer?.pause()
                    mMediaPlayer?.seekTo(0)
                }
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK ->
                    mMediaPlayer?.setVolume(0.25f, 0.25f)
            }
        }
        mAudioManager = context?.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        mFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
            .setOnAudioFocusChangeListener(onAudioFocusChangeListener)
            .build()
        val requestResult = mAudioManager.requestAudioFocus(mFocusRequest)
        if (requestResult == AudioManager.AUDIOFOCUS_REQUEST_FAILED)
            Toast.makeText(requireContext(), "Audio Focus Request Failed!", Toast.LENGTH_SHORT).show()
        else if (requestResult == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            // Attach audio playbacks to a variable
            mMediaPlayer = MediaPlayer.create(requireContext(), currentWord.audioPlayback)
            mMediaPlayer!!.start()
            mMediaPlayer!!.setOnCompletionListener {
                releaseMediaPlayer()
            }
        }
    }

    // Call release method if the user decides to exit our app in the middle of audio playback
    override fun onStop() {
        super.onStop()
        releaseMediaPlayer()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}