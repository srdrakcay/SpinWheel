package com.serdar.whell

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import kotlin.math.cos
import kotlin.math.sin

class CustomSpinWheelView : View {
    private var range = RectF()
    private var radius = 20
    private var spinArcPaint: Paint? = null
    private var backgroundPaint: Paint? = null
    private var textPaint: Paint? = null
    private val startAngle = 0f
    private var center = 0
    private var circleCenter = 0
    private var padding = 0
    private var targetIndex = 0
    private var roundOfNumber = 0
    private var isRunning = false
    private val desiredSpSize = 18f
    private var drawableCenterImage: Int? = null
    private var setSpinSpecialImage: Drawable? = null
    private var textColor = Color.WHITE
    private var spinWheelItemList: List<WheelItem>? = null
    private var spinRotateListener: SpinWheelRotateListener? = null
    private val strokePaint = Paint()
    private val respinTextPaint = Paint()
    private val desiredRespinSpSize = 20f

    companion object {
        const val BITMAP_CONVERT_OTHERS_POS = 1.4
        const val BITMAP_CONVERT_POS = 1.6
        const val BITMAP_CONVERT_STAR = 1.2
        const val CIRCLE_CIRCUMFERENCE = 360
        const val LETTER_SPACING = 0.6
        const val HALF_ALL = 2
    }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    fun setSpinRotateListener(listener: SpinWheelRotateListener?) {
        spinRotateListener = listener
    }

    private fun init() {
        spinArcPaint = Paint()
        spinArcPaint?.isAntiAlias = true
        spinArcPaint?.isDither = true
        textPaint = Paint()
        val screenSize = resources.displayMetrics
        val screenDensity = screenSize.density
        val textSizeInPixels = (desiredSpSize * screenDensity)

        textPaint?.textSize = textSizeInPixels
        textPaint?.letterSpacing = LETTER_SPACING.toFloat()
        textPaint?.style = Paint.Style.FILL_AND_STROKE
        range = RectF(
            padding.toFloat(),
            padding.toFloat(),
            (padding + radius).toFloat(),
            (padding + radius).toFloat()
        )
        strokePaint.isAntiAlias = true
        strokePaint.color = Color.BLACK
        strokePaint.style = Paint.Style.STROKE
        strokePaint.strokeWidth = 5f
        val textSizeInPixelsRespin = (desiredRespinSpSize * screenDensity)
        respinTextPaint.textSize = textSizeInPixelsRespin
        respinTextPaint.style = Paint.Style.FILL_AND_STROKE
        respinTextPaint.color = Color.WHITE
        roundOfNumber = spinWheelItemList?.size ?: 0
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        init()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawBackgroundColor(canvas)
        drawOutsideBitmap(canvas)
        var tmpAngle = startAngle
        val sweepAngle = (CIRCLE_CIRCUMFERENCE / spinWheelItemList!!.size).toFloat()
        for (i in spinWheelItemList!!.indices) {
            setTextPaint(i)
            spinArcPaint!!.color = spinWheelItemList!![i].color
            drawArc(canvas, tmpAngle, sweepAngle)
            drawSpinProductImage(spinWheelItemList, i, canvas, tmpAngle)
            drawVerticalReText(i, canvas, tmpAngle, sweepAngle)
            drawStrokeArc(canvas, tmpAngle, sweepAngle)
            tmpAngle += sweepAngle
        }
        drawCenterImage(canvas)
    }

