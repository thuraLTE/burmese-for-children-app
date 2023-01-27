package com.example.burmeseforkids.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.example.burmeseforkids.R
import com.example.burmeseforkids.adapters.CustomFragmentAdapter
import com.example.burmeseforkids.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var tabLayoutMediator: TabLayoutMediator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewpager2.apply {
            adapter = CustomFragmentAdapter(this@MainActivity)
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
        }

        if (tabLayoutMediator?.isAttached == true)
            tabLayoutMediator?.detach()
        tabLayoutMediator = TabLayoutMediator(binding.tabLayout, binding.viewpager2) { tab, position ->
            tab.text = getTabTitle(position)
        }
        tabLayoutMediator?.attach()
    }

    private fun getTabTitle(position: Int): String {
        return when (position) {
            0 -> getString(R.string.category_numbers)
            1 -> getString(R.string.category_family)
            2 -> getString(R.string.category_colors)
            else -> getString(R.string.category_phrases)
        }
    }

    override fun onStop() {
        super.onStop()
        if (tabLayoutMediator != null) {
            if (tabLayoutMediator?.isAttached == true)
                tabLayoutMediator?.detach()
            tabLayoutMediator = null
        }
    }
}