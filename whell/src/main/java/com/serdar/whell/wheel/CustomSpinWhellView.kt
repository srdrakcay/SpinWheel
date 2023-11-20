package com.serdar.whell.wheel

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
import com.serdar.whell.R
import kotlin.math.cos
import kotlin.math.sin

class CustomSpinWhellView : View {
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
    private var roundOfNumber = 4
    private var isRunning = false
    private var defaultBackgroundColor = -3
    private var drawableCenterImage: Int? = null
    private var setSpinSpecialImage: Int? = null
    private var textColor = Color.WHITE
    private var spinWheelItemList: List<WheelItem>? = null
    private var spinRotateListener: SpinWheelRotateListener? = null
    private val strokePaint = Paint()
    private val respinTextPaint = Paint()

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
        val desiredSpSize = 18f
        val textSizeInPixels = (desiredSpSize * screenDensity)

        textPaint?.textSize = textSizeInPixels
        textPaint?.letterSpacing = 0.6.toFloat()
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
        val desiredRespinSpSize = 20f
        val textSizeInPixelsRespin = (desiredRespinSpSize * screenDensity)
        respinTextPaint.textSize = textSizeInPixelsRespin
        respinTextPaint.style = Paint.Style.FILL_AND_STROKE
        respinTextPaint.color = Color.WHITE
    }

    fun setData(spinWheelItem: List<WheelItem>?) {
        spinWheelItemList = spinWheelItem
        invalidate()
    }

    fun setSpinBackgroundColor(color: Int) {
        defaultBackgroundColor = color
        invalidate()
    }

    fun setSpinSpecialImage(drawable: Int?) {
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

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        init()
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawBackgroundColor(canvas, Color.BLACK)
        drawOutsideBitmap(canvas)
        var tmpAngle = startAngle
        val sweepAngle = (360 / spinWheelItemList!!.size).toFloat()
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
        options.inSampleSize = 2
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_bg_wheel, options)
        bitmap.runIfSafe {
            val scaledBitmap = Bitmap.createScaledBitmap(
                it,
                center + radius.toInt(),
                center + radius.toInt(),
                true
            )
            val x = center
            val y = center
            val bitmapWidth = scaledBitmap.width
            val bitmapHeight = scaledBitmap.height
            val left = x - bitmapWidth / 2
            val top = y - bitmapHeight / 2
            val right = left + bitmapWidth
            val bottom = top + bitmapHeight
            val destRect = Rect(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
            canvas.drawBitmap(scaledBitmap, null, destRect, null)
        }
    }

    private fun drawStrokeArc(canvas: Canvas, tmpAngle: Float, sweepAngle: Float) {
        canvas.drawArc(range, tmpAngle, sweepAngle, true, strokePaint)
    }

    private fun drawVerticalReText(i: Int, canvas: Canvas, tmpAngle: Float, sweepAngle: Float) {
        if (i == 3) {
            drawVerticalText(canvas, tmpAngle, sweepAngle, spinWheelItemList!![i].amount)
        } else {
            drawText(canvas, tmpAngle, sweepAngle, spinWheelItemList!![i].amount)
        }

    }
    private fun drawCenterImage(canvas: Canvas) {
        val bitmap= drawableCenterImage?.let {
            BitmapFactory.decodeResource(
                resources,
                it
            )
        }
        bitmap.runIfSafe {
            canvas.drawBitmap(
                it,
                (measuredWidth / 2 - it.width / 2).toFloat(),
                (measuredHeight / 2 - it.height / 2).toFloat(),
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
        spinWheelItemList?.let {
            if (i == 5) {
                BitmapFactory.decodeResource(
                    resources,
                    spinWheelItemList[i].iconRes
                ).runIfSafe {
                    drawStarImage(canvas, tmpAngle, it)
                }
                val bitmap= setSpinSpecialImage?.let { it1 ->
                    BitmapFactory.decodeResource(
                        resources,
                        it1
                    )
                }
                bitmap.runIfSafe {
                    drawImage(canvas, tmpAngle, it)
                }
            } else if (i != 3) {
                BitmapFactory.decodeResource(resources, it[i].iconRes).runIfSafe {
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
        val angle = tmpAngle + sweepAngle / 2
        val x = center + radius / 2 * cos(Math.toRadians(angle.toDouble())).toFloat()
        val y = center + radius / 2 * sin(Math.toRadians(angle.toDouble())).toFloat()
        path.moveTo(x, y)
        path.lineTo(x + 350, y + text.length * 30)
        canvas.drawTextOnPath(text, path, text.length.toFloat() * 15 / 2, 0f, respinTextPaint)
    }

    private fun drawBackgroundColor(canvas: Canvas, color: Int) {
        if (color == -1) return
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
        radius = width - padding * 2
        center = width / 2
        circleCenter = measuredHeight
        setMeasuredDimension(width, width)
    }

    private fun drawOthersImage(canvas: Canvas, tmpAngle: Float, bitmap: Bitmap) {
        val imgWidth = radius / spinWheelItemList!!.size
        val angle = ((tmpAngle + 360 / spinWheelItemList!!.size / 2) * Math.PI / 180).toFloat()
        val x = (center + radius / 1.4 / 2 * cos(angle.toDouble())).toInt()
        val y = (center + radius / 1.4 / 2 * sin(angle.toDouble())).toInt()
        val rect = Rect(x - imgWidth / 2, y - imgWidth / 2, x + imgWidth / 2, y + imgWidth / 2)
        canvas.drawBitmap(bitmap, null, rect, null)
    }

    private fun drawImage(canvas: Canvas, tmpAngle: Float, bitmap: Bitmap) {
        val imgWidth = radius / spinWheelItemList!!.size
        val angle = ((tmpAngle + 360 / spinWheelItemList!!.size / 2) * Math.PI / 180).toFloat()
        val x = (center + radius / 1.6 / 2 * cos(angle.toDouble())).toInt()
        val y = (center + radius / 1.6 / 2 * sin(angle.toDouble())).toInt()
        val rect = Rect(x - imgWidth / 2, y - imgWidth / 2, x + imgWidth / 2, y + imgWidth / 2)
        canvas.drawBitmap(bitmap, null, rect, null)
    }

    private fun drawStarImage(canvas: Canvas, tmpAngle: Float, bitmap: Bitmap) {
        val imgWidth = radius / spinWheelItemList!!.size
        val angle = ((tmpAngle + 360 / spinWheelItemList!!.size / 2) * Math.PI / 180).toFloat()
        val x = (center + radius / 1.2 / 2 * cos(angle.toDouble())).toInt()
        val y = (center + radius / 1.2 / 2 * sin(angle.toDouble())).toInt()
        val rect = Rect(x - imgWidth / 2, y - imgWidth / 2, x + imgWidth / 2, y + imgWidth / 2)
        canvas.drawBitmap(bitmap, null, rect, null)
    }


    private fun drawText(canvas: Canvas, tmpAngle: Float, sweepAngle: Float, mStr: String) {
        val path = Path()
        path.addArc(range, tmpAngle, sweepAngle)
        val textWidth = textPaint!!.measureText(mStr)
        val hOffset = (radius * Math.PI / spinWheelItemList!!.size / 2 - textWidth / 2).toInt()
        val vOffset = radius / 3
        canvas.drawTextOnPath(
            mStr, path, hOffset.toFloat(), (vOffset - 40).toFloat(),
            textPaint!!
        )
    }

    private val angleOfIndexTarget: Float
        get() {
            val tempIndex = if (targetIndex == 0) 1 else targetIndex
            return (45 / spinWheelItemList!!.size * tempIndex).toFloat()
        }

    fun setRound(numberOfRound: Int) {
        roundOfNumber = numberOfRound
    }

    fun rotateTo(index: Int) {
        if (isRunning) {
            return
        }
        targetIndex = index
        rotation = 0f
        val targetAngle =
            360 * roundOfNumber + 270 - angleOfIndexTarget + 360 / spinWheelItemList!!.size / 2
        animate()
            .setInterpolator(DecelerateInterpolator())
            .setDuration(roundOfNumber * 250 + 1500L)
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
