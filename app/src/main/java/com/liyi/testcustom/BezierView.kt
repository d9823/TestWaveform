package com.liyi.testcustom

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View


/**
 * @author Created by ZhiBanQian on 2019/1/7.
 * Congratulations into the pit
 * May God bless you
 */
class BezierView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private var mClor: Int
    private var pointLevel: Int //设置有多少个不同的波动级别
    private var strokeWidth: Float = 10f
    private var percent: Float = 1f

    init {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.BezierView, 0, 0)
        mClor = a.getColor(R.styleable.BezierView_color, Color.RED)
        pointLevel = a.getInt(R.styleable.BezierView_pointLevel, 4)
        strokeWidth = a.getFloat(R.styleable.BezierView_strokeWidth, 10f)
        a?.recycle()
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        val path = Path()
        val pathSmall = Path()
        val listY = ArrayList<Int>()
        for (i in 1 .. pointLevel) { //去掉直线,从1开始
            val pow = Math.pow(-1.0, i.toDouble()) //做出上下的区别
            val temp = (i * measuredHeight / pointLevel) * percent
            val offset = temp * pow
            listY.add((offset + measuredHeight / 2).toInt())
        }
        val tempList = ArrayList<Int>().apply {
            addAll(listY)
            reverse()
        }
        for (i in tempList) {
            listY.add(measuredHeight - i)
        }
        path.moveTo(0f, measuredHeight / 2f)
        pathSmall.moveTo(0f, measuredHeight / 2f)
        for ((bezierIndexX, y) in listY.withIndex()) {
            path.quadTo(
                measuredWidth / (pointLevel * 4f) * (bezierIndexX * 2 + 1),
                y.toFloat(),
                measuredWidth / (pointLevel * 4f) * (bezierIndexX * 2 + 2),
                measuredHeight / 2f
            )
            pathSmall.quadTo(
                measuredWidth / (pointLevel * 4f) * (bezierIndexX * 2 + 1),
                y.toFloat() - 1.5f * (y.toFloat() - measuredHeight / 2f),
                measuredWidth / (pointLevel * 4f) * (bezierIndexX * 2 + 2),
                measuredHeight / 2f
            )
        }
        canvas?.apply {
            val paint = Paint().apply {
                style = Paint.Style.STROKE
                color = mClor
                strokeWidth = this@BezierView.strokeWidth
            }
//            path.addPath(pathSmall,measuredWidth / (pointLevel * 8f),0f) //第二条线可丑,怎么设计会好看点?
            canvas.drawPath(path, paint)
        }
        super.onDraw(canvas)
    }

    //    可以用来做声音波动效果
    fun setPercent(percent: Float) {
        if (this.percent >= percent + 0.2f) {
            this.percent -= 0.2f
        } else {
            when {
                percent >= 1f -> this.percent = 1f
                percent <= 0f -> this.percent = 0f
                else -> this.percent = percent
            }
        }
        postInvalidate()
    }

    val TAG = "BezierView"
}