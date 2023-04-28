package com.example.storyapp.ui.costume

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.example.storyapp.R

class CostumeInputPassword : AppCompatEditText, View.OnTouchListener {

    private lateinit var showPasswordIcon: Drawable

    constructor(context: Context) : super(context) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        showPasswordIcon = ContextCompat.getDrawable(context, R.drawable.baseline_remove_red_eye_24) as Drawable
        setOnTouchListener(this)
    }

    private fun showEyePasswordIcon() {
        setButtonDrawables(endOfTheText = showPasswordIcon)
    }

    private fun hideEyePasswordIcon() {
        setButtonDrawables()
    }

    private fun setButtonDrawables(
        startOfTheText: Drawable? = null,
        topOfTheText:Drawable? = null,
        endOfTheText:Drawable? = null,
        bottomOfTheText: Drawable? = null
    ) {
        setCompoundDrawablesWithIntrinsicBounds(
            startOfTheText,
            topOfTheText,
            endOfTheText,
            bottomOfTheText
        )
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        if (compoundDrawables[2] != null) {
            val showPasswordStart: Float
            val showPasswordEnd: Float
            var isShowPasswordButtonClicked = false

            if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                showPasswordEnd = (showPasswordIcon.intrinsicWidth + paddingStart).toFloat()
                when {
                    event.x < showPasswordEnd -> isShowPasswordButtonClicked = true
                }
            } else {
                showPasswordStart = (width - paddingEnd - showPasswordIcon.intrinsicWidth).toFloat()
                when {
                    event.x > showPasswordStart -> isShowPasswordButtonClicked = true
                }
            }
        }
        return false
    }
}
