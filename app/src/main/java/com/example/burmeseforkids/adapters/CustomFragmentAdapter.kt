package com.example.burmeseforkids.adapters

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.burmeseforkids.ui.ColorFragment
import com.example.burmeseforkids.ui.FamilyMemberFragment
import com.example.burmeseforkids.ui.NumberFragment
import com.example.burmeseforkids.ui.PhraseFragment

class CustomFragmentAdapter(activity: AppCompatActivity): FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> NumberFragment()
            1 -> FamilyMemberFragment()
            2 -> ColorFragment()
            else -> PhraseFragment()
        }
    }
}