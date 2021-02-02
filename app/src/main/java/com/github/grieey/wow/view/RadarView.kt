package com.github.grieey.wow.view

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.TypeEvaluator
import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import com.github.grieey.core_ext.click
import com.github.grieey.core_ext.dp
import com.github.grieey.core_ext.sp
import kotlin.math.sqrt

/**
 * description: 三维雷达View
 * @date: 2020/12/12 18:11
 * @author: Grieey
 */
class RadarView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

  // 背景颜色
  private val bgColor = Color.parseColor("#231F49")

  // 坐标轴线条的颜色
  private val lineColor = Color.parseColor("#68739F")

  // 文本的颜色
  private val textColor = Color.parseColor("#8C95DB")

  // 选中的数据源文本的背景色
  private val selectedBgColor = Color.parseColor("#4068739F")

  private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
  private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

  private val padding = 42.dp
  private val detailPadding = 8.dp

  // 坐标原点
  private val coordinateOrigin = PointF()

  // 坐标像素长度
  private var coordinateLength = 0F

  // x轴顶点
  private var xTop = PointF()

  // y轴顶点
  private var yTop = PointF()

  // z轴顶点
  private var zTop = PointF()

  private val trianglePath = Path()
  private val triangleTopTextBounds = Triple(Rect(), Rect(), Rect())

  // 下一个需要绘制显示的数据源索引
  private var prepare2ShowIndex: Int = 0
    set(value) {
      field = value
      invalidate()
    }

  // 被选中的分类的坐标最大值
  private var fraction: Float = 0F
    set(value) {
      field = value
      invalidate()
    }

  // 选择分类的索引, 正数为所选分类的索引值，负数为不选择
  private var selectedIndex: Int = -1
    set(value) {
      field = value
      invalidate()
    }
  private var change2Index = -1

  // 当两个分类改变时的坐标
  // 这个属性必须得是public才会生效
  var changingLoc: Triple<Double, Double, Double> = Triple(0.0, 0.0, 0.0)
    set(value) {
      field = value
      if (!field.isOrigin()) invalidate()
    }

  // 分类的视图缓存
  private val itemViews = mutableListOf<ItemView>()

  // 显示所有的分类动画
  private val showAllAnimator by lazy {
    ObjectAnimator.ofInt(this, "prepare2ShowIndex", 0, 0).apply {
      duration = 100
      doOnStart {
        if (selectedIndex >= 0) {
          if (selectedIndex == 0) selectedIndex =
            -dataSource.size else selectedIndex *= -1
          resetItemViewState(selectedIndex)
        }
      }
    }
  }

  // 显示缩略动画
  private val showBriefAnimator by lazy {
    ObjectAnimator.ofFloat(this, "fraction", 1F, 0F).apply {
      duration = animDuration
    }
  }

  // 显示详情动画
  private val showDetailAnimator by lazy {
    ObjectAnimator.ofFloat(this, "fraction", 0F, 1F).apply {
      duration = animDuration
    }
  }

  // 两次选择改变的动画
  private val changeAnimator by lazy {
    ObjectAnimator.ofObject(
      this,
      "changingLoc",
      TripleEvaluator(),
      Triple(0.0, 0.0, 0.0),
      Triple(0.0, 0.0, 0.0)
    ).apply {
      duration = animDuration
      doOnStart {
        resetItemViewState(change2Index)
      }
      doOnEnd {
        changingLoc = Triple(0.0, 0.0, 0.0)
        selectedIndex = change2Index
      }
    }
  }

  /**
   * 数据源
   */
  var dataSource: List<Data> = emptyList()
    set(value) {
      field = value
      showAllAnimator.setIntValues(0, field.lastIndex)
      removeAllViews()
      addView(TextView(context).apply {
        text = "CATEGORIES"
        textSize = 12.sp
        setTextColor(this@RadarView.textColor)
        click {
          startShowAllAnim()
        }
      })

      field.forEachIndexed { index, data ->
        val view = if (itemViews.isEmpty() || itemViews.lastIndex < index) {
          val temp = ItemView(data, context)
          itemViews.add(temp)
          temp
        } else {
          itemViews[index].apply {
            itemData = data
          }
        }

        view.click {
          if (selectedIndex < 0) {
            selectedIndex = index
            startShowDetailAnim()
          } else {
            changeAnimator.setObjectValues(
              dataSource[selectedIndex].values,
              dataSource[index].values
            )
            change2Index = index
            startChangeAnim()
          }
        }
        addView(view)
      }
    }

  /**
   * 动画时长
   */
  var animDuration: Long = 3500

  /**
   * x,y,z 轴对应的名字
   */
  var axisTitles = Triple("Tv", "Mobile Or Tablet", "Desktop")

  init {
    setBackgroundColor(bgColor)
    startShowAllAnim()
  }

  override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
    super.onSizeChanged(w, h, oldw, oldh)

    coordinateLength = width / 2F - padding

    coordinateOrigin.x = width / 2F
    coordinateOrigin.y = padding + coordinateLength

    val tops = Triple(100.0, 100.0, 100.0).convert2PointF()

    xTop = tops.first
    yTop = tops.second
    zTop = tops.third
  }

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)

    canvas.drawCoordinate()
    canvas.drawAxisTitle()

    // 绘制三角形
    dataSource.forEach {
      canvas.drawTriangle(
        it.values,
        it.color,
        it.color
      )
    }

    when {
      !changingLoc.isOrigin() -> {
        // 绘制改变中的三角形
        canvas.drawTriangle(
          changingLoc,
          dataSource[selectedIndex].heightLightColor,
          dataSource[selectedIndex].color,
          isFill = true
        )
        canvas.drawDetail()
      }
      selectedIndex >= 0 -> {
        // 绘制选中的三角形
        canvas.drawTriangle(
          dataSource[selectedIndex].values,
          dataSource[selectedIndex].heightLightColor,
          dataSource[selectedIndex].color,
          isFill = true
        )
        canvas.drawDetail()
      }
      else -> {
        // 绘制三角形
        dataSource
          // 改变排序，从已选中的分类开始绘制
          .sortedByDescending {
            dataSource.indexOf(it) == (if (selectedIndex == -dataSource.size) 0 else selectedIndex * -1)
          }
          .take(prepare2ShowIndex + 1)
          .forEach {
            canvas.drawTriangle(it.values, it.heightLightColor, it.color, isFill = true)
          }
      }
    }
  }

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    for (index in 0 until childCount) {
      val view = getChildAt(index)
      measureChild(view, widthMeasureSpec, heightMeasureSpec)
    }
  }

  override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
    if (changed) {
      var lastHeight = 0
      for (index in 0 until childCount) {
        val view = getChildAt(index)
        view.layout(
          (2 * detailPadding).toInt(),
          (measuredHeight / 2 + (index + 2) * detailPadding + lastHeight).toInt(),
          (2 * detailPadding).toInt() + view.measuredWidth,
          (measuredHeight / 2 + (index + 2) * detailPadding + lastHeight).toInt() + view.measuredHeight
        )

        lastHeight += view.measuredHeight
      }
    }
  }

  private fun Canvas.drawCoordinate() {
    paint.apply {
      color = lineColor
      strokeWidth = 2.dp
      style = Paint.Style.STROKE
    }

    drawLine(coordinateOrigin.x, coordinateOrigin.y, xTop.x, xTop.y, paint)
    drawLine(coordinateOrigin.x, coordinateOrigin.y, yTop.x, yTop.y, paint)
    drawLine(coordinateOrigin.x, coordinateOrigin.y, zTop.x, zTop.y, paint)

    paint.apply {
      color = lineColor
      style = Paint.Style.FILL
    }

    drawCircle(xTop.x, xTop.y, 3.dp, paint)
    drawCircle(yTop.x, yTop.y, 3.dp, paint)
    drawCircle(zTop.x, zTop.y, 3.dp, paint)
  }

  private fun Canvas.drawAxisTitle() {
    textPaint.apply {
      textSize = 12.dp
      color = textColor
      textAlign = Paint.Align.CENTER
    }

    val baseLineOffset = textPaint.fontMetrics.descent - textPaint.fontMetrics.ascent

    drawText(
      axisTitles.first,
      xTop.x,
      xTop.y - textPaint.fontMetrics.top + baseLineOffset,
      textPaint
    )
    drawText(axisTitles.second, yTop.x, yTop.y - baseLineOffset, textPaint)
    drawText(
      axisTitles.third,
      zTop.x,
      zTop.y - textPaint.fontMetrics.top + baseLineOffset,
      textPaint
    )
  }

  /**
   * 绘制三角形
   */
  private fun Canvas.drawTriangle(
    location: Triple<Double, Double, Double>,
    lineColor: Int,
    fillColor: Int,
    isFill: Boolean = false
  ) {
    val (x, y, z) = location.convert2PointF()

    paint.apply {
      color = lineColor
      style = Paint.Style.FILL
    }

    // 三角形顶点
    drawCircle(x.x, x.y, 5.dp, paint)
    drawCircle(y.x, y.y, 5.dp, paint)
    drawCircle(z.x, z.y, 5.dp, paint)


    paint.apply {
      color = lineColor
      style = Paint.Style.FILL_AND_STROKE
      strokeWidth = 2.dp
    }

    // 三角形三边
    drawLine(x.x, x.y, y.x, y.y, paint)
    drawLine(y.x, y.y, z.x, z.y, paint)
    drawLine(z.x, z.y, x.x, x.y, paint)

    if (isFill) {
      paint.apply {
        color = fillColor
        style = Paint.Style.FILL
      }

      trianglePath.reset()
      trianglePath.apply {
        moveTo(x.x, x.y)
        lineTo(y.x, y.y)
        lineTo(z.x, z.y)
      }
      drawPath(trianglePath, paint)
    }
  }

  private fun Canvas.drawDetail() {
    val selected = dataSource[selectedIndex]
    val (xTextBounds, yTextBounds, zTextBounds) = triangleTopTextBounds
    val (xText, yText, zText) = selected.values

    paint.apply {
      color = selected.heightLightColor
    }

    textPaint.apply {
      color = selected.heightLightColor
      textSize = 12.dp
      textAlign = Paint.Align.LEFT
    }

    textPaint.getTextBounds(
      xText.toString(),
      0,
      xText.toString().length,
      xTextBounds
    )

    textPaint.getTextBounds(
      yText.toString(),
      0,
      yText.toString().length,
      yTextBounds
    )

    textPaint.getTextBounds(
      zText.toString(),
      0,
      zText.toString().length,
      zTextBounds
    )

    // changingLoc非原点时，说明是移动中变化的三角形
    val (x, y, z) = (if (changingLoc.isOrigin()) selected.values else changingLoc).convert2PointF()

    val performDraw = { p: PointF, r: Rect, text: String ->
      // 3 * triangleTopDetailPadding 是左边一个圈的直径加上右边部分的padding，整个detail的组成是，一个大圆+文本+右边距
      val curWidth = (r.width() + 3 * detailPadding) * fraction
      val curHeight = (r.height() + detailPadding) * fraction

      // 绘制文本填充背景
      paint.style = Paint.Style.FILL_AND_STROKE
      paint.color = bgColor
      if (curWidth > (6 * detailPadding)) {
        drawRoundRect(
          p.x - detailPadding,
          p.y - curHeight / 2,
          p.x + curWidth - detailPadding,
          p.y + curHeight / 2,
          detailPadding * 5,
          detailPadding * 5,
          paint
        )
      } else {
        drawRoundRect(
          p.x - detailPadding * fraction,
          p.y - curHeight / 2,
          p.x + (curWidth - detailPadding) * fraction,
          p.y + curHeight / 2,
          detailPadding * 5,
          detailPadding * 5,
          paint
        )
      }

      // 绘制文本框
      paint.style = Paint.Style.STROKE
      paint.color = selected.heightLightColor
      if (curWidth > (6 * detailPadding)) {
        drawRoundRect(
          p.x - detailPadding,
          p.y - curHeight / 2,
          p.x + curWidth - detailPadding,
          p.y + curHeight / 2,
          detailPadding * 5,
          detailPadding * 5,
          paint
        )
      } else {
        drawRoundRect(
          p.x - detailPadding * fraction,
          p.y - curHeight / 2,
          p.x + (curWidth - detailPadding) * fraction,
          p.y + curHeight / 2,
          detailPadding * 5,
          detailPadding * 5,
          paint
        )
      }

      paint.style = Paint.Style.FILL
      // 绘制左边的实心点
      drawCircle(p.x, p.y, detailPadding / 2, paint)

      // 绘制文本
      val measuredWidth = floatArrayOf(0f)
      val maxWidth = if (fraction > 0.98) {
        // 加上2dp是因为完全按照curWidth - 3 * triangleTopDetailPadding这个值是一个闭区间，会导致最后一个字符的个数无法被算上
        curWidth - 3 * detailPadding + 2.dp
      } else {
        curWidth - 3 * detailPadding
      }

      val count = textPaint.breakText(
        text,
        0,
        text.length,
        true,
        maxWidth,
        measuredWidth
      )
      drawText(
        text,
        0,
        count,
        p.x + detailPadding,
        p.y - (textPaint.fontMetrics.descent + textPaint.fontMetrics.ascent) / 2F,
        textPaint
      )
    }

    performDraw(x, xTextBounds, xText.toString())
    performDraw(y, yTextBounds, yText.toString())
    performDraw(z, zTextBounds, zText.toString())
  }

  private fun Triple<Double, Double, Double>.isOrigin() =
    first == 0.0 && second == 0.0 && third == 0.0

  /**
   * 将x,y,z坐标转为像素点坐标
   */
  private fun Triple<Double, Double, Double>.convert2PointF(): Triple<PointF, PointF, PointF> {

    val x = PointF()
    val y = PointF()
    val z = PointF()

    x.x = coordinateOrigin.x + first.toFloat() / 100 * coordinateLength * 0.5F * sqrt(3F)
    x.y = coordinateOrigin.y + first.toFloat() / 100 * coordinateLength * 0.5F
    y.x = coordinateOrigin.x
    y.y = coordinateOrigin.y - second.toFloat() / 100 * coordinateLength
    z.x = coordinateOrigin.x - third.toFloat() / 100 * coordinateLength * 0.5F * sqrt(3F)
    z.y = coordinateOrigin.y + third.toFloat() / 100 * coordinateLength * 0.5F

    return Triple(x, y, z)
  }

  /**
   * 开始显示所有分类的动画
   */
  private fun startShowAllAnim() {
    if (selectedIndex >= 0) {
      AnimatorSet().apply {
        play(showAllAnimator).after(showBriefAnimator)
        start()
      }
    } else {
      showAllAnimator.start()
    }
  }

  /**
   * 开始详情动画
   */
  private fun startShowDetailAnim() {
    showDetailAnimator.start()
  }

  private fun startShowBriefAnim() {
    showBriefAnimator.start()
  }

  /**
   * 开始改变分类的动画
   */
  private fun startChangeAnim() {
    AnimatorSet().apply {
      duration = animDuration
      play(showDetailAnimator).after(changeAnimator)
      play(changeAnimator).with(showBriefAnimator)
      start()
    }
  }

  private fun resetItemViewState(selectedIndex: Int) {
    itemViews.forEachIndexed { index, view ->
      view.isSelected = index == selectedIndex
    }
  }

  private inner class TripleEvaluator : TypeEvaluator<Triple<Double, Double, Double>> {
    override fun evaluate(
      fraction: Float,
      startValue: Triple<Double, Double, Double>,
      endValue: Triple<Double, Double, Double>
    ): Triple<Double, Double, Double> {
      val (eX, eY, eZ) = endValue
      val (sX, sY, sZ) = startValue
      return Triple(
        sX + (eX - sX) * fraction,
        sY + (eY - sY) * fraction,
        sZ + (eZ - sZ) * fraction
      )
    }

  }

  private inner class ItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
  ) : View(context, attrs, defStyleAttr) {

    constructor(data: Data, context: Context) : this(context) {
      itemData = data
    }

    private val titleBounds = Rect()
    private var radius = 0.8F * detailPadding
      set(value) {
        field = value
        invalidate()
      }
    private val animator by lazy {
      ObjectAnimator.ofFloat(
        this,
        "radius",
        0.8F * detailPadding,
        1.2F * detailPadding
      )
    }

    lateinit var itemData: Data

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
      textPaint.textSize = 12.sp
      textPaint.getTextBounds(itemData.title, 0, itemData.title.length, titleBounds)
      val width = 6 * detailPadding + titleBounds.width()
      // TODO 优化高度的自适应
      val height = 4 * detailPadding
      setMeasuredDimension(width.toInt(), height.toInt())
    }

    override fun onDraw(canvas: Canvas) {
      super.onDraw(canvas)

      // 绘制选中的背景
      if (isSelected) {
        paint.color = selectedBgColor
        paint.style = Paint.Style.FILL
        canvas.drawRoundRect(
          0F,
          0F,
          width.toFloat(),
          height.toFloat(),
          height / 2F,
          height / 2F,
          paint
        )
      }

      // 左侧的小圆
      paint.color = itemData.heightLightColor
      canvas.drawCircle(
        2F * detailPadding,
        height / 2F,
        radius,
        paint
      )

      // title文本
      textPaint.color = if (isSelected) itemData.heightLightColor else textColor
      textPaint.textAlign = Paint.Align.LEFT
      canvas.drawText(
        itemData.title,
        0,
        itemData.title.length,
        4 * detailPadding,
        height / 2 - (textPaint.fontMetrics.descent + textPaint.fontMetrics.ascent) / 2F,
        textPaint
      )
    }

    override fun setSelected(selected: Boolean) {
      super.setSelected(selected)
      if (!selected) {
        radius = 0.8F * detailPadding
      } else {
        animator.start()
      }
    }
  }

  data class Data(
    /** 标题 */
    val title: String,
    /** 高亮颜色值 */
    val heightLightColor: Int,
    /** 普通颜色 */
    val color: Int,
    /** x, y, z 的坐标值*/
    val values: Triple<Double, Double, Double>
  )
}