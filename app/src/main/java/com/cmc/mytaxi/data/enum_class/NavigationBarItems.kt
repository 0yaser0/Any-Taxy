package com.cmc.mytaxi.data.enum_class

import com.cmc.mytaxi.R
import com.cmc.mytaxi.data.local.models.NavigationItem


enum class NavigationBarItems(val item: NavigationItem) {
    PROFILE(NavigationItem(0, "Profile", R.drawable.ic_nav_profile, "Profile")),
    HOME(NavigationItem(1, "Home", R.drawable.ic_nav_home, "Home")),
    SETTINGS(NavigationItem(2, "Settings", R.drawable.ic_nav_settings, "Settings")),
}