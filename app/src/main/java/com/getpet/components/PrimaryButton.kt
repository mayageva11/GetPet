package com.getpet.components

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import com.getpet.R

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
        this.setBackgroundResource(R.drawable.round_button)
        setPadding(INLINE_PADDING, BLOCK_PADDING, INLINE_PADDING, BLOCK_PADDING)
    }
}