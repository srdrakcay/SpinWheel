package com.serdar.whell.wheel

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import com.serdar.whell.R
import com.serdar.whell.databinding.CustomWheelLayoutBinding

@SuppressLint("CustomViewStyleable")
class SpinWheelView @JvmOverloads
constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int = 0) :
    FrameLayout(context, attributeSet, defStyleAttr), SpinWheelRotateListener {
    private var spinTextColor = 0
    private var spinCenterImage: Int? = null
    private var spinWheelSpecialImage: Drawable? = null
    private var spinCursorImage: Drawable? = null
    private var spinView: CustomSpinWheelView? = null
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
            spinTextColor =
                typedArray.getColor(R.styleable.SpinWheel_spinWheelTextColor, Color.WHITE)
            spinCursorImage = typedArray.getDrawable(R.styleable.SpinWheel_spinWheelCursor)
            typedArray.recycle()

        }
        spinView = _binding.spinView
        ivCursorView = _binding.cursorView
        spinView?.setSpinRotateListener(this)
        spinView?.setSpinCenterImage(spinCenterImage)
        spinView?.setSpinTextColor(spinTextColor)
        ivCursorView?.setImageDrawable(spinCursorImage)
        spinWheelSpecialImage?.let { spinView?.setSpinSpecialImage(it) }
    }
    fun setSpinData(data: List<WheelItem>?) {
        spinView?.setData(data)
    }
    fun setSpinRound(numberOfRound: Int) {
        spinView?.setRound(numberOfRound)
    }
    fun setSpinCenterImage(spinWheelCenterImage: Int) {
        spinCenterImage = spinWheelCenterImage
        spinView?.setSpinCenterImage(spinCenterImage)
    }
    fun startSpinWheel(index: Int) {
        spinView?.startRotate(index)
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
