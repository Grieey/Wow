package com.github.grieey.wow.constant

import com.github.grieey.coreui.CoreFragment
import com.github.grieey.wow.R
import com.github.grieey.wow.fragment.RadarFragment
import kotlin.reflect.KClass

/**
 * description: 页面路由
 * @date: 2021/2/8 10:47
 * @author: Grieey
 */
interface Router {
  companion object {
    val MAP = listOf(
      Pair("三角雷达坐标图", R.id.action_main_to_radar)
    )
  }
}