package com.serdar.whell.wheel

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.serdar.whell.R
import com.serdar.whell.databinding.CustomWheelLayoutBinding

@SuppressLint("CustomViewStyleable")
class SpinWheelView @JvmOverloads
constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int = 0) :
    FrameLayout(context, attributeSet, defStyleAttr), SpinWheelRotateListener {
    private var spinBackgroundColor = 0
    private var spinTextColor = 0
    private var spinText = "Spin"
    private var spinTextView: TextView? = null
    private var spinCenterImage: Drawable? = null
    private var spinCursorImage: Drawable? = null
    private var spinView: CustomSpinWhellView? = null
    private var ivCursorView: ImageView? = null
    private var spinWheelRoundSelectedListener: SpinWheelRoundSelectedListener? = null

    private val _binding =
        CustomWheelLayoutBinding.inflate(LayoutInflater.from(context), this, false)

    init {
        addView(_binding.root)
        setAttr(context, attributeSet)
    }

    private fun setAttr(ctx: Context, attrs: AttributeSet?) {
        if (attrs != null) {
            val typedArray = ctx.obtainStyledAttributes(attrs, R.styleable.SpinWheel)
            spinBackgroundColor =
                typedArray.getColor(R.styleable.SpinWheel_spinWheelBackgroundColor, -0x340000)
            spinTextColor =
                typedArray.getColor(R.styleable.SpinWheel_spinWheelTextColor, Color.WHITE)
            spinCursorImage = typedArray.getDrawable(R.styleable.SpinWheel_spinWheelCursor)
            spinCenterImage = typedArray.getDrawable(R.styleable.SpinWheel_spinWheelCenterImage)
            typedArray.recycle()
        }

        spinView = _binding.spinView
        ivCursorView = _binding.cursorView
        spinTextView = _binding.spinText
        spinTextView?.text = spinText
        spinView?.setPieRotateListener(this)
        spinView?.setPieBackgroundColor(spinBackgroundColor)
        spinView?.setPieCenterImage(spinCenterImage)
        spinView?.setPieTextColor(spinTextColor)
        ivCursorView?.setImageDrawable(spinCursorImage)
    }

    fun setSpinData(data: List<WheelItem>?) {
        spinView?.setData(data)
    }

    fun setSpinRound(numberOfRound: Int) {
        spinView?.setRound(numberOfRound)
    }

    fun setSpinText(text: String) {
        spinTextView?.text = text
    }

    fun startSpinWheel(index: Int) {
        spinView?.rotateTo(index)
    }

    fun setCursorAnimate() {
        spinView?.setCursorAnimate(ivCursorView!!)
    }

    override fun rotateDone(index: Int) {
        if (spinWheelRoundSelectedListener != null) {
            spinWheelRoundSelectedListener?.wheelRoundItemSelected(index)
        }
    }

}