    private fun drawOutsideBitmap(canvas: Canvas) {
        val options = BitmapFactory.Options()
        options.inSampleSize = HALF_ALL
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_bg_wheel, options)
        bitmap.runIfSafe {
            val scaledBitmap = Bitmap.createScaledBitmap(
                it,
                center + radius,
                center + radius,
                true
            )
            val x = center
            val y = center
            val bitmapWidth = scaledBitmap.width
            val bitmapHeight = scaledBitmap.height
            val left = x - bitmapWidth / HALF_ALL
            val top = y - bitmapHeight / HALF_ALL
            val right = left + bitmapWidth
            val bottom = top + bitmapHeight
            val destRect = Rect(left, top, right, bottom)
            canvas.drawBitmap(scaledBitmap, null, destRect, null)
        }
    }

    private fun drawStrokeArc(canvas: Canvas, tmpAngle: Float, sweepAngle: Float) {
        canvas.drawArc(range, tmpAngle, sweepAngle, true, strokePaint)
    }

    private fun drawVerticalReText(i: Int, canvas: Canvas, tmpAngle: Float, sweepAngle: Float) {
        if (i == 3) {
            drawVerticalText(canvas, tmpAngle, sweepAngle, spinWheelItemList!![i].credit)
        } else {
            drawText(canvas, tmpAngle, sweepAngle, spinWheelItemList!![i].credit)
        }
    }

    private fun drawCenterImage(canvas: Canvas) {
        val bitmap = drawableCenterImage?.let {
            BitmapFactory.decodeResource(
                resources,
                it
            )
        }
        bitmap.runIfSafe {
            canvas.drawBitmap(
                it,
                (measuredWidth / HALF_ALL - it.width / HALF_ALL).toFloat(),
                (measuredHeight / HALF_ALL - it.height / HALF_ALL).toFloat(),
                null
            )
        }
    }

    private fun drawSpinProductImage(
        spinWheelItemList: List<WheelItem>?,
        i: Int,
        canvas: Canvas,
        tmpAngle: Float
    ) {
        spinWheelItemList?.let { data ->
            if (i == 5) {
                BitmapFactory.decodeResource(
                    resources,
                    spinWheelItemList[i].iconRes
                ).runIfSafe {
                    drawStarImage(canvas, tmpAngle, it)
                }
                val specialImage = setSpinSpecialImage?.toBitmap()
                specialImage.runIfSafe {
                    drawImage(canvas, tmpAngle, it)
                }
            } else if (i != 3) {
                BitmapFactory.decodeResource(resources, data[i].iconRes).runIfSafe {
                    drawOthersImage(canvas, tmpAngle, it)
                }
            }
        }
    }

    private fun drawArc(canvas: Canvas, tmpAngle: Float, sweepAngle: Float) {
        canvas.drawArc(range, tmpAngle, sweepAngle, true, spinArcPaint!!)
    }


    private fun setTextPaint(i: Int) {
        when (i) {
            1 -> {
                textPaint?.color = Color.WHITE
            }

            5 -> {
                textPaint?.color = Color.WHITE
            }

            else -> {
                textPaint?.color = Color.BLACK
            }


        }
    }

    private fun drawVerticalText(canvas: Canvas, tmpAngle: Float, sweepAngle: Float, text: String) {
        val path = Path()
        val angle = tmpAngle + sweepAngle / HALF_ALL
        val x = center + radius / HALF_ALL * cos(Math.toRadians(angle.toDouble())).toFloat()
        val y = center + radius / HALF_ALL * sin(Math.toRadians(angle.toDouble())).toFloat()
        path.moveTo(x, y)
        path.lineTo(x + CIRCLE_CIRCUMFERENCE, y + text.length * 30)
        canvas.drawTextOnPath(
            text,
            path,
            text.length.toFloat() * 15 / HALF_ALL,
            0f,
            respinTextPaint
        )
    }

    private fun drawBackgroundColor(canvas: Canvas) {
        backgroundPaint = Paint()
        backgroundPaint?.color = Color.BLACK
        canvas.drawCircle(
            center.toFloat(), center.toFloat(), center.toFloat(),
            backgroundPaint!!
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = measuredWidth.coerceAtMost(measuredHeight)
        padding = if (paddingLeft == 0) 50 else paddingLeft
        radius = width - padding * HALF_ALL
        center = width / HALF_ALL
        circleCenter = measuredHeight
        setMeasuredDimension(width, width)
    }

    private fun drawOthersImage(canvas: Canvas, tmpAngle: Float, bitmap: Bitmap) {
        val imgWidth = radius / spinWheelItemList!!.size
        val angle =
            ((tmpAngle + CIRCLE_CIRCUMFERENCE / spinWheelItemList!!.size / HALF_ALL) * Math.PI / 180).toFloat()
        val x =
            (center + radius / BITMAP_CONVERT_OTHERS_POS / HALF_ALL * cos(angle.toDouble())).toInt()
        val y =
            (center + radius / BITMAP_CONVERT_OTHERS_POS / HALF_ALL * sin(angle.toDouble())).toInt()
        val rect = Rect(
            x - imgWidth / HALF_ALL,
            y - imgWidth / HALF_ALL,
            x + imgWidth / HALF_ALL,
            y + imgWidth / HALF_ALL
        )
        canvas.drawBitmap(bitmap, null, rect, null)
    }

    private fun drawImage(canvas: Canvas, tmpAngle: Float, bitmap: Bitmap) {
        val imgWidth = radius / spinWheelItemList!!.size
        val angle =
            ((tmpAngle + CIRCLE_CIRCUMFERENCE / spinWheelItemList!!.size / HALF_ALL) * Math.PI / 180).toFloat()
        val x = (center + radius / BITMAP_CONVERT_POS / HALF_ALL * cos(angle.toDouble())).toInt()
        val y = (center + radius / BITMAP_CONVERT_POS / HALF_ALL * sin(angle.toDouble())).toInt()
        val rect = Rect(
            x - imgWidth / HALF_ALL,
            y - imgWidth / HALF_ALL,
            x + imgWidth / HALF_ALL,
            y + imgWidth / HALF_ALL
        )
        canvas.drawBitmap(bitmap, null, rect, null)
    }

    private fun drawStarImage(canvas: Canvas, tmpAngle: Float, bitmap: Bitmap) {
        val imgWidth = radius / spinWheelItemList!!.size
        val angle =
            ((tmpAngle + CIRCLE_CIRCUMFERENCE / spinWheelItemList!!.size / HALF_ALL) * Math.PI / 180).toFloat()
        val x = (center + radius / BITMAP_CONVERT_STAR / HALF_ALL * cos(angle.toDouble())).toInt()
        val y = (center + radius / BITMAP_CONVERT_STAR / HALF_ALL * sin(angle.toDouble())).toInt()
        val rect = Rect(
            x - imgWidth / HALF_ALL,
            y - imgWidth / HALF_ALL,
            x + imgWidth / HALF_ALL,
            y + imgWidth / HALF_ALL
        )
        canvas.drawBitmap(bitmap, null, rect, null)
    }


    private fun drawText(canvas: Canvas, tmpAngle: Float, sweepAngle: Float, mStr: String) {
        val path = Path()
        path.addArc(range, tmpAngle, sweepAngle)
        val textWidth = textPaint!!.measureText(mStr)
        val hOffset =
            (radius * Math.PI / spinWheelItemList!!.size / HALF_ALL - textWidth / HALF_ALL).toInt()
        val vOffset = radius / 3
        canvas.drawTextOnPath(
            mStr, path, hOffset.toFloat(), (vOffset - 40).toFloat(),
            textPaint!!
        )
    }

    private val angleOfIndexTarget: Float
        get() {
            val tempIndex = if (targetIndex == 0) 1 else targetIndex
            return (CIRCLE_CIRCUMFERENCE / spinWheelItemList!!.size * tempIndex).toFloat()
        }

    fun setRound(numberOfRound: Int) {
        roundOfNumber = numberOfRound
    }

    fun setData(spinWheelItem: List<WheelItem>?) {
        spinWheelItemList = spinWheelItem
        invalidate()
    }

    fun setSpinSpecialImage(drawable: Drawable) {
        setSpinSpecialImage = drawable
        invalidate()
    }

    fun setSpinCenterImage(drawable: Int?) {
        drawableCenterImage = drawable
        invalidate()
    }

    fun setSpinTextColor(color: Int) {
        textColor = color
        invalidate()
    }

    fun startRotate(index: Int) {
        targetIndex = index
        rotation = 0f
        val targetAngle =
            CIRCLE_CIRCUMFERENCE * roundOfNumber + 270 - angleOfIndexTarget + CIRCLE_CIRCUMFERENCE / spinWheelItemList!!.size / 2
        animate()
            .setInterpolator(DecelerateInterpolator())
            .setDuration(roundOfNumber * 250 + 2000L)
            .setListener(object : WheelContract() {
                override fun onAnimationStart(animation: Animator) {
                    isRunning = true
                    setLayerType(LAYER_TYPE_HARDWARE, spinArcPaint)
                    setLayerType(LAYER_TYPE_HARDWARE, strokePaint)
                }

                override fun onAnimationEnd(animation: Animator) {
                    isRunning = false
                    if (spinRotateListener != null) {
                        spinRotateListener?.rotateDone(targetIndex)
                    }
                    setLayerType(LAYER_TYPE_NONE, spinArcPaint)
                    setLayerType(LAYER_TYPE_NONE, strokePaint)
                }

            })
            .rotation(targetAngle)
            .start()
    }

    @SuppressLint("ObjectAnimatorBinding")
    fun setCursorAnimate(cursor: ImageView) {
        val rotateRight = ObjectAnimator.ofFloat(cursor, "rotation", 15f, 0f)
        val rotateLeft = ObjectAnimator.ofFloat(cursor, "rotation", 0f, -15f)
        rotateRight.duration = 1000
        rotateLeft.duration = 1000
        val animatorSet = AnimatorSet()
        animatorSet.playSequentially(rotateRight, rotateLeft)
        animatorSet.start()
        animatorSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator) {
                animatorSet.start()
                isRunning = true
            }

            override fun onAnimationEnd(p0: Animator) {
                isRunning = false
                p0.cancel()
            }

            override fun onAnimationCancel(p0: Animator) {
                animatorSet.start()
            }

            override fun onAnimationRepeat(p0: Animator) {
                animatorSet.start()
            }
        })
    }
}
