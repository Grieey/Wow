package com.github.grieey.wow.widget.recyclerview

import android.content.Context
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import androidx.core.view.get
import androidx.recyclerview.widget.*
import com.github.grieey.core_ext.dp
import com.github.grieey.core_ext.int
import com.github.grieey.core_ext.safeLet
import com.github.grieey.core_ext.setWidthAndHeightInPx
import java.lang.ref.WeakReference

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

  private val snapHelper = LinearSnapHelper()
  private val smoothTime = 150F
  private val weakContext: WeakReference<Context> = WeakReference(context)
  var bgView: View? = ImageView(context)
  private var scrolled1 = 0

  private var dataSource = emptyList<String>()

  override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams =
    RecyclerView.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)

  override fun onAttachedToWindow(view: RecyclerView?) {
    super.onAttachedToWindow(view)
    snapHelper.attachToRecyclerView(view)
//    safeLet(view, bgView) { rv, bg ->
//      val parent = rv.parent as ViewGroup
//      var index = 0
//      for (childIndex in 0 until parent.childCount) {
//        if (parent.getChildAt(childIndex) == rv) {
//          index = childIndex
//          break
//        }
//      }
//
//      parent.addView(bg, index)
//    }
    bgView = view
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

  override fun onAdapterChanged(
    oldAdapter: RecyclerView.Adapter<*>?,
    newAdapter: RecyclerView.Adapter<*>?
  ) {
    super.onAdapterChanged(oldAdapter, newAdapter)
    if (newAdapter is SafeHintAdapter) {
      dataSource = newAdapter.dataSource
    }
  }

  override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
    super.onLayoutChildren(recycler, state)
    if (state.isPreLayout || childCount < 0 || dataSource.isEmpty()) return
  }

  override fun scrollVerticallyBy(
    dy: Int,
    recycler: RecyclerView.Recycler?,
    state: RecyclerView.State?
  ): Int {
    val scrolled = super.scrollVerticallyBy(dy, recycler, state)
    scrolled1 += dy
//    measureAndLayout(scrolled1)
    bgView?.setWidthInPx((300.dp + dy).int)
    return scrolled
  }

//  private fun layoutBg() {
//    if (bgView == null) {
//      createBg()
//    }
//    measureAndLayout()
//  }
//
//  private fun createBg() {
//    bgView = ImageView(weakContext.get())
//    bgView?.let {
//      addView(it, 0)
//    }
//  }

  private fun measureAndLayout(dy: Int) {
    bgView?.let {
      // 计算anchorView
      val lastPosition = findFirstVisibleItemPosition()
      if (dataSource.size == 1) {
        it.layout(0, 0, 100, 100)
      } else {
        val curPosition = if (lastPosition == dataSource.lastIndex) 0 else lastPosition + 1
        val curView = findViewByPosition(curPosition)
        val lastView = findViewByPosition(lastPosition)


//        it.postDelayed({
//          it.setWidthAndHeightInPx(newWidth = (10.dp + dy * 5.dp).int)
        it.setWidthInPx((10.dp + dy).int)
        Log.d("YANGQ", "SafeHintLayoutManager::measureAndLayout~$dy ${it.width}")
//        }, 1000L)

//        safeLet(curView, lastView) { cur, last ->
//          val max = cur.width - last.width
//          Log.d("YANGQ", "SafeHintLayoutManager::measureAndLayout~ ${cur.width}-${last.width}")
//          val mid = height / 2F
//          val childMid = (getDecoratedTop(cur) + getDecoratedBottom(cur)) / 2F
//          val process = childMid / mid
//          val curWidth = it.width + max * process
//          it.setWidthAndHeightInPx(curWidth.int)
//        }
      }
    }
  }
}

fun View.setWidthInPx(width: Int) {
  layoutParams = layoutParams.apply {
    this.width = width
  }
}