package com.cmc.mytaxi.ui.activity

import android.os.Bundle
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.cmc.mytaxi.R
import com.cmc.mytaxi.databinding.HomePageLayoutBinding
import com.cmc.mytaxi.ui.fragment.HomePageFragment
import com.cmc.mytaxi.utils.AnyTaxyActivity

class HomePage : AnyTaxyActivity(){
    private lateinit var binding: HomePageLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = HomePageLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, HomePageFragment())
            .commit()
    }
}