package com.github.grieey.wow.view

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import com.github.grieey.core_ext.dp
import com.github.grieey.core_ext.float
import com.github.grieey.core_ext.int

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
) : View(context, attrs, defStyleAttr) {

  private var bgWidth = 0
    set(value) {
      field = value
      invalidate()
    }

  private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
  private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)

  private val animator by lazy {
    ObjectAnimator.ofInt(this, "bgWidth", 0, 0)
  }

  var hintStr: String = ""

  override fun onDraw(canvas: Canvas) {
    canvas.drawRoundRect(8.dp, 0F, 8.dp + bgWidth, height.float, 4.dp, 4.dp, bgPaint)
  }

  fun startAnimation(lastStr: String) {
    val lastWidth = textPaint.measureText(lastStr)
    val curWidth = textPaint.measureText(hintStr)
    animator.setIntValues(lastWidth.int, curWidth.int)
    animator.start()
  }

  fun stopAnimation() {
    animator.cancel()
  }

}