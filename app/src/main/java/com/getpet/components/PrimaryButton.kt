package com.getpet.components

import android.R
import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.getpet.R as Resources


class PrimaryButton : AppCompatButton {

    companion object {
        val BLOCK_PADDING = 8
        val INLINE_PADDING = 16
    }

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        this.setBackgroundResource(Resources.drawable.round_button)
        setPadding(INLINE_PADDING, BLOCK_PADDING, INLINE_PADDING, BLOCK_PADDING)
        val typeface: Typeface? = ResourcesCompat.getFont(context, com.getpet.R.font.raleway)
        setTypeface(typeface, Typeface.BOLD)
        isAllCaps = false
        val newTextColor = ContextCompat.getColor(context, Resources.color.textColor)
        setTextColor(newTextColor)
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 20F)

    }
}