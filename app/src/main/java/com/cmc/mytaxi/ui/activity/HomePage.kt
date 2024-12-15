package com.cmc.mytaxi.ui.activity

import android.os.Bundle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.graphics.Color
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.cmc.mytaxi.R
import com.cmc.mytaxi.databinding.HomePageLayoutBinding
import com.cmc.mytaxi.ui.adapters.HomeAdapter
import com.cmc.mytaxi.utils.AnyTaxyActivity
import com.cmc.mytaxi.utils.BottomNavBar
import com.cmc.mytaxi.utils.SetupUI
import com.cmc.mytaxi.utils.StatusBarUtils

class HomePage : AnyTaxyActivity(){
    private lateinit var binding: HomePageLayoutBinding
    private lateinit var adapter: HomeAdapter
    private lateinit var viewPager: ViewPager

    private val selectedIndexState = mutableIntStateOf(1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = HomePageLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        StatusBarUtils.setStatusBarColor(this.window, R.color.yellow)
        SetupUI.setupUI(binding.root)

        viewPager = binding.viewPager
        adapter = HomeAdapter(supportFragmentManager)
        viewPager.adapter = adapter

        viewPager.currentItem = 1

        binding.composeView.setContent {
            BottomBarDemoTheme {
                BottomNavBar(
                    selectedIndexState = selectedIndexState,
                    onItemSelected = { index ->
                        viewPager.setCurrentItem(index, true)
                        adapter.navigateToFragment(viewPager, index)
                    }
                )
            }
        }

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                // No scroll
            }

            override fun onPageSelected(position: Int) {
                if (selectedIndexState.intValue != position) {
                    selectedIndexState.intValue = position
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
                // No scroll
            }
        })

    }
}


val DarkBlue = Color(0xFF0F2032)
val Black = Color(0xFF000000)
val Blue = Color(0xFF0491E9)
val DeathRed = Color(0xFFCC3D4E)
val White = Color(0xFFFFFFFF)
val Yellow = Color(0xFFE6BB11)
val LightGreen = Color(0xFF2BCE72)
val DarkGreen = Color(0xFF108C73)

private val LightColorScheme = lightColorScheme(
    primary = DeathRed,
    secondary = Yellow,
    background = LightGreen,
    onBackground = DarkGreen,
    surface = White,
    inverseSurface = Black
)

@Composable
fun BottomBarDemoTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        content = content
    )
}

@GlideModule
class MyAppGlideModule : AppGlideModule() {
}