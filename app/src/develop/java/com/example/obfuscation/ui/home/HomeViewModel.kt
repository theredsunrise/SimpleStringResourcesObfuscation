package com.example.obfuscation.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel(private val title: String) : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = title
    }
    val text: LiveData<String> = _text
}