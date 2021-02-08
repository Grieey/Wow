package com.github.grieey.wow.extension

import android.app.Application
import android.content.res.Resources
import com.github.grieey.coreui.CoreApplication
import com.github.grieey.wow.App

/**
 * description: 资源扩展
 * @date: 2021/2/8 11:52
 * @author: Grieey
 */

/**
 * 将资源获取的异常屏蔽转为null返回
 */
fun Int.getColor(): Int? {
  return this.runCatching {
    App.INSTANCE.getColor(this)
  }
    .getOrNull()
}

inline infix fun Int.applyColorTo(target: (Int) -> Unit) {
  getColor()?.let(target)
}