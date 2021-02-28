package com.github.grieey.wow.fragment

import android.os.Bundle
import android.view.View
import com.github.grieey.wow.R
import com.github.grieey.wow.databinding.FragmentSafeHintBinding
import com.github.grieey.wow.extension.applyColorTo
import com.github.grieey.wow.widget.recyclerview.SafeHintAdapter
import com.github.grieey.wow.widget.recyclerview.SafeHintLayoutManager

/**
 * description: 仿滴滴的安全提示view
 * @date: 2021/2/24 21:32
 * @author: Grieey
 */
class SafeHintFragment :
  ViewBindingFragment<FragmentSafeHintBinding>(FragmentSafeHintBinding::inflate) {

  private val dataList = listOf("行程不安全，亲人两行泪", "哈哈哈哈")

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initUI()
  }

  private fun initUI() {
//    binding.safeHintRv.layoutManager =
////      SafeHintLayoutManager(binding.safeHintRv.context).apply { bgView = binding.safeHintBgIv }
//      SafeHintLayoutManager(binding.safeHintRv.context)
//    binding.safeHintRv.adapter =
//      SafeHintAdapter(binding.safeHintRv.context).apply { dataSource = dataList }
    binding.saftHintView.dataSource = dataList
    binding.saftHintView.textStyle = {
      R.color.white_34 applyColorTo this::setTextColor
    }
  }
}