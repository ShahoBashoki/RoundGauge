package com.shaho.roundgauge

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin


class RoundGauge(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private var progressBarAnimator: ValueAnimator = ValueAnimator()

    private var progressBarValue = 100f
    private val progressBarMaxValue = 100f
    private var progressBarPercent = 0f

    private val backgroundPaint: Paint = Paint()
    private val progressBarPaint: Paint = Paint()
    private val markerPaint: Paint = Paint()
    private val backgroundRectF: RectF = RectF()

    private var gaugeBackgroundColor: Int = Color.BLACK
    private var gaugeBackgroundStroke: Float = 50F
    private var gaugeProgressBarColor: Int = Color.GREEN
    private var gaugeProgressBarStroke: Float = 25F
    private var gaugeProgressAcceleration: Float = 0F
    private var gaugeProgressDuration: Int = 2000
    private var gaugeProgressBarConstantAcceleration: Boolean = true
    private var gaugeNumberOfMarkers: Int = 8
    private var gaugeMarkersStroke: Float = 15F
    private var gaugeMarkersLength: Int = 50
    private var gaugeMarkersColor: Int = Color.BLACK

    private var defaultSize = dipToPx()

    private var listener: OnChangeProgressBarListener? = null

    init {
        attrs?.let { itAttrs ->
            initAttrs(itAttrs)
        }
        initPoint()
//        setValue(progressBarValue)
    }

    private fun initAttrs(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundGauge)

        try {
            gaugeBackgroundColor = typedArray.getColor(R.styleable.RoundGauge_gaugeBackgroundColor, Color.BLACK)
            gaugeBackgroundStroke = typedArray.getFloat(R.styleable.RoundGauge_gaugeBackgroundStroke, 50F)
            gaugeProgressBarColor = typedArray.getColor(R.styleable.RoundGauge_gaugeProgressBarColor, Color.GREEN)
            gaugeProgressBarStroke = typedArray.getFloat(R.styleable.RoundGauge_gaugeProgressBarStroke, 25F)
            gaugeProgressAcceleration = typedArray.getFloat(R.styleable.RoundGauge_gaugeProgressAcceleration, 0F)
            gaugeProgressDuration = typedArray.getInt(R.styleable.RoundGauge_gaugeProgressDuration, 2000)
            gaugeProgressBarConstantAcceleration = typedArray.getBoolean(R.styleable.RoundGauge_gaugeProgressBarConstantAcceleration, true)
            gaugeNumberOfMarkers = typedArray.getInt(R.styleable.RoundGauge_gaugeNumberOfMarkers, 8)
            gaugeMarkersStroke = typedArray.getFloat(R.styleable.RoundGauge_gaugeMarkersStroke, 15F)
            gaugeMarkersLength = typedArray.getInt(R.styleable.RoundGauge_gaugeMarkersLength, 50)
            gaugeMarkersColor = typedArray.getColor(R.styleable.RoundGauge_gaugeMarkersColor, Color.BLACK)
        } finally {
            typedArray.recycle()
        }
    }

    private fun initPoint() {
        backgroundPaint.isAntiAlias = true
        backgroundPaint.style = Paint.Style.STROKE
        backgroundPaint.strokeWidth = gaugeBackgroundStroke
        backgroundPaint.strokeCap = Paint.Cap.ROUND
        backgroundPaint.color = gaugeBackgroundColor

        progressBarPaint.isAntiAlias = true
        progressBarPaint.style = Paint.Style.STROKE
        progressBarPaint.strokeWidth = gaugeProgressBarStroke
        progressBarPaint.strokeCap = Paint.Cap.ROUND
        progressBarPaint.color = gaugeProgressBarColor

        markerPaint.isAntiAlias = true
        markerPaint.style = Paint.Style.STROKE
        markerPaint.strokeWidth = gaugeMarkersStroke
        markerPaint.strokeCap = Paint.Cap.ROUND
        markerPaint.color = gaugeMarkersColor
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(measureView(widthMeasureSpec, defaultSize.toInt()), measureView(heightMeasureSpec, defaultSize.toInt()))
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        defaultSize = if (w < h) {
            w.toFloat()
        } else {
            h.toFloat()
        }

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawArc(canvas)
    }

//    override fun onDetachedFromWindow() {
//        super.onDetachedFromWindow()
//        // Release resources
//    }

    private fun drawArc(canvas: Canvas?) {
        listener?.onChangeValue(progressBarPercent)

        canvas?.save()

        val marginStroke = gaugeBackgroundStroke / 2
        val insideMarkerLineRadius = (defaultSize / 2) - (marginStroke * 2)
        val externalMarkerLineRadius = (defaultSize / 2) - gaugeMarkersLength - marginStroke - (gaugeMarkersStroke / 2)
        val markerLineRadius = (defaultSize / 2)

        backgroundRectF.left = marginStroke
        backgroundRectF.top = marginStroke
        backgroundRectF.right = defaultSize - marginStroke
        backgroundRectF.bottom = defaultSize - marginStroke

        canvas?.drawArc(backgroundRectF, 120F, 300F, false, backgroundPaint)

        val partOfDegrees = 360.0 / gaugeNumberOfMarkers
        for (i in 0 until gaugeNumberOfMarkers) {
            val degrees: Double = i * partOfDegrees
            if (degrees > 150 && degrees < 210)
                continue

            val radians: Double = Math.toRadians(degrees)

            val startX: Float = markerLineRadius + sin(radians).toFloat() * insideMarkerLineRadius
            val startY: Float = markerLineRadius - cos(radians).toFloat() * insideMarkerLineRadius

            val stopX: Float = markerLineRadius + sin(radians).toFloat() * externalMarkerLineRadius
            val stopY: Float = markerLineRadius - cos(radians).toFloat() * externalMarkerLineRadius
            canvas?.drawLine(startX, startY, stopX, stopY, markerPaint)
        }

        canvas?.drawArc(backgroundRectF, 120F, 300F * progressBarPercent, false, progressBarPaint)

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

    private fun dipToPx(dip: Float = 400F): Float {
        val density = resources.displayMetrics.density
        return (dip * density) + (0.5f * (if (dip >= 0) 1 else -1))
    }

    fun setValue(value: Float) {
        var newValue = value
        if (newValue > progressBarMaxValue) {
            newValue = progressBarMaxValue
        }
        val start: Float = progressBarPercent
        val end: Float = newValue / progressBarMaxValue
        startAnimator(start, end)
    }

    private fun startAnimator(start: Float, end: Float, animTime: Long = gaugeProgressDuration.toLong()) {
        progressBarAnimator = ValueAnimator.ofFloat(start, end)
        progressBarAnimator.duration = animTime
        progressBarAnimator.addUpdateListener { animation ->
            progressBarPercent = animation.animatedValue as Float
            progressBarValue = progressBarPercent * progressBarMaxValue

            invalidate()
        }
        progressBarAnimator.start()
    }

    fun setOnChangeProgressBarListener(listener: OnChangeProgressBarListener) {
        this.listener = listener
    }

    interface OnChangeProgressBarListener {
        fun onChangeValue(value: Float)
    }
}