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
import com.example.burmeseforkids.databinding.FragmentFamilyMemberBinding
import com.example.burmeseforkids.models.Word

class FamilyMemberFragment : Fragment() {

    private val TAG = "FamilyMemberFragment"
    private var _binding: FragmentFamilyMemberBinding? = null
    private val binding get() = _binding!!
    private var mMediaPlayer: MediaPlayer? = null
    private lateinit var mAudioManager: AudioManager
    private lateinit var mFocusRequest: AudioFocusRequest
    private lateinit var currentWord: Word

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFamilyMemberBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        createWordAdapter()
    }

    private fun createWordAdapter() {
        val words = ArrayList<Word>()
        words.apply {
            add(Word(getString(R.string.father_mm), getString(R.string.father_eng), ContextCompat.getDrawable(requireContext(), R.drawable.family_father)!!, R.raw.family_father))
            add(Word(getString(R.string.mother_mm), getString(R.string.mother_eng), ContextCompat.getDrawable(requireContext(), R.drawable.family_mother)!!, R.raw.family_mother))
            add(Word(getString(R.string.son_mm), getString(R.string.son_eng), ContextCompat.getDrawable(requireContext(), R.drawable.family_son)!!, R.raw.family_son))
            add(Word(getString(R.string.daughter_mm), getString(R.string.daughter_eng), ContextCompat.getDrawable(requireContext(), R.drawable.family_daughter)!!, R.raw.family_daughter))
            add(Word(getString(R.string.older_brother_mm), getString(R.string.older_brother_eng), ContextCompat.getDrawable(requireContext(), R.drawable.family_older_brother)!!, R.raw.family_older_brother))
            add(
                Word(
                    getString(R.string.younger_brother_mm),
                    getString(R.string.younger_brother_eng),
                    ContextCompat.getDrawable(requireContext(), R.drawable.family_younger_brother)!!,
                    R.raw.family_younger_brother
                )
            )
            add(Word(getString(R.string.older_sister_mm), getString(R.string.older_sister_eng), ContextCompat.getDrawable(requireContext(), R.drawable.family_older_sister)!!, R.raw.family_older_sister))
            add(Word(getString(R.string.younger_sister_mm), getString(R.string.younger_sister_eng), ContextCompat.getDrawable(requireContext(), R.drawable.family_younger_sister)!!, R.raw.family_younger_sister))
            add(Word(getString(R.string.grandfather_mm), getString(R.string.grandfather_eng), ContextCompat.getDrawable(requireContext(), R.drawable.family_grandfather)!!, R.raw.family_grandfather))
            add(Word(getString(R.string.grandmother_mm), getString(R.string.grandmother_eng), ContextCompat.getDrawable(requireContext(), R.drawable.family_grandmother)!!, R.raw.family_grandmother))
        }

        for (i in words.indices) {
            Log.v(TAG, "Word at index ${i}: ${words[i]}")
        }

        val wordAdapter =
            WordAdapter(requireContext(), 0, words, ContextCompat.getColor(requireContext(), R.color.category_family))

        binding.familyMemberListView.apply {
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