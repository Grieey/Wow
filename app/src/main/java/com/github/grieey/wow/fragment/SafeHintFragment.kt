package com.github.grieey.wow.fragment

import android.os.Bundle
import android.view.View
import com.github.grieey.wow.databinding.FragmentSafeHintBinding

/**
 * description: 仿滴滴的安全提示view
 * @date: 2021/2/24 21:32
 * @author: Grieey
 */
class SafeHintFragment :
  ViewBindingFragment<FragmentSafeHintBinding>(FragmentSafeHintBinding::inflate) {


  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initUI()
  }

  private fun initUI() {
    TODO("实现UI")
  }
}