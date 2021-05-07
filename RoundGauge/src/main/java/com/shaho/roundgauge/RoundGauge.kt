package com.shaho.roundgauge

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.min


class RoundGauge(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private var mAnimator: ValueAnimator = ValueAnimator()
    private val mRectF: RectF = RectF()
    private val mCenterPoint: Point = Point()
    private val mDefaultSize = dipToPx(150f)
    private val mArcPaint: Paint = Paint()
    private val mBgArcPaint: Paint = Paint()
    private val mProgressArcPaint: Paint = Paint()

    private var mValue = 50f
    private val mMaxValue = 50f
    private var mPercent = 0f

    init {
        initAttrs(attrs)
        initPoint()
        setValue(mValue)
    }

    private fun initAttrs(attrs: AttributeSet?) {
//        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundGauge)
//
//        typedArray.recycle()
    }

    private fun initPoint() {
        mArcPaint.isAntiAlias=true
        mArcPaint.style = Paint.Style.STROKE
        mArcPaint.strokeWidth = 25F
        mArcPaint.strokeCap=Paint.Cap.ROUND
        mArcPaint.color = Color.BLACK

        mProgressArcPaint.isAntiAlias=true
        mProgressArcPaint.style = Paint.Style.STROKE
        mProgressArcPaint.strokeWidth = 25F
        mProgressArcPaint.strokeCap=Paint.Cap.ROUND
        mProgressArcPaint.color = Color.GREEN

        mBgArcPaint.isAntiAlias=true
        mBgArcPaint.style = Paint.Style.STROKE
        mBgArcPaint.strokeWidth = 50F
        mBgArcPaint.strokeCap=Paint.Cap.ROUND
        mBgArcPaint.color = Color.BLACK
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(measureView(widthMeasureSpec, mDefaultSize), measureView(heightMeasureSpec, mDefaultSize))
    }

//    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
//        super.onSizeChanged(w, h, oldw, oldh)
//    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawText(canvas)
        drawArc(canvas)
    }

//    override fun onDetachedFromWindow() {
//        super.onDetachedFromWindow()
//        // Release resources
//    }

    private fun drawText(canvas: Canvas?) {

    }

    private fun drawArc(canvas: Canvas?) {
        canvas?.save()

        val rectf = RectF(50F, 50F, 600F, 600F)
        canvas?.drawArc(rectf, 120F, 300F, false, mBgArcPaint)

        for (i in 0 until 8) {
            if ((45*i) > 150 && (45*i)<210)
                continue

            val startX: Float = 325F + Math.sin(Math.toRadians(45*i.toDouble())).toFloat() * (300 - 50)
            val startY: Float = 325F - Math.cos(Math.toRadians(45*i.toDouble())).toFloat() * (300 - 50)

            val stopX: Float = 325F + Math.sin(Math.toRadians(45*i.toDouble())).toFloat() * (300 - 100)
            val stopY: Float = 325F - Math.cos(Math.toRadians(45*i.toDouble())).toFloat() * (300 - 100)
            canvas?.drawLine(startX, startY, stopX, stopY, mArcPaint)
        }


        canvas?.drawArc(rectf, 120F, 300F*mPercent, false, mProgressArcPaint)

        canvas?.restore()
    }

    private fun measureView(measureSpec: Int, defaultSize: Int): Int {
        var result = defaultSize
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize
        } else if (specMode == MeasureSpec.AT_MOST) {
            result = min(result, specSize)
        }

        return result
    }

    private fun dipToPx(dip: Float): Int {
        val density = resources.displayMetrics.density
        return ((dip * density) + (0.5f * (if (dip >= 0) 1 else -1))).toInt()
    }

    fun setValue(value: Float) {
        var value = value
        if (value > mMaxValue) {
            value = mMaxValue
        }
        val start: Float = mPercent
        val end: Float = value / mMaxValue
        startAnimator(start, end, 5000)
    }

    private fun startAnimator(start: Float, end: Float, animTime: Long) {
        mAnimator = ValueAnimator.ofFloat(start, end)
        mAnimator.duration = animTime
        mAnimator.addUpdateListener { animation ->
            mPercent = animation.animatedValue as Float
            mValue = mPercent * mMaxValue

            invalidate()
        }
        mAnimator.start()
    }
}