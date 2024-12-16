package com.cmc.mytaxi.splash

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import androidx.annotation.RequiresApi
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.cmc.mytaxi.utils.AnyTaxyActivity
import com.cmc.mytaxi.R
import com.cmc.mytaxi.databinding.ActivitySplashBinding
import com.cmc.mytaxi.ui.activity.GetStarted

class Splash : AnyTaxyActivity() {
    private lateinit var binding: ActivitySplashBinding

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Glide.with(this)
            .load(R.drawable.mini_red_taxy)
            .into(binding.logo)

        // Gestion de l'apparence de la barre de statut
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            window.insetsController?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)

        binding.SplashMotion.addTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(motionLayout: MotionLayout?, startId: Int, endId: Int) {}

            override fun onTransitionChange(motionLayout: MotionLayout?, startId: Int, endId: Int, progress: Float) {}

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                startActivity(Intent(this@Splash, GetStarted::class.java))
                finish()
            }

            override fun onTransitionTrigger(motionLayout: MotionLayout?, triggerId: Int, positive: Boolean, progress: Float) {}
        })
    }
}
