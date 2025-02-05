package com.example.obfuscation.custom

import android.content.Context
import android.util.AttributeSet
import androidx.core.view.forEach
import com.example.obfuscation.Helper
import com.example.obfuscation.decrypt
import com.google.android.material.R
import com.google.android.material.navigation.NavigationView

class CustomNavigationView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttrs: Int = R.attr.navigationViewStyle
) :
    NavigationView(context, attrs, defStyleAttrs) {

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        menu.forEach {
            it.title = it.title?.toString()?.let { text -> text.decrypt }
        }
    }
}