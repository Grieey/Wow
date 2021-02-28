package com.github.grieey.wow.extension

import android.view.View

/**
 * description: View相关的扩展
 * @date: 2021/2/28 22:02
 * @author: Grieey
 */

fun View.setWidthInPx(width: Int) {
  layoutParams = layoutParams.apply {
    this.width = width
  }
}