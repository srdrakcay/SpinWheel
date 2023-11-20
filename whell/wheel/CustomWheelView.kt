package com.arasana.arasana.util.wheel

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.arasana.arasana.R

class CustomWheelView : RelativeLayout, WheelView.PieRotateListener {
    private var mBackgroundColor = 0
    private var mTextColor = 0
    private var spinText = "Spin For Free"
    private var spinTextView: TextView? = null
    private var mCenterImage: Drawable? = null
    private var mCursorImage: Drawable? = null
    private var pieView: WheelView? = null
    private var ivCursorView: ImageView? = null
    private var mWheelRoundItemSelectedListener: WheelRoundItemSelectedListener? = null
    override fun rotateDone(index: Int) {
        if (mWheelRoundItemSelectedListener != null) {
            mWheelRoundItemSelectedListener?.wheelRoundItemSelected(index)
        }
    }

    interface WheelRoundItemSelectedListener {
        fun wheelRoundItemSelected(index: Int)
    }

    fun setLuckyRoundItemSelectedListener(listener: WheelRoundItemSelectedListener?) {
        mWheelRoundItemSelectedListener = listener
    }

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    private fun init(ctx: Context, attrs: AttributeSet?) {
        if (attrs != null) {
            val typedArray = ctx.obtainStyledAttributes(attrs, R.styleable.LuckyWheelView)
            mBackgroundColor =
                typedArray.getColor(R.styleable.LuckyWheelView_lkwBackgroundColor, -0x340000)
            mTextColor = typedArray.getColor(R.styleable.LuckyWheelView_lkwTextColor, Color.WHITE)
            mCursorImage = typedArray.getDrawable(R.styleable.LuckyWheelView_lkwCursor)
            mCenterImage = typedArray.getDrawable(R.styleable.LuckyWheelView_lkwCenterImage)
            typedArray.recycle()
        }
        val inflater = LayoutInflater.from(context)
        val frameLayout = inflater.inflate(R.layout.lucky_wheel_layout, this, false) as FrameLayout
        pieView = frameLayout.findViewById<View>(R.id.pieView) as WheelView
        ivCursorView = frameLayout.findViewById<View>(R.id.cursorView) as ImageView
        spinTextView = frameLayout.findViewById<View>(R.id.spinText) as TextView
        spinTextView?.text=spinText
        pieView?.setPieRotateListener(this)
        pieView?.setPieBackgroundColor(mBackgroundColor)
        pieView?.setPieCenterImage(mCenterImage)
        pieView?.setPieTextColor(mTextColor)
        ivCursorView!!.setImageDrawable(mCursorImage)
        addView(frameLayout)
    }





    fun setData(data: List<WheelItem>?) {
        pieView?.setData(data)
    }

    fun setRound(numberOfRound: Int) {
        pieView?.setRound(numberOfRound)
    }
    fun setSpinText(text: String) {
        spinTextView?.text=text
    }

    fun startLuckyWheelWithTargetIndex(index: Int) {
        pieView?.rotateTo(index)
    }
     fun setCursorAnimate() {
     pieView?.setCursorAnimate(ivCursorView!!)
    }


}
