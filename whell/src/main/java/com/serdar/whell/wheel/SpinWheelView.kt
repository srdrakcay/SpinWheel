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
    private var spinCenterImage: Int? = null
    private var spinWheelSpecialImage: Int? = null
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
            typedArray.recycle()
        }

        spinView = _binding.spinView
        ivCursorView = _binding.cursorView
        spinView?.setSpinRotateListener(this)
        spinView?.setSpinBackgroundColor(spinBackgroundColor)
        spinView?.setSpinCenterImage(spinCenterImage)
        spinView?.setSpinTextColor(spinTextColor)
        ivCursorView?.setImageDrawable(spinCursorImage)
    }

    fun setSpinData(data: List<WheelItem>?) {
        spinView?.setData(data)
    }

    fun setSpinRound(numberOfRound: Int) {
        spinView?.setRound(numberOfRound)
    }


    fun setSpinSpecialImage(specialImage: Int) {
        spinWheelSpecialImage = specialImage
        spinView?.setSpinSpecialImage(spinWheelSpecialImage)
    }
    fun setSpinCenterImage(spinWheelCenterImage: Int) {
        spinCenterImage = spinWheelCenterImage
        spinView?.setSpinCenterImage(spinWheelSpecialImage)
    }

    fun startSpinWheel(index: Int) {
        spinView?.rotateTo(index)
    }

    fun setCursorAnimate() {
        spinView?.setCursorAnimate(ivCursorView!!)
    }
    fun setSpinRoundItemSelectedListener(listener: SpinWheelRoundSelectedListener?) {
        spinWheelRoundSelectedListener = listener
    }
    override fun rotateDone(index: Int) {
        if (spinWheelRoundSelectedListener != null) {
            spinWheelRoundSelectedListener?.selectedRoundItemSelected(index)
        }
    }

}
