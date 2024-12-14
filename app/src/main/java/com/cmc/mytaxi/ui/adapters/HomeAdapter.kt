package com.cmc.mytaxi.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.cmc.mytaxi.ui.fragment.HomePageFragment
import com.cmc.mytaxi.ui.fragment.SettingsFragment
import com.cmc.mytaxi.ui.fragments.profile.EditProfileFragment

class HomeAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            1 -> HomePageFragment()
            0 -> EditProfileFragment()
            2 -> SettingsFragment()
            else -> throw RuntimeException("Invalid position")
        }
    }

    override fun getCount(): Int {
        return 4
    }

    fun navigateToFragment(viewPager: ViewPager, index: Int) {
        if (index in 0 until count) {
            viewPager.currentItem = index
        } else {
            throw IllegalArgumentException("Invalid fragment index: $index")
        }
    }
}