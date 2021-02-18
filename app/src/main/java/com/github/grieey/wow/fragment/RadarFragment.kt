package com.github.grieey.wow.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.grieey.coreui.CoreFragment
import com.github.grieey.wow.databinding.FragmentRadarBinding
import com.github.grieey.wow.view.RadarView

/**
 * description: 雷达图页面
 * @date: 2021/2/8 10:52
 * @author: Grieey
 */
class RadarFragment : ViewBindingFragment<FragmentRadarBinding>(FragmentRadarBinding::inflate) {

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initUI()
  }

  private fun initUI() {
    binding.radarView.dataSource = listOf(
      RadarView.RadarData(
        "Fashion",
        Color.parseColor("#E15665"),
        Color.parseColor("#40E15665"),
        Triple(75.43, 65.53, 32.45)
      ),
      RadarView.RadarData(
        "Art & Technology",
        Color.parseColor("#83AED7"),
        Color.parseColor("#4083AED7"),
        Triple(22.73, 45.18, 77.15)
      ),
      RadarView.RadarData(
        "Entertainment",
        Color.parseColor("#63e7e5"),
        Color.parseColor("#4063e7e5"),
        Triple(33.33, 82.66, 55.55)
      ),
      RadarView.RadarData(
        "Boxing",
        Color.parseColor("#e3e2de"),
        Color.parseColor("#40e3e2de"),
        Triple(88.43, 11.53, 67.45)
      )
    )

    binding.radarView.animDuration = 250
  }
}