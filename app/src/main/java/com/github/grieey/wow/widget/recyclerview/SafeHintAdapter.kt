//package com.github.grieey.wow.widget.recyclerview
//
//import android.animation.ValueAnimator
//import android.content.Context
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import androidx.recyclerview.widget.RecyclerView
//import com.github.grieey.core_ext.dp
//import com.github.grieey.core_ext.int
//
///**
// * description: 仿滴滴的adapter
// * @date: 2021/2/24 22:34
// * @author: Grieey
// */
//class SafeHintAdapter(private val context: Context) :
//  RecyclerView.Adapter<SafeHintAdapter.SafeHintHolder>() {
//
//  lateinit var dataSource: List<String>
//
//  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SafeHintHolder {
//    return SafeHintHolder(
//      ItemSafeHintBinding.inflate(
//        LayoutInflater.from(parent.context),
//        parent,
//        false
//      )
//    )
//  }
//
//
//  override fun onBindViewHolder(holder: SafeHintHolder, position: Int) {
//    holder.itemBinding.itemSafeHintTv.text = dataSource[position]
////    holder.startAnimation()
//  }
//
//  override fun onViewDetachedFromWindow(holder: SafeHintHolder) {
//    super.onViewDetachedFromWindow(holder)
//    holder.stopAnimation()
//  }
//
//  override fun getItemCount() = dataSource.size
//
//  inner class SafeHintHolder(val itemBinding: ItemSafeHintBinding) :
//    RecyclerView.ViewHolder(itemBinding.root) {
//
//    private var animator: ValueAnimator? = null
//
//    fun startAnimation() {
//      val cur = itemBinding.itemSafeHintTv.width
//      animator = ValueAnimator.ofFloat(0F, 1F)
//        .apply {
//          addUpdateListener {
//            val process = it.animatedValue as Float
//            Log.d("YANGQ", "SafeHintHolder::startAnimation~ $process")
//            itemBinding.itemSafeHintTv.setWidthAndHeightInPx(newWidth = cur + (process * 20.dp).int)
//          }
//          start()
//        }
//
//    }
//
//    fun stopAnimation() {
//      animator?.cancel()
//    }
//  }
//}