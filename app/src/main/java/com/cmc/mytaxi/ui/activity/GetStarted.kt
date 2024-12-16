package com.cmc.mytaxi.ui.activity

import android.os.Bundle
import com.cmc.mytaxi.databinding.GetStartedBinding
import com.cmc.mytaxi.utils.AnyTaxyActivity
import com.cmc.mytaxi.ui.adapters.GetstartedAdapter

class GetStarted : AnyTaxyActivity() {

    lateinit var binding: GetStartedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = GetStartedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = GetstartedAdapter(this)
        binding.viewPager.adapter = adapter
        binding.indicator.setViewPager(binding.viewPager)

    }
}
