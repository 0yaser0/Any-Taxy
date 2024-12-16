package com.cmc.mytaxi.ui.fragments.getStarted

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.cmc.mytaxi.R
import com.cmc.mytaxi.databinding.GetStarted3Binding
import com.cmc.mytaxi.ui.activity.BuildProfile
import com.cmc.mytaxi.utils.AnyTaxyFragments
import com.cmc.mytaxi.utils.StatusBarUtils
import com.cmc.mytaxi.ui.activity.GetStarted

@Suppress("DEPRECATION")
class GetStarted3Fragment : AnyTaxyFragments() {
    private var _binding: GetStarted3Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = GetStarted3Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        StatusBarUtils.setStatusBarColor(requireActivity().window, R.color.yellow)

        Glide.with(this)
            .load(R.drawable.taxi_loader)
            .into(binding.imgGst2)

        binding.btnGetstarted.setOnClickListener {

                startActivity(Intent(requireActivity(), BuildProfile::class.java))

            requireActivity().overridePendingTransition(R.animator.slide_in_right, R.animator.slide_out_left)
        }
    }
}
