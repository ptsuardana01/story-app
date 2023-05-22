package com.example.storyapp.ui.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.example.storyapp.R

class CustomInputPassword : AppCompatEditText, View.OnTouchListener {

    var valid: Boolean = false
    private lateinit var eyesIcon: Drawable
    private lateinit var lockIcon: Drawable
    private lateinit var errorBg: Drawable
    private lateinit var defaultBg: Drawable

    private var errorText: String? = null

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

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        hint = "Password"
    }

    private fun init() {
        lockIcon = ContextCompat.getDrawable(context, R.drawable.baseline_lock_24) as Drawable
        eyesIcon = ContextCompat.getDrawable(context, R.drawable.baseline_eye_24) as Drawable
        errorBg = ContextCompat.getDrawable(context, R.drawable.custom_input_error_rounded) as Drawable
        defaultBg = ContextCompat.getDrawable(context, R.drawable.custom_input_rounded) as Drawable

        setOnTouchListener(this)

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not used
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Not used
            }

            override fun afterTextChanged(s: Editable?) {
                validatePassword(s.toString())
                background = if (isPassValid(s.toString())) {
                    defaultBg
                } else errorBg
            }
        })
    }

    private fun validatePassword(pass: String) {
        val isValid = isPassValid(pass)

        errorText = if (isValid) {
            valid = true
            null
        } else {
            valid = false
            context.getString(R.string.error_pass)
        }

        showErrorText(isValid)
    }

    private fun isPassValid(pass: String): Boolean {
        return  pass.length >= 8
    }

    private fun showErrorText(isValid: Boolean) {
        if (errorText != null && !isValid) {
            error = errorText
            setTextColor(Color.RED)

        } else {
            error = null
            setTextColor(Color.BLACK)
        }
    }

    private fun showEyePasswordIcon() {
        setButtonDrawables(startOfTheText = lockIcon, endOfTheText = eyesIcon)
    }

    private fun hideEyePasswordIcon() {
        setButtonDrawables(startOfTheText = lockIcon)
    }

    private fun setButtonDrawables(
        startOfTheText: Drawable? = null,
        topOfTheText: Drawable? = null,
        endOfTheText: Drawable? = null,
        bottomOfTheText: Drawable? = null,
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
                showPasswordEnd = (eyesIcon.intrinsicWidth + paddingStart).toFloat()
                when {
                    event.x < showPasswordEnd -> isShowPasswordButtonClicked = true
                }
            } else {
                showPasswordStart = (width - paddingEnd - eyesIcon.intrinsicWidth).toFloat()
                when {
                    event.x > showPasswordStart -> isShowPasswordButtonClicked = true
                }
            }
            if (isShowPasswordButtonClicked) {
                when (event.action) {
                    MotionEvent.ACTION_UP -> {
                        this.transformationMethod = PasswordTransformationMethod.getInstance()
                        Log.d("PasswordAct", "Icon show!")
                        return true
                    }

                    MotionEvent.ACTION_DOWN -> {
                        this.transformationMethod = HideReturnsTransformationMethod.getInstance()
                        Log.d("PasswordAct", "Icon hide!")
                    }
                }
            } else return false
        }
        return false
    }
}
