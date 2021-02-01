package com.github.grieey.wow.items

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import android.widget.TextView
import com.airbnb.epoxy.ModelProp
import com.airbnb.epoxy.ModelView
import com.airbnb.epoxy.TextProp
import com.github.grieey.core_ext.click
import com.github.grieey.core_ext.dp
import com.github.grieey.core_ext.int

/**
 * description: 仅文字的item
 * @date: 2021/2/1 17:36
 * @author: Grieey
 */
@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
class TextItem @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

  private val textView = TextView(context)
  private var target: Int = 0

  init {
    this.addView(
      textView,
      LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
        gravity = Gravity.CENTER_HORIZONTAL
        setMargins(8.dp.int, 8.dp.int, 8.dp.int, 8.dp.int)
      }
    )
  }

  @ModelProp(ModelProp.Option.IgnoreRequireHashCode)
  fun setStyle(style: (TextView.() -> Unit)?) {
    style?.let(textView::apply)
  }

  @ModelProp
  fun setTarget(id: Int) {
    target = id
  }

  @TextProp
  fun setText(text: CharSequence) {
    textView.text = text
  }

  @ModelProp(ModelProp.Option.IgnoreRequireHashCode)
  fun setClick(block: (view: View, id: Int) -> Unit) {
    textView.click {
      block(it, target)
    }
  }
}