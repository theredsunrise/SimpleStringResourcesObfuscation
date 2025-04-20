package com.example.obfuscation.ui.gallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GalleryViewModel(private val title: String) : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = title
    }
    val text: LiveData<String> = _text
}