package com.github.grieey.wow.widget.recyclerview

import android.content.Context
import android.util.DisplayMetrics
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView

/**
 * description: 自动翻页的layoutManager
 * @date: 2021/2/24 21:35
 * @author: Grieey
 */
class SafeHintLayoutManager @JvmOverloads constructor(
  context: Context,
  orientation: Int = RecyclerView.VERTICAL,
  reverseLayout: Boolean = false
) : LinearLayoutManager(context, orientation, reverseLayout) {

  // 用于进行手势滑动
  private val snapHelper = LinearSnapHelper()
  private val smoothTime = 150F

  override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams =
    RecyclerView.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)

  override fun onAttachedToWindow(view: RecyclerView?) {
    super.onAttachedToWindow(view)
    snapHelper.attachToRecyclerView(view)
  }

  override fun smoothScrollToPosition(
    recyclerView: RecyclerView,
    state: RecyclerView.State?,
    position: Int
  ) {
    val smoothScroller = object : LinearSmoothScroller(recyclerView.context) {
      override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
        return smoothTime / displayMetrics.densityDpi
      }
    }
    smoothScroller.targetPosition = position
    startSmoothScroll(smoothScroller)
  }

}