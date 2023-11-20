package com.arasana.arasana.util.wheel

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import com.arasana.arasana.R
import kotlin.math.cos
import kotlin.math.sin

class WheelView : View {
    private var mRange = RectF()
    private var mRadius = 20
    private var mArcPaint: Paint? = null
    private var mBackgroundPaint: Paint? = null
    private var mTextPaint: Paint? = null
    private val mStartAngle = 0f
    private var mCenter = 0
    private var mCircleCenter = 0
    private var mPadding = 0
    private var mTargetIndex = 0
    private var mRoundOfNumber = 4
    private var isRunning = false
    private var defaultBackgroundColor = -3
    private var drawableCenterImage: Drawable? = null
    private var textColor = Color.WHITE
    private var mLuckyItemList: List<WheelItem>? = null
    private var mPieRotateListener: PieRotateListener? = null
    private var startBohoColor = resources.getColor(R.color.start_wheel)
    private var endBohoColor = resources.getColor(R.color.end_wheel)
    private val strokePaint = Paint()
    interface PieRotateListener {
        fun rotateDone(index: Int)
    }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    fun setPieRotateListener(listener: PieRotateListener?) {
        mPieRotateListener = listener
    }

    private fun init() {
        mArcPaint = Paint()
        mArcPaint?.isAntiAlias = true
        mArcPaint?.isDither = true

        mTextPaint = Paint()
        val screenSize = resources.displayMetrics
        val screenDensity = screenSize.density
        val desiredSpSize = 18f

        val textSizeInPixels = (desiredSpSize * screenDensity)

        mTextPaint?.textSize = textSizeInPixels
        val font = ResourcesCompat.getFont(context, R.font.lexend_medium)
        mTextPaint?.typeface = font
        mTextPaint?.letterSpacing = 0.6.toFloat()
        mTextPaint?.style=Paint.Style.FILL_AND_STROKE
        mRange = RectF(
            mPadding.toFloat()  ,
            mPadding.toFloat() ,
            (mPadding + mRadius).toFloat() ,
            (mPadding + mRadius).toFloat()
        )

        strokePaint.isAntiAlias = true
        strokePaint.color = Color.BLACK
        strokePaint.style = Paint.Style.STROKE
        strokePaint.strokeWidth = 5f

    }

    fun setData(luckyItemList: List<WheelItem>?) {
        mLuckyItemList = luckyItemList
        invalidate()
    }

    fun setPieBackgroundColor(color: Int) {
        defaultBackgroundColor = color
        invalidate()
    }

    fun setPieCenterImage(drawable: Drawable?) {
        drawableCenterImage = drawable
        invalidate()
    }

    fun setPieTextColor(color: Int) {
        textColor = color
        invalidate()
    }


