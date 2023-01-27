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
import com.example.burmeseforkids.databinding.FragmentNumberBinding
import com.example.burmeseforkids.models.Word
import kotlin.properties.Delegates

class NumberFragment : Fragment() {

    private val TAG = "NumberFragment"
    private var _binding: FragmentNumberBinding? = null
    private val binding get() = _binding!!
    private var mMediaPlayer: MediaPlayer? = null
    private var mAudioManager: AudioManager? = null
    private lateinit var mFocusRequest: AudioFocusRequest
    private var currentWord by Delegates.notNull<Word>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNumberBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        createWordAdapter()
    }

    private fun createWordAdapter() {
        val words = ArrayList<Word>()
        words.apply {
            add(Word(getString(R.string.one_mm), getString(R.string.one_eng), ContextCompat.getDrawable(requireContext(), R.drawable.number_one)!!, R.raw.number_one))
            add(Word(getString(R.string.two_mm), getString(R.string.two_eng), ContextCompat.getDrawable(requireContext(), R.drawable.number_two)!!, R.raw.number_two))
            add(Word(getString(R.string.three_mm), getString(R.string.three_eng), ContextCompat.getDrawable(requireContext(), R.drawable.number_three)!!, R.raw.number_three))
            add(Word(getString(R.string.four_mm), getString(R.string.four_eng), ContextCompat.getDrawable(requireContext(), R.drawable.number_four)!!, R.raw.number_four))
            add(Word(getString(R.string.five_mm), getString(R.string.five_eng), ContextCompat.getDrawable(requireContext(), R.drawable.number_five)!!, R.raw.number_five))
            add(Word(getString(R.string.six_mm), getString(R.string.six_eng), ContextCompat.getDrawable(requireContext(), R.drawable.number_six)!!, R.raw.number_six))
            add(Word(getString(R.string.seven_mm), getString(R.string.seven_eng), ContextCompat.getDrawable(requireContext(), R.drawable.number_seven)!!, R.raw.number_seven))
            add(Word(getString(R.string.eight_mm), getString(R.string.eight_eng), ContextCompat.getDrawable(requireContext(), R.drawable.number_eight)!!, R.raw.number_eight))
            add(Word(getString(R.string.nine_mm), getString(R.string.nine_eng), ContextCompat.getDrawable(requireContext(), R.drawable.number_nine)!!, R.raw.number_nine))
            add(Word(getString(R.string.ten_mm), getString(R.string.ten_eng), ContextCompat.getDrawable(requireContext(), R.drawable.number_ten)!!, R.raw.number_ten))
        }

        for (i in words.indices) {
            Log.v(TAG, "Word at index ${i}: ${words[i]}")
        }

        val wordAdapter =
            WordAdapter(requireContext(), 0, words, ContextCompat.getColor(requireContext(), R.color.category_numbers))

        binding.numberListView.apply {
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
            mAudioManager?.abandonAudioFocusRequest(mFocusRequest)
        }
    }

    private fun initializeMediaPlayerInstance() {
        mMediaPlayer = MediaPlayer.create(requireContext(), currentWord.audioPlayback)
    }

    private fun createAudioFocusRequest() {
        // Create an instance of OnAudioFocusChangeListener
        val onAudioFocusChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
            when (focusChange) {
                AudioManager.AUDIOFOCUS_GAIN -> {
                    mMediaPlayer?.start()
                }
                AudioManager.AUDIOFOCUS_LOSS -> {
                    // Stop playback and clean resources
                    // mMediaPlayer?.reset()
                    releaseMediaPlayer()
                }
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                    mMediaPlayer?.pause()
                    mMediaPlayer?.seekTo(0)
                }
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                    mMediaPlayer?.setVolume(0.25f, 0.25f)
                }
            }
        }
        mFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
            .setOnAudioFocusChangeListener(onAudioFocusChangeListener)
            .build()
        mAudioManager = context?.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val requestResult = mAudioManager?.requestAudioFocus(mFocusRequest)

        // Check if audio focus request succeed or fails
        if (requestResult == AudioManager.AUDIOFOCUS_REQUEST_FAILED)
            Toast.makeText(requireContext(), "Audio Focus Request failed!", Toast.LENGTH_SHORT).show()
        else if (requestResult == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            // Attach audio playbacks to a variable
            initializeMediaPlayerInstance()
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