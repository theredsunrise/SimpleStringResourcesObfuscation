package com.example.nativelib

import android.content.Context
import androidx.annotation.StringRes

object NativeLib {
    external fun encrypt(id: String, content: String): String
    external fun decrypt(content: String): String

    init {
        System.loadLibrary("nativeLib")
    }
}

inline fun <reified T> specific(isDevelop: Boolean, develop: () -> T, generated: () -> T): T {
    return if (isDevelop) {
        develop()
    } else {
        generated()
    }
}

// Use for string.
val String.decrypt: String
    get() = specific(com.example.nativelib.BuildConfig.DEVELOP, develop = {
        //Development mode returns the plaintext.
        return this
    }) {
        //Obfuscate mode returns the decrypted value of a string resource that was encrypted earlier with Gradle during the build process.
        NativeLib.decrypt(this)
    }

// Use for string resource id.
fun Context.decrypt(@StringRes id: Int): String =
    specific(com.example.nativelib.BuildConfig.DEVELOP, develop = {
        //Development mode returns the plaintext.
        return getString(id)
    }) {
        //Obfuscate mode returns the decrypted value of a string resource that was encrypted earlier with Gradle during the build process.
        getString(id).decrypt
    }

// Use in view databinding.
object Helper {
    fun decrypt(string: String): String =
        specific(com.example.nativelib.BuildConfig.DEVELOP, develop = {
            //Development mode returns the plaintext.
            return string
        }) {
            //Obfuscate mode returns the decrypted value of a string resource that was encrypted earlier with Gradle during the build process.
            return string.decrypt
        }
}