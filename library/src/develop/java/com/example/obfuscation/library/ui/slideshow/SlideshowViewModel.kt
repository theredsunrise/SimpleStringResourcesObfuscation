package com.example.obfuscation.library.ui.slideshow

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SlideshowViewModel(private val title: String) : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = title
    }
    val text: LiveData<String> = _text
}