package com.github.grieey.wow.fragment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.github.grieey.core_ext.click
import com.github.grieey.wow.R
import com.github.grieey.wow.databinding.FragmentSafeHintBinding
import com.github.grieey.wow.extension.applyColorTo

/**
 * description: 仿滴滴的安全提示view
 * @date: 2021/2/24 21:32
 * @author: Grieey
 */
class SafeHintFragment :
  ViewBindingFragment<FragmentSafeHintBinding>(FragmentSafeHintBinding::inflate) {

  private val dataList = listOf("行程不安全，亲人行程不安全，亲人", "哈哈哈哈哈", "行程不安全，亲人两行泪", "行程不安全，亲人")

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initUI()
  }

  private fun initUI() {
    binding.saftHintView.dataSource = dataList
    binding.saftHintView.textStyle = {
      R.color.white_34 applyColorTo this::setTextColor
    }
    binding.saftHintView.click {
      Toast.makeText(activity, "hi", Toast.LENGTH_SHORT).show()
    }
  }
}