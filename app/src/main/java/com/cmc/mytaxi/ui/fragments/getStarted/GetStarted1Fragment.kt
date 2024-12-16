package com.cmc.mytaxi.ui.fragments.getStarted

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.cmc.mytaxi.R
import com.cmc.mytaxi.databinding.GetStarted1Binding
import com.cmc.mytaxi.utils.AnimationUtils.Companion.startJumpingAnimation
import com.cmc.mytaxi.utils.AnyTaxyFragments
import com.cmc.mytaxi.utils.StatusBarUtils
import com.cmc.mytaxi.ui.activity.GetStarted

class GetStarted1Fragment : AnyTaxyFragments() {
    private var _binding: GetStarted1Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = GetStarted1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        StatusBarUtils.setStatusBarColor(requireActivity().window, R.color.yellow)

        jumpingAnimation(binding.jumpingImage)

        binding.next.setOnClickListener {
            (activity as? GetStarted)?.binding?.viewPager?.currentItem = 1
        }

        binding.skip.setOnClickListener {
            (activity as? GetStarted)?.binding?.viewPager?.currentItem = 2
        }
    }

    private fun jumpingAnimation(imageView: ImageView) {
        startJumpingAnimation(imageView, -50f, 1000, 20)
    }
}
