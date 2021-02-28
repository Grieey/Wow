package com.github.grieey.wow.extension

import com.github.grieey.core_ext.int
import com.github.grieey.coreui.CoreApplication.Companion.INSTANCE

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
    INSTANCE.getColor(this)
  }
    .getOrNull()
}

fun Int.getSize(): Int? {
  return this.runCatching {
    INSTANCE.resources.getDimension(this).int
  }
    .getOrNull()
}

inline infix fun Int.applyColorTo(target: (Int) -> Unit) {
  getColor()?.let(target)
}

inline infix fun Int.applySizeTo(target: (Int) -> Unit) {
  getSize()?.let(target)
}