package com.cmc.mytaxi.utils

import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.cmc.mytaxi.data.enum_class.NavigationBarItems
import com.cmc.mytaxi.ui.fragment.HomePageFragment
import com.cmc.mytaxi.ui.fragment.SettingsFragment
import com.cmc.mytaxi.ui.fragments.profile.EditProfileFragment
import com.exyte.animatednavbar.AnimatedNavigationBar
import com.exyte.animatednavbar.animation.balltrajectory.Parabolic
import com.exyte.animatednavbar.animation.indendshape.Height
import com.exyte.animatednavbar.animation.indendshape.shapeCornerRadius
import com.exyte.animatednavbar.utils.noRippleClickable

@Composable
fun BottomNavBar(
    selectedIndexState: MutableState<Int>,
    onItemSelected: (Int) -> Unit
) {
    val navigationBarItems = remember { NavigationBarItems.entries.toTypedArray() }
    val selectedIndex = selectedIndexState.value

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        bottomBar = {
            AnimatedNavigationBar(
                modifier = Modifier.height(75.dp),
                selectedIndex = selectedIndex,
                cornerRadius = shapeCornerRadius(30.dp, 30.dp, 0.dp, 0.dp),
                ballAnimation = Parabolic(tween(500), 60.dp),
                indentAnimation = Height(tween(800), 50.dp, 10.dp),
                barColor = MaterialTheme.colorScheme.onBackground,
                ballColor = MaterialTheme.colorScheme.background
            ) {
                navigationBarItems.forEach { item ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .noRippleClickable {
                                selectedIndexState.value = item.item.index
                                onItemSelected(item.item.index)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                modifier = Modifier.size(25.dp),
                                painter = painterResource(id = item.item.iconResId),
                                contentDescription = item.item.description,
                                tint = if (selectedIndex == item.item.index) MaterialTheme.colorScheme.surface
                                else MaterialTheme.colorScheme.inverseSurface
                            )
                            if (selectedIndex == item.item.index) {
                                Text(
                                    text = item.item.label,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.surface,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedIndex) {
                NavigationBarItems.PROFILE.ordinal -> EditProfileFragment()
                NavigationBarItems.HOME.ordinal -> HomePageFragment()
                NavigationBarItems.SETTINGS.ordinal -> SettingsFragment()
            }
        }
    }
}

fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = composed {
    this.then(
        clickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() }
        ) {
            onClick()
        }
    )
}


