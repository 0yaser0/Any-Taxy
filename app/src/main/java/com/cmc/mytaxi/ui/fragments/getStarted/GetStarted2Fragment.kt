package com.cmc.mytaxi.ui.fragments.getStarted

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.cmc.mytaxi.R
import com.cmc.mytaxi.databinding.GetStarted2Binding
import com.cmc.mytaxi.ui.activity.GetStarted
import com.cmc.mytaxi.utils.AnyTaxyFragments
import com.cmc.mytaxi.utils.StatusBarUtils

class GetStarted2Fragment : AnyTaxyFragments() {
    private var _binding: GetStarted2Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = GetStarted2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        StatusBarUtils.setStatusBarColor(requireActivity().window, R.color.yellow)

        Glide.with(this)
            .load(R.drawable.taxy_animation_setup)
            .into(binding.imgGst2)

        binding.next.setOnClickListener {
            (activity as? GetStarted)?.binding?.viewPager?.currentItem = 2
        }

        binding.skip.setOnClickListener {
            (activity as? GetStarted)?.binding?.viewPager?.currentItem = 2
        }

    }
}
