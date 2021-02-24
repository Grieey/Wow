package com.github.grieey.wow.widget.recyclerview

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.grieey.wow.view.SafeHintView

/**
 * description: 仿滴滴的adapter
 * @date: 2021/2/24 22:34
 * @author: Grieey
 */
class SafeHintAdapter(private val context: Context) :
  RecyclerView.Adapter<SafeHintAdapter.SafeHintHolder>() {

  lateinit var dataSource: List<String>

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
    SafeHintHolder(SafeHintView(context))

  override fun onBindViewHolder(holder: SafeHintHolder, position: Int) {
    TODO("Not yet implemented")
  }

  override fun onViewAttachedToWindow(holder: SafeHintHolder) {
    super.onViewAttachedToWindow(holder)
    (holder.itemView as? SafeHintView)?.run {
      val curPos = dataSource.indexOf(this.hintStr)
      if (curPos == 0) {
        startAnimation(dataSource.last())
      } else {
        startAnimation(dataSource[curPos - 1])
      }
    }
  }

  override fun onViewDetachedFromWindow(holder: SafeHintHolder) {
    super.onViewDetachedFromWindow(holder)
    (holder.itemView as? SafeHintView)?.stopAnimation()
  }

  override fun getItemCount() = dataSource.size

  inner class SafeHintHolder(itemView: SafeHintView) : RecyclerView.ViewHolder(itemView)
}