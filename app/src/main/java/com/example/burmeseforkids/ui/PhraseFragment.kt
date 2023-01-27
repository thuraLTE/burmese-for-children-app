package com.example.burmeseforkids.ui

import android.content.Context
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.burmeseforkids.R
import com.example.burmeseforkids.adapters.WordAdapter
import com.example.burmeseforkids.databinding.FragmentPhraseBinding
import com.example.burmeseforkids.models.Word

class PhraseFragment : Fragment() {

    private val TAG = "PhraseFragment"
    private var _binding: FragmentPhraseBinding? = null
    private val binding get() = _binding!!
    private var mMediaPlayer: MediaPlayer? = null
    private lateinit var mAudioManager: AudioManager
    private lateinit var mFocusRequest: AudioFocusRequest
    private lateinit var currentWord: Word

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPhraseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        createWordAdapter()
    }

    private fun createWordAdapter() {
        val words = ArrayList<Word>()
        words.apply {
            add(Word(getString(R.string.where_are_you_going_mm), getString(R.string.where_are_you_going_eng), null, R.raw.phrase_where_are_you_going))
            add(Word(getString(R.string.what_is_your_name_mm), getString(R.string.what_is_your_name_eng), null, R.raw.phrase_what_is_your_name))
            add(Word(getString(R.string.my_name_is_mm), getString(R.string.my_name_is_eng), null, R.raw.phrase_my_name_is))
            add(Word(getString(R.string.how_are_you_feeling_mm), getString(R.string.how_are_you_feeling_eng), null, R.raw.phrase_how_are_you_feeling))
            add(Word(getString(R.string.i_am_feeling_good_mm), getString(R.string.i_am_feeling_good_eng), null, R.raw.phrase_i_am_feeling_good))
            add(Word(getString(R.string.are_you_coming_mm), getString(R.string.are_you_coming_eng), null, R.raw.phrase_are_you_coming))
            add(Word(getString(R.string.yes_i_am_coming_mm), getString(R.string.yes_i_am_coming_eng), null, R.raw.yes_i_am_coming))
            add(Word(getString(R.string.good_morning_mm), getString(R.string.good_morning_eng), null, R.raw.phrase_good_morning))
            add(Word(getString(R.string.let_is_go_mm), getString(R.string.let_is_go_eng), null, R.raw.phrase_let_is_go))
            add(Word(getString(R.string.come_here_mm), getString(R.string.come_here_eng), null, R.raw.phrase_come_here))
        }

        for (i in words.indices) {
            Log.v(TAG, "Word at index ${i}: ${words[i]}")
        }

        val wordAdapter =
            WordAdapter(requireContext(), 0, words, ContextCompat.getColor(requireContext(), R.color.category_phrases))

        binding.phraseListView.apply {
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