package com.gov.gruhalakshmi.common

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import android.view.inputmethod.BaseInputConnection
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.webkit.WebView

class CustomWebView : WebView {
    constructor(context: Context?) : super(context!!) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context!!,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    protected fun init() {
        isFocusable = true
        isFocusableInTouchMode = true
    }

    override fun onCreateInputConnection(outAttrs: EditorInfo): InputConnection {
        val baseInputConnection = BaseInputConnection(this, false)
        outAttrs.imeOptions = EditorInfo.IME_ACTION_DONE
        outAttrs.inputType = InputType.TYPE_CLASS_TEXT
        return baseInputConnection
    }

    override fun onCheckIsTextEditor(): Boolean {
        return true
    }
}