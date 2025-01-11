package com.example.shared

import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

inline fun <reified T> specific(isDevelop: Boolean, develop: () -> T, generated: () -> T): T {
    return if (isDevelop) {
        develop()
    } else {
        generated()
    }
}

object Obfuscation {
    @OptIn(ExperimentalEncodingApi::class)
    fun encrypt(string: String) = Base64.encode(string.toByteArray(Charsets.UTF_8))

    @OptIn(ExperimentalEncodingApi::class)
    fun decrypt(string: String) = Base64.decode(string).toString(Charsets.UTF_8)
}