package com.example.storyapp.ui.custom

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.example.storyapp.R

class CustomInputEmail : AppCompatEditText {

    private lateinit var emailIcon: Drawable
    var valid: Boolean = false
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

    private fun init() {
        emailIcon = ContextCompat.getDrawable(context, R.drawable.baseline_email_24) as Drawable
        errorBg =
            ContextCompat.getDrawable(context, R.drawable.custom_input_error_rounded) as Drawable
        defaultBg =
            ContextCompat.getDrawable(context, R.drawable.custom_input_rounded) as Drawable

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not used
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Not used
            }

            override fun afterTextChanged(s: Editable?) {
                validateEmail(s.toString())
                background = if (isEmailValid(s.toString())) {
                    defaultBg
                } else errorBg
            }
        })
    }

    private fun validateEmail(email: String) {
        val isValid = isEmailValid(email)

        errorText = if (isValid) {
            valid = true
            null
        } else {
            valid = false
            context.getString(R.string.error_email)
        }

        showErrorText(isValid)
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

    private fun isEmailValid(email: String): Boolean {
        return Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$").matches(email)
    }
}