    private fun drawBitmapOutsideArc(canvas: Canvas, bitmap: Bitmap) {
        val radius = mRadius.toFloat()
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap,  mCenter+radius.toInt(),  mCenter+radius.toInt(), true)
        val x = mCenter
        val y = mCenter
        val bitmapWidth = scaledBitmap.width
        val bitmapHeight = scaledBitmap.height
        val left = x - bitmapWidth / 2
        val top = y - bitmapHeight / 2
        val right = left + bitmapWidth
        val bottom = top + bitmapHeight
        val destRect = Rect(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
        canvas.drawBitmap(scaledBitmap, null, destRect, null)
    }





    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        init()

    }




    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (mLuckyItemList == null) {
            return
        }
        setOnDraw(canvas)
        canvas.restore()
    }
    private fun setOnDraw(canvas:Canvas){
        drawBackgroundColor(canvas,R.color.black)
        val options = BitmapFactory.Options()
        options.inSampleSize = 2
        val bitmap=BitmapFactory.decodeResource(resources, R.drawable.ic_bg_wheel,options)
        var tmpAngle = mStartAngle
        val sweepAngle = (360 / mLuckyItemList!!.size).toFloat()
        for (i in mLuckyItemList!!.indices) {
            setTextPaint(i)
            mArcPaint!!.color = mLuckyItemList!![i].color
            drawArc(canvas, i, tmpAngle, sweepAngle)
            drawProductImage(mLuckyItemList,i, canvas,tmpAngle)
            drawVerticalReText(i, canvas, tmpAngle, sweepAngle)
            tmpAngle += sweepAngle
        }
        drawBitmapOutsideArc(canvas, bitmap)
    }
    private fun drawVerticalReText(i:Int,canvas: Canvas,tmpAngle:Float,sweepAngle:Float){
        if (i==3){
            drawVerticalText(canvas,tmpAngle,sweepAngle,mLuckyItemList!![i].text)
            //  drawRespinText(canvas, tmpAngle, sweepAngle, mLuckyItemList!![i].text)
        }else{
            drawText(canvas, tmpAngle, sweepAngle, mLuckyItemList!![i].text)
        }

    }
    private fun drawProductImage(mLuckyItemList: List<WheelItem>?, i:Int, canvas: Canvas, tmpAngle:Float){
        mLuckyItemList?.let {
            if (i==5){
                drawStarImage(canvas,tmpAngle,BitmapFactory.decodeResource(resources,
                    R.drawable.ic_whell_star
                ))
                drawImage(canvas, tmpAngle, BitmapFactory.decodeResource(resources,
                    R.drawable.ic_jackpot
                ))
            }else if (i!=3){
                drawOthersImage(canvas, tmpAngle, BitmapFactory.decodeResource(resources, it[i].icon))
            }
        }
    }
    private fun drawArc(canvas: Canvas,i: Int,tmpAngle:Float,sweepAngle:Float){
        if (i==5){
            val paint=Paint()
            val linearGradient = LinearGradient(
                mRange.left,
                mRange.top,
                mRange.right,
                mRange.bottom,
                startBohoColor,
                Color.WHITE,
                Shader.TileMode.CLAMP
            )
            paint.shader = linearGradient
            canvas.drawArc(mRange, tmpAngle, sweepAngle , true, paint!!)

            canvas.drawArc(mRange, tmpAngle, sweepAngle, true, strokePaint)
        }else{
            canvas.drawArc(mRange, tmpAngle, sweepAngle , true, mArcPaint!!)
            canvas.drawArc(mRange, tmpAngle, sweepAngle, true, strokePaint)
        }
    }
    private fun setTextPaint(i:Int){
        when(i){
            1->{
                mTextPaint?.color=Color.WHITE

            }
            5->{
                mTextPaint?.color=Color.WHITE

            }
            else->{
                mTextPaint?.color=Color.BLACK
            }


        }
    }
    private fun drawVerticalText(canvas: Canvas, tmpAngle: Float, sweepAngle: Float, text: String) {
        val path = Path()
        val angle = tmpAngle + sweepAngle / 2
        val radius = mRadius / 2

        val x = mCenter + radius * cos(Math.toRadians(angle.toDouble())).toFloat()
        val y = mCenter + radius * sin(Math.toRadians(angle.toDouble())).toFloat()
        path.moveTo(x, y)
        path.lineTo(x+350, y + text.length * 30)
        val textPaint = Paint()
        val screenSize = resources.displayMetrics
        val screenDensity = screenSize.density
        val desiredSpSize = 20f

        val textSizeInPixels = (desiredSpSize * screenDensity)
        textPaint?.textSize = textSizeInPixels
        val font = ResourcesCompat.getFont(context, R.font.lexend_medium)
        textPaint?.typeface = font
        textPaint?.style=Paint.Style.FILL_AND_STROKE
        textPaint.color = Color.WHITE
        canvas.drawTextOnPath(text, path, text.length.toFloat()*15/2, 0f, textPaint)
    }
    private fun drawBackgroundColor(canvas: Canvas, color: Int) {
        if (color == -1) return
        mBackgroundPaint = Paint()
        mBackgroundPaint?.color = Color.BLACK
        canvas.drawCircle(
            mCenter.toFloat() , mCenter.toFloat() , mCenter.toFloat() ,
            mBackgroundPaint!!
        )

    }



    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = measuredWidth.coerceAtMost(measuredHeight)
        mPadding = if (paddingLeft == 0) 50 else paddingLeft
        mRadius = width - mPadding * 2
        mCenter = width / 2
        mCircleCenter=measuredHeight
        setMeasuredDimension(width, width)
    }
    private fun drawOthersImage(canvas: Canvas, tmpAngle: Float, bitmap: Bitmap) {
        val imgWidth = mRadius / mLuckyItemList!!.size
        val angle = ((tmpAngle + 360 / mLuckyItemList!!.size / 2) * Math.PI / 180).toFloat()
        val x = (mCenter + mRadius / 1.4 / 2 * cos(angle.toDouble())).toInt()
        val y = (mCenter + mRadius / 1.4 / 2 * sin(angle.toDouble())).toInt()
        val rect = Rect(x - imgWidth / 2, y - imgWidth / 2, x + imgWidth / 2, y + imgWidth / 2)
        canvas.drawBitmap(bitmap, null, rect, null)
    }

    private fun drawImage(canvas: Canvas, tmpAngle: Float, bitmap: Bitmap) {
        val imgWidth = mRadius / mLuckyItemList!!.size
        val angle = ((tmpAngle + 360 / mLuckyItemList!!.size / 2) * Math.PI / 180).toFloat()
        val x = (mCenter + mRadius / 1.6 / 2 * cos(angle.toDouble())).toInt()
        val y = (mCenter + mRadius / 1.6 / 2 * sin(angle.toDouble())).toInt()
        val rect = Rect(x - imgWidth / 2, y - imgWidth / 2, x + imgWidth / 2, y + imgWidth / 2)
        canvas.drawBitmap(bitmap, null, rect, null)
    }
    private fun drawStarImage(canvas: Canvas, tmpAngle: Float, bitmap: Bitmap) {
        val imgWidth = mRadius / mLuckyItemList!!.size
        val angle = ((tmpAngle + 360 / mLuckyItemList!!.size / 2) * Math.PI / 180).toFloat()
        val x = (mCenter + mRadius / 1.2 / 2 * cos(angle.toDouble())).toInt()
        val y = (mCenter + mRadius / 1.2 / 2 * sin(angle.toDouble())).toInt()
        val rect = Rect(x - imgWidth / 2, y - imgWidth / 2, x + imgWidth / 2, y + imgWidth / 2)
        canvas.drawBitmap(bitmap, null, rect, null)
    }



    private fun drawText(canvas: Canvas, tmpAngle: Float, sweepAngle: Float, mStr: String) {
        val path = Path()
        path.addArc(mRange, tmpAngle, sweepAngle)
        val textWidth = mTextPaint!!.measureText(mStr)
        val hOffset = (mRadius * Math.PI / mLuckyItemList!!.size / 2 - textWidth / 2).toInt()
        val vOffset = mRadius / 3
        canvas.drawTextOnPath(
            mStr, path, hOffset.toFloat(), (vOffset - 40).toFloat(),
            mTextPaint!!
        )
    }
    private fun drawRespinText(canvas: Canvas, tmpAngle: Float, sweepAngle: Float, mStr: String) {
        val path = Path()
        path.addArc(mRange, tmpAngle, sweepAngle)
        val textWidth = mTextPaint!!.measureText(mStr)+10f
        val hOffset = (mRadius * Math.PI / mLuckyItemList!!.size / 2 - textWidth / 2).toInt()
        val vOffset = mRadius / 3
        canvas.drawTextOnPath(
            mStr, path, hOffset.toFloat(), (vOffset - 40).toFloat(),
            mTextPaint!!
        )
    }

    private val angleOfIndexTarget: Float
        get() {
            val tempIndex = if (mTargetIndex == 0) 1 else mTargetIndex
            return (45 / mLuckyItemList!!.size * tempIndex).toFloat()
        }

    fun setRound(numberOfRound: Int) {
        mRoundOfNumber = numberOfRound
    }
    fun rotateTo(index: Int) {
        if (isRunning) {
            return
        }
        mTargetIndex = index
        rotation = 0f
        val targetAngle =
            360 * mRoundOfNumber + 270 - angleOfIndexTarget + 360 / mLuckyItemList!!.size / 2
        animate()
            .setInterpolator(DecelerateInterpolator())
            //Dönme Süresi
            .setDuration(mRoundOfNumber * 250 + 1500L)
            .setListener(object : WheelContract() {

                override fun onAnimationStart(animation: Animator) {
                    isRunning = true
                    setLayerType(LAYER_TYPE_HARDWARE,mArcPaint)
                    setLayerType(LAYER_TYPE_HARDWARE,strokePaint)
                }

                override fun onAnimationEnd(animation: Animator) {
                    isRunning = false
                    if (mPieRotateListener != null) {
                        mPieRotateListener!!.rotateDone(mTargetIndex)
                    }
                    setLayerType(LAYER_TYPE_NONE,mArcPaint)
                    setLayerType(LAYER_TYPE_NONE,strokePaint)
                }

            })
            //Dönme Hızı
            .rotation(targetAngle)
            .start()
    }

     @SuppressLint("ObjectAnimatorBinding")
     fun setCursorAnimate(cursor: ImageView){
        val rotateRight = ObjectAnimator.ofFloat(cursor, "rotation", 15f, 0f)
        val rotateLeft = ObjectAnimator.ofFloat(cursor, "rotation", 0f,-15f)
        rotateRight.duration = 1000
        rotateLeft.duration = 1000
        val animatorSet = AnimatorSet()
        animatorSet.playSequentially(rotateRight,rotateLeft)
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
