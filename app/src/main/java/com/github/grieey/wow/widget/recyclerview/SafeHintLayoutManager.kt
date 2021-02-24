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
 *              重新写个思路，还是在onLayoutChildren的方法中动手脚，因为recyclerview的滑动过程就是不停的调用该方法
 *              所以，item仅为文字的view，在layoutManager中增加一个bgView来模拟动画的背景动画。可以参考epoxy的吸顶逻辑
 *              同时，参考picker中对动画开始和结束的控制逻辑来控制bgView的开始和结束。
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