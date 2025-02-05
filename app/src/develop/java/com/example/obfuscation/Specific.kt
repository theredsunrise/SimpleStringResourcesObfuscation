package com.example.obfuscation

import android.content.Context
import androidx.annotation.StringRes
import com.example.shared.Obfuscation
import com.example.shared.specific

// Use for string.
val String.decrypt: String
    get() = specific(com.example.obfuscation.library.BuildConfig.DEVELOP, develop = {
        //Development mode returns the plaintext.
        return this
    }) {
        //Obfuscate mode returns the decrypted value of a string resource that was encrypted earlier with Gradle during the build process.
        Obfuscation.decrypt(this)
    }

// Use for string resource id.
fun Context.decrypt(@StringRes id: Int): String =
    specific(com.example.obfuscation.library.BuildConfig.DEVELOP, develop = {
        //Development mode returns the plaintext.
        return getString(id)
    }) {
        //Obfuscate mode returns the decrypted value of a string resource that was encrypted earlier with Gradle during the build process.
        getString(id).decrypt
    }

// Use in view databinding.
object Helper {
    fun decrypt(string: String): String = specific(BuildConfig.DEVELOP, develop = {
        //Development mode returns the plaintext.
        return string
    }) {
        //Obfuscate mode returns the decrypted value of a string resource that was encrypted earlier with Gradle during the build process.
        return string.decrypt
    }
}