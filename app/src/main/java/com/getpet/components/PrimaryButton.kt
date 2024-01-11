package com.getpet.components

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import com.getpet.R

class PrimaryButton : AppCompatButton {

    constructor(context: Context) : super(context) {
        // Your constructor code here
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        // Your constructor code here
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        // Your constructor code here
        init()
    }

    // Additional methods or overrides can be added here
    fun init() {
        this.setBackgroundResource(R.drawable.round_button)
        setPadding(16, 8, 16, 8)
    }
}