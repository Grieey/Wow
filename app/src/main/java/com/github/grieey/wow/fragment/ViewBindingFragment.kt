package com.github.grieey.wow.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.github.grieey.coreui.CoreFragment

/**
 * description: ViewBindingFragment的基类
 * @date: 2021/2/18 17:41
 * @author: Grieey
 */
abstract class ViewBindingFragment<VB : ViewBinding>(
  private val inflateBlock: (inflater: LayoutInflater, container: ViewGroup?, attach: Boolean) -> VB
) : CoreFragment() {

  protected lateinit var binding: VB

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    binding = inflateBlock(inflater, container, false)
    return binding.root
  }
}