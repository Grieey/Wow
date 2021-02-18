package com.github.grieey.wow.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment
import com.github.grieey.core_ext.dp
import com.github.grieey.core_ext.int
import com.github.grieey.core_ext.sp
import com.github.grieey.coreui.CoreFragment
import com.github.grieey.wow.R
import com.github.grieey.wow.constant.Router
import com.github.grieey.wow.databinding.FragmentMainBinding
import com.github.grieey.wow.extension.applyColorTo
import com.github.grieey.wow.items.textItem

/**
 * description:列表的fragment
 * @date: 2021/2/8 16:32
 * @author: Grieey
 */
class MainFragment : CoreFragment() {

  private lateinit var binding: FragmentMainBinding

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    binding = FragmentMainBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    initEpoxy()
  }

  private fun initEpoxy() {
    binding.epoxy.withModels {
      Router.MAP.forEach {
        textItem {
          id(it.first)
          target(it.second)
          text(it.first)
          style {
            it.textSize = 12.sp
            R.color.purple_500 applyColorTo it::setTextColor
            it.setPadding(8.dp.int, 8.dp.int, 8.dp.int, 8.dp.int)
          }
          click { _, _ ->
            NavHostFragment.findNavController(this@MainFragment).navigate(it.second)
          }
        }
      }
    }
  }
}