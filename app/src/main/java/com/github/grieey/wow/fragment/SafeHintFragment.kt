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

  private val dataList =
    listOf("平台已完成对出租车司机和车辆信息核验", "出租车已全部完成综合身份核验", "平台已完成对出租车司机和车辆信息核验2", "行程不安全，亲人")

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initUI()
  }

  private fun initUI() {
    binding.safeHintView.dataSource = dataList
    binding.safeHintView.textStyle = {
      R.color.white applyColorTo this::setTextColor
    }
    binding.safeHintView.click {
      Toast.makeText(activity, "hi", Toast.LENGTH_SHORT).show()
    }
  }
}