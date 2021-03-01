package com.github.grieey.wow.view

import android.animation.ObjectAnimator
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.setPadding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.github.grieey.core_ext.dp
import com.github.grieey.core_ext.int
import com.github.grieey.core_ext.safeLet
import com.github.grieey.wow.R
import com.github.grieey.wow.extension.applyColorTo
import com.github.grieey.wow.extension.applySizeTo
import com.github.grieey.wow.extension.setWidthInPx

/**
 * description: 仿滴滴的itemView
 *              这里的动画效果是，上一个向上消失，同时宽度向右增加，当增加到下一个可以容纳时，下一个出现
 *              应该是上一个消失和下一个出现平分了向右增加的宽度的时间。
 * @date: 2021/2/24 22:38
 * @author: Grieey
 */
class SafeHintView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

  companion object {
    private const val UPDATE_POSITION = 0x11
  }

  private val safeAdapter = Adapter()
  private val recyclerView = RecyclerView(context).apply {
    layoutManager = LayoutManager(context)
    this.adapter = safeAdapter
    R.dimen.small_padding applySizeTo this::setPadding
  }
  private val bgView = ImageView(context).apply {
    R.color.purple_200 applyColorTo this::setBackgroundColor
  }

  private val intervalHandler = object : Handler(Looper.getMainLooper()) {
    override fun handleMessage(msg: Message) {
      super.handleMessage(msg)
      if (msg.what == UPDATE_POSITION) {
        val nextPosition = (msg.obj as Int)
        recyclerView.smoothScrollToPosition(nextPosition)
      }
    }
  }

  private var curPosition = 0
  private var animatedPosition = -1

  var dataSource = emptyList<String>()
    set(value) {
      field = value
      safeAdapter.notifyDataSetChanged()
    }

  var textStyle: (TextView.() -> Unit)? = null

  var autoUpdateInterval = 1000L

  init {
    willNotDraw()
    addView(bgView, LayoutParams(MATCH_PARENT, MATCH_PARENT))
    addView(recyclerView, LayoutParams(WRAP_CONTENT, MATCH_PARENT))
  }

  private inner class LayoutManager @JvmOverloads constructor(
    context: Context,
    orientation: Int = RecyclerView.VERTICAL,
    reverseLayout: Boolean = false
  ) : LinearLayoutManager(context, orientation, reverseLayout) {
    private val snapHelper = LinearSnapHelper()
    private val smoothTime = 1000F
    private val animatior by lazy { ObjectAnimator.ofInt(0, 0) }

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

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State) {
      super.onLayoutChildren(recycler, state)
      if (childCount < 0 || state.isPreLayout || dataSource.isEmpty()) return

      // 这里会回调多次，但是curPosition都是+1，不是自增，所以短时间内handler收到的值都是一样的。
      intervalHandler.sendMessageDelayed(Message.obtain().apply {
        what = UPDATE_POSITION
        obj = curPosition + 1
      }, autoUpdateInterval)
    }

    override fun onScrollStateChanged(state: Int) {
      super.onScrollStateChanged(state)
      if (state == RecyclerView.SCROLL_STATE_IDLE) {
        val curView = snapHelper.findSnapView(this)
        curView?.let {
          // 修正position
          curPosition = getPosition(it)
        }
        intervalHandler.sendMessageDelayed(Message.obtain().apply {
          what = UPDATE_POSITION
          obj = ++curPosition
        }, autoUpdateInterval)
      }
    }

    override fun canScrollHorizontally() = false

    override fun canScrollVertically() = true

    override fun scrollVerticallyBy(
      dy: Int,
      recycler: RecyclerView.Recycler?,
      state: RecyclerView.State?
    ): Int {
      animationBg()
      return super.scrollVerticallyBy(dy, recycler, state)
    }

    private fun animationBg() {
      // 当curPosition 为 0 时，是第一次的滑动，此时滑动没有结束，还没有进行数据的修正，所以特殊处理
      // 其余的值在[onScrollStateChanged]中对position进行了修正。
      val next = if (curPosition == 0) 1 else curPosition
      val cur = next - 1
      val curView = findViewByPosition(cur)
      val nextView = findViewByPosition(next)
      safeLet(curView, nextView) { cv, nv ->
        if (animatior.isRunning || animatedPosition == next) return@safeLet
        animatior.apply {
          setIntValues(0, nv.width - cv.width)
          addUpdateListener {
            bgView.setWidthInPx(cv.width + it.animatedValue as Int + 16.dp.int)
          }
          duration = (smoothTime * 0.5).toLong()
          animatedPosition = next
          start()
        }
      }
    }
  }

  private inner class Adapter : RecyclerView.Adapter<Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
      Holder(TextView(context).apply {
        layoutParams = LayoutParams(WRAP_CONTENT, MATCH_PARENT)
        gravity = Gravity.CENTER_VERTICAL
        textStyle?.let(this::apply)
      })

    override fun onBindViewHolder(holder: Holder, position: Int) {
      holder.textView.text = dataSource[position % dataSource.size]
    }

    override fun getItemCount() = Int.MAX_VALUE

  }

  private class Holder(val textView: TextView) : RecyclerView.ViewHolder(textView)
}